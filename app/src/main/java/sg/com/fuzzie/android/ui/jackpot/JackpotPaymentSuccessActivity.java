package sg.com.fuzzie.android.ui.jackpot;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.TextView;

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
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;

/**
 * Created by joma on 10/17/17.
 */

@EActivity(R.layout.activity_jackpot_payment_success)
public class JackpotPaymentSuccessActivity extends BaseActivity{

    @Extra
    boolean isPowerUpPack;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvDrawDate)
    TextView tvDrawDate;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @AfterViews
    public void calledAfterViewInjection() {

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));

        Picasso.get()
                .load(R.drawable.bg_jackpot_pay_success)
                .placeholder(R.drawable.bg_jackpot_pay_success)
                .into(ivBackground);

        tvTitle.setText("WELL DONE " + currentUser.getCurrentUser().getFirstName() + "!");

        if (isPowerUpPack){
            tvBody.setText("Your purchase and Jackpot tickets have been added to your wallet.");
        } else {
            tvBody.setText("Your voucher and Jackpot tickets have been added to your wallet.");
        }

        if (FuzzieUtils.isCuttingOffLiveDraw(context)){
            tvDrawDate.setText(TimeUtils.jackpotPaySuccessFormat(dataManager.getHome().getJackpot().getNextDrawTime()));
        } else {
            tvDrawDate.setText(TimeUtils.jackpotPaySuccessFormat(dataManager.getHome().getJackpot().getDrawTime()));
        }

    }

    @Click(R.id.cvJoin)
    void joinButtonClicked(){

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

    @Override
    public void onBackPressed() {

    }
}
