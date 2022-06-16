package sg.com.fuzzie.android.ui.club;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.nets.enets.exceptions.InvalidPaymentRequestException;
import com.nets.enets.listener.PaymentCallback;
import com.nets.enets.network.PaymentRequestManager;
import com.nets.enets.utils.result.NETSError;
import com.nets.enets.utils.result.PaymentResponse;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
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
import sg.com.fuzzie.android.ui.payments.PaymentMethodsActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.NetsUtils;
import sg.com.fuzzie.android.utils.PaymentCardType;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.EditTextBackEvent;
import sg.com.fuzzie.android.views.FZPopView;
import timber.log.Timber;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_PAYMENT_METHODS;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;
import static sg.com.fuzzie.android.utils.Constants.NETSPAY_API_KEY_ID;
import static sg.com.fuzzie.android.utils.Constants.NETSPAY_SECRET_KEY;

@EActivity(R.layout.activity_club_subscribe_payment)
public class ClubSubscribePaymentActivity extends BaseActivity {

    static final int REQUEST_PAYMENT_METHOD = 1;

    FastItemAdapter bannerAdapter;

    boolean deletePromoCode = false;
    boolean isActiveCode = false;

    PaymentMethod paymentMethod;
    double totalCashback;
    double totalPrice;
    double creditsToUse = 0.0;
    String promoCode;
    double promoCashbackPercentage = 0.0;
    double promoCashback = 0.0;
    double promoDiscount = 0.0f;
    String netsPayTransactionRef = "";

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.cvAddPaymentMethod)
    CardView cvAddPaymentMethod;

    @ViewById(R.id.cvPaymentMethod)
    CardView cvPaymentMethod;

    @ViewById(R.id.ivPaymentCard)
    ImageView ivPaymentCard;

    @ViewById(R.id.tvCardNumber)
    TextView tvCardNumber;

    @ViewById(R.id.rvBanner)
    RecyclerView rvBanner;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.tvSubtotal)
    TextView tvSubtotal;

    @ViewById(R.id.llFuzzieUsed)
    View llFuzzieUsed;

    @ViewById(R.id.tvFuzzieUsed)
    TextView tvFuzzieUsed;

    @ViewById(R.id.tvPayable)
    TextView tvPayable;

    @ViewById(R.id.cvUseCredits)
    CardView cvUseCredits;

    @ViewById(R.id.tvCreditsLeft)
    TextView tvCreditsLeft;

    @ViewById(R.id.ivFuzzieCredits)
    ImageView ivFuzzieCredits;

    @ViewById(R.id.ivUseCredits)
    ImageView ivUseCredits;

    @ViewById(R.id.llEarn)
    View llEarn;

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

    @ViewById(R.id.etReferralCode)
    EditText etReferralCode;

    @ViewById(R.id.tvMember)
    TextView tvMember;

    @AfterViews
    void callAfterViewInjection(){

        totalPrice = currentUser.getCurrentUser().getFuzzieClub().getMembershipPrice();
        totalCashback = currentUser.getCurrentUser().getFuzzieClub().getBonusForSubscriber();

        dataManager.cardTempAdded = false;
        dataManager.cardTemp = null;

        setupUI(findViewById(R.id.activity_club_subscribe_payment_id));

        tvMember.setText("Fuzzie Club membership " + Calendar.getInstance().get(Calendar.YEAR));

        tvPrice.setText(FuzzieUtils.getFormattedValue(totalPrice, 0.75f));
        tvSubtotal.setText(FuzzieUtils.getFormattedValue(totalPrice, 0.75f));

        setBanner();

        if (currentUser.getCurrentUser().getFuzzieClub().isMembership()){

            ViewUtils.gone(findViewById(R.id.llAddReferralCode));
        }

        updateCashbackEarn();

        checkPaymentMethod();

        updateCreditUse();

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
    }

    protected void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof android.widget.EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (etPromoCode.getText().length() == 0) {
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

    private void setBanner(){

        if (dataManager.getMiniBanners("Bank").size() > 0) {

            bannerAdapter = new FastItemAdapter();
            rvBanner.setLayoutManager(new LinearLayoutManager(context));
            rvBanner.setAdapter(bannerAdapter);
            bannerAdapter.add(new MiniBannerItem(dataManager.getMiniBanners("Bank")));

        }
    }

    void updateCashbackEarn() {

        if (totalCashback + promoCashback > 0) {

            ViewUtils.visible(llEarn);

            tvEarn.setText(FuzzieUtils.getFormattedValue(totalCashback + promoCashback, 0.75f));

        } else {

            ViewUtils.gone(llEarn);

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

                } else if (response.code() == 417) {

                    logoutUser(currentUser, dataManager, ClubSubscribePaymentActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<PaymentMethod>> call, Throwable t) {
                Timber.d("Payment Method Error: " + t.getLocalizedMessage());
            }
        });
    }

    void checkPaymentMethod(){

        if (dataManager.getSelectedPaymentMethod() != null) { // Choose default if already have.

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

                if (paymentMethod.getCardType().equals("ENETS")){

                    ivPaymentCard.setImageResource(R.drawable.ic_nets);
                    tvCardNumber.setText("NETSPay");

                } else {

                    Picasso.get().load(paymentMethod.getImageUrl()).into(ivPaymentCard);
                    tvCardNumber.setText("●●●●●●● " + paymentMethod.getLast4());
                }
            }
        }

    }

    void updateCreditUse() {

        if (creditsToUse > 0) {

            ViewUtils.visible(llFuzzieUsed);

            double balance = Math.max(currentUser.getCurrentUser().getWallet().getBalance() - creditsToUse, 0.0);
            if (balance % 1 == 0) {
                tvCreditsLeft.setText("S$" + String.format("%.0f", balance) + " left");
            } else {
                tvCreditsLeft.setText("S$" + String.format("%.2f", balance) + " left");
            }

            Drawable creditsDrawable = ContextCompat.getDrawable(context, R.drawable.switch_on);
            ivUseCredits.setImageDrawable(creditsDrawable);

            SpannableString spannableString = new SpannableString("-");
            tvFuzzieUsed.setText(TextUtils.concat(spannableString, FuzzieUtils.getFormattedValue(creditsToUse, 0.75f)));
            tvPayable.setText(FuzzieUtils.getFormattedValue(totalPrice - creditsToUse, 0.75f));

        } else {

            ViewUtils.gone(llFuzzieUsed);

            if (currentUser.getCurrentUser().getWallet().getBalance() % 1 == 0) {
                tvCreditsLeft.setText("S$" + String.format("%.0f", currentUser.getCurrentUser().getWallet().getBalance()) + " available");
            } else {
                tvCreditsLeft.setText("S$" + String.format("%.2f", currentUser.getCurrentUser().getWallet().getBalance()) + " available");
            }

            tvCreditsLeft.setTextColor(Color.parseColor("#6D6D6D"));

            Drawable creditsDrawable = ContextCompat.getDrawable(context, R.drawable.switch_off);
            ivUseCredits.setImageDrawable(creditsDrawable);
            tvPayable.setText(FuzzieUtils.getFormattedValue(totalPrice, 0.75f));

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvUseCredits)
    void useCreditsClicked() {

        if (creditsToUse > 0) {

            creditsToUse = 0.0;

        } else {

            double creditBalance = currentUser.getCurrentUser().getWallet().getBalance();

            if (creditBalance > 0) {

                creditsToUse = Math.min(creditBalance, totalPrice);

                // Show Pop Up
                popView = new FZPopView(this, "", "-S$" + String.format("%.0f", creditsToUse), R.drawable.fuzzie_credit_logo);
                popView.setFZPopViewListener(this);
                if (!isFinishing()) {
                    popView.show();
                    popView.getTvBody().setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Brandon_blk.otf"));
                    popView.getTvBody().setTextColor(Color.parseColor("#424242"));
                    popView.getTvBody().setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
                    SpannableString minutes = new SpannableString("-");
                    popView.getTvBody().setText(TextUtils.concat(minutes, FuzzieUtils.getFormattedValue(creditsToUse, 0.8f)));

                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            hidePopView();
                        }
                    };

                    handler.postDelayed(runnable, 1000);
                }

            } else { // No Credit Balance
                showPopView("OOPS!", "Looks like you don't have any fuzzie credits yet", R.drawable.bear_dead, false, "GOT IT");
            }
        }

        updateCreditUse();
    }

    @Click(R.id.tvDelete)
    void deletePromoCode() {

        deletePromoCode = true;
        showFZAlert("ARE YOU SURE?", "Are you sure you want to delete this promo code?", "YES, DELETE", "No, Cancel", R.drawable.ic_bear_baby_white);
    }

    @Click(R.id.cvAddPaymentMethod)
    void addPaymentMethodClicked() {
        PaymentMethodsActivity_.intent(context).fromPaymentPage(true).startForResult(REQUEST_PAYMENT_METHOD);
    }

    @Click(R.id.cvPaymentMethod)
    void paymentMethodClicked() {
        PaymentMethodsActivity_.intent(context).fromPaymentPage(true).startForResult(REQUEST_PAYMENT_METHOD);
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked() {
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
                "mailto", "support@fuzzie.com.sg", null));
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    void processPromoCode() {

        Timber.d("Promo Code Entered: " + etPromoCode.getText());

        hideKeyboard();
        etPromoCode.clearFocus();
        final String code = etPromoCode.getText().toString().toUpperCase();
        if (code.length() > 0) {

            showPopView("VALIDATING", "", R.drawable.bear_craft, true);

            Call<JsonObject> call = FuzzieAPI.APIService().validateClubPromoCode(currentUser.getAccesstoken(), code);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    hidePopView();

                    if (response.code() == 200 && response.body() != null
                            && response.body().getAsJsonPrimitive("code") != null
                            && ((response.body().getAsJsonPrimitive("discount_value") != null) || response.body().getAsJsonPrimitive("cash_back_percentage") != null)) {

                        promoCode = response.body().getAsJsonPrimitive("code").getAsString();

                        ViewUtils.visible(llDeletePromoCode);
                        ViewUtils.gone(llAddPromoCode);

                        if (response.body().getAsJsonPrimitive("cash_back_percentage") != null && response.body().getAsJsonPrimitive("cash_back_percentage").getAsDouble() != 0){

                            promoCashbackPercentage = response.body().getAsJsonPrimitive("cash_back_percentage").getAsDouble();
                            promoCashback = promoCashbackPercentage / 100 * totalPrice;

                            if (promoCashback % 1 == 0) {
                                tvPromoCode.setText("+S$" + String.format("%.0f Cashback", promoCashback));
                            } else {
                                tvPromoCode.setText("+S$" + String.format("%.2f Cashback", promoCashback));
                            }

                            updateCashbackEarn();

                            showPopView("SUCCESS!", "+" + String.format("%.0f", promoCashbackPercentage) + "% cashback!", R.drawable.bear_good, false, "DONE");

                        } else if (response.body().getAsJsonPrimitive("discount_value") != null && response.body().getAsJsonPrimitive("discount_value").getAsDouble() != 0){

                            promoDiscount = response.body().getAsJsonPrimitive("discount_value").getAsDouble();

                            DecimalFormat decimalFormat = new DecimalFormat("##.##");
                            tvPromoCode.setText(String.format("+S$ %s Discount", decimalFormat.format(promoDiscount)));

                            updateCreditUse();

                            showPopView("SUCCESS!", String.format("-S$%s off your bill", decimalFormat.format(promoDiscount)), R.drawable.bear_good, false, "DONE");

                        }

                    } else if ((response.code() == 404 || response.code() == 410 || response.code() == 411 || response.code() == 416) && response.errorBody() != null) {

                        String errorMessage = "Unknown error occurred: " + response.code();

                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()) {
                                errorMessage = jsonObject.get("error").getAsString();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (response.code() == 411 || response.code() == 416) {
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
                    showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }

            });

        }
    }

    @Click(R.id.cvPayTheBear)
    void payButtonClicked() {

        if (creditsToUse == totalPrice){

            processPayment(null);

        } else {

            if (dataManager.cardTempAdded) {

                if (FuzzieUtils.getPaymentCardType(dataManager.cardTemp.getCardNumber()) == PaymentCardType.PAYMENT_CARD_TYPE_NONE) {

                    showFZAlert("OOPS!", "Our payment gateway is not able to accept your card. Change to another and tyr again.", "GOT IT", "", R.drawable.ic_bear_dead_white);

                } else {

                    processPayment(null);
                }

            } else {

                if (paymentMethod != null){

                    if (paymentMethod.getCardType().equals("ENETS")){

                        callNets();

                    } else {

                        processPayment(paymentMethod.getToken());
                    }

                } else {

                    showFZAlert("OOPS!", "Please choose a payment method first.", "GOT IT", "", R.drawable.ic_bear_dead_white);

                }
            }
        }

    }

    private void callNets() {

        String hmac = "";
        netsPayTransactionRef = NetsUtils.getRandomString(20);
        String txn = NetsUtils.getTransactionRequest(netsPayTransactionRef, (int)(totalPrice * 100));

        try {
            hmac = FuzzieUtils.generateSignature(txn, NETSPAY_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PaymentRequestManager manager = PaymentRequestManager.getSharedInstance();

        try {
            manager.sendPaymentRequest(NETSPAY_API_KEY_ID, hmac, txn, new PaymentCallback() {
                @Override
                public void onResult(PaymentResponse paymentResponse) {

                    processPaymentWithNets();
                }

                @Override
                public void onFailure(NETSError netsError) {

                    if (!netsError.responeCode.contains("69078")){ // 69078 : The error code when click back from Nets app.

                        showAlert("Oops!", NetsUtils.netsError(netsError.responeCode));

                    }

                }
            }, ClubSubscribePaymentActivity.this);

        } catch (InvalidPaymentRequestException e) {

            showFZAlert("Oops!", "InvalidPaymentRequestException... " + e.getMessage(), "OK", "", R.drawable.ic_bear_dead_white);

        } catch (Exception e) {

            Timber.e("Exception... ", e.getMessage());
            showFZAlert("Oops!", "Unknown Error -  " + e.getMessage(), "OK", "", R.drawable.ic_bear_dead_white);
        }
    }

    private void processPayment(final String paymentToken) {

        String paymentMode = "CREDIT_CARD";

        Call<JsonObject> call = null;

        if (dataManager.cardTempAdded){

            call = FuzzieAPI.APIService().clubSubscribe(currentUser.getAccesstoken(), creditsToUse, dataManager.cardTemp.getCardNumber(), dataManager.cardTemp.getExpDate(), dataManager.cardTemp.getCvv(),  paymentMode, promoCode, etReferralCode.getText().toString());

        } else {

            call = FuzzieAPI.APIService().clubSubscribe(currentUser.getAccesstoken(), creditsToUse, paymentToken,  paymentMode, promoCode, etReferralCode.getText().toString());
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

    private void processPaymentWithNets(){

        Call<JsonObject> call = FuzzieAPI.APIService().clubSubscribeWithNets(currentUser.getAccesstoken(), creditsToUse, netsPayTransactionRef,  "NETS", promoCode, etReferralCode.getText().toString());
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

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));

            if (dataManager.cardTempAdded) {

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_PAYMENT_METHODS));

                dataManager.cardTempAdded = false;
                dataManager.cardTemp = null;
            }

            ClubSubscribeSuccessActivity_.intent(context).start();

        } else if (response.code() == 417){

            logoutUser(currentUser, dataManager,  ClubSubscribePaymentActivity.this);

        } else {

            String errorMessage = "Unknown error occurred: " + response.code();

            if (response.code() != 500 && response.errorBody() != null) {

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
            }

            showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);

        }
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (deletePromoCode) {

            deletePromoCode = false;

            etPromoCode.setText("");
            promoCode = null;
            promoCashbackPercentage = 0.0;
            promoCashback = 0.0;
            promoDiscount = 0.0;
            ViewUtils.gone(llDeletePromoCode);
            ViewUtils.visible(llAddPromoCode);

            updateCashbackEarn();

        } else if (isActiveCode) {

            isActiveCode = false;
            CodeActivity_.intent(context).start();
        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

}
