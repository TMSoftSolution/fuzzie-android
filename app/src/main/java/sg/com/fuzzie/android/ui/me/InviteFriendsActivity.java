package sg.com.fuzzie.android.ui.me;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ShareCompat;
import android.text.*;
import android.text.style.*;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;


/**
 * Created by hiennh on 20/04/17.
 */

@EActivity(R.layout.activity_invite_friends)
public class InviteFriendsActivity extends BaseActivity {

    boolean isRefreshing;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvReferralTitle)
    TextView tvReferralTitle;

    @ViewById(R.id.tvReferralMoreInfo)
    TextView tvReferralMoreInfo;

    @ViewById(R.id.tvReferralCode)
    TextView tvReferralCode;

    @ViewById(R.id.tvTotalReferalsValue)
    TextView tvTotalReferalsValue;

    @ViewById(R.id.tvSuccessfulReferalsValue)
    TextView tvSuccessfulReferalsValue;

    @ViewById(R.id.tvCreditsRewardedValue)
    TextView tvCreditsRewardedValue;

    @ViewById(R.id.rlMain)
    RelativeLayout mainLayout;

    @ViewById(R.id.tvViewReferrals)
    TextView tvViewReferrals;

    @ViewById(R.id.pbSuccess)
    ProgressBar pbSuccess;

    @ViewById(R.id.pbTotal)
    ProgressBar pbTotal;

    @ViewById(R.id.pbAwarded)
    ProgressBar pbAwarded;

    @AfterViews
    public void calledAfterViewInjection() {
        setupViews();
        getReferralInfo();
    }

    private void setupViews() {

        String referralText = "<font color='#424242'>THEY GET </font><font color='#FA3E3F'>$5 </font><font color='#424242'>YOU GET </font><font color='#FA3E3F'>$5</font>";

        tvReferralTitle.setText(Html.fromHtml(referralText));

        String referralBody = getResources().getString(R.string.referral_promo_more_info);
        SpannableString spannablecontent=new SpannableString(referralBody);
        spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),62,71, 0);
        tvReferralMoreInfo.setText(spannablecontent);

        tvTotalReferalsValue.setText("0");
        tvCreditsRewardedValue.setText("S$0");
        tvReferralCode.setText(currentUser.getCurrentUser().getReferralCode());
    }

    private void getReferralInfo(){
        if (isRefreshing){
            displayProgressDialog("Loading...");
        } else {
            showActivator();
        }
        Call<JsonObject> callReferral = FuzzieAPI.APIService().getReferrals(currentUser.getAccesstoken());
        callReferral.enqueue(new Callback<JsonObject>(){
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (isRefreshing){
                    dismissProgressDialog();
                } else {
                    hideActivator();
                }

                if (response.code() == 200  && response.body() != null) {

                    if (response.body().get("credits") != null) {
                        int credits = response.body().get("credits").getAsInt();
                        tvCreditsRewardedValue.setText("S$" + String.valueOf(credits));
                    } else {
                        tvCreditsRewardedValue.setText("0S$");
                    }

                    int referred_users_count = 0;
                    if (response.body().get("referred_users_count") != null){
                        referred_users_count = response.body().get("referred_users_count").getAsInt();
                    }
                    tvTotalReferalsValue.setText(String.valueOf(referred_users_count));

                    int successful_referred_users_count = 0;
                    if (response.body().get("successful_referred_users_count") != null){
                        successful_referred_users_count = response.body().get("successful_referred_users_count").getAsInt();
                    }
                    tvSuccessfulReferalsValue.setText(String.valueOf(successful_referred_users_count));

                }  else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  InviteFriendsActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isRefreshing){
                    dismissProgressDialog();
                } else {
                    hideActivator();
                }
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.ivShare)
    void shareIconClicked(){
        callRatePush();
        openShareMenu();
    }

    @Click(R.id.cvSendInvite)
    void sendWhatsAppClicked() {
        PackageManager pm=getPackageManager();
        try {
            callRatePush();
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "I’m using Fuzzie to get INSTANT cashback on Grab, Deliveroo, Qoo10 and many more brands. They have a weekly S$150,000 Jackpot too! Sign up with this link to enjoy FREE $5 credits to buy anything on Fuzzie: " + currentUser.getCurrentUser().getReferralUrl();
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private void openShareMenu() {
        ShareCompat.IntentBuilder.from(this) .setType("text/plain") .setText("I’m using Fuzzie to get INSTANT cashback on Grab, Deliveroo, Qoo10 and many more brands. They have a weekly S$150,000 Jackpot too! Sign up with this link to enjoy FREE $5 credits to buy anything on Fuzzie: " + currentUser.getCurrentUser().getReferralUrl()).setSubject("Get free $5 credits on Fuzzie").startChooser();
    }

    @Click(R.id.tvReferralCode)
    void copyReferralCode(){
        ClipboardManager cm = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("text", tvReferralCode.getText());
        assert cm != null;
        cm.setPrimaryClip(cData);
        showClipboardPopup();
    }

    @Click({R.id.tvViewReferrals, R.id.llSuccessReferral, R.id.llTotalReferral})
    void referralsButtonClicked(){
        ReferralsActivity_.intent(context).start();
    }

    @Click(R.id.tvRefresh)
    void refreshButtonClicked(){
        isRefreshing = true;
        getReferralInfo();
    }

    private void showActivator(){

        ViewUtils.visible(pbSuccess);
        ViewUtils.visible(pbTotal);
        ViewUtils.visible(pbAwarded);
        ViewUtils.gone(tvSuccessfulReferalsValue);
        ViewUtils.gone(tvTotalReferalsValue);
        ViewUtils.gone(tvCreditsRewardedValue);
    }

    private void hideActivator(){

        ViewUtils.gone(pbSuccess);
        ViewUtils.gone(pbTotal);
        ViewUtils.gone(pbAwarded);
        ViewUtils.visible(tvSuccessfulReferalsValue);
        ViewUtils.visible(tvTotalReferalsValue);
        ViewUtils.visible(tvCreditsRewardedValue);
    }

    private void callRatePush(){
        Call<Void> call = FuzzieAPI.APIService().callRateApp(currentUser.getAccesstoken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
