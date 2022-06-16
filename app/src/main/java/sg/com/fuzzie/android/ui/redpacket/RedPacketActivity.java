package sg.com.fuzzie.android.ui.redpacket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;


/**
 * Created by mac on 2/26/18.
 */

@EActivity(R.layout.activity_red_packet)
public class RedPacketActivity extends BaseActivity {

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.ticketBanner)
    View ticketBanner;

    @ViewById(R.id.tvTicket)
    TextView tvTicket;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @AfterViews
    public void calledAfterViewInjection() {

        tvTitle.setText(FuzzieUtils.fromHtml("<font color='#FFA800'>LUCKY</font> PACKETS"));

        if (currentUser.getCurrentUser().getSettings().isBoughtFirstRedPacketBundle()){

            ViewUtils.gone(ticketBanner);

        } else {

            int ticketCounts = dataManager.getHome().getJackpotTicketsCountForFirstRedPacketSending();
            if (ticketCounts == 1){
                tvTicket.setText("GET 1 FREE JACKPOT TICKET");
            } else {
                tvTicket.setText(String.format("GET %d FREE JACKPOT TICKETS", ticketCounts));
            }
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.tvHelp)
    void learnMoreButtonClicked(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.activate_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        JackpotLearnMoreActvity_.intent(context).fromRedPacket(true).start();
                        break;
                    case 1:
                        emailSupport();
                        break;
                    case 2:
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

    @Click(R.id.cvLearnMore)
    void jackpotLearnMoreButtonClicked(){
        JackpotLearnMoreActvity_.intent(context).fromRedPacket(false).start();
    }

    @Click(R.id.cvGroup)
    void startButtonClicked(){
        RedPacketQuantityActivity_.intent(context).start();
    }

    @Click(R.id.cvPerson)
    void personButtonClicked(){
        RedPacketItemsAddActivity_.intent(context).quantity(1).start();
    }
}
