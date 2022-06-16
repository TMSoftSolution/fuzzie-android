package sg.com.fuzzie.android.ui.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import timber.log.Timber;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.BuildConfig;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.me.profile.EditPasswordActivity_;
import sg.com.fuzzie.android.ui.me.profile.EditProfileActivity_;
import sg.com.fuzzie.android.ui.me.profile.EditProfilePicActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by nurimanizam on 15/12/16.
 */

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.swShare)
    SwitchCompat swShare;

    @ViewById(R.id.tvVersion)
    TextView tvVersion;

    @AfterViews
    public void calledAfterViewInjection() {
        tvVersion.setText("FUZZIE - VERSION " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        swShare.setChecked(currentUser.getCurrentUser().getSettings().isShareLikeWishlist());
    }

    @Click(R.id.tvProfile)
    void editProfileClicked(){
        EditProfileActivity_.intent(context).start();
    }

    @Click(R.id.tvProfilePic)
    void editProfilePicClicked(){
        EditProfilePicActivity_.intent(context).start();
    }

    @Click(R.id.tvPassword)
    void editPasswordClicked(){
        EditPasswordActivity_.intent(context).start();
    }

    @CheckedChange(R.id.swShare)
    void changedShareWishlist(){

        if (currentUser.getCurrentUser().getSettings().isShareLikeWishlist() == swShare.isChecked()) return;

        Call<Void> call = FuzzieAPI.APIService().updatePrivacyShareLikeWishlistState(currentUser.getAccesstoken(), swShare.isChecked());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_REFRESH_USER));
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  SettingsActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.tvPrivacy)
    void privacyButtonClicked() {
        WebActivity_.intent(this).titleExtra("PRIVACY POLICY").urlExtra("http://fuzzie.com.sg/privacy").start();
    }

    @Click(R.id.tvTerms)
    void termsButtonClicked() {
        WebActivity_.intent(this).titleExtra("TERMS OF SERVICES").urlExtra("http://fuzzie.com.sg/terms").start();
    }

    @Click(R.id.tvLogout)
    void logoutButtonClicked() {
        Timber.d("Logout Button Clicked");

        if(!((Activity) context).isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Log out");
            alertDialog.setMessage("Are you sure?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Log Out",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            logoutUser(currentUser, dataManager,  SettingsActivity.this);
                        }
                    });
            alertDialog.show();
        }

    }

}
