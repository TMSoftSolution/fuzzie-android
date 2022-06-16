package sg.com.fuzzie.android.ui.gift;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

/**
 * Created by mac on 7/7/17.
 */

@EActivity(R.layout.activity_gift_it_how_it_wors)
public class GiftItHowItWorksActivity extends BaseActivity {

    @AfterViews
    public void calledAfterViewInjection() {

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
