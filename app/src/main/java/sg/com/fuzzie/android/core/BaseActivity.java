package sg.com.fuzzie.android.core;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.auth.LandingActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.views.FZAlertDialog;
import sg.com.fuzzie.android.views.FZClubExclusiveDialog;
import sg.com.fuzzie.android.views.FZLoaderView;
import sg.com.fuzzie.android.views.FZLocationNoMatchDialog;
import sg.com.fuzzie.android.views.FZLocationVerifyDialog;
import sg.com.fuzzie.android.views.FZPopView;
import sg.com.fuzzie.android.views.FZPopViewListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by johnsonmaung on 12/8/16.
 */

@EActivity
public class BaseActivity extends AppCompatActivity implements FZPopViewListener, FZAlertDialog.FZAlertDialogListener, FZClubExclusiveDialog.FZClubExclusiveDialogListener, FZLocationNoMatchDialog.FZLocationNoMatchDialogListener {

  private AppCompatActivity mActivity;
  protected ProgressDialog progress;
  protected FZLoaderView loaderView;
  protected FZPopView popView;
  protected FZAlertDialog alertDialog;
  protected FZClubExclusiveDialog clubExclusiveDialog;
  protected FZLocationVerifyDialog locationVerifyDialog;
  protected FZLocationNoMatchDialog locationNoMatchDialog;

  protected Context context;

  protected int getActionBarSize() {
    TypedValue typedValue = new TypedValue();
    int[] textSizeAttr = new int[]{R.attr.actionBarSize};
    int indexOfAttrTextSize = 0;
    TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
    int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
    a.recycle();
    return actionBarSize;
  }

  protected int getScreenHeight() {
    return findViewById(android.R.id.content).getHeight();
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    mActivity = this;
    context = this;

    loaderView = new FZLoaderView(mActivity);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }


  protected void showSuccessToast(){
    Toast.makeText(context, "Successfully updated profile", Toast.LENGTH_SHORT).show();
  }

  @UiThread
  protected void displayProgressDialog(String message) {
    progress = new ProgressDialog(mActivity);
    progress.setMessage(message);
    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progress.setIndeterminate(true);
    progress.setCancelable(false);
    if (!isFinishing()){
      progress.show();
    }
  }

  @UiThread
  protected void dismissProgressDialog() {
    if (progress != null && progress.isShowing()) {

      try {
        progress.dismiss();
      } catch (Exception e) {

      }

      progress = null;
    }
  }

  @UiThread
  protected void showLoader(){
      if (loaderView == null){
        loaderView = new FZLoaderView(mActivity);
      }
      if (!loaderView.isShowing()){
        loaderView.show();
      }
  }

  @UiThread
  protected void hideLoader(){
      if (loaderView != null && loaderView.isShowing()){
          try {
              loaderView.dismiss();
          } catch (Exception e) {

          }

          loaderView = null;
      }
  }

  @UiThread
  protected void showPopView(String title, String body, int imageRes){
    popView = new FZPopView(mActivity, title, body, imageRes);
    popView.setFZPopViewListener(this);
    if (!((Activity)context).isFinishing()){
      popView.show();

      final Handler handler  = new Handler();
      final Runnable runnable = new Runnable() {
        @Override
        public void run() {
          hidePopView();
        }
      };

      handler.postDelayed(runnable, 2000);
    }

  }

  @UiThread
  protected void showPopView(String title, String body, int imageRes, boolean showGif){
    popView = new FZPopView(mActivity, title, body, imageRes, showGif);
    popView.setFZPopViewListener(this);
    if (!isFinishing()){
      popView.show();
    }
  }

  @UiThread
  protected void showPopView(String title, String body, int imageRes, boolean showGif, String buttonTitle){
    popView = new FZPopView(mActivity, title, body, imageRes, showGif, buttonTitle);
    popView.setFZPopViewListener(this);
    if (!isFinishing()){
      popView.show();
    }
  }

  @UiThread
  protected void hidePopView(){
    if (popView != null && popView.isShowing()){
      try {
        popView.dismiss();
      } catch (Exception e) {

      }

      popView = null;

    }
  }

  @UiThread
  protected void showKeyboard() {
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    if (getCurrentFocus() != null) {
      imm.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
    }
  }

  @UiThread
  protected void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    if (getCurrentFocus() != null) {
      imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  @UiThread
  protected void setupUI(final View view){
    // Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText)) {
      view.setOnTouchListener(new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
          view.clearFocus();
          hideKeyboard();
          return false;
        }
      });
    }

    //If a layout container, iterate over children and seed recursion.
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        setupUI(innerView);
      }
    }
  }

  @UiThread
  protected void showAlert(String title, String message) {
    if(!isFinishing()) {
      AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
      alertDialog.setTitle(title);
      alertDialog.setMessage(message);
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {

        }
      });

      try {
        alertDialog.show();
      } catch (Exception e) {

      }
    }
  }

  /*
  * FZPopViewListner
  * */

  @Override
  public void okButtonClicked() {
      hidePopView();
  }

  @Override
  public void cancelButtonClicked() {

  }

  public void logoutUser(CurrentUser currentUser, FuzzieData dataManager, Activity activity) {

    currentUser.setAccesstoken(null);
    currentUser.setCurrentUser(null);

    dataManager.resetFuzzieData();

    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    mPrefs.edit().putBoolean("loggedin", false).apply();

    Intent intent = new Intent(activity, LandingActivity_.class);
    startActivity(intent);
    setResult(RESULT_OK, null);
    finish();
  }

  protected void showFZAlert(){
    if (alertDialog == null){
      alertDialog = new FZAlertDialog(mActivity);
      alertDialog.setListener(this);
    }

    if (!alertDialog.isShowing() && !isFinishing()){
      alertDialog.show();
    }
  }

  protected void showFZAlert(String title, String body, String okButtonTitle, String cancelButtonTitle, int imageRes){
    alertDialog = new FZAlertDialog(mActivity, title, body, okButtonTitle, cancelButtonTitle, imageRes);
    alertDialog.setListener(this);
    if (!alertDialog.isShowing() && !isFinishing()){
      alertDialog.show();
    }
  }

  protected void showFZAlert(String title, SpannableString body, String okButtonTitle, String cancelButtonTitle, int imageRes){
    alertDialog = new FZAlertDialog(mActivity, title, body, okButtonTitle, cancelButtonTitle, imageRes);
    alertDialog.setListener(this);
    if (!alertDialog.isShowing() && !isFinishing()){
      alertDialog.show();
    }
  }

  protected void hideFZAlert(){
    if (alertDialog != null && alertDialog.isShowing()){
      alertDialog.dismiss();
      alertDialog = null;
    }
  }

  @Override
  public void FZAlertOkButtonClicked() {

    hideFZAlert();

  }

  @Override
  public void FZAlertCancelButtonClicked() {
      hideFZAlert();
  }

  protected void showClubExclusiveAlert(){

    if (clubExclusiveDialog == null){

      clubExclusiveDialog = new FZClubExclusiveDialog(mActivity, R.style.FullScreenDialogStyle);
      clubExclusiveDialog.setListener(this);

    }

    if (!clubExclusiveDialog.isShowing() && !isFinishing()){

      clubExclusiveDialog.show();
    }
  }

  protected void hideClubExclusiveAlert(){

    if (clubExclusiveDialog != null && clubExclusiveDialog.isShowing()){

      clubExclusiveDialog.dismiss();
      clubExclusiveDialog = null;

    }
  }

  @Override
  public void FZClubExclusiveDialogExploreButtonClicked() {
    hideClubExclusiveAlert();
  }

  @Override
  public void FZClubExclusiveDialogCloseButtonClicked() {
    hideClubExclusiveAlert();
  }

  protected void showLocationVerifyDialog(){

    if (locationVerifyDialog == null){

      locationVerifyDialog = new FZLocationVerifyDialog(this);

    }

    if (!locationVerifyDialog.isShowing() && !isFinishing()){

      locationVerifyDialog.show();
    }
  }

  protected void hideLocationVerifyDialog(){

    if (locationVerifyDialog != null && locationVerifyDialog.isShowing()){

      locationVerifyDialog.dismiss();
      locationVerifyDialog = null;
    }
  }

  protected void showLocationNoMatchDialog(){

    if (locationNoMatchDialog == null){

      locationNoMatchDialog = new FZLocationNoMatchDialog(this);
      locationNoMatchDialog.setListener(this);

    }

    if (!locationNoMatchDialog.isShowing() && !isFinishing()){

      locationNoMatchDialog.show();
    }
  }

  protected void hideLocationNoMatchDialog(){

    if (locationNoMatchDialog != null && locationNoMatchDialog.isShowing()){

      locationNoMatchDialog.dismiss();
      locationNoMatchDialog = null;
    }
  }

  @Override
  public void FZLocationNoMatchDialogChangeButtonClicked() {

    hideLocationNoMatchDialog();
  }

  @Override
  public void FZLocationNoMatchDialogCancelButtonClicked() {

    hideLocationNoMatchDialog();
  }

  public void showClipboardPopup() {

    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.layout_code_copied);
    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int dialogWidth = (int)(displayMetrics.widthPixels * 0.9);
    int dialogHeight = (int)(dialogWidth*0.8);
    dialog.getWindow().setLayout(dialogWidth, dialogHeight);
    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
    // Hide after some seconds
    final Handler handler  = new Handler();
    final Runnable runnable = new Runnable() {
      @Override
      public void run() {
        if (dialog.isShowing()) {
          dialog.dismiss();
        }
      }
    };
    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
        handler.removeCallbacks(runnable);
      }
    });
    handler.postDelayed(runnable, 3000);
    dialog.show();
  }

}
