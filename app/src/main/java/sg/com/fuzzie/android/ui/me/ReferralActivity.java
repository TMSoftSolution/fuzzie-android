package sg.com.fuzzie.android.ui.me;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.club.ClubReferralActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;

@EActivity(R.layout.activity_referral)
public class ReferralActivity extends BaseActivity {


    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @ViewById(R.id.tvTickets)
    TextView tvTickets;


    @AfterViews
    public void calledAfterViewInjection() {

        if (currentUser.getCurrentUser().getReferralRewards() != null){

            tvCredits.setText(String.format("S$%.2f", currentUser.getCurrentUser().getReferralRewards().getCredits()).replace(".00", ""));
            tvTickets.setText(String.format("X %d", currentUser.getCurrentUser().getReferralRewards().getTickets()));

        } else {

            tvCredits.setText("S$0");
            tvTickets.setText("X 0");
        }
    }

    @Click(R.id.vInviteFuzzie)
    void fuzzieInviteButtonClicked(){

        InviteFriendsActivity_.intent(context).start();
    }

    @Click(R.id.vInviteClub)
    void clubInviteButtonClicked(){

        ClubReferralActivity_.intent(context).start();
    }


    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}

