package sg.com.fuzzie.android.ui.giftbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.views.FZSlideButton;

@EActivity(R.layout.activity_gift_redeem_gps)
public class GiftRedeemGPSActivity extends BaseActivity implements FZSlideButton.OnSlideCompleteListener {

    @Extra
    String giftId;

    @Extra
    String storeNameExtra;

    private Gift gift;


    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvGiftName)
    TextView tvGiftName;

    @ViewById(R.id.tvStoreName)
    TextView tvStoreName;

    @ViewById(R.id.slideButton)
    FZSlideButton slideButton;

    @AfterViews
    public void calledAfterViewInjection() {

        if (giftId == null) return;

        gift = dataManager.getGiftById(giftId, false);
        if (gift == null) return;

        if (gift.getGiftCard() != null){

            tvGiftName.setText(String.format("%s%s %s", gift.getGiftCard().getPrice().getCurrencySymbol(), gift.getGiftCard().getPrice().getValue(), gift.getGiftCard().getDisplayName()));

        } else if (gift.getService() != null){

            tvGiftName.setText(String.format("%s%s %s", gift.getService().getPrice().getCurrencySymbol(), gift.getService().getPrice().getValue(), gift.getService().getDisplayName()));

        }

        tvStoreName.setText(storeNameExtra);

        slideButton.setOnSlideCompleteListener(this);

    }

    private void giftRedeem(){

        if (dataManager.myLocation == null || giftId == null) return;

        displayProgressDialog("Verifying...");
        FuzzieAPI.APIService().redeemGiftWithGPS(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), giftId).enqueue(new Callback<PayResponse>() {
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

                    logoutUser(currentUser, dataManager,  GiftRedeemGPSActivity.this);

                } else if (response.code() == 422) {

                    showFZAlert("Oops!", "Sorry! The gift you're trying to redeem has already expired.", "OK", "", R.drawable.ic_bear_dead_white);

                } else if (response.code() == 410 || response.code() == 413) {

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

                    showFZAlert("Oops!", "Sorry! The gift you're trying to redeem has already been redeemed.", "OK", "", R.drawable.ic_bear_dead_white);

                } else {

                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);


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

    @Override
    public void onSlideComplete(@NonNull FZSlideButton var1) {

        giftRedeem();
    }
}
