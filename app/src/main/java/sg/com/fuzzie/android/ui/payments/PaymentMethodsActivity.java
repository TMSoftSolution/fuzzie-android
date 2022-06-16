package sg.com.fuzzie.android.ui.payments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.utils.FuzzieUtils;
import timber.log.Timber;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.payment.AddCreditCardItem;
import sg.com.fuzzie.android.items.payment.PaymentMethodItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.NETSPAY_APP_PACKAGE_NAME;

/**
 * Created by nurimanizam on 17/12/16.
 */

@EActivity(R.layout.activity_payment_method)
public class PaymentMethodsActivity extends BaseActivity {

    static final int REQUEST_PAYMENT_METHOD = 1;

    private boolean deleteMode = false;
    private boolean showDeleteMode = false;
    private boolean showNetsMode = false;
    private PaymentMethod selectedMethod;
    private int selectedMethodPosition;

    FastItemAdapter paymentAdapter;

    @Extra
    boolean fromPaymentPage;

    @Extra
    boolean showNets = true;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvEdit)
    TextView tvEdit;

    @ViewById(R.id.rvPaymentMethods)
    RecyclerView rvPaymentMethods;


    @AfterViews
    public void calledAfterViewInjection() {

        if (dataManager.getSelectedPaymentMethod() == null && dataManager.getPaymentMethods() != null && dataManager.getPaymentMethods().size() > 0){
            dataManager.setSelectedPaymentMethod(dataManager.getPaymentMethods().get(0));
        }

        paymentAdapter = new FastItemAdapter<>();
        paymentAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {

                if (item instanceof AddCreditCardItem){

                    AddPaymentMethodActivity_.intent(context).fromPaymentPage(fromPaymentPage).startForResult(REQUEST_PAYMENT_METHOD);

                } else {

                    final PaymentMethodItem paymentMethodItem = (PaymentMethodItem)item;
                    final PaymentMethod paymentMethod = paymentMethodItem.getPaymentMethod();

                    if (deleteMode) {

                        if (!paymentMethod.getCardType().equals("ENETS")){

                            showFZAlert("ARE YOU SURE?", "Are you sure you want to delete this payment method?", "YES, DELETE", "No, Cancel", R.drawable.ic_bear_baby_white);
                            showDeleteMode = true;
                            selectedMethod = paymentMethod;
                            selectedMethodPosition = position;
                        }

                    } else {

                        if (paymentMethod.getCardType().equals("ENETS") && !FuzzieUtils.isNetsInstalled(context)){

                            showFZAlert("SET UP NETSPay", "Would you like to set up NETSPay account now?", "YES, SET UP NOW", "No, Cancel", R.drawable.ic_bear_baby_white);
                            showNetsMode = true;

                        } else {

                            dataManager.setSelectedPaymentMethod(paymentMethod);

                            if (fromPaymentPage){

                                dataManager.cardTemp = null;
                                dataManager.cardTempAdded = false;

                                String methodJson = PaymentMethod.toJSON(paymentMethod);

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("Method", methodJson);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();

                            } else {

                                setupPaymentMethods();
                            }
                        }
                    }
                }

                return true;
            }
        });

        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(context));
        rvPaymentMethods.setAdapter(paymentAdapter);

        setupPaymentMethods();
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (showDeleteMode){

            deletePaymentMethod();
            showDeleteMode = false;

        } else if (showNetsMode){

            downLoadNets();
            showNetsMode = false;
        }
    }

    private void deletePaymentMethod(){

        Timber.d("Delete Tapped");

        if (selectedMethod == null) return;

        displayProgressDialog("Deleting...");
        Call<ResponseBody> call = FuzzieAPI.APIService().deletePaymentMethod(currentUser.getAccesstoken(), selectedMethod.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();

                if (response.code() == 200
                        && response.body() != null) {

                    paymentAdapter.remove(selectedMethodPosition);
                    dataManager.getPaymentMethods().remove(selectedMethod);
                    selectedMethod = null;
                    if (dataManager.getPaymentMethods().size() > 0){
                        dataManager.setSelectedPaymentMethod(dataManager.getPaymentMethods().get(0));
                    } else {
                        dataManager.setSelectedPaymentMethod(null);
                    }
                    editButtonClicked();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  PaymentMethodsActivity.this);
                } else {
                    selectedMethod = null;
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                selectedMethod = null;
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }

        });
    }

    private void downLoadNets(){

        // you can also use BuildConfig.APPLICATION_ID
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + NETSPAY_APP_PACKAGE_NAME));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+NETSPAY_APP_PACKAGE_NAME));
            context.startActivity(webIntent);
        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        showDeleteMode = false;
        showNetsMode = false;
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Click(R.id.tvEdit)
    void editButtonClicked() {

        deleteMode = !deleteMode;

        if (deleteMode) {

            tvEdit.setText("Done");

        } else {

            tvEdit.setText("Edit");
        }

        setupPaymentMethods();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT_METHOD) {
            if (resultCode == RESULT_OK) {

                if (data.getStringExtra("Method") != null){

                    String methodJson = data.getStringExtra("Method");
                    Timber.d("Received New Payment Method: " + methodJson);
                    setupPaymentMethods();

                } else if (dataManager.cardTempAdded){

                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                }

            }
        }
    }

    void setupPaymentMethods() {

        if (paymentAdapter == null){

            paymentAdapter = new FastItemAdapter();

        } else {

            paymentAdapter.clear();

        }

        if (dataManager.getPaymentMethods() != null){
            for (PaymentMethod paymentMethod: dataManager.getPaymentMethods()) {
                paymentAdapter.add(new PaymentMethodItem(paymentMethod, deleteMode));
            }
        }

        if (showNets && !deleteMode){

            paymentAdapter.add(new PaymentMethodItem(FuzzieUtils.eNetsPaymentMethod(), deleteMode));

        }

        if (!deleteMode){

            paymentAdapter.add(new AddCreditCardItem());

        }
    }

}
