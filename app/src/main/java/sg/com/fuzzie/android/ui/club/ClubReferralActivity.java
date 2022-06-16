package sg.com.fuzzie.android.ui.club;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v4.app.ShareCompat;
import android.widget.TextView;

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
import sg.com.fuzzie.android.ui.me.ReferralsActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_club_referral)
public class ClubReferralActivity extends BaseActivity {


    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvReferralCode)
    TextView tvReferralCode;

    @ViewById(R.id.tvTotalReferalsValue)
    TextView tvTotalReferalsValue;

    @ViewById(R.id.tvCreditsRewardedValue)
    TextView tvCreditsRewardedValue;

    @AfterViews
    void callAfterViewInjection(){

        tvReferralCode.setText(currentUser.getCurrentUser().getFuzzieClub().getReferralCode());

        getReferralInfo();

    }

    private void getReferralInfo(){

        Call<JsonObject> callReferral = FuzzieAPI.APIService().getClubReferrals(currentUser.getAccesstoken());
        callReferral.enqueue(new Callback<JsonObject>(){
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

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

                }  else if (response.code() == 417){

                    logoutUser(currentUser, dataManager,  ClubReferralActivity.this);

                } else {

                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    private void openShareMenu() {
        ShareCompat.IntentBuilder.from(this) .setType("text/plain") .setText(String.format("Check out the Fuzzie Club for cool 1-for-1 deals and more from popular restaurants, attractions, spas and online shopping sites. I’m using it to save a lot of money. Use my code to get an extra $10 credits when you subscribe. %s www.fuzzie.com.sg/club", currentUser.getCurrentUser().getFuzzieClub().getReferralCode())).setSubject("Get free $10 credits on Fuzzie").startChooser();
    }

    @Click(R.id.cvInvite)
    void inviteButtonClicked(){

        openShareMenu();
    }

    @Click(R.id.cvViewReferral)
    void viewReferralButtonClicked(){

        ReferralsActivity_.intent(context).isClubReferral(true).start();
    }

    @Click(R.id.vCopy)
    void clipboardCopy(){

        ClipboardManager cm = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("text", currentUser.getCurrentUser().getFuzzieClub().getReferralCode());
        assert cm != null;
        cm.setPrimaryClip(cData);

        showClipboardPopup();
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
