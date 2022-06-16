package sg.com.fuzzie.android.ui.auth;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import timber.log.Timber;
import android.widget.TextView;

import com.universalvideoview.UniversalVideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.core.BaseActivity;

/**
 * Created by johnsonmaung on 12/8/16.
 */

@EActivity(R.layout.activity_landing)
public class LandingActivity extends BaseActivity {

    static final int REQUEST_FINISH = 1;

    @ViewById(R.id.tvJoin)
    TextView tvJoin;

    @ViewById(R.id.tvSignIn)
    TextView tvSignIn;

    @ViewById(R.id.videoView)
    UniversalVideoView videoView;

    @AfterViews
    public void calledAfterViewInjection() {

        String uriPath = "android.resource://" + getPackageName() + "/" + R.raw.login;
        Uri uri = Uri.parse(uriPath);

        videoView.setVideoURI(uri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        videoView.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        videoView.pause();

    }

    @Click(R.id.cvSignIn)
    void loginButtonClicked() {
        Timber.d("Login Button Clicked");
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivityForResult(intent, REQUEST_FINISH);
    }

    @Click(R.id.cvJoin)
    void joinButtonClicked() {
        Timber.d("Signup Button Clicked");
        Intent intent = new Intent(this, JoinActivity_.class);
        startActivityForResult(intent, REQUEST_FINISH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FINISH) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

}
