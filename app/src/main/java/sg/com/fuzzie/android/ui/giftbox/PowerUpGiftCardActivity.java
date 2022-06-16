package sg.com.fuzzie.android.ui.giftbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.ValidOptionNewItem;
import sg.com.fuzzie.android.ui.activate.ActivateSuccessActivity_;
import sg.com.fuzzie.android.ui.shop.BrandRedeemDetailsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandTermsActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.TimeUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_USED;

/**
 * Created by mac on 2/19/18.
 */

@EActivity(R.layout.activity_power_up_gift_card)
public class PowerUpGiftCardActivity extends BaseActivity {

    @Extra
    String giftId;

    @Extra
    boolean used;

    Gift gift;
    Coupon coupon;
    FastItemAdapter<ValidOptionNewItem> validOptionAdapter;

    boolean showPowerUpCardMode;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.llValidOptions)
    View llValidOptions;

    @ViewById(R.id.rvValidOption)
    RecyclerView rvValidOption;

    @ViewById(R.id.llDescription)
    LinearLayout llDescription;

    @ViewById(R.id.tvDescription)
    TextView tvDescription;

    @ViewById(R.id.cvRedeem)
    CardView cvRedeem;

    @ViewById(R.id.tvRedeem)
    TextView tvRedeem;

    @ViewById(R.id.tvOnlineValidValue)
    TextView tvOnlineValidValue;

    @ViewById(R.id.tvValidValue)
    TextView tvValidValue;

    @ViewById(R.id.tvOrderNumber)
    TextView tvOrderNumber;


    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @AfterViews
    public void calledAfterViewInjection() {

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (giftId == null) return;

        gift = dataManager.getGiftById(giftId, used);
        if (gift == null) return;

        coupon = gift.getCoupon();
        if (coupon == null) return;

        if (gift.getTimeToExpire() == 1){
            tvBrandName.setText("Power Up Card - 1 Hour");
        } else{
            tvBrandName.setText(String.format("Power Up Card - %d Hours", gift.getTimeToExpire()));

        }

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

        if (coupon.getDescription() != null && coupon.getDescription().length() > 0){

            llDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(coupon.getDescription());

        } else {

            llDescription.setVisibility(View.GONE);
        }

        tvOrderNumber.setText(String.valueOf(gift.getOrderNumber()));
        updateGift();
    }

    private void updateGift(){

        if (gift.getRedeemedTime() != null){

            cvRedeem.setVisibility(View.VISIBLE);
            tvRedeem.setText("REDEEMED");

            cvRedeem.setEnabled(false);
            cvRedeem.setCardBackgroundColor(Color.parseColor("#D9D9D9"));

            tvValidValue.setText("Redeemed on " + TimeUtils.dateTimeDDMMYYYY(gift.getRedeemedTime()));
            tvValidValue.setTextColor(context.getResources().getColor(R.color.primary));
            tvValidValue.setBackgroundColor(context.getResources().getColor(R.color.colorPowerUpCardRedeemed));


        } else {

            tvValidValue.setText("Valid till " + TimeUtils.dateTimeDDMMYYYY(gift.getExpirationDate()));

            if (gift.isExpired()){

                cvRedeem.setVisibility(View.VISIBLE);

                cvRedeem.setEnabled(false);
                cvRedeem.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
                tvRedeem.setText("EXPIRED");

                tvValidValue.setTextColor(context.getResources().getColor(R.color.primary));
                tvValidValue.setBackgroundColor(context.getResources().getColor(R.color.colorPowerUpCardRedeemed));

            } else {

                cvRedeem.setVisibility(View.VISIBLE);

                cvRedeem.setEnabled(true);
                cvRedeem.setCardBackgroundColor(Color.parseColor("#FA3E3F"));

                tvRedeem.setText("ACTIVATE POWER UP CARD");
            }
        }

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

    @Click(R.id.cvRedeem)
    void redeemButtonClicked(){

        showPowerUpCardMode = true;

        if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null && !currentUser.getCurrentUser().getPowerUpExpirationTime().equals("")){

            showFZAlert("HANG ON!", "You have another Power Up in session, which this activation will override. Do you wish to continue?", "ACTIVATE NOW", "NO, CANCEL", R.drawable.ic_power_up_card);

        } else {

            String first = "After activation, all brands will be powered up for ";
            String second = "";
            if (gift.getTimeToExpire() > 1){
                second = String.valueOf(gift.getTimeToExpire()) + " hours.";
            } else {
                second = String.valueOf(gift.getTimeToExpire()) + " hour.";
            }

            SpannableString spannableString  = new SpannableString(first + second);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    first.length(),first.length() + second.length(), 0);

            showFZAlert("ARE YOU SURE?", spannableString, "ACTIVATE NOW", "NO, CANCEL", R.drawable.ic_power_up_card);

        }
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (showPowerUpCardMode) {

            showPowerUpCardMode = false;

            showPopView("ACTIVATING", "", R.drawable.bear_craft, true);
            FuzzieAPI.APIService().redeemPowerUpGiftCard(currentUser.getAccesstoken(), giftId).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    hidePopView();

                    if (response.code() == 200 && response.body() != null) {

                        dataManager.removeActiveGift(gift);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_GIFTBOX_USED));

                        // Decrease Active Gift Count
                        currentUser.changeActiveGiftCount(-1);

                        ActivateSuccessActivity_.intent(context).jsonExtra(response.body().toString()).fromPowerUpGift(true).start();

                    } else if (response.code() == 410 || response.code() == 409 || response.code() == 404) {
                        if (response.errorBody() != null) {
                            try {
                                String jsonString = response.errorBody().string();
                                JsonParser parser = new JsonParser();
                                JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                                if (jsonObject.get("message") != null) {
                                    showFZAlert("Oops!", jsonObject.get("message").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showFZAlert("Oops!", "Sorry! The code you provided was already redeemed previously. Please contact us for assistance.", "OK", "", R.drawable.ic_bear_dead_white);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    hidePopView();
                    showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            });
        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
        showPowerUpCardMode = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
