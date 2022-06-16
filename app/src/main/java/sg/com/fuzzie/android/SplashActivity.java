package sg.com.fuzzie.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

/**
 * Created by nurimanizam on 14/12/16.
 */

@EActivity
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity_.class);
        startActivity(intent);
        finish();
    }
}
