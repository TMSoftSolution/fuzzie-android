package sg.com.fuzzie.android.ui.shop;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.api.models.BrandLink;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.ui.jackpot.JackpotBrandCouponListActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotCouponActivity_;
import sg.com.fuzzie.android.ui.jackpot.PowerUpCouponActivity_;
import timber.log.Timber;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.kevinsawicki.timeago.TimeAgo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.TopBrand;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.BrandListItem;
import sg.com.fuzzie.android.ui.payments.ShoppingBagActivity_;
import sg.com.fuzzie.android.ui.shop.filter.BrandListCategoryRefineActivity_;
import sg.com.fuzzie.android.ui.shop.filter.BrandListRefineActivity_;
import sg.com.fuzzie.android.ui.shop.filter.BrandListSortActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_SHOPPING_BAG;

/**
 * Created by nurimanizam on 17/12/16.
 */

@EActivity(R.layout.activity_brands_list)
public class BrandsListActivity extends BaseActivity {

    public static int REQUEST_SORT =            0;
    public static int REQUEST_REFINE =          1;
    public static int REQUEST_CATEGORY_REFINE = 2;

    Home home;
    List<Brand> brands;
    List<Brand> sortedBrands;
    List<Brand> filteredBrands;
    Category category;
    FastItemAdapter<BrandListItem> brandAdapter;

    BroadcastReceiver broadcastReceiver;


    @Extra
    String titleExtra;

    @Extra
    String brandsExtra;

    @Extra
    String brandIdsExtra;

    @Extra
    int categoryId = 0;

    @Extra
    boolean showFilter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvShoppingBag)
    TextView tvShoppingBag;

    @ViewById(R.id.rvBrands)
    RecyclerView rvBrands;

    @ViewById(R.id.llPowerUpTimer)
    LinearLayout llPowerUpTimer;

    @ViewById(R.id.tvPowerUpTimer)
    TextView tvPowerUpTimer;

    @ViewById(R.id.llFilter)
    LinearLayout llFilter;

    @ViewById(R.id.tvSortBy)
    TextView tvSortBy;

    @ViewById(R.id.tvRefine)
    TextView tvRefine;

    @ViewById(R.id.tvCount)
    TextView tvCount;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if (currentUser != null
                    && currentUser.getCurrentUser() != null
                    && currentUser.getCurrentUser().getPowerUpExpirationTime() != null
                    && currentUser.getCurrentUser().getPowerUpExpirationTime().length() > 0
                    && tvPowerUpTimer != null
                    && llPowerUpTimer != null) {

                llPowerUpTimer.setVisibility(View.VISIBLE);

                TimeAgo timeAgo = new TimeAgo();
                DateTimeFormatter parser = ISODateTimeFormat.dateTime();

                DateTime now = new DateTime();
                DateTime expiryDate = parser.parseDateTime(currentUser.getCurrentUser().getPowerUpExpirationTime());

                long milliseconds = expiryDate.getMillis() - now.getMillis();
                int hours = (int) (milliseconds/3600000);
                int mins = (int) (milliseconds/60000);
                mins %= 60;
                int secs = (int) (milliseconds/1000);
                secs %= 60;

                tvPowerUpTimer.setText(String.format("%02d:%02d:%02d",hours,mins,secs));

                Timber.d("Power Up Timer: " + milliseconds);

            } else if (llPowerUpTimer != null) {
                llPowerUpTimer.setVisibility(View.GONE);
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @AfterViews
    public void calledAfterViewInjection() {

        if (showFilter){
            llFilter.setVisibility(View.VISIBLE);
            if (categoryId == 0){
                tvRefine.setText("Categories");
            } else {
                tvRefine.setText("All");
            }
            tvCount.setVisibility(View.GONE);
        } else {
            llFilter.setVisibility(View.GONE);
        }

        tvTitle.setText(titleExtra);
        refreshShoppingBag();

        home = dataManager.getHome();
        if (home == null) return;
        brands = new ArrayList<>();

        if (brandsExtra != null){

            Gson gson = new Gson();
            Type type = new TypeToken<List<Brand>>(){}.getType();
            brands = gson.fromJson(brandsExtra,type);

        } else if (brandIdsExtra != null && !brandIdsExtra.equals("")){

            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            List<String> brandIds = gson.fromJson(brandIdsExtra, type);

            for (String brandId : brandIds) {
                for (Brand brand : home.getBrands()) {
                    if (brandId.equals(brand.getId())) {
                        brands.add(brand);
                        break;
                    }
                }
            }

        } else if (categoryId != 0 && home.getCategories() != null && home.getBrands() != null){
            for (Category category : home.getCategories()){
                if (category.getId() == categoryId){
                    this.category = category;
                    break;
                }
            }

            if (category != null){
                for (String brandId : category.getBrands()){
                    for (Brand brand : home.getBrands()){
                        if (brandId.equals(brand.getId())){
                            brands.add(brand);
                            break;
                        }
                    }
                }
            }


        } else {
            switch (titleExtra) {
                case "RECOMMENDED BRANDS":
                    if (home.getRecommendedBrands() != null){
                        for (String brandId : home.getRecommendedBrands()) {
                            for (Brand brand : home.getBrands()) {
                                if (brandId.equals(brand.getId())) {
                                    Brand mBrand = new Brand(brand);
                                    brands.add(mBrand);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case "CLUB BRANDS":
                    if (home.getNewBrands() != null){
                        for (TopBrand topBrand : home.getClubBrands()){
                            for (Brand brand : home.getBrands()){
                                if (brand.getId().equals(topBrand.getBrandId())){
                                    Brand mBrand = new Brand(brand);
                                    mBrand.setBrandLink(topBrand.getBrandLink());
                                    brands.add(mBrand);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case "TRENDING BRANDS":
                    if (home.getPopularBrands() != null){
                        for (String brandId : home.getPopularBrands()) {
                            for (Brand brand : home.getBrands()) {
                                if (brandId.equals(brand.getId())) {
                                    Brand mBrand = new Brand(brand);
                                    brands.add(mBrand);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case "NEW BRANDS":
                    if (home.getNewBrands() != null){
                        for (TopBrand topBrand : home.getNewBrands()){
                            for (Brand brand : home.getBrands()){
                                if (brand.getId().equals(topBrand.getBrandId())){
                                    Brand mBrand = new Brand(brand);
                                    mBrand.setBrandLink(topBrand.getBrandLink());
                                    brands.add(mBrand);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case "TOP BRANDS":
                    if (home.getTopBrands() != null){
                        for (TopBrand topBrand : home.getTopBrands()){
                            for (Brand brand : home.getBrands()){
                                if (brand.getId().equals(topBrand.getBrandId())){
                                    Brand mBrand = new Brand(brand);
                                    mBrand.setBrandLink(topBrand.getBrandLink());
                                    mBrand.setCustomImageUrl(topBrand.getImage());
                                    brands.add(mBrand);
                                    break;
                                }
                            }
                        }
                    }
                    break;
            }
        }

        brandAdapter = new FastItemAdapter<>();
        brandAdapter.withOnClickListener(new OnClickListener<BrandListItem>() {
            @Override
            public boolean onClick(View v, IAdapter<BrandListItem> adapter, BrandListItem item, int position) {

                Brand brand = item.getBrand();

                if (titleExtra.equals("NEW BRANDS") || titleExtra.equals("TOP BRANDS") || titleExtra.equals("CLUB BRANDS")){

                    if (brand.getBrandLink() != null){

                        if (brand.getBrandLink().getType().equals("brand")){

                            goNormalBrandPage(context, brand);

                        } else if (brand.getBrandLink().getType().equals("jackpot_coupon")){

                            if (brand.getBrandLink().getCouponId() != null && !brand.getBrandLink().getCouponId().equals("")){

                                Coupon coupon = dataManager.getCouponById(brand.getBrandLink().getCouponId());
                                if (coupon != null){
                                    if (coupon.getPowerUpPack() != null){

                                        PowerUpCouponActivity_.intent(context).couponId(brand.getBrandLink().getCouponId()).start();

                                    } else {

                                        JackpotCouponActivity_.intent(context).couponId(brand.getBrandLink().getCouponId()).start();

                                    }
                                }
                            }

                        } else if (brand.getBrandLink().getType().equals("jackpot_coupon_list")){

                            if (brand.getBrandLink().getCouponIds() != null && brand.getBrandLink().getCouponIds().size() > 0){
                                JackpotBrandCouponListActivity_.intent(context).brandLinkJson(BrandLink.toJSON(brand.getBrandLink())).start();
                            }
                        }
                    }


                } else {

                    goNormalBrandPage(context, brand);

                }

                return true;
            }
        });

        rvBrands.setLayoutManager(new LinearLayoutManager(context));
        rvBrands.setAdapter(brandAdapter);

        dataManager.selectedBrandsCategoryIds = new TreeSet<>();
        dataManager.selectedBrandsSubCategoryIds = new TreeSet<>();
        dataManager.selectedSortByIndex = 0;

        if (showFilter){
            updateSortBy();
        } else {
            for (Brand brand: brands) {
                brandAdapter.add(new BrandListItem(home, brand));
            }
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_REFRESHED_SHOPPING_BAG)) {
                    refreshShoppingBag();
                }

            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_REFRESHED_SHOPPING_BAG));

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.ivShoppingBag)
    void shoppingBagButtonClicked() {
        ShoppingBagActivity_.intent(context).start();
    }

    void refreshShoppingBag() {
        if (dataManager.getShoppingBag() != null) {
            if (dataManager.getShoppingBag().getItems().size() > 0) {
                tvShoppingBag.setVisibility(View.VISIBLE);
                tvShoppingBag.setText(String.format("%d", dataManager.getShoppingBag().getItems().size()));
            } else {
                tvShoppingBag.setVisibility(View.GONE);
            }
        } else {
            tvShoppingBag.setVisibility(View.GONE);
        }
    }

    @Click(R.id.llSort)
    void sortButtonClicked(){
        BrandListSortActivity_.intent(context).sortType(0).startForResult(REQUEST_SORT);
    }

    private void updateSortBy(){
        tvSortBy.setText(FuzzieUtils.SORT_BY[dataManager.selectedSortByIndex]);
        tvSortBy.requestLayout();

        sort();
    }

    private void sort(){

        if (brands != null){

            switch (dataManager.selectedSortByIndex){
                case 0:
                    sortedBrands = new ArrayList<>(brands);
                    break;
                case 1:
                    Collections.sort(sortedBrands, new Comparator<Brand>() {
                        @Override
                        public int compare(Brand o1, Brand o2) {
                            return Integer.compare(o2.getLikersCount(), o1.getLikersCount());
                        }
                    });
                    break;
                case 2:
                    Collections.sort(sortedBrands, new Comparator<Brand>() {
                        @Override
                        public int compare(Brand o1, Brand o2) {
                            return Double.compare(o2.getCashBack().getCashBackPercentage(), o1.getCashBack().getCashBackPercentage());
                        }
                    });
                    break;
                case 3:
                    Collections.sort(sortedBrands, new Comparator<Brand>() {
                        @Override
                        public int compare(Brand o1, Brand o2) {
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                    });
                    break;
            }

            updateRefine();
        }

    }

    private void updateRefine(){
        if (categoryId == 0){

            if (dataManager.selectedBrandsCategoryIds.size() == 0){
                tvCount.setVisibility(View.GONE);
                filteredBrands = new ArrayList<>(sortedBrands);
            } else {
                tvCount.setText(String.valueOf(dataManager.selectedBrandsCategoryIds.size()));
                tvCount.setVisibility(View.VISIBLE);

                Set<Brand> brandSet = new TreeSet<>();
                for (int categoryId : dataManager.selectedBrandsCategoryIds){
                    for (Brand brand : sortedBrands){
                        if (brand.getCategoryIds().contains(String.valueOf(categoryId))){
                            brandSet.add(brand);
                        }
                    }
                }

                filteredBrands = new ArrayList<>(brandSet);
            }

        } else {
            if (dataManager.selectedBrandsSubCategoryIds.size() == 0){
                tvCount.setVisibility(View.GONE);
                filteredBrands = new ArrayList<>(sortedBrands);
                tvRefine.setText("All");
            } else {
                tvCount.setText(String.valueOf(dataManager.selectedBrandsSubCategoryIds.size()));
                tvCount.setVisibility(View.VISIBLE);
                tvRefine.setText("Subcategories");

                if (filteredBrands == null){
                    filteredBrands = new ArrayList<>();
                } else {
                    filteredBrands.clear();
                }

                for (int categoryId : dataManager.selectedBrandsSubCategoryIds){
                    for (Brand brand : sortedBrands){
                        if (brand.getSubCategoryId() == categoryId){
                            filteredBrands.add(brand);
                        }
                    }
                }

            }
        }

        switch (dataManager.selectedSortByIndex){
            case 0:
                break;
            case 1:
                Collections.sort(filteredBrands, new Comparator<Brand>() {
                    @Override
                    public int compare(Brand o1, Brand o2) {
                        return Integer.compare(o2.getLikersCount(), o1.getLikersCount());
                    }
                });
                break;
            case 2:
                Collections.sort(filteredBrands, new Comparator<Brand>() {
                    @Override
                    public int compare(Brand o1, Brand o2) {
                        return Double.compare(o2.getCashBack().getCashBackPercentage(), o1.getCashBack().getCashBackPercentage());
                    }
                });
                break;
            case 3:
                Collections.sort(filteredBrands, new Comparator<Brand>() {
                    @Override
                    public int compare(Brand o1, Brand o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                break;
        }

        if (brandAdapter == null){
            brandAdapter = new FastItemAdapter<>();
        } else {
            brandAdapter.clear();
        }

        for (Brand brand: filteredBrands) {
            brandAdapter.add(new BrandListItem(home, brand));
        }
    }

    private void goNormalBrandPage(Context context, Brand brand){

        if (brand.getServices() != null && brand.getServices().size() > 0) {
            if (brand.getServices().size() == 1 && (brand.getGiftCards() == null || brand.getGiftCards().size() == 0) ) {
                BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(brand.getServices().get(0))).start();
            } else {
                BrandServiceListActivity_.intent(context).brandId(brand.getId()).start();
            }
        } else if (brand.getGiftCards() != null && brand.getGiftCards().size() > 0) {
            BrandGiftActivity_.intent(context).brandId(brand.getId()).start();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_SORT){
                updateSortBy();
            } else if (requestCode == REQUEST_REFINE){
                updateRefine();
            } else if (requestCode == REQUEST_CATEGORY_REFINE){
                updateRefine();
            }
        }

    }

    @Click(R.id.llRefine)
    void refineButtonClicked(){
        if (brands != null){

            dataManager.selectedBrands = new ArrayList<>(brands);
            if (categoryId == 0){
                BrandListCategoryRefineActivity_.intent(context).startForResult(REQUEST_CATEGORY_REFINE);
            } else {
                BrandListRefineActivity_.intent(context).titleExtra(titleExtra).refineType(0).startForResult(REQUEST_REFINE);
            }
        }

    }

}
