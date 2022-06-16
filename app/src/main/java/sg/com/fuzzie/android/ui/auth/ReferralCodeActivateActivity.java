package sg.com.fuzzie.android.ui.auth;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.FuzzieUtils;

@EActivity(R.layout.activity_referral_code_activate)
public class ReferralCodeActivateActivity extends BaseActivity {

    @Extra
    double creditsExtra = 5.0;

    @ViewById(R.id.tvCredits)
    TextView tvCredits;

    @AfterViews
    public void calledAfterViewInjection() {

        tvCredits.setText(FuzzieUtils.getFormattedPaymentSuccessCashbackValue(context, creditsExtra, 0.6667f));

    }

    @Click(R.id.cvGotIt)
    void gotButtonClicked(){

        finish();

    }
}
