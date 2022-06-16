package sg.com.fuzzie.android.ui.jackpot;

import android.support.v4.app.ShareCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;

/**
 * Created by joma on 10/23/17.
 */

@EActivity(R.layout.activity_jackpot_live_won)
public class JackpotLiveWonActivity extends BaseActivity {

    @Extra
    int prize;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.ivBackground)
    ImageView ivBackground;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;


    @AfterViews
    public void calledAfterViewInjection() {

        Picasso.get()
                .load(R.drawable.code_success_red)
                .placeholder(R.drawable.code_success_red)
                .fit()
                .into(ivBackground);

        tvCredits.setText("S$" + String.valueOf(prize));
        tvBody.setText("Congrats "  + currentUser.getCurrentUser().getFirstName() + "! An email will be sent after the draw with details on how you can claim your prize.");

    }

    @Click(R.id.cvShare)
    void shareButtonClicked(){
        String shareBody = "I won S$" + prize + " cash on the Fuzzie Jackpot! You too can win awesome cash prizes on the Fuzzie Jackpot every week. Check it out: https://fuzzie.com.sg/jackpot";
        String subject = currentUser.getCurrentUser().getFirstName() + " won the Fuzzie Jackpot!";
        ShareCompat.IntentBuilder.from(this) .setType("text/plain") .setText(shareBody).setSubject(subject).startChooser();

    }

    @Click(R.id.cvBack)
    void backButtonClicked(){
        finish();
    }
}
