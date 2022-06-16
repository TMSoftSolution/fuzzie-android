package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by mac on 2/27/18.
 */

@EActivity(R.layout.activity_red_packet_payment_success)
public class RedPacketPaymentSuccessActivity extends BaseActivity {

    @AfterViews
    public void calledAfterViewInjection() {

    }

    @Click(R.id.cvNow)
    void sendNowButtonClicked(){

        RedPacketDeliverActivity_.intent(context).start();
    }

    @Click(R.id.tvLater)
    void sendLaterButtonClicked(){

        goRedPacketHistoryPage();

    }

    private void goRedPacketHistoryPage(){

        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("go_red_packet_history_page", true);
        intent.putExtra("selected_tab", 1);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
