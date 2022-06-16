package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.BrandLink;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.TopBrand;
import sg.com.fuzzie.android.ui.jackpot.JackpotBrandCouponListActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotCouponActivity_;
import sg.com.fuzzie.android.ui.jackpot.PowerUpCouponActivity_;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.ui.shop.BrandsListActivity_;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.HomeBrandsType;

/**
 * Created by nurimanizam on 16/12/16.
 */

public class ShopBrandsItem extends AbstractItem<ShopBrandsItem, ShopBrandsItem.ViewHolder> {

    private List<Brand> brands;
    private String accessToken;
    private int limit ;
    private List<Brand> limitBrands;
    private HomeBrandsType type;
    private boolean fromClub;

    public ShopBrandsItem(Home home, HomeBrandsType type, String accessToken) {
        this.type = type;
        this.accessToken = accessToken;

        limit = 10;

        brands = new ArrayList<>();

        if (type == HomeBrandsType.RECOMMENDED) {
            for (String brandId : home.getRecommendedBrands()) {
                for (Brand brand : home.getBrands()) {
                    if (brandId.equals(brand.getId())) {
                        Brand mBrand = new Brand(brand);
                        brands.add(mBrand);
                        break;
                    }
                }
            }
        } else if (type == HomeBrandsType.CLUB) {
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
        } else if (type == HomeBrandsType.TRENDING) {
            for (String brandId : home.getPopularBrands()) {
                for (Brand brand : home.getBrands()) {
                    if (brandId.equals(brand.getId())) {
                        Brand mBrand = new Brand(brand);
                        brands.add(mBrand);
                        break;
                    }
                }
            }
        } else if (type == HomeBrandsType.NEW) {
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
        } else if (type == HomeBrandsType.TOP) {
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

        if (type == HomeBrandsType.TOP){
            limit = brands.size();
        } else {
            if (brands.size() <= 10){
                limit = brands.size();
            } else {
                limit = 10;
            }
        }

        limitBrands = brands.subList(0, limit);

    }

    public ShopBrandsItem(Home home, HomeBrandsType type, Brand aBrand, String accessToken) {
        this.type = type;
        this.accessToken = accessToken;

        limit = 10;

        if (home == null || home.getBrands() == null) return;

        brands = new ArrayList<>();

        if (type == HomeBrandsType.RECOMMENDED) {
            for (String brandId : home.getRecommendedBrands()) {
                for (Brand brand : home.getBrands()) {
                    if (brandId.equals(brand.getId())) {
                        Brand mBrand = new Brand(brand);
                        brands.add(mBrand);
                        break;
                    }
                }
            }
        } else if (type == HomeBrandsType.TRENDING) {
            for (String brandId : home.getPopularBrands()) {
                for (Brand brand : home.getBrands()) {
                    if (brandId.equals(brand.getId())) {
                        Brand mBrand = new Brand(brand);
                        brands.add(mBrand);
                        break;
                    }
                }
            }
        } else if (type == HomeBrandsType.NEW) {
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
        else if (type == HomeBrandsType.TOP) {
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
        } else if (type == HomeBrandsType.OTHER_ALSO_BOUGHT) {

            for (String brandId : aBrand.getOthersAlsoBought()) {
                for (Brand brand : home.getBrands()) {
                    if (brandId.equals(brand.getId())) {

                        Brand mBrand = new Brand(brand);
                        brands.add(mBrand);
                        break;
                    }
                }
            }
        }

        if (type == HomeBrandsType.TOP){
            limit = brands.size();
        } else {
            if (brands.size() <= 10){
                limit = brands.size();
            } else {
                limit = 10;
            }
        }

        limitBrands = brands.subList(0, limit);

    }

    public void setFromClub(boolean fromClub) {
        this.fromClub = fromClub;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_shop_brands_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_shop_brands;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ShopBrandsItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

        viewHolder.tvViewAll.setVisibility(View.VISIBLE);

        if (type == HomeBrandsType.RECOMMENDED) {
            viewHolder.tvTitle.setText("RECOMMENDED BRANDS");
        } else if (type == HomeBrandsType.CLUB) {
            viewHolder.tvTitle.setText(fromClub ? "CASHBACK FOR MEMBERS-ONLY" :"CLUB BRANDS");
        } else if (type == HomeBrandsType.TRENDING) {
            viewHolder.tvTitle.setText("TRENDING BRANDS");
        } else if (type == HomeBrandsType.NEW) {
            viewHolder.tvTitle.setText("NEW BRANDS");
        } else if (type == HomeBrandsType.TOP) {
            viewHolder.tvTitle.setText("TOP BRANDS");
        } else if (type == HomeBrandsType.OTHER_ALSO_BOUGHT) {
            viewHolder.tvTitle.setText("OTHERS ALSO BOUGHT");
            viewHolder.tvViewAll.setVisibility(View.GONE);
        }

        FastItemAdapter<ShopBrandItem> brandAdapter = new FastItemAdapter<>();
        brandAdapter.withOnClickListener(new OnClickListener<ShopBrandItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ShopBrandItem> adapter, ShopBrandItem item, int position) {

                Brand brand = item.getBrand();

                if (type == HomeBrandsType.NEW || type == HomeBrandsType.CLUB){

                    goBrandLinkPage(context, brand, HomeBrandsType.NEW);

                } else {

                    goNormalBrandPage(context, brand);
                }

                return true;
            }
        });

        FastItemAdapter<ShopTopBrandItem> topBrandAdapter = new FastItemAdapter<>();
        topBrandAdapter.withOnClickListener(new OnClickListener<ShopTopBrandItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ShopTopBrandItem> adapter, ShopTopBrandItem item, int position) {

                Brand brand = item.getBrand();

                goBrandLinkPage(context, brand, HomeBrandsType.TOP);

                return true;
            }
        });

        if (type == HomeBrandsType.TOP){
            viewHolder.rvBrands.setAdapter(topBrandAdapter);
        } else {
            viewHolder.rvBrands.setAdapter(brandAdapter);
        }
        viewHolder.rvBrands.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        if (type == HomeBrandsType.TOP){
            topBrandAdapter.clear();
            if (limitBrands != null){
                for (Brand brand : limitBrands){
                    topBrandAdapter.add(new ShopTopBrandItem(brand));

                }
            }
        } else {
            brandAdapter.clear();
            if (limitBrands != null){
                for (Brand brand : limitBrands){
                    brandAdapter.add(new ShopBrandItem(brand, false, accessToken));
                }
            }
        }

        viewHolder.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (type == HomeBrandsType.RECOMMENDED) {
                    BrandsListActivity_.intent(context).titleExtra("RECOMMENDED BRANDS").showFilter(true).start();
                } else if (type == HomeBrandsType.CLUB) {
                    BrandsListActivity_.intent(context).titleExtra("CLUB BRANDS").start();
                } else if (type == HomeBrandsType.TRENDING) {
                    BrandsListActivity_.intent(context).titleExtra("TRENDING BRANDS").showFilter(true).start();
                } else if (type == HomeBrandsType.NEW) {
                    BrandsListActivity_.intent(context).titleExtra("NEW BRANDS").start();
                } else if (type == HomeBrandsType.TOP) {
                    BrandsListActivity_.intent(context).titleExtra("TOP BRANDS").start();
                }
            }
        });

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

    private void goBrandLinkPage(Context context, Brand brand, HomeBrandsType type){

        FuzzieData dataManager = FuzzieData_.getInstance_(context);

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

        } else {

            goNormalBrandPage(context, brand);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvViewAll;
        protected RecyclerView rvBrands;

        public ViewHolder(View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);
            this.rvBrands = (RecyclerView) view.findViewById(R.id.rvBrands);
        }
    }

}
