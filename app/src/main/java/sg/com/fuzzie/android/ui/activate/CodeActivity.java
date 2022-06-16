package sg.com.fuzzie.android.ui.activate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import sg.com.fuzzie.android.ui.club.ClubSubscribeActivity_;
import sg.com.fuzzie.android.ui.club.ClubSubscribeSuccessActivity_;
import timber.log.Timber;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 7/10/17.
 */

@EActivity(R.layout.activity_code)
public class CodeActivity extends BaseActivity {

    private boolean isClubReferralCode;
    private boolean isClubMembershipCode;
    private boolean force;
    private String code;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvHelp)
    TextView tvHelp;

    @ViewById(R.id.etCode)
    EditText etCode;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @AfterViews
    void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_code_activity));

        Picasso.get()
                .load(R.drawable.bg_activate_code)
                .placeholder(R.drawable.bg_activate_code)
                .into(ivBackground);

    }

    @Click({R.id.cvCode, R.id.btCode})
    void activateButtonClicked() {
        Timber.d("Activate Button Clicked");

        if (etCode.getText() != null && etCode.getText().length() != 0) {

            code = etCode.getText().toString();
            activateCode(code);

        } else {

            showFZAlert("Oops!", "You need to enter a code.", "OK", "", R.drawable.ic_bear_dead_white);
        }

    }

    private void activateCode(String code){

        displayProgressDialog("Activating...");

        Call<JsonObject> call;

        if (force){

            call = FuzzieAPI.APIService().activateCode(currentUser.getAccesstoken(), code, true);
            force = false;

        } else {

            call = FuzzieAPI.APIService().activateCode(currentUser.getAccesstoken(), code);

        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                dismissProgressDialog();
                etCode.setText("");

                if (response.code() == 200 && response.body() != null) {

                    if (response.body().get("type").getAsString().equals("ActivationCode")){

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_REFRESH_GIFTBOX_ACTIVE));

                    }

                    if (response.body().get("type").getAsString().equals("JackpotTicketCode")){

                        ActivateSuccessJackpotActivity_.intent(context).jsonExtra(response.body().toString()).start();

                    } else if (response.body().get("type").getAsString().equals("ClubMembershipActivationCode")){

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_REFRESH_USER));
                        ClubSubscribeSuccessActivity_.intent(context).start();

                    } else {

                        ActivateSuccessActivity_.intent(context).jsonExtra(response.body().toString()).start();
                    }


                }  else if (response.code() == 410 || response.code() == 409 || response.code() == 404) {

                    if (response.errorBody() != null) {

                        try {

                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

                            if (jsonObject.get("message") != null){
                                showFZAlert("Oops!", jsonObject.get("message").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                            }

                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                    } else {

                        showFZAlert("Oops!", "Sorry! The code you provided was already redeemed previously. Please contact us for assistance.", "OK", "", R.drawable.ic_bear_dead_white);
                    }

                } else if (response.code() == 415 && response.errorBody() != null){

                    String errorMessage = "This is a Fuzzie Club Referral Code. Use it when you subscribe to Fuzzie Club.";

                    try {

                        String jsonString = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

                        if (jsonObject.get("message") != null){

                            errorMessage = jsonObject.get("message").getAsString();
                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    showFZAlert("Oops!", errorMessage, "CLUB SUBSCRIBE", "No, cancel", R.drawable.ic_bear_dead_white);

                    isClubReferralCode = true;

                } else if (response.code() == 418 && response.errorBody() != null){     // This error code is for ClubMembershipActivate Code

                    String errorMessage = "You still have an active Club subscription. Would you like to restart your subscription with this code?";

                    try {

                        String jsonString = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

                        if (jsonObject.get("message") != null){

                            errorMessage = jsonObject.get("message").getAsString();
                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    showFZAlert("Oops!", errorMessage, "YES", "No, cancel", R.drawable.ic_bear_dead_white);

                    isClubMembershipCode = true;

                } else {

                    showFZAlert("Oops!", "Unknown error occurred. Please try again or contact us for assistance. Code:" + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                dismissProgressDialog();
                etCode.setText("");

                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (isClubReferralCode){

            isClubReferralCode = false;

            ClubSubscribeActivity_.intent(context).start();

        } else if (isClubMembershipCode){

            isClubMembershipCode = false;
            force = true;

            activateCode(code);
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        isClubReferralCode = false;
        isClubMembershipCode = false;
        force = false;
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.activate_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        WebActivity_.intent(context).titleExtra("FAQ").urlExtra("http://fuzzie.com.sg/faq.html").start();
                        break;
                    case 1:
                        emailSupport();
                        break;
                    case 2:
                        facebookSupport();
                        break;

                    default:
                        break;
                }
            }
        }).show();

    }

    void emailSupport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","support@fuzzie.com.sg", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help with code.");
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

}
