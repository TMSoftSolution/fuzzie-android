package sg.com.fuzzie.android.ui.me.profile;

import android.support.v7.widget.CardView;
import android.widget.EditText;

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
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 7/19/17.
 */

@EActivity(R.layout.activity_edit_email)
public class EditEmailActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.etEmail)
    EditText etEmail;

    @ViewById(R.id.cvSave)
    CardView cvSave;

    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_activity_edit_email));
        etEmail.setText(currentUser.getCurrentUser().getEmail());
        updateSaveButton();
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){

        updateEmail();
    }

    @AfterTextChange(R.id.etEmail)
    void emailChanged(){
        updateSaveButton();
    }

    private void updateSaveButton(){
        if (etEmail.getText().toString().equals(currentUser.getCurrentUser().getEmail()) || !FuzzieUtils.isValidEmail(etEmail.getText().toString())){
            cvSave.setEnabled(false);
            cvSave.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
        } else {
            cvSave.setEnabled(true);
            cvSave.setCardBackgroundColor(getResources().getColor(R.color.primary));
        }
    }

    private void updateEmail(){
        displayProgressDialog("Updating email...");
        Call<JsonObject> call = FuzzieAPI.APIService().updateEmail(currentUser.getAccesstoken(), etEmail.getText().toString());
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
                    logoutUser(currentUser, dataManager,  EditEmailActivity.this);
                } else if(response.code() == 422){
                    String jsonString = null;
                    try {
                        jsonString = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                        if (jsonObject.get("message") != null){
                            showFZAlert("Oops!", jsonObject.get("message").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else  {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void saveUserInfo(JsonObject object){
        FZUser user = FZUser.fromJSON(object.getAsJsonObject("user").toString());
        currentUser.setCurrentUser(user);
    }
}
