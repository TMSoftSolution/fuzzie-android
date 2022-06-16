package sg.com.fuzzie.android.ui.gift;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
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
 * Created by mac on 6/20/17.
 */

@EActivity(R.layout.activity_email_send)
public class EmailSendActivity extends BaseActivity {

    private boolean copied;
    private boolean sentSuccess;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @Extra
    String extraGiftId;

    @Extra
    boolean fromRedPacket;

    @ViewById(R.id.cvCopy)
    CardView cvCopy;

    @ViewById(R.id.ivCopy)
    ImageView ivCopy;

    @ViewById(R.id.etEmail)
    EditText etEmail;

    @ViewById(R.id.tvSend)
    TextView tvSend;

    @AfterViews
    public void calledAfterViewInjection() {

        if (fromRedPacket){

            tvSend.setText("SEND LUCKY PACKET");

            if (dataManager.redPacketBundle == null){
                return;
            }

        } else {

            if (extraGiftId == null)
                return;
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvCopy)
    void copyButtonClicked(){
        copied = !copied;
        if (copied){
            cvCopy.setCardBackgroundColor(getResources().getColor(R.color.primary));
            ivCopy.setImageResource(R.drawable.email_check);
        } else {
            cvCopy.setCardBackgroundColor(Color.parseColor("#D7D7D7"));
            ivCopy.setImageResource(R.drawable.email_uncheck);
        }
    }

    @Click(R.id.cvSend)
    void sendButtonClicked(){
        if (!FuzzieUtils.isValidEmail(etEmail.getText().toString())){
            showFZAlert("Oops!", "You need to enter a valid email address.", "OK", "", R.drawable.ic_bear_dead_white);
        } else {

            hideKeyboard();
            displayProgressDialog("Sending...");

            Call<Void> call = null;

            if (fromRedPacket){

                call = FuzzieAPI.APIService().sendRedPackets(currentUser.getAccesstoken(), dataManager.redPacketBundle.getId(), etEmail.getText().toString(), copied);

            } else {

                call = FuzzieAPI.APIService().sendGiftWithEmail(currentUser.getAccesstoken(), extraGiftId, etEmail.getText().toString(), copied);
            }

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    dismissProgressDialog();
                    if (response.code() == 200){

                        sentSuccess = true;

                        if (fromRedPacket){
                            showFZAlert("EMAIL SENT!", "Your Lucky Packet has been successfully sent.", "DONE", "", R.drawable.ic_success_white);
                        } else {
                            showFZAlert("EMAIL SENT!", "Your gift has been successfully delivered.", "DONE", "", R.drawable.ic_success_white);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    dismissProgressDialog();
                    showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            });
        }
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (sentSuccess){
            finish();
        }
    }

    @Override
    public void cancelButtonClicked() {
        super.cancelButtonClicked();

        sentSuccess = false;
    }
}
