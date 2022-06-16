package sg.com.fuzzie.android.ui.redpacket;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.shop.MiniBannerItem;
import sg.com.fuzzie.android.ui.activate.CodeActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotLearnMoreActvity_;
import sg.com.fuzzie.android.ui.shop.topup.TopUpActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.EditTextBackEvent;
import sg.com.fuzzie.android.views.FZPopView;
import timber.log.Timber;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_TOP_UP_PAYMENT;

/**
 * Created by mac on 2/27/18.
 */

@EActivity(R.layout.activity_red_packet_payment)
public class RedPacketPaymentActivity extends BaseActivity {

    @Extra
    boolean isRandomMode;

    @Extra
    int quantity;

    @Extra
    double total;

    @Extra
    int ticketCount;

    @Extra
    String message;

    FastItemAdapter<MiniBannerItem> bannerAdapter;

    boolean deletePromoCode = false;
    boolean topUp = false;
    boolean isActiveCode = false;
    double totalCashback = 0.0;
    String promoCode = null;
    double promoCashbackPercentage = 0.0;
    double promoCashback = 0.0;
    double creditsToUse = 0.0;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvBanner)
    RecyclerView rvBanner;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.llAddPromoCode)
    LinearLayout llAddPromoCode;

    @ViewById(R.id.etPromoCode)
    EditTextBackEvent etPromoCode;

    @ViewById(R.id.llDeletePromoCode)
    LinearLayout llDeletePromoCode;

    @ViewById(R.id.tvPromoCode)
    TextView tvPromoCode;

    @ViewById(R.id.tvEarn)
    TextView tvEarn;

    @ViewById(R.id.tvSubtotal)
    TextView tvSubtotal;

    @ViewById(R.id.llFuzzieUsed)
    LinearLayout llFuzzieUsed;

    @ViewById(R.id.tvFuzzieUsed)
    TextView tvFuzzieUsed;

    @ViewById(R.id.cvUseCredits)
    CardView cvUseCredits;

    @ViewById(R.id.tvCreditsLeft)
    TextView tvCreditsLeft;

    @ViewById(R.id.ivFuzzieCredits)
    ImageView ivFuzzieCredits;

    @ViewById(R.id.ivUseCredits)
    ImageView ivUseCredits;

    @ViewById(R.id.cvPayTheBear)
    CardView cvPayTheBear;

    @ViewById(R.id.tvTicketCount)
    TextView tvTicketCount;

    @ViewById(R.id.tvLuck)
    TextView tvLuck;

    @ViewById(R.id.tvPayable)
    TextView tvPayable;

    @ViewById(R.id.cashContainer)
    View cashContainer;

    @ViewById(R.id.cashbackInfo)
    View cashbackInfo;

    @ViewById(R.id.ticketInfo)
    View ticketInfo;

    @AfterViews
    public void calledAfterViewInjection() {

        setupUI(findViewById(R.id.id_activity_red_packet_payment));

        setBanner();

        tvPrice.setText(FuzzieUtils.getFormattedValue(total, 0.75f));
        tvSubtotal.setText(FuzzieUtils.getFormattedValue(total, 0.75f));

        updateTicket();
        updateCashbackEarn();
        updateCreditUse();

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

    }

    private void setBanner(){
        if (dataManager.getMiniBanners("Bank").size() > 0){
            bannerAdapter = new FastItemAdapter();
            rvBanner.setLayoutManager(new LinearLayoutManager(context));
            rvBanner.setAdapter(bannerAdapter);
            bannerAdapter.add(new MiniBannerItem(dataManager.getMiniBanners("Bank")));
        }
    }

    void updateTicket(){

        if (quantity > 1){

            tvLuck.setText(String.format("Lucky Packet x%d", quantity));
        }

        if (currentUser.getCurrentUser().getSettings().isBoughtFirstRedPacketBundle()){

            ViewUtils.gone(ticketInfo);

        } else {

            tvTicketCount.setText(String.valueOf(dataManager.getHome().getJackpotTicketsCountForFirstRedPacketSending()));
        }
    }

    void updateCashbackEarn() {
        if (totalCashback + promoCashback > 0) {

            ViewUtils.visible(cashbackInfo);
            ViewUtils.visible(cashContainer);

            tvEarn.setText(FuzzieUtils.getFormattedValue(totalCashback + promoCashback, 0.75f));

        } else {

            ViewUtils.gone(cashbackInfo);
            if (currentUser.getCurrentUser().getSettings().isBoughtFirstRedPacketBundle()){

                ViewUtils.gone(cashContainer);

            }
        }
    }

    void updateCreditUse() {
        if (creditsToUse > 0) {
            llFuzzieUsed.setVisibility(View.VISIBLE);

            double balance = Math.max(currentUser.getCurrentUser().getWallet().getCashableBalance() - creditsToUse, 0.0);
            if (balance % 1 == 0){
                tvCreditsLeft.setText("S$" + String.format("%.0f",balance) + " left" );
            } else {
                tvCreditsLeft.setText("S$" + String.format("%.2f",balance) + " left" );
            }


            Drawable creditsDrawable = ContextCompat.getDrawable(context,R.drawable.switch_on);
            ivUseCredits.setImageDrawable(creditsDrawable);

            SpannableString minutes = new SpannableString("-");
            tvFuzzieUsed.setText(TextUtils.concat(minutes, FuzzieUtils.getFormattedValue(creditsToUse, 0.75f)));

            cvPayTheBear.setEnabled(true);
            cvPayTheBear.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvPayable.setText(FuzzieUtils.getFormattedValue(total, 0.75f));

        } else {
            llFuzzieUsed.setVisibility(View.GONE);

            if (currentUser.getCurrentUser().getWallet().getCashableBalance() % 1 == 0){
                tvCreditsLeft.setText("S$" + String.format("%.0f",currentUser.getCurrentUser().getWallet().getCashableBalance()) + " available" );
            } else {
                tvCreditsLeft.setText("S$" + String.format("%.2f",currentUser.getCurrentUser().getWallet().getCashableBalance()) + " available" );
            }

            tvCreditsLeft.setTextColor(Color.parseColor("#6D6D6D"));

            Drawable creditsDrawable = ContextCompat.getDrawable(context,R.drawable.switch_off);
            ivUseCredits.setImageDrawable(creditsDrawable);

            cvPayTheBear.setEnabled(false);
            cvPayTheBear.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            tvPayable.setText(FuzzieUtils.getFormattedValue(creditsToUse, 0.75f));

        }
    }

    void processPromoCode() {
        Timber.d("Promo Code Entered: " + etPromoCode.getText());

        hideKeyboard();
        etPromoCode.clearFocus();
        final String code = etPromoCode.getText().toString();

        if (code.length() > 0){

            showPopView("VALIDATING", "", R.drawable.bear_craft, true);
            FuzzieAPI.APIService().validatePromoCode(currentUser.getAccesstoken(), code).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    hidePopView();

                    if (response.code() == 200 && response.body() != null
                            && response.body().getAsJsonPrimitive("code") != null
                            && response.body().getAsJsonPrimitive("cash_back_percentage") != null
                            && response.body().getAsJsonObject("promo_code_type") != null) {

                        JsonObject promoCodeType = response.body().getAsJsonObject("promo_code_type");
                        String type = promoCodeType.get("type").getAsString();
                        if (!type.equals("FUZZIE") && creditsToUse > 0){
                            showPopView("OOPS!", "Fuzzie credits cannot be used with your promo code. To use your credits, remove your promo code.", R.drawable.bear_dead, false, "GOT IT");
                            etPromoCode.setText("");
                        } else {

                            promoCode = response.body().getAsJsonPrimitive("code").getAsString();
                            promoCashbackPercentage = response.body().getAsJsonPrimitive("cash_back_percentage").getAsDouble();
                            promoCashback = promoCashbackPercentage / 100 * total;

                            llDeletePromoCode.setVisibility(View.VISIBLE);
                            if (promoCashback % 1 == 0){
                                tvPromoCode.setText("+S$" + String.format("%.0f Cashback", promoCashback));
                            } else {
                                tvPromoCode.setText("+S$" + String.format("%.2f Cashback", promoCashback));
                            }
                            llAddPromoCode.setVisibility(View.GONE);
                            updateCashbackEarn();

                            showPopView("SUCCESS!", "+" + String.format("%.0f", promoCashbackPercentage) + "% cashback!", R.drawable.bear_good, false, "DONE");
                        }

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

    @Click(R.id.tvDelete)
    void deletePromoCode() {

        deletePromoCode = true;
        showFZAlert("ARE YOU SURE?", "Are you sure you want to delete this promo code?", "YES, DELETE", "No, Cancel", R.drawable.ic_bear_baby_white);
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

            double creditBalance = currentUser.getCurrentUser().getWallet().getCashableBalance();

            if (creditBalance >= total) {

                creditsToUse = total;

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
                topUp = true;
                showFZAlert("OOPS!", "You have insufficient Fuzzie credits. Top up your credits or earn more cashback.", "TOP UP CREDITS NOW", "Close", R.drawable.ic_bear_dead_white);
            }
        }

        updateCreditUse();
    }

    @Click(R.id.tvHelp)
    void helpButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.jackpot_help_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        JackpotLearnMoreActvity_.intent(context).fromRedPacket(true).start();
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
    void payButtonClicked(){

        processRedPacketPayment();

    }

    private void processRedPacketPayment(){

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        String spiltType = "";
        if (isRandomMode){
            spiltType = "RANDOM";
        } else {
            spiltType = "EQUAL";
        }

        FuzzieAPI.APIService().purchaseRedPacket(currentUser.getAccesstoken(), message, quantity, ticketCount, spiltType, total).enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                hidePopView();

                if (response.code() == 200 && response.body() != null){

                    if (response.body().getRedPacketBundle() != null){

                        dataManager.redPacketBundle = response.body().getRedPacketBundle();
                        if (dataManager.getRedPacketBundles() != null){
                            dataManager.getRedPacketBundles().add(0, response.body().getRedPacketBundle());
                        }
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));

                    if (currentUser.getCurrentUser().getSettings().isBoughtFirstRedPacketBundle()){

                        RedPacketPaymentSuccessActivity_.intent(context).start();
                        
                    } else {

                        RedPacketPaymentSuccessJackpotActivity_.intent(context).start();

                    }

                } else {

                    String errorMessage = "";
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
                    } else {
                        errorMessage = "Unknown error occurred: " + response.code();
                    }

                    showFZAlert("Oops!", errorMessage, "GOT IT", "", R.drawable.ic_bear_dead_white);

                }

            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

                hidePopView();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });

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

        } else if (topUp){

            topUp = false;
            TopUpActivity_.intent(context).startForResult(REQUEST_TOP_UP_PAYMENT);

        } else if (isActiveCode){

            isActiveCode = false;
            CodeActivity_.intent(context).start();

        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
        deletePromoCode = false;
        topUp = false;
        isActiveCode = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TOP_UP_PAYMENT){
            if (resultCode == RESULT_OK){
                updateCreditUse();
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
