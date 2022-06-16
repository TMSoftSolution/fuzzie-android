package sg.com.fuzzie.android.ui.rate;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by mac on 8/2/17.
 */

@EActivity(R.layout.activity_rate_experience)
public class RateExperienceActivity extends BaseActivity {

    @Bean
    FuzzieData dataManager;

    @AfterViews
    public void calledAfterViewInjection() {

    }

    @Click(R.id.ivClose)
    void closeButtonClicked(){
        dataManager.isShowingRatePage = false;
        finish();
    }

    @Click(R.id.cvGreat)
    void greatButtonClicked(){
        RateThanksActivity_.intent(context).start();
        finish();
    }

    @Click(R.id.cvNot)
    void sorryButtonClicked(){
        RateSorryActivity_.intent(context).start();
        finish();
    }
}
