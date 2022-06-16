package sg.com.fuzzie.android.ui.payments;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.CardTemp;
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import timber.log.Timber;

/**
 * Created by nurimanizam on 17/12/16.
 */

@EActivity(R.layout.activity_add_payment_method)
public class AddPaymentMethodActivity extends BaseActivity {

    static final int REQUEST_SCAN = 1;
    private boolean cardAdded;
    private boolean prePay;
    private JsonObject paymentMethodJsonObject;

    String cardNumber;
    String expiry;
    String cvv;


    @Extra
    boolean fromPaymentPage;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.etCardNumber)
    EditText etCardNumber;

    @ViewById(R.id.cardValid)
    ImageView cardValid;

    @ViewById(R.id.etExpiry)
    EditText etExpiry;

    @ViewById(R.id.expiryValid)
    ImageView expiryValid;

    @ViewById(R.id.etCVV)
    EditText etCVV;

    @ViewById(R.id.cvvValid)
    ImageView cvvValid;

    @Click(R.id.ivBack)
    void backButtonClicked() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @AfterViews
    public void calledAfterViewInjection() {

    }

    @AfterTextChange(R.id.etCardNumber)
    void cardTextChanged() {

        if (etCardNumber.length() > 0){
            char c = etCardNumber.getText().charAt(etCardNumber.length() - 1);
            if (!Character.isDigit(c)){
                etCardNumber.getText().delete(etCardNumber.length() - 1, etCardNumber.length());
            }
        }

        if (etCardNumber.getText().toString().replaceAll("\\s+","").length() == 15 || etCardNumber.getText().toString().replaceAll("\\s+","").length() == 16) {
            cardValid.setVisibility(View.VISIBLE);
        } else {
            cardValid.setVisibility(View.GONE);
        }

        if (etCardNumber.getText().toString().replaceAll("\\s+","").length() == 16){
            etExpiry.requestFocus();
        }

        if (etCardNumber.getText().toString().replaceAll("\\s+","").length() > 16){
            etCardNumber.setText(etCardNumber.getText().toString().substring(0, 16));
        }
    }

    @AfterTextChange(R.id.etExpiry)
    void expiryTextChanged() {

        if (etExpiry.length() > 0){
            char c = etExpiry.getText().charAt(etExpiry.length() - 1);
            if (!Character.isDigit(c)){
                etExpiry.getText().delete(etExpiry.length() - 1, etExpiry.length());
            }
        }

        if (etExpiry.length() > 0 && etExpiry.length() == 3) {
            final char c = etExpiry.getText().charAt(etExpiry.length() - 1);
            if ('/' == c) {
                etExpiry.getText().delete(etExpiry.length() - 1, etExpiry.length());
            }
        }

        if (etExpiry.length() > 0 && etExpiry.length()  == 3) {
            if (TextUtils.split(etExpiry.getText().toString(), String.valueOf("/")).length <= 2) {
                etExpiry.getText().insert(etExpiry.length() - 1, String.valueOf("/"));
            }
        }

        if (etExpiry.length() == 1){

            char first = etExpiry.getText().charAt(0);

            if (Character.getNumericValue(first) > 1){
                etExpiry.setText("0" + first);
                etExpiry.setSelection(2);
            }
        }

        if (etExpiry.length() == 2){

            char first = etExpiry.getText().charAt(0);
            char second = etExpiry.getText().charAt(etExpiry.length() - 1);

            if (Character.getNumericValue(first) > 0 && Character.getNumericValue(second) > 2 || Character.getNumericValue(first) == 0 && Character.getNumericValue(second) == 0){
                etExpiry.getText().delete(etExpiry.length() - 1, etExpiry.length());
            }
        }

        if (etExpiry.length() == 5){

            String[] expiryArray = etExpiry.getText().toString().split("/");
            int year = Integer.parseInt(expiryArray[1]);

            if (year > 17){
                expiryValid.setVisibility(View.VISIBLE);
                etCVV.requestFocus();
            } else {
                expiryValid.setVisibility(View.GONE);
                etExpiry.setSelection(5);
            }

        } else {
            expiryValid.setVisibility(View.GONE);
        }

        if (etExpiry.getText().toString().length() > 5){
            etExpiry.setText(etExpiry.getText().toString().substring(0, 5));
        }

    }

    @AfterTextChange(R.id.etCVV)
    void cvvTextChanged() {

        if (etCVV.length() > 0){
            char c = etCVV.getText().charAt(etCVV.length() - 1);
            if (!Character.isDigit(c)){
                etCVV.getText().delete(etCVV.length() - 1, etCVV.length());
            }
        }

        if (etCVV.getText().length() == 3 || etCVV.getText().length() == 4) {
            cvvValid.setVisibility(View.VISIBLE);
        } else {
            cvvValid.setVisibility(View.GONE);
        }

        if (etCVV.getText().toString().length() > 4){
            etCVV.setText(etCVV.getText().toString().substring(0, 4));
            etCVV.setSelection(4);
        }
    }

    @Click(R.id.llScan)
    void scanButtonClicked() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        startActivityForResult(scanIntent, REQUEST_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCAN) {

            String resultDisplayStr = "";

            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                etCardNumber.setText(scanResult.cardNumber);

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";

                    String month;
                    if (scanResult.expiryMonth < 10) {
                        month = "0" + scanResult.expiryMonth;
                    } else {
                        month = "" + scanResult.expiryMonth;
                    }

                    String year = "" + (scanResult.expiryYear);
                    etExpiry.setText(month + "/" + year.substring(2));
                }

                if (scanResult.cvv != null) {
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    etCVV.setText(scanResult.cvv);
                }

            }
            else {
                resultDisplayStr = "Scan was cancelled.";
            }

            Timber.e(resultDisplayStr);

        }

    }

    @Click(R.id.cvAdd)
    void addButtonClicked() {

        cardNumber = etCardNumber.getText().toString().replaceAll("\\s+","");
        expiry = etExpiry.getText().toString();
        final String[] expiryArray = expiry.split("/");

        cvv = etCVV.getText().toString();

        if (TextUtils.isEmpty(cardNumber)) {
            showFZAlert("Oops!", "You've missed your card number.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }


//        if (dataManager.getPaymentMethods() != null) {
//            String lastFourDigits = cardNumber.substring(cardNumber.length()-4,cardNumber.length());
//            for (PaymentMethod paymentMethod : dataManager.getPaymentMethods()) {
//                if (paymentMethod.getLast4().equals(lastFourDigits)) {
//                    showFZAlert("Oops!", "You've already added this card before.", "OK", "", R.drawable.ic_bear_dead_white);
//                    return;
//                }
//            }
//        }

        if (TextUtils.isEmpty(expiry)) {
            showFZAlert("Oops!", "You've missed the expiry date.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        if (!expiry.contains("/") || expiryArray.length != 2) {
            showFZAlert("Oops!", "Your expiry format is incorrect.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        if (TextUtils.isEmpty(cvv)) {
            showFZAlert("Oops!", "You've missed the CVV.", "OK", "", R.drawable.ic_bear_dead_white);
            return;
        }

        if (!fromPaymentPage){

            showFZAlert("CARD VALIDATION", "To validate, your card will be charged $0.01 that will be immediately voided. Do you wish to continue?", "YES, VALIDATE NOW", "No, Cancel", R.drawable.ic_bear_baby_white);
            prePay = true;

        } else {

            passCardInfo();

        }


    }

    private void addCard(){

        hideKeyboard();
        displayProgressDialog("Verifying...");

        String[] expiryArray = expiry.split("/");
        String expDate = expiryArray[0] + "20" + expiryArray[1];

        Call<JsonObject> call = FuzzieAPI.APIService().addPaymentMethod(currentUser.getAccesstoken(), cardNumber, expDate, cvv);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissProgressDialog();

                if (response.code() == 200
                        && response.body() != null) {

                    PaymentMethod paymentMethod = PaymentMethod.fromJSON(response.body().toString());

                    if (dataManager.getPaymentMethods() == null) {
                        dataManager.setPaymentMethods(new ArrayList<PaymentMethod>());
                    }

                    dataManager.getPaymentMethods().add(paymentMethod);

                    paymentMethodJsonObject = response.body();

                    cardAdded = true;

                    showPopView("SUCCESS!", "Your new credit/debit card has been added.",R.drawable.baby_bear, false, "DONE");

                } else {

                    String errorMessage = "Unknown error occurred: " + response.code();

                    if (response.code() == 406 && response.errorBody() != null) {

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

                        showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);

                    } else {

                        showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);

                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });
    }

    private void passCardInfo(){

        String[] expiryArray = expiry.split("/");
        String expDate = expiryArray[0] + "20" + expiryArray[1];

        dataManager.cardTemp = new CardTemp(cardNumber, expDate, cvv);
        dataManager.cardTempAdded = true;

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void okButtonClicked() {
        super.okButtonClicked();

        if (cardAdded){

            cardAdded = false;

            Intent resultIntent = new Intent();
            resultIntent.putExtra("Method", paymentMethodJsonObject.toString());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        }

    }

    @Override
    public void cancelButtonClicked() {
        super.cancelButtonClicked();

        cardAdded = false;
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (prePay) {

            prePay = false;

            addCard();

        }
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        prePay = false;
    }
}
