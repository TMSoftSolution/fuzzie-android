package sg.com.fuzzie.android.ui.payments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.PaymentCardType;
import sg.com.fuzzie.android.utils.ViewUtils;
import timber.log.Timber;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.shop.MiniBannerItem;
import sg.com.fuzzie.android.ui.activate.CodeActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.views.EditTextBackEvent;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_PAYMENT_METHODS;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by mac on 10/10/17.
 */

@EActivity(R.layout.activity_top_up_payment)
public class TopUpPaymentActivity extends BaseActivity {

    static final int REQUEST_PAYMENT_METHOD = 1;

    FastItemAdapter bannerAdapter;

    boolean deletePromoCode = false;
    boolean isActiveCode = false;

    PaymentMethod paymentMethod;
    double totalPrice = 0.0;
    String promoCode = null;
    double promoCashbackPercentage = 0.0;
    double promoCashback = 0.0;

    @Extra
    int topUpPrice;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTopUpPrice)
    TextView tvTopUpPrice;

    @ViewById(R.id.rvBanner)
    RecyclerView rvBanner;

    @ViewById(R.id.cvAddPaymentMethod)
    CardView cvAddPaymentMethod;

    @ViewById(R.id.cvPaymentMethod)
    CardView cvPaymentMethod;

    @ViewById(R.id.ivPaymentCard)
    ImageView ivPaymentCard;

    @ViewById(R.id.tvCardNumber)
    TextView tvCardNumber;

    @ViewById(R.id.llFuzzieUsed)
    LinearLayout llFuzzieUsed;

    @ViewById(R.id.tvFuzzieUsed)
    TextView tvFuzzieUsed;

    @ViewById(R.id.tvPayable)
    TextView tvPayable;

    @ViewById(R.id.llEarn)
    LinearLayout llEarn;

    @ViewById(R.id.tvEarn)
    TextView tvEarn;

    @ViewById(R.id.llAddPromoCode)
    LinearLayout llAddPromoCode;

    @ViewById(R.id.etPromoCode)
    EditTextBackEvent etPromoCode;

    @ViewById(R.id.llDeletePromoCode)
    LinearLayout llDeletePromoCode;

    @ViewById(R.id.tvPromoCode)
    TextView tvPromoCode;

    @AfterViews
    public void calledAfterViewInjection() {

        dataManager.cardTempAdded = false;
        dataManager.cardTemp = null;

        setupUI(findViewById(R.id.activity_top_up_payment_id));

        totalPrice = topUpPrice;

        tvTopUpPrice.setText("S$" + String.valueOf(topUpPrice));
        tvPayable.setText("S$" + String.valueOf(topUpPrice));

        updateCashbackEarn();

        checkPaymentMethod();

        llDeletePromoCode.setVisibility(View.GONE);
        etPromoCode.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent ctrl, String text) {
                processPromoCode();
            }
        });

        etPromoCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    processPromoCode();
                    return true;
                }
                return false;
            }
        });

        if (dataManager.getMiniBanners("Bank").size() > 0){
            bannerAdapter = new FastItemAdapter();
            rvBanner.setLayoutManager(new LinearLayoutManager(context));
            rvBanner.setAdapter(bannerAdapter);
            bannerAdapter.add(new MiniBannerItem(dataManager.getMiniBanners("Bank")));
        }
    }

    void updateCashbackEarn() {
        if (promoCashback > 0) {
            llEarn.setVisibility(View.VISIBLE);
            tvEarn.setText("GET +S$" + String.format("%.0f",promoCashback) + " CASHBACK" );
        } else {
            llEarn.setVisibility(View.GONE);
        }
    }

    void checkPaymentMethod(){

        if (dataManager.getSelectedPaymentMethod() != null && !dataManager.getSelectedPaymentMethod().getCardType().equals("ENETS")) { // Choose default if already have.

            paymentMethod = dataManager.getSelectedPaymentMethod();

        } else if (dataManager.getPaymentMethods() != null) {

            if (dataManager.getPaymentMethods().size() > 0) {

                paymentMethod = dataManager.getPaymentMethods().get(0);

            } else {

                paymentMethod = null;
            }

        } else {

            paymentMethod = null;
            fetchPaymentMethods();
        }

        setupPaymentMethod();
    }

    void setupPaymentMethod() {

        if (dataManager.cardTempAdded) {

            if (dataManager.cardTemp != null) {

                ViewUtils.gone(cvAddPaymentMethod);
                ViewUtils.visible(cvPaymentMethod);

                ivPaymentCard.setImageResource(FuzzieUtils.getPaymentCardIcon(FuzzieUtils.getPaymentCardType(dataManager.cardTemp.getCardNumber())));
                tvCardNumber.setText("●●●●●●● " + dataManager.cardTemp.getCardNumber().substring(dataManager.cardTemp.getCardNumber().length() - 4));

            } else {

                ViewUtils.visible(cvAddPaymentMethod);
                ViewUtils.gone(cvPaymentMethod);
            }


        } else {

            if (paymentMethod == null) {

                ViewUtils.visible(cvAddPaymentMethod);
                ViewUtils.gone(cvPaymentMethod);

            } else {

                ViewUtils.gone(cvAddPaymentMethod);
                ViewUtils.visible(cvPaymentMethod);

                Picasso.get().load(paymentMethod.getImageUrl()).into(ivPaymentCard);
                tvCardNumber.setText("●●●●●●● " + paymentMethod.getLast4());

            }
        }
    }

    void fetchPaymentMethods() {

        Call<List<PaymentMethod>> call = FuzzieAPI.APIService().getPaymentMethods(currentUser.getAccesstoken());
        call.enqueue(new Callback<List<PaymentMethod>>() {
            @Override
            public void onResponse(Call<List<PaymentMethod>> call, Response<List<PaymentMethod>> response) {

                Timber.d("Payment Method Loaded: " + response.code());

                if (response.code() == 200 && response.body() != null) {
                    dataManager.setPaymentMethods(response.body());

                    if (response.body().size() > 0) {
                        paymentMethod = response.body().get(0);
                        setupPaymentMethod();
                    }
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  TopUpPaymentActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<PaymentMethod>> call, Throwable t) {
                Timber.d("Payment Method Error: " + t.getLocalizedMessage());
            }
        });
    }

    void processPromoCode() {

        Timber.d("Promo Code Entered: " + etPromoCode.getText());

        hideKeyboard();
        etPromoCode.clearFocus();
        final String code = etPromoCode.getText().toString().toUpperCase();
        if (code.length() > 0) {

            etPromoCode.setText("");

            showPopView("VALIDATING", "", R.drawable.bear_craft, true);

            Call<JsonObject> call;

            if (paymentMethod != null && paymentMethod.getToken() != null && !paymentMethod.getToken().equals("")){

                call = FuzzieAPI.APIService().validatePromoCode(currentUser.getAccesstoken(), code, paymentMethod.getToken());

            } else {

                call = FuzzieAPI.APIService().validatePromoCode(currentUser.getAccesstoken(), code);
            }

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    hidePopView();

                    if (response.code() == 200 && response.body() != null
                            && response.body().getAsJsonPrimitive("code") != null
                            && response.body().getAsJsonPrimitive("cash_back_percentage") != null) {

                        promoCode = response.body().getAsJsonPrimitive("code").getAsString();
                        promoCashbackPercentage = response.body().getAsJsonPrimitive("cash_back_percentage").getAsDouble();
                        promoCashback = promoCashbackPercentage / 100 * totalPrice;

                        llDeletePromoCode.setVisibility(View.VISIBLE);
                        tvPromoCode.setText("+S$" + String.format("%.0f", promoCashback));
                        llAddPromoCode.setVisibility(View.GONE);
                        updateCashbackEarn();

                        showPopView("SUCCESS!", "+" + String.format("%.0f", promoCashbackPercentage) + "% cashback!", R.drawable.bear_good, false, "DONE");

                    } else if ((response.code() == 409 || response.code() == 411 || response.code() == 412 || response.code() == 416) && response.errorBody() != null){

                        String errorMessage = "Unknown error occurred: " + response.code();

                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()){
                                errorMessage = jsonObject.get("error").getAsString();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (response.code() == 411 || response.code() == 416){
                            showFZAlert("Oops!", errorMessage, "GO TO FUZZIE CODE", "Cancel", R.drawable.ic_bear_dead_white);
                            isActiveCode = true;

                        } else {

                            showFZAlert("Oops!", errorMessage, "TRY AGAIN", "", R.drawable.ic_bear_dead_white);
                        }



                    } else {

                        showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    hidePopView();
                    showFZAlert("Oops!",  t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }

            });

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    @Click(R.id.cvAddPaymentMethod)
    void addPaymentMethodClicked() {

        if (dataManager.getPaymentMethods() != null && dataManager.getPaymentMethods().size() > 0){

            PaymentMethodsActivity_.intent(context).fromPaymentPage(true).startForResult(REQUEST_PAYMENT_METHOD);

        } else {

            AddPaymentMethodActivity_.intent(context).fromPaymentPage(true).startForResult(REQUEST_PAYMENT_METHOD);

        }
    }

    @Click(R.id.cvPaymentMethod)
    void paymentMethodClicked() {
        PaymentMethodsActivity_.intent(context).fromPaymentPage(true).showNets(false).startForResult(REQUEST_PAYMENT_METHOD) ;
    }

    @Click(R.id.tvDelete)
    void deletePromoCode() {

        deletePromoCode = true;
        showFZAlert("ARE YOU SURE?", "Are you sure you want to delete this promo code?", "YES, DELETE", "No, Cancel", R.drawable.ic_bear_baby_white);
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.activate_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        WebActivity_.intent(context).titleExtra("FAQ").urlExtra("http://fuzzie.com.sg/faq.html").start();
                        break;
                    case 1:
                        emailSupport();
                        break;
                    case 2:
                        facebookSupport();
                        break;

                    default:
                        break;
                }
            }
        }).show();
    }

    void emailSupport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","support@fuzzie.com.sg", null));
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.cvPayTheBear)
    void payButtonClicked() {

        if (dataManager.cardTempAdded){

            if (FuzzieUtils.getPaymentCardType(dataManager.cardTemp.getCardNumber()) == PaymentCardType.PAYMENT_CARD_TYPE_NONE){

                showFZAlert("OOPS!", "Our payment gateway is not able to accept your card. Change to another and tyr again.", "GOT IT", "", R.drawable.ic_bear_dead_white);

            } else {

                processPayment(null);

            }

        } else {

            if (paymentMethod == null) {

                showFZAlert("OOPS!", "Please choose a payment method first.", "GOT IT", "", R.drawable.ic_bear_dead_white);

            } else {

                processPayment(paymentMethod.getToken());

            }
        }

    }

    private void processPayment(final String paymentToken) {

        String paymentMode = "CREDIT_CARD";

        Call<JsonObject> call = null;

        if (dataManager.cardTempAdded){

            call = FuzzieAPI.APIService().purchaseTopUp(currentUser.getAccesstoken(), totalPrice, dataManager.cardTemp.getCardNumber(), dataManager.cardTemp.getExpDate(), dataManager.cardTemp.getCvv(),  paymentMode, promoCode);

        } else {

            call = FuzzieAPI.APIService().purchaseTopUp(currentUser.getAccesstoken(), totalPrice, paymentToken,  paymentMode, promoCode);
        }

        displayProgressDialog("Processing...");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissProgressDialog();

                handlePaymentResponse(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });

    }

    private void handlePaymentResponse(Response<JsonObject> response){

        if (response.code() == 200 && response.body() != null){

            if (dataManager.cardTempAdded) {

                Intent refreshIntent = new Intent(BROADCAST_REFRESH_PAYMENT_METHODS);
                LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

                dataManager.cardTempAdded = false;
                dataManager.cardTemp = null;
            }

            double cashback = response.body().get("cash_back").getAsDouble();
            PaymentSuccessActivity_.intent(context).isTopUp(true).topUpValue(totalPrice).cashbackExtra(cashback).startForResult(REQUEST_TOP_UP_PAYMENT);

        } else if ((response.code() == 406 || response.code() == 409 || response.code() == 410 || response.code() == 422) && response.errorBody() != null){

            try {
                String jsonString = response.errorBody().string();
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()){
                    showFZAlert("Oops!", jsonObject.get("error").getAsString(), "OK", "", R.drawable.ic_bear_dead_white);
                } else {
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (response.code() == 417){
            logoutUser(currentUser, dataManager,  TopUpPaymentActivity.this);
        } else {
            showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
        }
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (deletePromoCode){

            deletePromoCode = false;

            etPromoCode.setText("");
            promoCode = null;
            promoCashbackPercentage = 0.0;
            promoCashback = 0.0;
            llDeletePromoCode.setVisibility(View.GONE);
            llAddPromoCode.setVisibility(View.VISIBLE);
            updateCashbackEarn();

        } else if (isActiveCode){

            isActiveCode = false;
            CodeActivity_.intent(context).start();

        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertOkButtonClicked();
        deletePromoCode = false;
        isActiveCode = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT_METHOD) {
            if (resultCode == RESULT_OK) {

                checkPaymentMethod();

            }

        } else if (requestCode == REQUEST_TOP_UP_PAYMENT){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    protected void setupUI(View view){
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof android.widget.EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (etPromoCode.getText().length() == 0){
                        hideKeyboard();
                        etPromoCode.clearFocus();
                    }
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
}
