package sg.com.fuzzie.android.ui.club;

import android.content.Intent;
import android.provider.Settings;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

@EActivity(R.layout.activity_location_switch)
public class LocationEnableActivity extends BaseActivity {

    @AfterViews
    void callAfterViewInjection(){

    }

    @Click(R.id.cvEnable)
    void enableButtonClicked(){

        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        finish();
    }

    @Click(R.id.btnLater)
    void laterButtonClicked(){

        finishWithTransition();

    }

    private void finishWithTransition(){

        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);
    }

    @Override
    public void onBackPressed() {

        finishWithTransition();
    }
}
