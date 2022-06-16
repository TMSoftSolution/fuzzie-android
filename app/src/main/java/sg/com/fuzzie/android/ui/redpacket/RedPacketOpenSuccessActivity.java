package sg.com.fuzzie.android.ui.redpacket;

import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CustomTypefaceSpan;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_OPEN;

/**
 * Created by movdev on 3/2/18.
 */

@EActivity(R.layout.activity_red_packet_open_success)
public class RedPacketOpenSuccessActivity extends BaseActivity {

    private RedPacket redPacket;

    @Extra
    String redPacketId;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @ViewById(R.id.tvTicketCount)
    TextView tvTicketCount;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @ViewById(R.id.tvGotIt)
    TextView tvGotIt;

    @AfterViews
    public void calledAfterViewInjection() {

        if (redPacketId == null) return;

        redPacket = dataManager.getRedPacketWithId(redPacketId);
        if (redPacket == null) return;


        if (redPacket.getValue() > 0){

            tvCredits.setText(FuzzieUtils.getFormattedValue(redPacket.getValue(), 0.75f));

        } else {

            ViewUtils.gone(findViewById(R.id.llCredits));

        }

        if (redPacket.getTicketCount() > 0){

            tvTicketCount.setText("X" + redPacket.getTicketCount());

        } else {

            ViewUtils.gone(findViewById(R.id.llTickets));
        }

        updateNote();

        if (redPacket.isChampion()){
            tvGotIt.setText("CONTINUE");
        }
    }

    private void updateNote(){


        if (redPacket.getValue() > 0 && redPacket.getTicketCount() > 0){

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
            TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);

            Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
            TypefaceSpan typefaceSpan1 = new CustomTypefaceSpan("", typeface1);

            SpannableString body1 = new SpannableString(" Fuzzie Credits and ");
            SpannableString body2 = new SpannableString(" have been added to your account.");
            SpannableString creditsSpan  = new SpannableString(FuzzieUtils.getFormattedValue(redPacket.getValue(), 0.75f));
            creditsSpan.setSpan(typefaceSpan, 0, creditsSpan.length() , 0);

            SpannableString ticketSpan;
            if (redPacket.getTicketCount() == 1){
                ticketSpan = new SpannableString("1 Jackpot Ticket");
            } else {
                ticketSpan = new SpannableString(String.format("%d Jackpot Tickets", redPacket.getTicketCount()));
            }
            ticketSpan.setSpan(typefaceSpan1, 0, ticketSpan.length(), 0);


            tvBody.setText(TextUtils.concat(creditsSpan , body1, ticketSpan, body2));

        } else {

            if (redPacket.getValue() > 0){

                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
                TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);

                SpannableString body = new SpannableString(" Fuzzie Credits have been added to your account.");

                SpannableString creditsSpan  = new SpannableString(FuzzieUtils.getFormattedValue(redPacket.getValue(), 0.75f));
                creditsSpan.setSpan(typefaceSpan, 0, creditsSpan.length() , 0);

                tvBody.setText(TextUtils.concat(creditsSpan , body));

            } else if (redPacket.getTicketCount() > 0){

                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
                TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);

                SpannableString ticketSpan;
                if (redPacket.getTicketCount() == 1){
                    ticketSpan = new SpannableString("1 Jackpot Ticket");
                } else {
                    ticketSpan = new SpannableString(String.format("%d Jackpot Tickets", redPacket.getTicketCount()));
                }
                ticketSpan.setSpan(typefaceSpan, 0, ticketSpan.length(), 0);

                SpannableString body = new SpannableString(" have been added to your account.");
                tvBody.setText(TextUtils.concat(ticketSpan , body));
            }
        }
    }

    @Click(R.id.cvGotIt)
    void continueButtonClicked(){

        if (redPacket != null){

            if (redPacket.isChampion()){

                RedPacketChampionActivity_.intent(context).redPacketId(redPacketId).startForResult(REQUEST_RED_PACKET_OPEN);

            } else {

                setResult(RESULT_OK, null);
                finish();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RED_PACKET_OPEN){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
