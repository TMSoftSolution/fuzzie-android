package sg.com.fuzzie.android.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import sg.com.fuzzie.android.ui.auth.LandingActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.views.FZAlertDialog;
import sg.com.fuzzie.android.views.FZLoaderView;
import sg.com.fuzzie.android.views.FZCoachMarkDialog;
import sg.com.fuzzie.android.views.FZPopView;
import sg.com.fuzzie.android.views.FZPopViewListener;

import static android.app.Activity.RESULT_OK;

/**
 * Created by h3r0 on 8/26/14.
 */

@EFragment
public class BaseFragment extends Fragment implements FZPopViewListener, FZAlertDialog.FZAlertDialogListener, FZCoachMarkDialog.FZCoachMarkDialogListener{

  public AppCompatActivity mActivity;
  protected ProgressDialog progress;
  protected FZLoaderView loaderView;
  protected FZPopView popView;
  protected Context context;
  protected FZAlertDialog alertDialog;
  protected FZCoachMarkDialog coachMarkDialog;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = (AppCompatActivity)getActivity();
    context = getActivity();

    popView = new FZPopView(mActivity);
    popView.setFZPopViewListener(this);

    loaderView = new FZLoaderView(mActivity);
  }

  @Override
  public void onDestroy() {
    dismissProgressDialog();
    super.onDestroy();
  }

  @UiThread
  protected void displayProgressDialog(String message) {
    progress = new ProgressDialog(mActivity);
    progress.setMessage(message);
    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progress.setIndeterminate(true);
    progress.setCancelable(false);
    if (!mActivity.isFinishing()){
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
  protected void showPopView(){
    if (!mActivity.isFinishing()){
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
    }
  }

  protected void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    if (getActivity().getCurrentFocus() != null) {
      imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
  }

  @UiThread
  protected void showAlert(String title, String message) {
    if(!mActivity.isFinishing()) {
      android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(mActivity).create();
      alertDialog.setTitle(title);
      alertDialog.setMessage(message);
      alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
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

  }

  @Override
  public void cancelButtonClicked() {

  }

  public void logoutUser(CurrentUser currentUser, FuzzieData dataManager, Activity activity) {

    currentUser.setAccesstoken(null);
    currentUser.setCurrentUser(null);

    dataManager.resetFuzzieData();

    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
    mPrefs.edit().putBoolean("loggedin", false).apply();

    if (isAdded()){
      Intent intent = new Intent(activity, LandingActivity_.class);
      startActivity(intent);
      activity.setResult(RESULT_OK, null);
      activity.finish();
    }

  }

  protected void showFZAlert(){
    alertDialog = new FZAlertDialog(mActivity);
    alertDialog.setListener(this);
    if (!alertDialog.isShowing() && !mActivity.isFinishing()){
      alertDialog.show();
    }
  }

  protected void showFZAlert(String title, String body, String okButtonTitle, String cancelButtonTitle, int imageRes){
    alertDialog = new FZAlertDialog(mActivity, title, body, okButtonTitle, cancelButtonTitle, imageRes);
    alertDialog.setListener(this);
    if (!alertDialog.isShowing() && !mActivity.isFinishing()){
      alertDialog.show();
    }
  }

  protected void hideFZAlert(){
    if (alertDialog != null && alertDialog.isShowing()){
      alertDialog.dismiss();
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

  protected void showCoachMarkDialog(){
    if (coachMarkDialog == null){
      coachMarkDialog = new FZCoachMarkDialog(context);
      coachMarkDialog.setListener(this);
    }

    if (!coachMarkDialog.isShowing() && !mActivity.isFinishing()){
      coachMarkDialog.show();
    }
  }

  protected void hideCoachMarkDialog(){
    if (coachMarkDialog != null && coachMarkDialog.isShowing()){
      coachMarkDialog.dismiss();
    }
  }

  @Override
  public void coachMarkDialogTryButtonClicked() {

  }

  @Override
  public void coachMarkDialogClicked() {

  }
}