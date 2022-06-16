package sg.com.fuzzie.android.ui.club;

import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

@EActivity(R.layout.activity_club_subscribe)
public class ClubSubscribeActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    @ViewById(R.id.header)
    View header;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.tvValid)
    TextView tvValid;

    @AfterViews
    public void callAfterViewInjection(){

        final int thresholdY = (int)(ViewUtils.getScreenWidth(context) * 0.9);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > thresholdY){

                    header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


                } else {

                    header.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                }

            }
        });

        tvPrice.setText(String.format("%s/year", FuzzieUtils.getFormattedValue(currentUser.getCurrentUser().getFuzzieClub().getMembershipPrice(), 1)));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM YY");
        tvValid.setText(String.format("Valid from today till %s", timeFormat.format(nextYear)));

}


    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvSubscribe)
    void subscribeButtonClicked(){
        ClubSubscribePaymentActivity_.intent(context).start();
    }

    @Click(R.id.btnFAQ)
    void faqButtonClicked(){

        JackpotLearnMoreActvity_.intent(context).clubFaqs(true).start();
    }

    @Click(R.id.btnTerms)
    void termsButtonClicked(){

        JackpotLearnMoreActvity_.intent(context).clubTerms(true).start();
    }
}
