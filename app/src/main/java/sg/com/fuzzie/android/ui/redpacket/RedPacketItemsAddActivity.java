package sg.com.fuzzie.android.ui.redpacket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_ADD_CREDITS;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_ADD_MESSAGE;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_ADD_TICKETS;

/**
 * Created by movdev on 4/2/18.
 */

@EActivity(R.layout.activity_red_packet_items_add)
public class RedPacketItemsAddActivity extends BaseActivity {

    private int ticketCount;
    private double credits;
    private String message;
    private boolean isRandomMode;

    private boolean isDiscard;
    private boolean isNoMessage;


    @Extra
    int quantity;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvQuantity)
    TextView tvQuantity;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @ViewById(R.id.tvTickets)
    TextView tvTickets;

    @ViewById(R.id.tvMessage)
    TextView tvMessage;

    @ViewById(R.id.tvCreditsNote)
    TextView tvCreditsNote;

    @ViewById(R.id.tvTicketNote)
    TextView tvTicketNote;

    @ViewById(R.id.cvPrepare)
    CardView cvPrepare;

    @ViewById(R.id.tvMessageTitle)
    TextView tvMessageTitle;

    @ViewById(R.id.tvCreditsTitle)
    TextView tvCreditsTitle;

    @ViewById(R.id.tvTicketsTitle)
    TextView tvTicketsTitle;

    @AfterViews
    public void calledAfterViewInjection() {

        tvQuantity.setText(String.valueOf(quantity));

        initData();
        updateMessage();
        updateCredits();
        updateTickets();
        updatePrepareButton();
    }

    private void initData(){

        message = "";
        credits = 0;
        ticketCount = 0;
        isRandomMode = true;

    }

    private void updateMessage(){

        if (message != null && message.length() > 0){

            ViewUtils.visible(tvMessage);

            StringTokenizer tokenizer = new StringTokenizer(message);
            StringBuilder builder = new StringBuilder();
            while (tokenizer.hasMoreTokens()){
                builder.append(tokenizer.nextToken()).append(" ");
            }
            tvMessage.setText(builder.toString());

            ViewUtils.gone(findViewById(R.id.ivMessageArrow));
            ViewUtils.visible(findViewById(R.id.tvMessageEdit));

            tvMessageTitle.setText("YOUR MESSAGE");

        } else {

            ViewUtils.gone(tvMessage);
            ViewUtils.visible(findViewById(R.id.ivMessageArrow));
            ViewUtils.gone(findViewById(R.id.tvMessageEdit));

            tvMessageTitle.setText("ADD A MESSAGE");
        }
    }

    private void updateCredits(){

        tvCredits.setText(FuzzieUtils.getFormattedValue(credits, 0.75f));

        if (credits > 0){

            ViewUtils.gone(findViewById(R.id.ivCreditsArrow));
            ViewUtils.visible(findViewById(R.id.tvCreditsEdit));

            if (quantity == 1){

                tvCreditsNote.setText(String.format("S$%.2f", credits));

            } else {

                if (isRandomMode){

                    tvCreditsNote.setText("Split randomly");

                } else {

                    double credit = credits / quantity;
                    tvCreditsNote.setText(String.format("S$%.2f in each packet", credit));

                }
            }

            tvCreditsTitle.setText("FUZZIE CREDITS");

        } else {

            tvCreditsNote.setText(String.format("S$%.2f available", currentUser.getCurrentUser().getWallet().getCashableBalance()));
            ViewUtils.visible(findViewById(R.id.ivCreditsArrow));
            ViewUtils.gone(findViewById(R.id.tvCreditsEdit));

            tvCreditsTitle.setText("ADD FUZZIE CREDITS");

        }
    }

    private void updateTickets(){

        tvTickets.setText(String.valueOf(ticketCount * quantity));

        if (ticketCount > 0){

            if (ticketCount == 1){

                tvTicketNote.setText("1 Jackpot ticket per packet");

            } else {

                tvTicketNote.setText(String.format("%d Jackpot tickets per packet", ticketCount));

            }

            ViewUtils.gone(findViewById(R.id.ivTicketArrow));
            ViewUtils.visible(findViewById(R.id.tvTicketEdit));

            tvTicketsTitle.setText("JACKPOT TICKETS");

        } else {

            if (currentUser.getCurrentUser().getAvailableJackpotTicketsCount() > 1){

                tvTicketNote.setText(String.format("%d Jackpot tickets available", currentUser.getCurrentUser().getAvailableJackpotTicketsCount()));

            } else {

                tvTicketNote.setText(String.format("%d Jackpot ticket available", currentUser.getCurrentUser().getAvailableJackpotTicketsCount()));

            }

            ViewUtils.visible(findViewById(R.id.ivTicketArrow));
            ViewUtils.gone(findViewById(R.id.tvTicketEdit));

            tvTicketsTitle.setText("ADD JACKPOT TICKETS");
        }
    }

    private void updatePrepareButton(){

        if (ticketCount > 0 || credits > 0.0){

            cvPrepare.setEnabled(true);
            cvPrepare.setCardBackgroundColor(getResources().getColor(R.color.primary));

        } else {

            cvPrepare.setEnabled(false);
            cvPrepare.setCardBackgroundColor(getResources().getColor(R.color.loblolly));

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){

        if (credits != 0 || ticketCount != 0 ||message.length() != 0){

            showFZAlert("DISCARD LUCKY PACKET?", "Do you wish to discard your Lucky Packet?", "YES, DISCARD", "No, cancel", R.drawable.ic_normal_bear);
            isDiscard = true;

        } else {

            finish();

        }
    }

    @Click(R.id.cvPrepare)
    void prepareButtonClicked(){

        if (message.length() == 0){

            showFZAlert("NO MESSAGE?", "Are you sure you want to send your Lucky Packet without a message?", "YES, CONTINUE", "NO, CANCEL", R.drawable.ic_bear_dead_white);
            isNoMessage = true;

        } else {

            processPayment();

        }
    }

    @Click(R.id.cvCredits)
    void creditsButtonClicked(){
        RedPacketCreditAddActivity_.intent(context).quantity(quantity).credits(credits).isRandom(isRandomMode).startForResult(REQUEST_RED_PACKET_ADD_CREDITS);
    }

    @Click(R.id.cvMessage)
    void messageButtonClicked(){
        RedPacketAddMessageActivity_.intent(context).message(message).startForResult(REQUEST_RED_PACKET_ADD_MESSAGE);
    }

    @Click(R.id.cvTickets)
    void ticketButtonClicked(){
        RedPacketTicketAddActivity_.intent(context).quantity(quantity).count(ticketCount).startForResult(REQUEST_RED_PACKET_ADD_TICKETS);
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_RED_PACKET_ADD_MESSAGE){

                String messageExtra = data.getStringExtra("message");
                if (messageExtra != null){
                    message = messageExtra;
                    updateMessage();
                }

            } else if (requestCode == REQUEST_RED_PACKET_ADD_TICKETS){

                ticketCount = data.getIntExtra("count", 0);
                updateTickets();
                updatePrepareButton();

            } else if (requestCode == REQUEST_RED_PACKET_ADD_CREDITS){

                credits = data.getDoubleExtra("credits", 0);
                isRandomMode = data.getBooleanExtra("isRandom", true);
                updateCredits();
                updatePrepareButton();

            }

        }
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (isDiscard){

            finish();


        } else if (isNoMessage){

            processPayment();
            isNoMessage = false;

        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        isDiscard = false;
        isNoMessage = false;
    }

    @Override
    public void onBackPressed() {
        backButtonClicked();
    }

    private void processPayment(){

        if (credits == 0){

            processRedPacketPayment();

        } else {

            RedPacketPaymentActivity_.intent(context).isRandomMode(isRandomMode).message(message).quantity(quantity).total(credits).ticketCount(ticketCount).start();

        }
    }

    private void processRedPacketPayment(){

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        String spiltType = "";
        if (isRandomMode){
            spiltType = "RANDOM";
        } else {
            spiltType = "EQUAL";
        }

        FuzzieAPI.APIService().purchaseRedPacket(currentUser.getAccesstoken(), message, quantity, ticketCount, spiltType, credits).enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                hidePopView();

                if (response.code() == 200 && response.body() != null){

                    if (response.body().getRedPacketBundle() != null){

                        dataManager.redPacketBundle = response.body().getRedPacketBundle();
                        if (dataManager.getRedPacketBundles() != null){
                            dataManager.getRedPacketBundles().add(0, response.body().getRedPacketBundle());
                        }
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));

                    if (currentUser.getCurrentUser().getSettings().isBoughtFirstRedPacketBundle()){

                        RedPacketPaymentSuccessActivity_.intent(context).start();

                    } else {

                        RedPacketPaymentSuccessJackpotActivity_.intent(context).start();

                    }

                } else {

                    String errorMessage = "";
                    if (response.code() != 500 && response.errorBody() != null) {
                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()){
                                errorMessage = jsonObject.get("error").getAsString();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        errorMessage = "Unknown error occurred: " + response.code();
                    }

                    showFZAlert("Oops!", errorMessage, "GOT IT", "", R.drawable.ic_bear_dead_white);

                }

            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

                hidePopView();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });

    }
}
