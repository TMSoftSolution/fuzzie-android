package sg.com.fuzzie.android.ui.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import sg.com.fuzzie.android.utils.TimeUtils;
import timber.log.Timber;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by nurimanizam on 10/12/16.
 */

@SuppressWarnings("ALL")
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    static final int REQUEST_FINISH = 1;

    CallbackManager callbackManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvFacebook)
    TextView tvFacebook;

    @ViewById(R.id.tvSignIn)
    TextView tvSignIn;

    @ViewById(R.id.etPhone)
    EditText etPhone;

    @AfterViews
    public void calledAfterViewInjection() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logOut();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Timber.d("Facebook Access Token: " + AccessToken.getCurrentAccessToken().getToken());
                String facebookToken = AccessToken.getCurrentAccessToken().getToken();

                showPopView("PROCESSING", "", R.drawable.bear_craft, true);

                Call<JsonObject> call =  FuzzieAPI.APIService().loginViaFacebook(facebookToken);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        hidePopView();

                        if (response.body() != null && response.body().getAsJsonObject("user") != null) {

                            FZUser user = FZUser.fromJSON(response.body().getAsJsonObject("user").toString());

                            currentUser.setAccesstoken(user.getFuzzieToken());

                            if (user.getPhone() != null && user.getPhone().length() > 0) {

                                currentUser.setCurrentUser(user);

                                Intent intent = new Intent(context, MainActivity_.class);
                                startActivity(intent);
                                setResult(RESULT_OK, null);
                                finish();

                            } else { // Incomplete User

                                String userJson = FZUser.toJSON(user);
                                SignUpActivity_.intent(context).userExtra(userJson).start();

                            }


                        } else if (response.body() != null) {

                            FZUser user = new FZUser();

                            if (response.body().getAsJsonPrimitive("email") != null) {
                                user.setEmail(response.body().getAsJsonPrimitive("email").getAsString());
                            }

                            if (response.body().getAsJsonPrimitive("first_name") != null) {
                                user.setFirstName(response.body().getAsJsonPrimitive("first_name").getAsString());
                            }

                            if (response.body().getAsJsonPrimitive("last_name") != null) {
                                user.setLastName(response.body().getAsJsonPrimitive("last_name").getAsString());
                            }

                            if (response.body().getAsJsonPrimitive("gender") != null) {

                                String gender = response.body().getAsJsonPrimitive("gender").getAsString();
                                if (gender.length() > 0){
                                    user.setGender(gender.substring(0, 1));
                                }
                            }

                            if (response.body().getAsJsonPrimitive("birthday") != null) {

                                String birthday = response.body().getAsJsonPrimitive("birthday").getAsString();
                                if (!birthday.equals("")){
                                    user.setBirthday(TimeUtils.birthdayFormatFromFacebook(birthday));
                                }
                            }

                            String userJson = FZUser.toJSON(user);
                            SignUpActivity_.intent(context).userExtra(userJson).start();

                        } else {
                            SignUpActivity_.intent(context).start();
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        hidePopView();
                        showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                    }

                });

            }

            @Override
            public void onCancel() {
                Timber.d("Facebook Login Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                showFZAlert("Facebook Error", exception.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.cvFacebook)
    void facebookButtonClicked() {
        Timber.d("Facebook Button Clicked");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_friends","user_birthday"));
    }

    @Click(R.id.cvSignin)
    void loginButtonClicked() {
        Timber.d("Login Button Clicked");

        hideKeyboard();

        final String phone = etPhone.getText().toString();

        if (TextUtils.isEmpty(phone)){

            showFZAlert("Oops!", "Your mobile number is missing.", "OK", "", R.drawable.ic_bear_dead_white);

            return;

        } else if (!FuzzieUtils.isValidPhone(phone)){

            showFZAlert("Oops!", "Invalid mobile number.", "OK", "", R.drawable.ic_bear_dead_white);

            return;

        }

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        Call<Void> call = FuzzieAPI.APIService().checkPhoneRegistered(phone);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {


                hidePopView();

                if (response.code() == 200){

                    VerificationCodeActivity_.intent(context).phoneExtra(phone).startForResult(REQUEST_FINISH);


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

    @Click(R.id.tvHelp)
    void helpButtonClicked(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.auth_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        emailSupport();
                        break;
                    case 1:
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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fuzzie Support");
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FINISH) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }

    @Override
    public void okButtonClicked() {

        hidePopView();
    }
}
