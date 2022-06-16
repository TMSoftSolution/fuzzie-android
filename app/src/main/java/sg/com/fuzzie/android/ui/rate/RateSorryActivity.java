package sg.com.fuzzie.android.ui.rate;

import android.content.Intent;
import android.net.Uri;

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

@EActivity(R.layout.activity_rate_sorry)
public class RateSorryActivity extends BaseActivity {

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

    @Click(R.id.cvFacebook)
    void facebookButtonClicked(){
        dataManager.isShowingRatePage = false;
        facebookSupport();
        finish();
    }

    @Click(R.id.cvEmail)
    void emailButtonClicked(){
        dataManager.isShowingRatePage = false;
        emailSupport();
        finish();
    }

    void emailSupport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","support@fuzzie.com.sg", null));
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback for Fuzzie");
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
