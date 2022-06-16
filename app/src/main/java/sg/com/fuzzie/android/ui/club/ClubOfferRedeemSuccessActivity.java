package sg.com.fuzzie.android.ui.club;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.FuzzieData;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;


@EActivity(R.layout.activity_club_offer_redeem_success)
public class ClubOfferRedeemSuccessActivity extends BaseActivity {

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvOrderNumber)
    TextView tvOrderNumber;

    @AfterViews
    void callAfterViewInjection(){

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));

        if (dataManager.offer == null) return;

        StringBuilder s = new StringBuilder(dataManager.offer.getRedeemDetail().getOrderNumber());

        for (int i = s.length() - 3 ; i > 0 ; i-=3){
           s.insert(i, " ");
        }
        tvOrderNumber.setText(s.toString());
    }

    @Override
    public void onBackPressed() {

    }

    @Click(R.id.cvDone)
    void doneButtonClicked(){

        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        Intent intent1 = new Intent(BROADCAST_CHANGE_TAB);
        intent1.putExtra("tabId",R.id.tab_club);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
    }
}
