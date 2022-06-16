package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.widget.EditText;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

/**
 * Created by movdev on 4/2/18.
 */

@EActivity(R.layout.activity_red_packet_add_message)
public class RedPacketAddMessageActivity extends BaseActivity {

    private String tempMessage;
    private boolean isDiscard;

    @Extra
    String message;

    @ViewById(R.id.etMessage)
    EditText etMessage;

    @ViewById(R.id.cvAdd)
    CardView cvAdd;


    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_red_packet_add_message_activity));

        tempMessage = message;

        if (tempMessage != null){
            etMessage.setText(tempMessage);
        }

    }


    @AfterTextChange(R.id.etMessage)
    void messageChanged(){

        tempMessage = etMessage.getText().toString();

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){

        if (!message.equals(tempMessage)){

            showFZAlert("DISCARD MESSAGE?", "Do you wish to discard your message?", "YES, DISCARD", "No, cancel", R.drawable.ic_normal_bear);
            isDiscard = true;

        } else {

            finish();

        }
    }

    @Click(R.id.cvAdd)
    void addButtonClicked(){

        Intent intent = new Intent();
        intent.putExtra("message", tempMessage);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (isDiscard){

            finish();
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        isDiscard = false;
    }

    @Override
    public void onBackPressed() {
        backButtonClicked();
    }
}
