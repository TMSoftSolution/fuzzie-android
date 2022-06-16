package sg.com.fuzzie.android.ui.auth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;

import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.views.EditTextBackEvent;
import timber.log.Timber;

import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by nurimanizam on 10/12/16.
 */

@EActivity(R.layout.activity_signup)
public class SignUpActivity extends BaseActivity implements com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    static final int REQUEST_FINISH = 1;

    @Bean
    CurrentUser currentUser;

    String gender = "";
    String birthday = "";
    String referralCode = "";
    File profileImageFile;

    @Extra
    String userExtra;

    @ViewById(R.id.etFirstName)
    EditText firstNameView;

    @ViewById(R.id.etLastName)
    EditText lastNameView;

    @ViewById(R.id.etEmail)
    EditText emailView;

    @ViewById(R.id.llPassword)
    View llPassword;

    @ViewById(R.id.etPassword)
    EditText passwordView;

    @ViewById(R.id.etPhone)
    EditText phoneView;

    @ViewById(R.id.etBirth)
    EditText etBirth;

    @ViewById(R.id.etGender)
    EditText etGender;

    @ViewById(R.id.llMain)
    LinearLayout llMain;

    @ViewById(R.id.ivProfilePic)
    ImageView ivProfilePic;

    @ViewById(R.id.tvDescription)
    TextView tvDescription;

    @ViewById(R.id.etReferralCode)
    EditTextBackEvent etReferralCode;

    @AfterViews
    public void calledAfterViewInjection() {

        firstNameView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        lastNameView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        linkTextView(tvDescription);

        etReferralCode.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent ctrl, String text) {
                checkReferralCodeValidation();
            }
        });

        etReferralCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReferralCodeValidation();
                    return true;
                }
                return false;
            }
        });

        Nammu.init(this);

        EasyImage.configuration(this)
                .setImagesFolderName("Fuzzie");

        if (userExtra!= null) {

            llPassword.setVisibility(View.GONE);

            FZUser user = FZUser.fromJSON(userExtra);

            if (user != null){

                if (user.getEmail() != null) {
                    emailView.setText(user.getEmail());
                }

                if (user.getFirstName() != null) {
                    firstNameView.setText(user.getFirstName());
                }

                if (user.getLastName() != null) {
                    lastNameView.setText(user.getLastName());
                }

                if (user.getGender() != null){

                    gender = user.getGender();

                    switch (gender) {
                        case "m":
                            etGender.setText("Male");
                            break;
                        case "f":
                            etGender.setText("Female");
                            break;
                        default:
                            etGender.setText("");
                            break;
                    }

                }


                if (user.getBirthday() != null && !user.getBirthday().equals("")){

                    etBirth.setText(TimeUtils.dateTimeFormat(user.getBirthday(), "yyyy-MM-dd", "d MMMM yyyy"));
                    birthday = TimeUtils.dateTimeFormat(user.getBirthday(), "yyyy-MM-dd", "MM/dd/yyyy");

                }

            }

        }

    }

    private void checkReferralCodeValidation(){

        hideKeyboard();
        focusDisable();
        final String code = etReferralCode.getText().toString();

        if (code.length() > 0){

            showPopView("VALIDATING", "", R.drawable.bear_craft, true);

            FuzzieAPI.APIService().validateReferralCode(code).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    hidePopView();

                    if (response.code() == 200 && response.body() != null){

                        referralCode = code;

                        double credits = response.body().getAsJsonPrimitive("credits").getAsDouble();
                        ReferralCodeActivateActivity_.intent(context).creditsExtra(credits).start();

                    } else {

                        referralCode = "";
                        etReferralCode.setText("");
                        String errorMessage = "Unknown error occurred: " + response.code();

                        if (response.code() == 402 || response.code() == 403 || response.code() == 405){

                            try {

                                String jsonString = response.errorBody().string();
                                JsonParser parser = new JsonParser();
                                JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                                if (jsonObject != null && jsonObject.get("message") != null && !jsonObject.get("message").isJsonNull()){
                                    errorMessage = jsonObject.get("message").getAsString();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        showFZAlert("Oops!", errorMessage, response.code() == 405 ? "GOT IT" : "TRY AGAIN", "", R.drawable.ic_bear_dead_white);

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    referralCode = "";
                    etReferralCode.setText("");
                    hidePopView();
                    showFZAlert("Oops!",  t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            });
        }

    }

    private void linkTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By joining Fuzzie, you agree with the Bear's ");
        spanTxt.append(Html.fromHtml("<b><u>Privacy Policy</u></b>"));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                privacyButtonClicked();
            }
        }, spanTxt.length() - "Privacy Policy".length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), spanTxt.length() - "Privacy Policy".length(), spanTxt.length(), 0);
        spanTxt.append(" and ");
        spanTxt.append(Html.fromHtml("<b><u>Terms and Conditions.</u></b>"));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                termButtonClicked();
            }
        }, spanTxt.length() - "Terms and Conditions.".length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), spanTxt.length() - "Terms and Conditions.".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void showGenderMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.gender_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        genderUpdate(0);
                        break;
                    case 1:
                        genderUpdate(1);
                        break;

                    default:
                        focusDisable();
                        break;
                }
            }
        }).show();
    }

    private void focusDisable(){
        llMain.requestFocus();
    }

    @Click({R.id.llBirth, R.id.etBirth})
    void birthButtonClicked(){

        hideKeyboard();

        String[] splits = birthday.split("/");

        if (!birthday.equals("") && splits.length == 3){


            int year = Integer.parseInt(splits[2]);
            int month = Integer.parseInt(splits[0]);
            int  date = Integer.parseInt(splits[1]);

            showDate(year, month - 1, date);

        } else {

            Calendar calendar = Calendar.getInstance();
            showDate(calendar.get(Calendar.YEAR) - 25, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        }

    }

    void showDate(int year, int monthOfYear, int dayOfMonth) {

        new SpinnerDatePickerDialogBuilder()
                .context(SignUpActivity.this)
                .callback(SignUpActivity.this)
                .spinnerTheme( R.style.DatePickerSpinner)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    @Click({R.id.llGender, R.id.etGender})
    void genderButtonClicked(){

        hideKeyboard();
        showGenderMenu();
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.cvJoin)
    void signupButtonClicked() {

        final String firstName = firstNameView.getText().toString();
        final String lastName = lastNameView.getText().toString();
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        final String phone = phoneView.getText().toString();

        MultipartBody.Part image = null;
        if (profileImageFile != null){
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            image = MultipartBody.Part.createFormData("profile_picture", profileImageFile.getName(), reqFile);
        }

        // Check for name
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
            showFZAlert("Oops!", "We need your first name and last name.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            showFZAlert("Oops!", "Your email is missing.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        } else if (!FuzzieUtils.isValidEmail(email)){
            showFZAlert("Oops!", "Invalid email address.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        // Check for a valid password when not facebook login
        if (userExtra == null && TextUtils.isEmpty(password)) {
            showFZAlert("Oops!", "Your password is missing.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        } else if (userExtra == null && password.length() < 6){
            showFZAlert("Oops!", "Your password is too short.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        // Check for name
        if (TextUtils.isEmpty(phone)) {
            showFZAlert("Oops!", "Your mobile number is missing.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        } else if (!FuzzieUtils.isValidPhone(phone)) {
            showFZAlert("Oops!", "Invalid mobile number.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        hideKeyboard();
        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        Call<JsonObject> call;
        if (userExtra == null) {
            call = FuzzieAPI.APIService().signUpViaEmail(RequestBody.create(MediaType.parse("text/plain"),email),
                    RequestBody.create(MediaType.parse("text/plain"),firstName),
                    RequestBody.create(MediaType.parse("text/plain"),lastName),
                    RequestBody.create(MediaType.parse("text/plain"),password),
                    RequestBody.create(MediaType.parse("text/plain"),password),
                    RequestBody.create(MediaType.parse("text/plain"),phone),
                    RequestBody.create(MediaType.parse("text/plain"),referralCode),
                    RequestBody.create(MediaType.parse("text/plain"),gender),
                    RequestBody.create(MediaType.parse("text/plain"),birthday),
                    image);
        } else {

            String facebookToken = AccessToken.getCurrentAccessToken().getToken();
            call = FuzzieAPI.APIService().signUpViaFacebook(facebookToken, email, firstName, lastName, phone, referralCode, gender, birthday);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hidePopView();

                if ((response.code() == 200 || response.code() == 201) && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    FZUser user = FZUser.fromJSON(response.body().getAsJsonObject("user").toString());
                    currentUser.setAccesstoken(user.getFuzzieToken());
                    OTPActivity_.intent(context).extraPhone(phone).startForResult(REQUEST_FINISH);

                } else {

                    if (response.code() != 500 && response.errorBody() != null) {
                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            showFZAlert("Oops!", jsonObject.get("message").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hidePopView();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }

        });


    }

    void genderUpdate(int index){

        if (index == 0){

            gender = "m";
            etGender.setText("Male");

        } else if (index == 1){

            gender = "f";
            etGender.setText("Female");

        } else {

            gender = "";
            etGender.setText("");
        }

        focusDisable();

    }

    @Click(R.id.ivProfilePic)
    void profilePickerButtonClicked(){
        showPhotoPicker();
    }

    private void showPhotoPicker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.photo_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        fromCamera();
                        break;
                    case 1:
                        fromPhoto();
                        break;

                    default:
                        break;
                }
            }
        }).show();
    }

    void fromPhoto(){

        if (Build.VERSION.SDK_INT >= 23){

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Nammu.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //Nothing, this sample saves to Public gallery so it needs permission
                        EasyImage.openGallery(SignUpActivity.this, 0);
                    }

                    @Override
                    public void permissionRefused() {
                        finish();
                    }
                });
            } else {
                EasyImage.openGallery(this, 0);
            }

        } else {
            EasyImage.openGallery(this, 0);
        }

    }

    void fromCamera(){

        if (Build.VERSION.SDK_INT >= 23){

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Nammu.askForPermission(this, Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //Nothing, this sample saves to Public gallery so it needs permission
                        EasyImage.openCamera(SignUpActivity.this, 0);
                    }

                    @Override
                    public void permissionRefused() {
                        finish();
                    }
                });
            } else {
                EasyImage.openCamera(this, 0);
            }

        } else {
            EasyImage.openCamera(this, 0);
        }
    }

    @Override
    public void okButtonClicked() {
        hidePopView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Timber.e("Image Pick Fail");
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                Timber.e("Image Pick Success");

                if (imageFiles.size() > 0){

                    Picasso.get()
                            .load(imageFiles.get(0))
                            .placeholder(R.drawable.ic_profile_pic_placeholder)
                            .transform(new CropCircleTransformation())
                            .fit()
                            .into(ivProfilePic);

                    profileImageFile = imageFiles.get(0);
                }

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                Timber.e("Image Pick Cancelled");

                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(SignUpActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });


        if (requestCode == REQUEST_FINISH) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }

    private void termButtonClicked(){
        WebActivity_.intent(context).titleExtra("TERMS OF SERVICE").urlExtra("http://fuzzie.com.sg/terms").start();
    }

    private void privacyButtonClicked(){
        WebActivity_.intent(context).titleExtra("PRIVACY POLICY").urlExtra("http://fuzzie.com.sg/privacy").start();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        birthday = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
        etBirth.setText(TimeUtils.dateTimeFormat(birthday, "MM/dd/yyyy", "d MMMM yyyy"));
        focusDisable();

    }
}
