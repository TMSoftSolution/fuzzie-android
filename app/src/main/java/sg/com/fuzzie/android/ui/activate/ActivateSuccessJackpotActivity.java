package sg.com.fuzzie.android.ui.activate;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.jackpot.JackpotTicketSetActivity_;
import sg.com.fuzzie.android.utils.FuzzieData;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;

/**
 * Created by mac on 1/21/18.
 */

@EActivity(R.layout.activity_activate_success_jackpot)
public class ActivateSuccessJackpotActivity extends BaseActivity {

    @Extra
    String jsonExtra;

    @Bean
    FuzzieData dataManager;

    JsonObject jsonObject;

    int ticketCounts;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.tvCount)
    TextView tvCount;

    @ViewById(R.id.tvTickets)
    TextView tvTickets;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @AfterViews
    void callAfterViewInjection(){

        if (jsonExtra == null) return;

        JsonParser parser = new JsonParser();
        jsonObject = parser.parse(jsonExtra).getAsJsonObject();

        if (jsonObject.get("number_of_jackpot_tickets") != null){

            ticketCounts = jsonObject.get("number_of_jackpot_tickets").getAsInt();
        }

        if (ticketCounts > 1){

            tvTickets.setText("JACKPOT TICKETS");
            tvBody.setText("Your Jackpot tickets have been added to your wallet.");

        } else {

            tvTickets.setText("JACKPOT TICKET");
            tvBody.setText("Your Jackpot ticket has been added to your wallet.");

        }

        tvCount.setText("X" + String.valueOf(ticketCounts));

        Picasso.get()
                .load(R.drawable.bg_jackpot_pay_success)
                .placeholder(R.drawable.bg_jackpot_pay_success)
                .into(ivBackground);

        Intent refreshIntent = new Intent(BROADCAST_REFRESH_USER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

    }

    @Click(R.id.cvSet)
    void setButtonClicked(){

        JackpotTicketSetActivity_.intent(context).start();

    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){

        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        Intent intent1 = new Intent(BROADCAST_CHANGE_TAB);
        intent1.putExtra("tabId",R.id.tab_wallet);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
    }


}
