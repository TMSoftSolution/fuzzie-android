package sg.com.fuzzie.android;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.util.Base64;
import timber.log.Timber;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.sjl.foreground.Foreground;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by nurimanizam on 6/12/16.
 */

@EApplication public class FuzzieApplication extends Application {

  public Bitmap lastActivityBitmap;
  private  Context context;

  public Context getContext() {
    return context;
  }

  @Override public void onCreate() {
    super.onCreate();

    context = getApplicationContext();

    Fabric.with(this, new Crashlytics(), new Answers());
    FacebookSdk.sdkInitialize(this);
    AppEventsLogger.activateApp(this);

    generateKeyHashForFacebook();

    if (BuildConfig.DEBUG){
      Timber.plant(new Timber.DebugTree());
//      Picasso picasso = new Picasso.Builder(getApplicationContext())
//              .indicatorsEnabled(true)
//              .loggingEnabled(true) //add other settings as needed
//              .build();
//      Picasso.setSingletonInstance(picasso);
    }

    Foreground.init(this);
  }

  private void generateKeyHashForFacebook() {
    try {
      PackageInfo info = getPackageManager().getPackageInfo(
              "sg.com.fuzzie.android",
              PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Timber.d("KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {

    } catch (NoSuchAlgorithmException e) {

    }

  }

}
