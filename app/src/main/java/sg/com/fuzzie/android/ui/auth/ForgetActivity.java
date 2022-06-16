package sg.com.fuzzie.android.ui.auth;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import timber.log.Timber;
import android.widget.EditText;

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
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by nurimanizam on 13/12/16.
 */

@EActivity(R.layout.activity_forgot_password)
public class ForgetActivity extends BaseActivity {

    boolean success;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.etEmail)
    EditText emailView;

    @AfterViews
    public void calledAfterViewInjection() {

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.cvForget)
    void forgetButtonClicked() {
        Timber.d("Forget Button Clicked");

        final String email = emailView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            showFZAlert("Oops!", "Your email is missing.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        if (!FuzzieUtils.isValidEmail(email)){
            showFZAlert("Oops!", "Invalid email address.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        hideKeyboard();

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        Call<JsonObject> call =  FuzzieAPI.APIService().resetPassword(email);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hidePopView();

                if (response.code() == 200) {
                    success = true;
                    String body = "We've sent an email to " + email + ". Go to your email to reset your password.";
                    SpannableString spannablecontent=new SpannableString(body);
                    spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),23,23+email.length(), 0);
                    showFZAlert("RESET EMAIL SENT!", spannablecontent, "GOT IT", "", R.drawable.ic_success_white);

                } else if (response.code() == 404){
                    success = false;
                    showFZAlert("Oops!", "There's no Fuzzie account linked to this email address.", "OK", "", R.drawable.ic_bear_dead_white);
                } else {
                    success = false;
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hidePopView();
                success = false;
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });

    }

    @Override
    public void okButtonClicked() {
        hidePopView();
    }

    // FZAlertDialog Listener

    @Override
    public void FZAlertOkButtonClicked() {
        hideFZAlert();
        if (success){
            finish();
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
    }
}
