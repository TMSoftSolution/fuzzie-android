package sg.com.fuzzie.android.ui.jackpot;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by mac on 1/24/18.
 */

@EActivity(R.layout.activity_jackpot_set)
public class JackpotTicketSetActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvCount)
    TextView tvCount;

    @ViewById(R.id.cvSet)
    CardView cvSet;

    @ViewById(R.id.etCount)
    EditText etCount;

    int limitCount;
    int availableTicketsCount;
    int currentTicketsCount;

    int count;
    boolean isCutOffTime;
    boolean isGetMore;

    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_activity_jackpot_set));

        limitCount = dataManager.getHome().getJackpot().getNumberOfTicketsPerWeek();
        availableTicketsCount = currentUser.getCurrentUser().getAvailableJackpotTicketsCount();
        currentTicketsCount = currentUser.getCurrentUser().getCurrentJackpotTicketsCount();


        if (availableTicketsCount > 1){
            tvCount.setText(FuzzieUtils.fromHtml("You've got " + "<font color='#F8C736'><b>" + String.valueOf(availableTicketsCount) + " Jackpot tickets </b></font>" + "available."));
        } else {
            tvCount.setText(FuzzieUtils.fromHtml("You've got " + "<font color='#F8C736'><b>" + String.valueOf(availableTicketsCount) + " Jackpot ticket </b></font>" + "available."));
        }

        updateSetButton();
    }

    private void updateSetButton(){
        if (etCount.getText().toString().length() < 1){
            cvSet.setEnabled(false);
            cvSet.setCardBackgroundColor(getResources().getColor(R.color.loblolly));

        } else {
            cvSet.setEnabled(true);
            cvSet.setCardBackgroundColor(getResources().getColor(R.color.primary));

        }
    }

    @AfterTextChange(R.id.etCount)
    void countChanged(){

        if (etCount.getText().toString().startsWith("0")){
            etCount.setText("");
        }

        updateSetButton();

    }

    @Click(R.id.cvSet)
    void setButtonClicked(){

        count = Integer.parseInt(etCount.getText().toString());

        if (count > availableTicketsCount){

            isGetMore = true;
            showFZAlert("OOPS!", "You don't have enough Jackpot tickets available.", "GET MORE TICKETS", "GOT IT", R.drawable.ic_bear_dead_white);

        } else if (count + currentTicketsCount > limitCount){

            showFZAlert("OOPS!", "You are exceeding " + limitCount + " Jackpot tickets for this draw.", "GOT IT", "", R.drawable.ic_bear_dead_white);

        } else if (FuzzieUtils.isCuttingOffLiveDraw(context)){

            isCutOffTime = true;
            showFZAlert("CUT OF TIME", "The cut off time for this draw has been reached. Your Jackpot ticket will be valid only for next week's draw. Do you wish to continue?", "OK, GOT IT", "No, Cancel", R.drawable.ic_bear_baby_white);

        } else {

            JackpotDigitEnterActivity_.intent(context).count(count).start();

        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (isCutOffTime){

            isCutOffTime = false;

            JackpotDigitEnterActivity_.intent(context).count(count).start();

        } else if (isGetMore){

            isGetMore = false;

            Intent intent = new Intent(context, MainActivity_.class);
            intent.putExtra("go_jackpot_coupon_list", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        }
    }
}
