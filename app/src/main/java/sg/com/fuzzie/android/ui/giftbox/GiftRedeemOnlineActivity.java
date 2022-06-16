package sg.com.fuzzie.android.ui.giftbox;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

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

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_ACTIVE;
import static sg.com.fuzzie.android.utils.Constants.COUNTDOWN_TIME;

/**
 * Created by mac on 5/30/17.
 */

@EActivity(R.layout.activity_gift_redeem_online)
public class GiftRedeemOnlineActivity extends BaseActivity {

    @Extra
    String giftId;

    @Extra
    boolean used;

    private Gift gift;
    Brand brand;
    GiftCard giftCard;
    Service service;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvGiftName)
    TextView tvGiftName;

    @ViewById(R.id.tvCode)
    TextView tvCode;


    @ViewById(R.id.tvTimer)
    TextView tvTimer;

    @ViewById(R.id.tvMarkAsUnredeemed)
    TextView tvMarkAsUnredeemed;

    CountDownTimer timer;

    @AfterViews
    public void calledAfterViewInjection(){

        if (giftId == null) return;

        gift = dataManager.getGiftById(giftId, used);
        if (gift == null) return;

        brand = gift.getBrand();
        giftCard = gift.getGiftCard();
        service = gift.getService();

        if (giftCard != null){
            tvGiftName.setText(giftCard.getPrice().getCurrencySymbol()+giftCard.getPrice().getValue() + " " + giftCard.getDisplayName());
        } else if (service != null){
            tvGiftName.setText(service.getPrice().getCurrencySymbol()+service.getPrice().getValue() + " " + service.getDisplayName());
        }

        tvMarkAsUnredeemed.setText(FuzzieUtils.fromHtml("<u>Mark as unredeemed</u>"));

        tvCode.setText(gift.getThirdPartyCode());

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

    private void startTimer(){

        tvTimer.setVisibility(View.VISIBLE);

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
        tvTimer.setVisibility(View.GONE);

        if (timer != null){
            timer.cancel();
            timer = null;
        }

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

    @Click(R.id.tvCopy)
    void copyButtonClicked() {
        if (gift == null || gift.getThirdPartyCode() == null) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", gift.getThirdPartyCode());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context,"Code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.cvStore)
    void storeButtonClicked() {
        if (gift == null) return;
        if (gift.getExternalRedeemPage() != null && !gift.getExternalRedeemPage().equals("")){
            String url = gift.getExternalRedeemPage();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
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
                    logoutUser(currentUser, dataManager, GiftRedeemOnlineActivity.this);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                dismissProgressDialog();
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
                    logoutUser(currentUser, dataManager,  GiftRedeemOnlineActivity.this);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataManager.setActiveGiftOnlineRedeemActivity(true);
        if (gift != null){

            if (gift.getRedeemedTime() == null && gift.getRedeemTimerStartedAt() != null){

                startTimer();

            } else {

                endTimer();
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataManager.setActiveGiftOnlineRedeemActivity(false);
        endTimer();
    }

    private void showGiftBoxPage(){

        setResult(RESULT_OK);
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
}
