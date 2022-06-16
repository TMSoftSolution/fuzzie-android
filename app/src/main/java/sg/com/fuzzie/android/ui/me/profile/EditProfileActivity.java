package sg.com.fuzzie.android.ui.me.profile;

import android.Manifest;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import sg.com.fuzzie.android.utils.FZAlarmManager;
import sg.com.fuzzie.android.utils.TimeUtils;
import timber.log.Timber;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
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
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.AlarmReceiverAction.INTENT_BIRTHDAY;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_PHOTO_PICKER;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_MY_BIRTHDAY;

/**
 * Created by mac on 5/18/17.
 */

@EActivity(R.layout.activity_eidt_profile)
public class EditProfileActivity extends BaseActivity implements TextView.OnEditorActionListener, View.OnFocusChangeListener, com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    String gender = "";
    String birthday = "";

    @ViewById(R.id.ivAvatar)
    ImageView ivProfilePic;

    @ViewById(R.id.etFirstName)
    EditText etFirstName;

    @ViewById(R.id.etLastName)
    EditText etLastName;

    @ViewById(R.id.tvEmail)
    TextView tvEmail;

    @ViewById(R.id.tvBirth)
    TextView tvBirth;

    @ViewById(R.id.ivMale)
    ImageView ivMale;

    @ViewById(R.id.tvMale)
    TextView tvMale;

    @ViewById(R.id.ivFemale)
    ImageView ivFemale;

    @ViewById(R.id.tvFemale)
    TextView tvFemale;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @AfterViews
    public void calledAfterViewInjection() {
        setupProfileInfo();
    }

    private void setupProfileInfo(){

        if (currentUser == null) return;

        Nammu.init(this);

        EasyImage.configuration(this)
                .setImagesFolderName("Fuzzie");

        setUserInfo();

        etFirstName.setOnEditorActionListener(this);
        etLastName.setOnEditorActionListener(this);
        etFirstName.setOnFocusChangeListener(this);
        etLastName.setOnFocusChangeListener(this);
    }

    private void setUserInfo(){

        updateProfilePic();

        etFirstName.setText(currentUser.getCurrentUser().getFirstName());
        etLastName.setText(currentUser.getCurrentUser().getLastName());
        tvEmail.setText(currentUser.getCurrentUser().getEmail());
        if (currentUser.getCurrentUser().getBirthday() != null && !currentUser.getCurrentUser().getBirthday().equals("")){
            tvBirth.setText(TimeUtils.dateTimeFormat(currentUser.getCurrentUser().getBirthday(), "yyyy-MM-dd", "d MMMM yyyy"));
            birthday = TimeUtils.dateTimeFormat(currentUser.getCurrentUser().getBirthday(), "yyyy-MM-dd", "dd/MM/yyyy");

        }
        gender = currentUser.getCurrentUser().getGender();
        updateGenderButton();
    }

    private void updateProfilePic(){

        if (currentUser.getCurrentUser().getProfileImage() != null && !currentUser.getCurrentUser().getProfileImage().equals("")){
            Picasso.get()
                    .load(currentUser.getCurrentUser().getProfileImage())
                    .placeholder(R.drawable.profile_image_placeholder)
                    .transform(new CropCircleTransformation())
                    .fit()
                    .into(ivProfilePic);

        } else {
            Picasso.get()
                    .load(R.drawable.profile_image_placeholder)
                    .fit()
                    .into(ivProfilePic);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.rlEmail)
    void emailButtonClicked(){
        EditEmailActivity_.intent(context).start();
    }

    @Click(R.id.rlPassword)
    void passwordButtonClicked(){
        EditPasswordActivity_.intent(context).start();
    }

    @Click(R.id.rlBirth)
    void birthButtonClicked(){
        showCalendar();
    }

    @Click(R.id.llMale)
    void maleButtonClicked(){
        gender = "m";
        updateGenderButton();
        updateGender();
    }

    @Click(R.id.llFemale)
    void femaleButtonClicked(){
        gender = "f";
        updateGenderButton();
        updateGender();

    }

    @Click(R.id.ivCamera)
    void cameraButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.profile_photo_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        showProfileImageview();
                        break;
                    case 1:
                        fromCamera();
                        break;
                    case 2:
                        fromPhoto();
                        break;
                    case 3:
                        removeProfileImage();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void showProfileImageview(){
        EditProfilePicActivity_.intent(context).startForResult(REQUEST_PHOTO_PICKER);
    }

    private void fromPhoto(){
        if (Build.VERSION.SDK_INT >= 23){

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Nammu.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //Nothing, this sample saves to Public gallery so it needs permission
                        EasyImage.openGallery(EditProfileActivity.this, 0);
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

    private void fromCamera(){

        if (Build.VERSION.SDK_INT >= 23){

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Nammu.askForPermission(this, Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //Nothing, this sample saves to Public gallery so it needs permission
                        EasyImage.openCamera(EditProfileActivity.this, 0);
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

    private void removeProfileImage(){
        if (currentUser.getCurrentUser().getProfileImage() != null && !currentUser.getCurrentUser().getProfileImage().equals("")){
            deleteProfileImage();
        }

    }

    private void showCalendar(){

        hideKeyboard();

        String[] splits = birthday.split("/");

        if (!birthday.equals("") && splits.length == 3){


            int year = Integer.parseInt(splits[2]);
            int month = Integer.parseInt(splits[1]);
            int  date = Integer.parseInt(splits[0]);

            showDate(year, month - 1, date);

        } else {

            Calendar calendar = Calendar.getInstance();
            showDate(calendar.get(Calendar.YEAR) - 25, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        }
    }

    void showDate(int year, int monthOfYear, int dayOfMonth) {

        new SpinnerDatePickerDialogBuilder()
                .context(EditProfileActivity.this)
                .callback(EditProfileActivity.this)
                .spinnerTheme( R.style.DatePickerSpinner)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    private void updateGenderButton(){
        switch (gender) {
            case "m":
                ivMale.setImageResource(R.drawable.ic_male_selected);
                tvMale.setTextColor(Color.parseColor("#424242"));
                ivFemale.setImageResource(R.drawable.ic_female_normal);
                tvFemale.setTextColor(Color.parseColor("#ADADAD"));
                break;
            case "f":
                ivMale.setImageResource(R.drawable.ic_male_normal);
                tvMale.setTextColor(Color.parseColor("#ADADAD"));
                ivFemale.setImageResource(R.drawable.ic_female_selected);
                tvFemale.setTextColor(Color.parseColor("#424242"));
                break;
            default:
                ivMale.setImageResource(R.drawable.ic_male_normal);
                tvMale.setTextColor(Color.parseColor("#ADADAD"));
                ivFemale.setImageResource(R.drawable.ic_female_normal);
                tvFemale.setTextColor(Color.parseColor("#ADADAD"));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Timber.e("Image Pick Fail");

                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditProfileActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {

                if (imageFiles.size() > 0){
                    Picasso.get()
                            .load(imageFiles.get(0))
                            .placeholder(R.drawable.profile_image_placeholder)
                            .transform(new CropCircleTransformation())
                            .fit()
                            .into(ivProfilePic);

                    uploadProfilePhoto(imageFiles.get(0));
                }

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                Timber.e("Image Pick Cancell");
            }

        });

        if (requestCode == REQUEST_PHOTO_PICKER){

            if (resultCode == RESULT_OK){

                updateProfilePic();
            }
        }
    }

    private void deleteProfileImage(){
        Call<JsonObject> call = FuzzieAPI.APIService().deleteProfilePhoto(currentUser.getAccesstoken());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null){

                    saveUserInfo(response.body());
                    updateProfilePic();
                    showSuccessToast();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager, EditProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void uploadProfilePhoto(File file){
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_picture", file.getName(), reqFile);

        displayProgressDialog("Updating Profile Image...");

        Call<JsonObject> call = FuzzieAPI.APIService().uploadProfilePhoto(currentUser.getAccesstoken(), body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissProgressDialog();

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    saveUserInfo(response.body());
                    showSuccessToast();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  EditProfileActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
                Timber.e(t.getLocalizedMessage());
            }
        });

    }

    private void updateFirstName(){
        if (etFirstName.getText().toString().equals("")) return;
        Call<JsonObject> call = FuzzieAPI.APIService().updateFirstName(currentUser.getAccesstoken(), etFirstName.getText().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    saveUserInfo(response.body());
                    showSuccessToast();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  EditProfileActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void updateLastName(){
        if (etLastName.getText().toString().equals("")) return;
        Call<JsonObject> call = FuzzieAPI.APIService().updateLastName(currentUser.getAccesstoken(), etLastName.getText().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    saveUserInfo(response.body());
                    showSuccessToast();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  EditProfileActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void updateBirth(){
        Call<JsonObject> call = FuzzieAPI.APIService().updateBirthday(currentUser.getAccesstoken(), birthday);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    saveUserInfo(response.body());
                    showSuccessToast();

                    FZAlarmManager.getInstance().cancelScheduledNotification(context, INTENT_BIRTHDAY, NOTIFICATION_ID_MY_BIRTHDAY);
                    FZAlarmManager.getInstance().scheduleMyBirthdayNotification(context);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  EditProfileActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void updateGender(){
        Call<JsonObject> call = FuzzieAPI.APIService().updateGender(currentUser.getAccesstoken(), gender);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    saveUserInfo(response.body());
                    showSuccessToast();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  EditProfileActivity.this);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void saveUserInfo(JsonObject object){
        FZUser user = FZUser.fromJSON(object.getAsJsonObject("user").toString());
        currentUser.setCurrentUser(user);
    }

    @Override
    protected void hideKeyboard() {
        super.hideKeyboard();
        etLastName.clearFocus();
        etLastName.clearFocus();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE){
            if (v == etFirstName){
                etFirstName.clearFocus();
                hideKeyboard();
                if (!currentUser.getCurrentUser().getFirstName().equals(etFirstName.getText().toString())){
                    updateFirstName();
                }
            } else if (v == etLastName){
                etLastName.clearFocus();
                hideKeyboard();
                if (!currentUser.getCurrentUser().getLastName().equals(etLastName.getText().toString())){
                    updateLastName();
                }
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == etFirstName && !hasFocus){
            if (!currentUser.getCurrentUser().getFirstName().equals(etFirstName.getText().toString())){
                updateFirstName();
            }
        } else if (v == etLastName && !hasFocus){
            if (!currentUser.getCurrentUser().getLastName().equals(etLastName.getText().toString())){
                updateLastName();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        birthday = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        tvBirth.setText(TimeUtils.dateTimeFormat(birthday, "dd/MM/yyyy", "d MMMM yyyy"));
        updateBirth();
    }
}
