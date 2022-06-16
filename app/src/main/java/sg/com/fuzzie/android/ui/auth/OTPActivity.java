package sg.com.fuzzie.android.ui.auth;

import android.content.Intent;
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
 * Created by nurimanizam on 23/12/16.
 */

@EActivity(R.layout.activity_otp)
public class OTPActivity extends BaseActivity {

    @Extra
    String extraPhone;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvPhone)
    TextView tvPhone;

    @ViewById(R.id.pinEntry)
    PinEntryView pinEntry;

    @AfterViews
    public void calledAfterViewInjection() {

        if (extraPhone != null) {
            tvPhone.setText("+65 " + extraPhone);
        }

        pinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pin = charSequence.toString();
                if (pin.length() == 4) {
                    verifyOTP();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.cvResend)
    void resendButtonClicked() {
        showPopView("Resending code", "", R.drawable.bear_craft, true);
        Call<Object> call = FuzzieAPI.APIService().resendOTPCode(currentUser.getAccesstoken());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                hidePopView();
                showFZAlert("SUCCESS!", "A new code is on its way.", "OK", "", R.drawable.ic_success_white);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                hidePopView();
                showFZAlert("OOPS!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }

        });

    }

    void verifyOTP() {
        hideKeyboard();
        showPopView("VERIFYING", "", R.drawable.bear_craft, true);

        Call<JsonObject> call = FuzzieAPI.APIService().verifyOTPCode(currentUser.getAccesstoken(), pinEntry.getText().toString(), true);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hidePopView();

                if (response.code() == 200 && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    FZUser user = FZUser.fromJSON(response.body().getAsJsonObject("user").toString());

                    currentUser.setAccesstoken(user.getFuzzieToken());
                    currentUser.setCurrentUser(user);

                    Intent intent = new Intent(context, MainActivity_.class);
                    startActivity(intent);
                    setResult(RESULT_OK, null);
                    finish();

                } else if (response.code() == 404) {
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

    @Override
    public void okButtonClicked() {
        hidePopView();
    }
}
