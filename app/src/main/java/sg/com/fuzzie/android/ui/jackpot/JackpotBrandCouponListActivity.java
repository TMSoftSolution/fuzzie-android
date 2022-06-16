package sg.com.fuzzie.android.ui.jackpot;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.api.models.BrandLink;
import timber.log.Timber;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.CouponTemplate;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.CouponItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by joma on 10/18/17.
 */

@EActivity(R.layout.activity_jackpot_brand_coupon_list)
public class JackpotBrandCouponListActivity extends BaseActivity {

    Brand brand;
    List<Coupon> coupons;

    FastItemAdapter<CouponItem> adapter;

    @Extra
    String brandId;

    @Extra
    String brandLinkJson;

    @Extra
    String couponIdsExtra;

    @Extra
    String titleExtra;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvCoupon)
    RecyclerView rvCoupon;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void calledAfterViewInjection() {

        adapter = new FastItemAdapter<>();
        rvCoupon.setLayoutManager(new LinearLayoutManager(context));
        rvCoupon.setAdapter(adapter);

        if (titleExtra != null && !titleExtra.equals("")){

            tvTitle.setText(titleExtra);
        }

        if (brandId != null && !brandId.equals("")){

            brand = dataManager.getBrandById(brandId);
            if (brand == null) return;

            tvTitle.setText(brand.getName() + " JACKPOT");

            loadCouponTemplate(brandId);

        } else if (brandLinkJson != null && !brandLinkJson.equals("")){

            BrandLink brandLink = BrandLink.fromJSON(brandLinkJson);
            if (brandLink != null){

                if (brandLink.getTitle() != null){
                    tvTitle.setText(brandLink.getTitle());
                }

                if (brandLink.getCouponIds() != null && brandLink.getCouponIds().size() > 0){

                    coupons = new ArrayList<Coupon>();
                    for (String couponId : brandLink.getCouponIds()){
                        Coupon coupon = dataManager.getCouponById(couponId);
                        if (coupon != null){
                            coupons.add(coupon);
                        }
                    }

                    showCoupons();
                }
            }

        } else if (couponIdsExtra != null && !couponIdsExtra.equals("")){

            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            List<String> couponIds = gson.fromJson(couponIdsExtra, type);

            coupons = new ArrayList<Coupon>();
            if (couponIds != null && couponIds.size() > 0){

                for (String couponId : couponIds){
                    Coupon coupon = dataManager.getCouponById(couponId);
                    if (coupon != null){
                        coupons.add(coupon);
                    }
                }

                showCoupons();
            }
        }

    }

    private void loadCouponTemplate(String brandId){

        showLoader();

        FuzzieAPI.APIService().getCouponTemplatesWithBrandId(currentUser.getAccesstoken(), brandId).enqueue(new Callback<CouponTemplate>() {
            @Override
            public void onResponse(Call<CouponTemplate> call, Response<CouponTemplate> response) {

                hideLoader();

                if (response.code() == 200 && response.body() != null){
                    fetchCoupons(response.body());

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager, JackpotBrandCouponListActivity.this);
                }
            }

            @Override
            public void onFailure(Call<CouponTemplate> call, Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void fetchCoupons(CouponTemplate couponTemplate){


        List<Coupon> templates = new ArrayList<Coupon>();
        for (Coupon coupon : couponTemplate.getCoupons()){
            if (brand != null){
                coupon.setBrandName(brand.getName());
                coupon.setSubCategoryId(brand.getSubCategoryId());
            }

            templates.add(coupon);
        }

        coupons = templates;

        showCoupons();
    }

    private void showCoupons(){

        if (adapter == null){
            adapter = new FastItemAdapter<>();
        } else {
            adapter.clear();
        }

        for (Coupon coupon : coupons){
            adapter.add(new CouponItem(coupon));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvViewAll)
    void viewAllButtonClicked(){
        JackpotHomeActivity_.intent(context).start();
    }
}
