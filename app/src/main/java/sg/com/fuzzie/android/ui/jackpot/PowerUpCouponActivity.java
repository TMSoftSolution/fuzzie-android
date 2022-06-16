package sg.com.fuzzie.android.ui.jackpot;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.PowerUpPreview;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.ValidOptionNewItem;
import sg.com.fuzzie.android.items.shop.ShopBrandItem;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandRedeemDetailsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.ui.shop.BrandTermsActivity_;
import sg.com.fuzzie.android.ui.shop.CashbackInfoActivity_;
import sg.com.fuzzie.android.utils.BlurBuilder;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 2/19/18.
 */

@EActivity(R.layout.activity_power_up_coupon)
public class PowerUpCouponActivity extends BaseActivity {

    private Coupon coupon;
    FastItemAdapter<ValidOptionNewItem> validOptionAdapter;
    FastItemAdapter<ShopBrandItem> previewAdapter;

    @Extra
    String couponId;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvSoldOut)
    TextView tvSoldOut;

    @ViewById(R.id.tvCouponName)
    TextView tvCouponName;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.tvCashback)
    TextView tvCashback;

    @ViewById(R.id.tvPowerUp)
    TextView tvPowerUp;

    @ViewById(R.id.tvTicketCount)
    TextView tvTicketCount;

    @ViewById(R.id.tvEarn)
    TextView tvEarn;

    @ViewById(R.id.rvValidOption)
    RecyclerView rvValidOption;

    @ViewById(R.id.llValidOptions)
    View llValidOptions;

    @ViewById(R.id.llDesc)
    View llDesc;

    @ViewById(R.id.tvDesc)
    TextView tvDesc;

    @ViewById(R.id.tvRedeemValid)
    TextView tvRedeemValid;

    @ViewById(R.id.cvChoose)
    CardView cvChoose;

    @ViewById(R.id.cashbackInfo)
    View cashbackInfo;

    @ViewById(R.id.tvCashbackPercentage)
    TextView tvCashbackPercentage;

    @ViewById(R.id.ivCover)
    ImageView ivCover;

    @ViewById(R.id.rvPreview)
    RecyclerView rvPreview;

    @AfterViews
    public void calledAfterViewInjection() {

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coupon = dataManager.getCouponById(couponId);
        if (coupon == null) return;

        if (coupon.getPowerUpPack() != null && coupon.getPowerUpPack().getImage() != null && !coupon.getPowerUpPack().getImage().equals("")){

            Picasso.get()
                    .load(coupon.getPowerUpPack().getImage())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(ivCover);
        }

        if (coupon.isSoldOut()) {
            tvSoldOut.setRotation(-20);
            tvSoldOut.setVisibility(View.VISIBLE);
            cvChoose.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            cvChoose.setEnabled(false);
        }

        tvCouponName.setText(coupon.getName());
        double price = Double.parseDouble(coupon.getPrice().getValue());
        tvPrice.setText(FuzzieUtils.getFormattedValue(price, 0.7f));

        tvPowerUp.setVisibility(View.GONE);
        if (coupon.getCashBack().getPercentage() > 0) {

            tvCashback.setText(String.format("%.0f", coupon.getCashBack().getPercentage()) + "%");
            tvCashbackPercentage.setText(String.format("%.0f%% Instant Cashback", coupon.getCashBack().getPercentage()));
            tvEarn.setText(FuzzieUtils.getFormattedValue(coupon.getCashBack().getValue(), 0.75f));


        } else {

            tvCashback.setVisibility(View.GONE);
            cashbackInfo.setVisibility(View.GONE);

        }

        tvTicketCount.setText(String.valueOf(coupon.getTicketCount()));

        if (coupon.getOptions() != null && coupon.getOptions().size() > 0){

            llValidOptions.setVisibility(View.VISIBLE);

            validOptionAdapter = new FastItemAdapter<>();
            rvValidOption.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvValidOption.setAdapter(validOptionAdapter);

            validOptionAdapter.clear();

            for (String option : coupon.getOptions()){

                validOptionAdapter.add(new ValidOptionNewItem(option));

            }

        } else {

            llValidOptions.setVisibility(View.GONE);

        }

        if (coupon.getDescription().length() == 0){
            llDesc.setVisibility(View.GONE);

        } else {
            llDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(coupon.getDescription());
        }

        showRedeemValidInfo();

        previewAdapter = new FastItemAdapter<ShopBrandItem>();
        rvPreview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvPreview.setAdapter(previewAdapter);
        previewAdapter.withOnClickListener(new OnClickListener<ShopBrandItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ShopBrandItem> adapter, ShopBrandItem item, int position) {

                Brand brand = item.getBrand();

                if (brand != null){

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

                return true;
            }
        });

        loadPreview();
    }

    private void showRedeemValidInfo(){

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date endDate = dateFormat.parse(coupon.getRedeemEndDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endDate.getTime());

            Calendar now = Calendar.getInstance();
            long days = (calendar.getTimeInMillis() - now.getTimeInMillis()) / (24 * 60 * 60 * 1000);

            if (calendar.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY)){
                tvRedeemValid.setText("Valid for " + days + " days from purchase");
            } else {
                tvRedeemValid.setText("Valid for " + (days + 1) + " days from purchase");

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadPreview(){

        FuzzieAPI.APIService().getPowerUpPreview(currentUser.getAccesstoken()).enqueue(new Callback<PowerUpPreview>() {
            @Override
            public void onResponse(Call<PowerUpPreview> call, Response<PowerUpPreview> response) {

                if (response.code() == 200 && response.body() != null && response.body().getBrandIds() != null){
                    showPreview(response.body().getBrandIds());
                }
            }

            @Override
            public void onFailure(Call<PowerUpPreview> call, Throwable t) {

            }
        });
    }

    private void showPreview(List<String> brandIds){

        if (previewAdapter == null){
            previewAdapter = new FastItemAdapter<ShopBrandItem>();
        } else {
            previewAdapter.clear();
        }

        for (String brandId : brandIds){
            Brand brand = dataManager.getBrandById(brandId);
            if (brand != null){
                previewAdapter.add(new ShopBrandItem(brand, false, currentUser.getAccesstoken()));
            }
        }
    }

    @Click(R.id.tvLearnMore)
    void cashBackInfoButtonClicked(){
        ((FuzzieApplication_) getApplication()).lastActivityBitmap = BlurBuilder.blur(findViewById(android.R.id.content));
        CashbackInfoActivity_.intent(context).start();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Click(R.id.btnHowToRedeem)
    void howToRedeemButtonClicked() {
        BrandRedeemDetailsActivity_.intent(context).isPowerUpCardMode(true).start();
    }

    @Click(R.id.btnTermsAndConditions)
    void termsButtonClicked() {

        if (coupon.getTerms() != null){

            StringBuilder sb = new StringBuilder();
            for (String terms: coupon.getTerms()) {
                sb.append(terms);
                sb.append("\n");
            }

            BrandTermsActivity_.intent(context).termsExtra(sb.toString()).start();
        }

    }

    @Click(R.id.cvChoose)
    void chooseButtonClicked(){

        JackpotPaymentActivity_.intent(context).couponId(couponId).fromPowerUpPack(true).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
