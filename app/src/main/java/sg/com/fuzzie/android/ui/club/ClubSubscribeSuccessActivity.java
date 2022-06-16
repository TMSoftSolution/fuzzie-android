package sg.com.fuzzie.android.ui.club;

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

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_SUBSCRIBE_SUCCESS;

@EActivity(R.layout.activity_club_subscribe_success)
public class ClubSubscribeSuccessActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvBody)
    TextView tvBody;

    @AfterViews
    void callAfterViewInjection(){

        tvBody.setText(String.format("Great work, %s! You now have full access to all Fuzzie Club offers.", currentUser.getCurrentUser().getFirstName()));
    }

    @Click(R.id.tvStart)
    void startButtonClicked(){

        Intent intent = new Intent(context, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        Intent intent1 = new Intent(BROADCAST_CHANGE_TAB);
        intent1.putExtra("tabId",R.id.tab_club);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);

        Intent intent2 = new Intent(BROADCAST_CLUB_SUBSCRIBE_SUCCESS);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

    }

    @Override
    public void onBackPressed() {

    }
}
