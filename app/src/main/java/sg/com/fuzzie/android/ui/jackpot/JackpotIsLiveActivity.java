package sg.com.fuzzie.android.ui.jackpot;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_START;

/**
 * Created by joma on 10/20/17.
 */

@EActivity(R.layout.activity_jackpot_is_live)
public class JackpotIsLiveActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @AfterViews
    public void calledAfterViewInjection() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_IS_START));
    }

    @Click(R.id.cvClose)
    void closeButtonClicked(){
        finish();
    }

    @Click(R.id.cvJoin)
    void joinButtonClicked(){
        Intent intent = new Intent(this, MainActivity_.class);
        intent.putExtra("jackpot_live", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
