package sg.com.fuzzie.android.ui.giftbox;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_UPDATED_SENT_GIFT;

/**
 * Created by mac on 7/25/17.
 */

@EActivity(R.layout.activity_gift_edit)
public class GiftEditActivity extends BaseActivity {

    @Extra
    String giftId;

    private Gift gift;
    private boolean isBack;
    private boolean showSuccess;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.etName)
    EditText etName;

    @ViewById(R.id.etMessage)
    EditText etMessage;

    @ViewById(R.id.cvSave)
    CardView cvSave;

    @AfterViews
    public void calledAfterViewInjection() {

        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


        if (giftId == null) return;

        gift = dataManager.getSentGiftById(giftId);
        if (gift == null) return;

        etName.setText(gift.getReceiver().getName());
        etMessage.setText(gift.getMessage());

        updateSaveButton();
    }

    private void updateSaveButton(){
        if (gift.getReceiver().getName().equals(etName.getText().toString()) && gift.getMessage().equals(etMessage.getText().toString())
                || etName.getText().toString().equals("") || etMessage.getText().toString().equals("")){
            cvSave.setEnabled(false);
            cvSave.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
        } else {
            cvSave.setEnabled(true);
            cvSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @AfterTextChange({R.id.etName, R.id.etMessage})
    void editChanged(){
        updateSaveButton();
    }



    @Click(R.id.ivBack)
    void backButtonClicked(){
        if (!gift.getReceiver().getName().equals(etName.getText().toString()) || !gift.getMessage().equals(etMessage.getText().toString())){
            isBack = true;
            showFZAlert("DISCARD CHANGES?", "Sure you would like to discard your changes on your gift?", "DISCARD", "Cancel", R.drawable.ic_bear_musach_white);
        } else {
            finish();
        }

    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){
        hideKeyboard();
        displayProgressDialog("Saving...");
        Call<Gift> call = FuzzieAPI.APIService().updateGiftInfo(currentUser.getAccesstoken(), giftId, etName.getText().toString(), etMessage.getText().toString());
        call.enqueue(new Callback<Gift>() {
            @Override
            public void onResponse(Call<Gift> call, Response<Gift> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null){
                    gift = response.body();
                    dataManager.updateSentGift(gift);
                    showFZAlert("SUCCESS!", "Your gift has been updated.", "OK", "", R.drawable.ic_success_white);
                    showSuccess = true;
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_UPDATED_SENT_GIFT));

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager, GiftEditActivity.this);

                } else if (response.code() == 401 && response.errorBody() != null){
                    try {
                        String jsonString = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                        if (jsonObject.get("message") != null){
                            showFZAlert("Oops!", jsonObject.get("message").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code() , "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<Gift> call, Throwable t) {
                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();
        if (isBack){
            isBack = false;
            finish();
        } else if(showSuccess){
            finish();
        } else {
            updateSaveButton();
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
        isBack = false;
        showSuccess = false;
    }
}
