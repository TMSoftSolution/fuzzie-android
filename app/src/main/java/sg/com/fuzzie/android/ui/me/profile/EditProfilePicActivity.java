package sg.com.fuzzie.android.ui.me.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import timber.log.Timber;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import java.io.File;
import java.util.List;

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
import sg.com.fuzzie.android.utils.ViewUtils;


/**
 * Created by mac on 5/24/17.
 */

@EActivity(R.layout.activity_edit_profile_pic)
public class EditProfilePicActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.ivProfilePic)
    ImageView ivProfilePic;

    @AfterViews
    public void calledAfterViewInjection() {

        Nammu.init(this);

        EasyImage.configuration(this)
                .setImagesFolderName("Fuzzie");

        setProfilePic();

    }

    private void setProfilePic(){
        ivProfilePic.getLayoutParams().height = ViewUtils.getScreenWidth(context);
        ivProfilePic.getLayoutParams().width = ViewUtils.getScreenWidth(context);
        ivProfilePic.requestLayout();

        if (currentUser.getCurrentUser().getProfileImage() != null && !currentUser.getCurrentUser().getProfileImage().equals("")) {
            Picasso.get()
                    .load(currentUser.getCurrentUser().getProfileImage())
                    .placeholder(R.drawable.profile_image_placeholder)
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

    @Click(R.id.tvEdit)
    void editButtonClicked(){
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
                        EasyImage.openGallery(EditProfilePicActivity.this, 0);
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
                        EasyImage.openCamera(EditProfilePicActivity.this, 0);
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

    private void uploadProfilePhoto(File file){
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_picture", file.getName(), reqFile);

        displayProgressDialog("Uploading Profile Image...");

        Call<JsonObject> call = FuzzieAPI.APIService().uploadProfilePhoto(currentUser.getAccesstoken(), body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissProgressDialog();
                Timber.e("Uploading Success");
                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    FZUser user = FZUser.fromJSON(response.body().getAsJsonObject("user").toString());
                    currentUser.setCurrentUser(user);

                    setResult(RESULT_OK);
                    finish();
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  EditProfilePicActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
            }
        });

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

                if (imageFiles.size() > 0){

                    Timber.e("Image Pick Success");
                    Picasso.get()
                            .load(imageFiles.get(0))
                            .placeholder(R.drawable.profile_image_placeholder)
                            .fit()
                            .into(ivProfilePic);
                    uploadProfilePhoto(imageFiles.get(0));

                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                Timber.e("Image Pick Cancelled");

                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditProfilePicActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });
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
}
