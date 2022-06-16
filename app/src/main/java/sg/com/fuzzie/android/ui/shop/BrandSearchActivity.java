package sg.com.fuzzie.android.ui.shop;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.ui.jackpot.JackpotBrandCouponListActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotCouponActivity_;
import timber.log.Timber;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.BrandSearchItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by nurimanizam on 16/12/16.
 */

@EActivity(R.layout.activity_brand_search)
public class BrandSearchActivity extends BaseActivity {

    List<Brand> brands;
    FastItemAdapter<BrandSearchItem> brandAdapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.etSearch)
    EditText etSearch;

    @ViewById(R.id.rvSearch)
    RecyclerView rvSearch;

    @ViewById(R.id.llEmpty)
    LinearLayout llEmpty;

    @ViewById(R.id.iv_bear)
    ImageView ivBear;

    @AfterViews
    public void calledAfterViewInjection() {

        if (dataManager.getHome() == null) return;

        brands = dataManager.getHome().getBrands();
        Collections.sort(brands);

        brandAdapter = new FastItemAdapter<>();
        brandAdapter.withOnClickListener(new OnClickListener<BrandSearchItem>() {
            @Override
            public boolean onClick(View v, IAdapter<BrandSearchItem> adapter, BrandSearchItem item, int position) {

                Brand brand = item.getBrand();

                if (brand.getServices() != null && brand.getServices().size() > 0) {

                    if (brand.getServices().size() == 1 && (brand.getGiftCards() == null || brand.getGiftCards().size() == 0) ) {

                        BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(brand.getServices().get(0))).start();

                    } else {

                        BrandServiceListActivity_.intent(context).brandId(brand.getId()).start();

                    }

                } else if (brand.getGiftCards() != null && brand.getGiftCards().size() > 0) {

                    BrandGiftActivity_.intent(context).brandId(brand.getId()).start();

                } else if (brand.isCouponOnly()){

                    if (brand.getCoupons() != null && brand.getCoupons().size() > 0){

                        List<String> couponIds = new ArrayList<>();

                        for (Coupon coupon : brand.getCoupons()){

                            couponIds.add(coupon.getId());
                        }

                        if (couponIds.size() == 1){

                            JackpotCouponActivity_.intent(context).couponId(couponIds.get(0)).start();

                        } else {

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<String>>(){}.getType();

                            JackpotBrandCouponListActivity_.intent(context).couponIdsExtra(gson.toJson(couponIds, type)).start();
                        }
                    }

                }

                return true;
            }
        });

        rvSearch.setLayoutManager(new LinearLayoutManager(context));
        rvSearch.setAdapter(brandAdapter);

        brandAdapter.clear();
        for (Brand brand: brands) {
            brandAdapter.add(new BrandSearchItem(brand,dataManager.getHome()));
        }

        Picasso.get()
                .load(R.drawable.bear_dead)
                .placeholder(R.drawable.bear_dead)
                .into(ivBear);

    }

    @AfterTextChange(R.id.etSearch)
    void searchEditTextChanged() {
        Timber.d("Search Term: " + etSearch.getText());

        if (brandAdapter == null){
            brandAdapter = new FastItemAdapter<BrandSearchItem>();
        } else {
            brandAdapter.clear();
        }

        if (brands != null){
            for (Brand brand: brands) {
                if (brand.getName().toLowerCase().contains(etSearch.getText().toString().toLowerCase()) && !brand.getName().equals("Fuzzie")) {
                    brandAdapter.add(new BrandSearchItem(brand,dataManager.getHome()));
                }
                if (brandAdapter.getAdapterItemCount() == 0){
                    llEmpty.setVisibility(View.VISIBLE);
                    rvSearch.setVisibility(View.GONE);
                } else {
                    llEmpty.setVisibility(View.GONE);
                    rvSearch.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Click(R.id.tvCancel)
    void backButtonClicked() {
        finish();
    }

}
