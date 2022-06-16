package sg.com.fuzzie.android.ui.giftbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_ACTIVE;
import static sg.com.fuzzie.android.utils.Constants.COUNTDOWN_TIME;

/**
 * Created by mac on 5/30/17.
 */

@EActivity(R.layout.activity_gift_redeem_physical)
public class GiftRedeemPhysicalActivity extends BaseActivity {

    @Extra
    String giftId;

    @Extra
    boolean used;

    Gift gift;
    Brand brand;
    GiftCard giftCard;
    Service service;

    private String redeemType;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvGiftName)
    TextView tvGiftName;

    @ViewById(R.id.pinEntry)
    PinEntryView pinEntry;

    @ViewById(R.id.ivCode)
    ImageView ivCode;

    @ViewById(R.id.tvCode)
    TextView tvCode;

    @ViewById(R.id.llBottom)
    View llBottom;

    @ViewById(R.id.tvTimer)
    TextView tvTimer;

    @ViewById(R.id.tvMarkAsUnredeemed)
    TextView tvMarkAsUnredeemed;

    CountDownTimer timer;

    @AfterViews
    public void calledAfterViewInjection() {

        if (giftId == null) return;

        gift = dataManager.getGiftById(giftId, used);
        if (gift == null) return;

        brand = gift.getBrand();
        giftCard = gift.getGiftCard();
        service = gift.getService();

        if (giftCard != null){

            tvGiftName.setText(giftCard.getPrice().getCurrencySymbol()+giftCard.getPrice().getValue() + " " + giftCard.getDisplayName());
            redeemType = giftCard.getRedemptionType();

        } else if (service != null){

            tvGiftName.setText(service.getPrice().getCurrencySymbol()+service.getPrice().getValue() + " " + service.getDisplayName());
            redeemType = service.getRedemptionType();

        }

        if (redeemType.equals("pin") || gift.getQrCode() == null){

            ViewUtils.visible(findViewById(R.id.llPinView));
            ViewUtils.gone(findViewById(R.id.llBarCode));


        } else {

            ViewUtils.gone(findViewById(R.id.llPinView));
            ViewUtils.visible(findViewById(R.id.llBarCode));

            tvCode.setText(gift.getQrCode());

            if (redeemType.equals("qr_code")){

                try {
                    ivCode.setImageBitmap(FuzzieUtils.encodeAsBitmap(gift.getQrCode(), BarcodeFormat.QR_CODE, 600, 600));
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                ViewUtils.visible(llBottom);

            } else if (redeemType.equals("bar_code")){

                try {
                    ivCode.setImageBitmap(FuzzieUtils.encodeAsBitmap(gift.getQrCode(), BarcodeFormat.CODE_128, 900, 300));
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                ViewUtils.visible(llBottom);

            } else {

                ViewUtils.visible(findViewById(R.id.llPinView));
                ViewUtils.gone(findViewById(R.id.llBarCode));

            }
        }

        pinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pin = charSequence.toString();
                if (pin.length() == 4) {
                    verifyPIN();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvMarkAsUnredeemed.setText(FuzzieUtils.fromHtml("<u>Mark as unredeemed</u>"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((redeemType.equals("qr_code") || redeemType.equals("bar_code")) && gift.getQrCode() != null){

            dataManager.setActiveGiftOnlineRedeemActivity(true);
            if (gift != null){

                if (gift.getRedeemedTime() == null && gift.getRedeemTimerStartedAt() != null){

                    startTimer();

                } else {

                    endTimer();
                }

            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if ((redeemType.equals("qr_code") || redeemType.equals("bar_code")) && gift.getQrCode() != null){

            dataManager.setActiveGiftOnlineRedeemActivity(false);
            endTimer();
        }
    }

    private void updateGift(){

        gift = dataManager.getGiftById(giftId, used);
        if (gift == null) return;

        if (gift.getRedeemedTime() == null && gift.getRedeemTimerStartedAt() != null){

            startTimer();

        } else {

            endTimer();

        }
    }

    void verifyPIN() {
        hideKeyboard();
        displayProgressDialog("Verifying...");

        Call<PayResponse> call = FuzzieAPI.APIService().redeemGift(currentUser.getAccesstoken(), giftId, pinEntry.getText().toString());
        call.enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {
                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null && response.body().getGift() != null) {

                    dataManager.removeActiveGift(gift);
                    dataManager.getUsedGifts().add(0, response.body().getGift());

                    // Decrease Active Gift Count
                    currentUser.changeActiveGiftCount(-1);

                    GiftRedeemSuccessActivity_.intent(context).orderNumberExtra(gift.getOrderNumber()).start();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftRedeemPhysicalActivity.this);
                } else if (response.code() == 404 || response.code() == 401) {
                    pinEntry.clearText();
                    showFZAlert("Oops!", "Invalid code. Please try again.", "OK", "", R.drawable.ic_bear_dead_white);
                } else if (response.code() == 422) {
                    pinEntry.clearText();
                    showFZAlert("Oops!", "Sorry! The gift you're trying to redeem has already expired.", "OK", "", R.drawable.ic_bear_dead_white);
                } else if (response.code() == 410) {
                    pinEntry.clearText();

                    String errorMessage = "";
                    if (response.errorBody() != null) {
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
                    } else {
                        errorMessage = "Unknown error occurred: " + response.code();
                    }

                    showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);

                } else if (response.code() == 412) {
                    pinEntry.clearText();
                    showFZAlert("Oops!", "Sorry! The gift you're trying to redeem has already been redeemed.", "OK", "", R.drawable.ic_bear_dead_white);
                } else {
                    pinEntry.clearText();
                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                dismissProgressDialog();
                pinEntry.clearText();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }

        });
    }

    void redeemGift() {

        Call<PayResponse> call = FuzzieAPI.APIService().markGiftRedeemed(currentUser.getAccesstoken(), gift.getId());
        call.enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                if (response.code() == 200 && response.body() != null && response.body().getGift() != null) {

                    used = true;

                    dataManager.removeActiveGift(gift);
                    dataManager.getUsedGifts().add(0, response.body().getGift());

                    // Decrease Active Gift Count
                    currentUser.changeActiveGiftCount(-1);

                    showGiftBoxPage();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftRedeemPhysicalActivity.this);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

            }

        });
    }

    private void startTimer(){

        ViewUtils.visible(tvTimer);

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        DateTime now = new DateTime();
        DateTime redeemStartTime = parser.parseDateTime(gift.getRedeemTimerStartedAt());

        long milliseconds = COUNTDOWN_TIME - (now.getMillis() - redeemStartTime.getMillis());

        if (timer != null){
            timer.cancel();
            timer = null;
        }

        timer = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                int hours = (int) (millisUntilFinished/3600000);
                int mins = (int) (millisUntilFinished/60000);
                mins %= 60;
                int secs = (int) (millisUntilFinished/1000);
                secs %= 60;

                tvTimer.setText(String.format("Your item will be marked as used in %02dh%02dm%02ds",hours,mins,secs));
            }

            public void onFinish() {
                redeemGift();
                endTimer();
            }
        };
        timer.start();
    }

    private void endTimer(){

        tvTimer.setText("");
        ViewUtils.gone(tvTimer);

        if (timer != null){
            timer.cancel();
            timer = null;
        }

    }

    @Click(R.id.tvMarkAsUnredeemed)
    void markAsUnredeemedButtonClicked(){

        displayProgressDialog("Processing...");
        Call<PayResponse> call = FuzzieAPI.APIService().markAsUnredeemed(currentUser.getAccesstoken(), giftId);
        call.enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null){

                    if (used){

                        used = false;

                        dataManager.removeUsedGift(gift);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_GIFTBOX_ACTIVE));

                        // Increase Active Gift Count
                        currentUser.changeActiveGiftCount(1);

                    } else {

                        dataManager.updateActiveGift(response.body().getGift());

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED));

                    }

                    gift = response.body().getGift();

                    updateGift();

                    showMessage("SUCCESS!", 3000);

                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager, GiftRedeemPhysicalActivity.this);

                } else {

                    String errorMessage = "Unknown error occurred: " + response.code();

                    try {
                        String jsonString = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                        if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()) {
                            errorMessage = jsonObject.get("error").getAsString();
                        } else if (jsonObject != null && jsonObject.get("message") != null && !jsonObject.get("message").isJsonNull()) {
                            errorMessage = jsonObject.get("message").getAsString();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                dismissProgressDialog();
            }
        });
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
                "mailto","support@fuzzie.com.sg", null));
        startActivity(Intent.createChooser(emailIntent, "Email support@fuzzie.com.sg"));
    }

    void facebookSupport() {
        String url = "http://m.me/fuzzieapp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    public void showMessage(String message, int miliSeconds){

        showPopView(message, "", R.drawable.bear_good);

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showGiftBoxPage();
            }
        };

        handler.postDelayed(runnable, miliSeconds);
    }

    private void showGiftBoxPage(){

        setResult(RESULT_OK);
        finish();
    }

}
