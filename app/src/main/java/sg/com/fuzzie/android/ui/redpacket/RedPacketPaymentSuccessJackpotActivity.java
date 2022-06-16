package sg.com.fuzzie.android.ui.redpacket;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by movdev on 3/14/18.
 */

@EActivity(R.layout.activity_red_packet_payment_success_jackpot)
public class RedPacketPaymentSuccessJackpotActivity extends BaseActivity {

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @AfterViews
    public void calledAfterViewInjection() {

        int ticketCount = dataManager.getHome().getJackpotTicketsCountForFirstRedPacketSending();

        if (ticketCount == 1){

            tvTitle.setText(FuzzieUtils.fromHtml("CONGRATS! YOU\'VE UNLOCKED <font color='#F8C736'>1 FREE JACKPOT TICKET</font>"));

        } else {

            tvTitle.setText(FuzzieUtils.fromHtml("CONGRATS! YOU\'VE UNLOCKED <font color='#F8C736'>" + String.valueOf(ticketCount) + " FREE JACKPOT TICKETS</font>"));
        }

    }

    @Click(R.id.cvContinue)
    void continueButtonClicked(){
        RedPacketPaymentSuccessActivity_.intent(context).start();
    }
}
