package sg.com.fuzzie.android.ui.giftbox;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseFragment;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotTicketsActivity_;
import sg.com.fuzzie.android.ui.redpacket.RedPacketHistoryActivity_;
import sg.com.fuzzie.android.ui.shop.topup.TopUpActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by joma on 10/18/17.
 */
@EFragment(R.layout.fragment_wallet)
public class WalletFragment extends BaseFragment {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvBallance)
    TextView tvBallance;

    @ViewById(R.id.tvCardBadge)
    TextView tvCardBadge;

    @ViewById(R.id.tvTicketBadge)
    TextView tvTicketBadge;

    @ViewById(R.id.tvRedPacketBadge)
    TextView tvRedPacketBadge;

    @ViewById(R.id.tvCard)
    TextView tvCard;

    @ViewById(R.id.tvTicket)
    TextView tvTicket;

    @ViewById(R.id.tvRedPacket)
    TextView tvRedPacket;

    @AfterViews
    public void calledAfterViewInjection() {


    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentUser.getCurrentUser() != null){
            tvBallance.setText(FuzzieUtils.getFormattedValue(context, currentUser.getCurrentUser().getWallet().getBalance(), 0.667f));

            updateGiftBadge();
            updateTicketBadge();
            updateRedPacketBadge();

            showActiveCardCount();
            showAvailableTicketCount();
            showUnOpenedRedPacketCount();
        }

    }

    private void  updateGiftBadge(){

        if (currentUser.getCurrentUser().getUnOpenedGiftCount() > 0){
            tvCardBadge.setVisibility(View.VISIBLE);
            tvCardBadge.setText(String.valueOf(currentUser.getCurrentUser().getUnOpenedGiftCount()));
        } else {
            tvCardBadge.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTicketBadge(){

        if (currentUser.getCurrentUser().getUnOpenedTicketCount() > 0){
            tvTicketBadge.setVisibility(View.VISIBLE);
            tvTicketBadge.setText(String.valueOf(currentUser.getCurrentUser().getUnOpenedTicketCount()));
        } else {
            tvTicketBadge.setVisibility(View.INVISIBLE);
        }
    }

    private void updateRedPacketBadge(){

        if (currentUser.getCurrentUser().getUnOpenedRedPacketCount() > 0){
            tvRedPacketBadge.setVisibility(View.VISIBLE);
            tvRedPacketBadge.setText(String.valueOf(currentUser.getCurrentUser().getUnOpenedRedPacketCount()));
        } else {
            tvRedPacketBadge.setVisibility(View.INVISIBLE);
        }
    }

    private void showActiveCardCount(){

        if (currentUser.getCurrentUser().getActiveGiftCount() > 0){

            tvCard.setVisibility(View.VISIBLE);

            if (currentUser.getCurrentUser().getActiveGiftCount() == 1){

                tvCard.setText(String.format("%d active card", currentUser.getCurrentUser().getActiveGiftCount()));

            } else {

                tvCard.setText(String.format("%d active cards", currentUser.getCurrentUser().getActiveGiftCount()));
            }

        } else {

            tvCard.setVisibility(View.GONE);
        }
    }

    private void showAvailableTicketCount(){

        if (currentUser.getCurrentUser().getAvailableJackpotTicketsCount() > 0){

            tvTicket.setVisibility(View.VISIBLE);

            if (currentUser.getCurrentUser().getAvailableJackpotTicketsCount() == 1){

                tvTicket.setText(String.format("%d ticket available", currentUser.getCurrentUser().getAvailableJackpotTicketsCount()));

            } else {

                tvTicket.setText(String.format("%d tickets available", currentUser.getCurrentUser().getAvailableJackpotTicketsCount()));
            }

        } else {

            tvTicket.setVisibility(View.GONE);
        }
    }

    private void showUnOpenedRedPacketCount(){

        if ((currentUser.getCurrentUser().getUnOpenedRedPacketCount() > 0 || currentUser.getCurrentUser().getOpenedRedPacketCount() > 0)){

            tvRedPacket.setVisibility(View.VISIBLE);

            int count = currentUser.getCurrentUser().getOpenedRedPacketCount() + currentUser.getCurrentUser().getUnOpenedRedPacketCount();

            if (count == 1){

                tvRedPacket.setText(String.format("%d packet received", count));

            } else {

                tvRedPacket.setText(String.format("%d packets received", count));
            }

        } else {

            tvRedPacket.setVisibility(View.GONE);
        }
    }

    @Click(R.id.cvTopUp)
    void topUpButtonClicked(){
        TopUpActivity_.intent(context).startForResult(REQUEST_TOP_UP_PAYMENT);
    }

    @Click(R.id.cvFuzzieCard)
    void fuzzieButtonClicked(){
        GiftBoxActivity_.intent(context).start();
    }

    @Click(R.id.cvTickets)
    void ticketButtonClicked(){
        if (dataManager.getHome() != null && dataManager.getHome().getJackpot() != null && dataManager.getHome().getJackpot().isEnabled()){
            JackpotTicketsActivity_.intent(context).start();
        }
    }

    @Click(R.id.cvRedPacket)
    void redPacketButtonClicked(){
        RedPacketHistoryActivity_.intent(context).start();
    }
}
