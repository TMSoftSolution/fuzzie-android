package sg.com.fuzzie.android.ui.auth;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;

/**
 * Created by mac on 1/3/18.
 */

@EActivity(R.layout.activity_verification_code)
public class VerificationCodeActivity extends BaseActivity {

    @Extra
    String phoneExtra;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvPhone)
    TextView tvPhone;

    @ViewById(R.id.pinEntry)
    PinEntryView pinEntry;


    @AfterViews
    void callAfterViewInjection(){

        if (phoneExtra != null) {
            tvPhone.setText("+65 " + phoneExtra);
        }

        pinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pin = charSequence.toString();
                if (pin.length() == 4) {
                    verifyCode();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void verifyCode(){

        hideKeyboard();
        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        FuzzieAPI.APIService().loginViaPhone(phoneExtra, pinEntry.getText().toString()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hidePopView();

                if (response.code() == 200 && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    FZUser user = FZUser.fromJSON(response.body().getAsJsonObject("user").toString());

                    currentUser.setAccesstoken(user.getFuzzieToken());
                    currentUser.setCurrentUser(user);

                    showPopView("SUCCESS!", "", R.drawable.bear_good, false);

                    goHome();

                } else if (response.code() == 401) {

                    pinEntry.clearText();
                    showFZAlert("OOPS!", "Invalid Code.", "OK", "", R.drawable.ic_bear_dead_white);

                } else {

                    pinEntry.clearText();
                    showFZAlert("OOPS!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);


                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                hidePopView();
                pinEntry.clearText();
                showFZAlert("OOPS!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });

    }

    private void goHome(){

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                hidePopView();

                Intent intent = new Intent(context, MainActivity_.class);
                startActivity(intent);
                setResult(RESULT_OK, null);
                finish();
            }
        };

        handler.postDelayed(runnable, 1000);

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvResend)
    void resendButtonClicked(){

        hideKeyboard();

        showPopView("Resending code", "", R.drawable.bear_craft, true);

        FuzzieAPI.APIService().checkPhoneRegistered(phoneExtra).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                hidePopView();

                if (response.code() == 200){

                    showFZAlert("SUCCESS!", "A new code is on its way.", "OK", "", R.drawable.ic_success_white);


                } else if (response.code() == 404){

                    showFZAlert("Oops!", "There's no Fuzzie account linked to this mobile number.", "OK", "", R.drawable.ic_bear_dead_white);

                } else {

                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                hidePopView();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });

    }

    @Override
    public void okButtonClicked() {
        hidePopView();
    }
}
