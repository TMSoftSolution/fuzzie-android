package sg.com.fuzzie.android.ui.payments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.gift.DeliveryMethodsActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotPaymentSuccessActivity_;
import sg.com.fuzzie.android.ui.redpacket.RedPacketPaymentSuccessActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;

/**
 * Created by nurimanizam on 19/12/16.
 */

@EActivity(R.layout.activity_payment_success)
public class PaymentSuccessActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @Extra
    double cashbackExtra;

    @Extra
    int giftsExtra;

    @Extra
    Boolean cashbackMode;

    @Extra
    boolean isSent;

    @Extra
    boolean isTopUp;

    @Extra
    double topUpValue;

    @Extra
    boolean fromJackpot;

    @Extra
    boolean isPowerUpPack;

    @Extra
    boolean fromRedPacket;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.ivCircle)
    ImageView ivCircle;

    @ViewById(R.id.ivPresent)
    ImageView ivPresent;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvDescription)
    TextView tvDescription;

    @ViewById(R.id.tvDone)
    TextView tvDone;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void calledAfterViewInjection() {

        Intent refreshIntent = new Intent(BROADCAST_REFRESH_USER);

        if (isTopUp){

            LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

            Picasso.get()
                    .load(R.drawable.code_success_red)
                    .placeholder(R.drawable.code_success_red)
                    .resize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels)
                    .centerCrop()
                    .into(ivBackground);

            Picasso.get()
                    .load(R.drawable.success_circle)
                    .placeholder(R.drawable.success_circle)
                    .fit()
                    .into(ivCircle);

            ivPresent.setVisibility(View.INVISIBLE);
            tvCredits.setText(FuzzieUtils.getFormattedPaymentSuccessCashbackValue(this, topUpValue + cashbackExtra, 0.667f));
            tvTitle.setText("FUZZIE CREDITS ADDED TO YOUR ACCOUNT!");
            tvDescription.setText("Great job, " + currentUser.getCurrentUser().getFirstName() + "!");
            tvDone.setText("START SHOPPING");

        } else if (fromJackpot || fromRedPacket) {

            if (fromRedPacket){
                LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);
            }

            Picasso.get()
                    .load(R.drawable.code_success_blue)
                    .placeholder(R.drawable.code_success_blue)
                    .resize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels)
                    .centerCrop()
                    .into(ivBackground);

            ivPresent.setVisibility(View.INVISIBLE);

            tvCredits.setText(FuzzieUtils.getFormattedPaymentSuccessCashbackValue(this, cashbackExtra, 0.667f));

            tvTitle.setText("CASHBACK HAS BEEN CREDITED TO YOUR FUZZIE ACCOUNT");
            tvDescription.setText("Thanks for shopping with us!");
            tvDone.setText("CONTINUE");
            tvDone.setTextColor(Color.parseColor("#388DD1"));

        } else {

            if (cashbackExtra > 0 && cashbackMode) {

                Picasso.get()
                        .load(R.drawable.code_success_blue)
                        .placeholder(R.drawable.code_success_blue)
                        .resize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels)
                        .centerCrop()
                        .into(ivBackground);

                Picasso.get()
                        .load(R.drawable.success_circle)
                        .placeholder(R.drawable.success_circle)
                        .fit()
                        .into(ivCircle);

                ivPresent.setVisibility(View.INVISIBLE);

                tvCredits.setText(FuzzieUtils.getFormattedPaymentSuccessCashbackValue(this, cashbackExtra, 0.667f));
                tvTitle.setText("CASHBACK HAS BEEN CREDITED TO YOUR FUZZIE ACCOUNT");
                tvDescription.setText("Thanks for shopping with us!");

                if (isSent){
                    tvDone.setText("SEND YOUR GIFT NOW");
                } else {
                    tvDone.setText("GO TO MY WALLET");
                }

                tvDone.setTextColor(Color.parseColor("#388DD1"));

            } else {

                LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

                Picasso.get()
                        .load(R.drawable.code_success_red)
                        .placeholder(R.drawable.code_success_red)
                        .resize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels)
                        .centerCrop()
                        .into(ivBackground);

                Picasso.get()
                        .load(R.drawable.success_circle)
                        .placeholder(R.drawable.success_circle)
                        .fit()
                        .into(ivCircle);

                tvCredits.setText("");

                ivPresent.setVisibility(View.VISIBLE);

                if (isSent){
                    tvTitle.setText("YOUR GIFT IS READY FOR DELIVERY!");
                } else {
                    if (giftsExtra > 1) {
                        tvTitle.setText("YOUR GIFT CARDS HAVE BEEN ADDED TO YOUR WALLET");
                    } else {
                        tvTitle.setText("YOUR GIFT CARD HAS BEEN ADDED TO YOUR WALLET");
                    }
                }

                tvDescription.setText("Great job, " + currentUser.getCurrentUser().getFirstName() + "!");

                if (cashbackExtra > 0) {
                    tvDone.setText("VIEW CASHBACK EARNED");
                } else {
                    if (isSent){
                        tvDone.setText("SEND YOUR GIFT NOW");
                    } else {
                        tvDone.setText("GO TO MY WALLET");
                    }
                }

            }

        }

        if (!isTopUp){
            Intent intent = new Intent(BROADCAST_CHANGE_TAB);
            intent.putExtra("tabId",R.id.tab_wallet);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        dataManager.selectedBrand = null;
        dataManager.selectedGiftCard = null;
        dataManager.selectedService = null;
    }

    @Click(R.id.tvDone)
    void doneButtonClicked() {

        if (isTopUp){

            setResult(RESULT_OK, null);
            finish();

        } else if (fromJackpot){
            JackpotPaymentSuccessActivity_.intent(context).isPowerUpPack(isPowerUpPack).start();

        } else if (fromRedPacket){

            RedPacketPaymentSuccessActivity_.intent(context).start();

        } else {
            if (cashbackExtra > 0 && !cashbackMode) {
                PaymentSuccessActivity_.intent(context).cashbackExtra(cashbackExtra).cashbackMode(true).isSent(isSent).start();
            } else {
                if (isSent){

                    DeliveryMethodsActivity_.intent(context).start();

                } else {
                    Intent intent = new Intent(context, MainActivity_.class);
                    intent.putExtra("go_gift_box", true);
                    intent.putExtra("selected_tab", 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }

            }
        }


    }

    @Override
    public void onBackPressed() {

    }
}
