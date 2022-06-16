package sg.com.fuzzie.android.ui.me.profile;

import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import timber.log.Timber;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterTextChange;
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
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 5/24/17.
 */

@EActivity(R.layout.activity_edit_password)
public class EditPasswordActivity extends BaseActivity{

    boolean sendResetLink;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;


    @ViewById(R.id.etCurrentPass)
    EditText etCurrentPassword;

    @ViewById(R.id.etNewPass)
    EditText etNewPassword;

    @ViewById(R.id.etConfirmPass)
    EditText etConfirmPass;

    @ViewById(R.id.tvForget)
    TextView tvForget;

    @ViewById(R.id.cvSave)
    CardView cvSave;

    @AfterViews
    public void calledAfterViewInjection() {

        tvForget.setPaintFlags(tvForget.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        updateSaveButton();
    }

    private void updateSaveButton(){
        if (etCurrentPassword.getText().length() != 0 && etNewPassword.getText().length() != 0 && etConfirmPass.getText().length() != 0){
            cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary));
            cvSave.setEnabled(true);
        } else {
            cvSave.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            cvSave.setEnabled(false);
        }
    }

    @AfterTextChange({R.id.etCurrentPass, R.id.etNewPass, R.id.etConfirmPass})
    void editTextChanged(){
        updateSaveButton();
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.tvForget)
    void forgetButtonClicked() {
        Timber.d("Forget Button Clicked");
        sendResetLink = true;
        showFZAlert("RESET PASSWORD?", "Would you like to reset your password? We will send a reset link to your account email.", "SEND RESET LINK", "CANCEL", R.drawable.ic_bear_baby_white);

    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){
        hideKeyboard();

        if (TextUtils.isEmpty(etCurrentPassword.getText())){
            showFZAlert("Oops!", "Missing Current Password", "GOT IT", "", R.drawable.ic_bear_dead_white);
        } else if ((TextUtils.isEmpty(etNewPassword.getText()) || (etNewPassword.getText().length() < 6))){
            showFZAlert("Oops!", "Password must be at least 6 characters.", "GOT IT", "", R.drawable.ic_bear_dead_white);
        } else if(!etNewPassword.getText().toString().equals(etConfirmPass.getText().toString())){
            showFZAlert("Oops!", "Password does not match.", "GOT IT", "", R.drawable.ic_bear_dead_white);
        } else {

            displayProgressDialog("The Bear is updating your password...");

            Call<Void> call = FuzzieAPI.APIService().updateUserPassword(currentUser.getAccesstoken(),etCurrentPassword.getText().toString(), etNewPassword.getText().toString());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    dismissProgressDialog();
                    if (response.code() == 200){
                        showPopView("SUCCESS!", "", R.drawable.bear_good);
                    } else {
                        if (response.code() == 401 || response.code() == 406){
                            if (response.errorBody() != null){
                                try {
                                    String jsonString = response.errorBody().string();
                                    JsonParser parser = new JsonParser();
                                    JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                                    showFZAlert("Oops!", jsonObject.get("message").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        } else if (response.code() == 417){
                            logoutUser(currentUser, dataManager,  EditPasswordActivity.this);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    dismissProgressDialog();
                    showFZAlert("Error!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            });
        }
    }

    // FZAlert Dialog Listener

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
        sendResetLink = false;
    }

    @Override
    public void FZAlertOkButtonClicked() {
        hideFZAlert();
        if (sendResetLink){
            sendResetLink = false;

            displayProgressDialog("Processing...");

            Call<JsonObject> call =  FuzzieAPI.APIService().resetPassword(currentUser.getCurrentUser().getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    dismissProgressDialog();

                    if (response.code() == 200) {
                        String body = "We've sent an email to " + currentUser.getCurrentUser().getEmail() + ". Go to your email to reset your password.";
                        SpannableString spannablecontent=new SpannableString(body);
                        spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),23,23+currentUser.getCurrentUser().getEmail().length(), 0);

                        showFZAlert("RESET EMAIL SENT!", spannablecontent, "GOT IT", "", R.drawable.ic_success_white);

                    } else if (response.code() == 417){
                        logoutUser(currentUser, dataManager,  EditPasswordActivity.this);
                    } else {
                        showFZAlert("Oops!", "There's no Fuzzie account linked to this email address.", "OK", "", R.drawable.ic_bear_dead_white);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dismissProgressDialog();
                    showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            });
        }
    }
}
