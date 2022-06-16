package sg.com.fuzzie.android.ui.club;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.FZSlideButton;

@EActivity(R.layout.activity_club_offer_redeem)
public class ClubOfferRedeemActivity extends BaseActivity implements FZSlideButton.OnSlideCompleteListener {

    private Offer offer;
    private Brand brand;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.pinEntry)
    PinEntryView pinEntry;

    @ViewById(R.id.tvOfferName)
    TextView tvOfferName;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvStoreName)
    TextView tvStoreName;

    @ViewById(R.id.slideButton)
    FZSlideButton slideButton;

    @ViewById(R.id.llLocation)
    View locationView;

    @ViewById(R.id.llPin)
    View pinView;

    @AfterViews
    void callAfterViewInjection(){

        offer = dataManager.offer;
        ClubStore clubStore = dataManager.clubStore;

        if (offer == null) return;

        tvOfferName.setText(offer.getName());

        brand = dataManager.getBrandById(offer.getBrandId());
        tvBrandName.setText(brand != null ? brand.getName() : "");

        tvStoreName.setText(clubStore == null ? "" : clubStore.getStoreName());

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

        slideButton.setOnSlideCompleteListener(this);

        if (brand.isNoPinRedemption()){

            ViewUtils.visible(locationView);
            ViewUtils.visible(slideButton);

            ViewUtils.gone(pinView);

        } else {

            ViewUtils.gone(locationView);
            ViewUtils.gone(slideButton);

            ViewUtils.visible(pinView);
        }
    }

    void verifyPIN() {

        hideKeyboard();

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        FuzzieAPI.APIService().redeemOffer(currentUser.getAccesstoken(), offer.getId(), pinEntry.getText().toString()).enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {

                hidePopView();

                if (response.code() == 200 && response.body() != null){

                    dataManager.offer = response.body();

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_CLUB_OFFER_REDEEMED));
                    ClubOfferRedeemSuccessActivity_.intent(context).start();

                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager, ClubOfferRedeemActivity.this);

                } else {

                    pinEntry.clearText();

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
            public void onFailure(Call<Offer> call, Throwable t) {

                hidePopView();
                pinEntry.clearText();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });

    }

    private void redeemOffer(){

        showPopView("PROCESSING", "", R.drawable.bear_craft, true);

        FuzzieAPI.APIService().redeemOfferWithGPS(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), offer.getId()).enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {

                hidePopView();

                if (response.code() == 200 && response.body() != null){

                    dataManager.offer = response.body();

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_CLUB_OFFER_REDEEMED));
                    ClubOfferRedeemSuccessActivity_.intent(context).start();

                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager, ClubOfferRedeemActivity.this);

                } else if (response.code() == 413){

                    String errorMessage = "Unknown error occurred: " + response.code();

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
                    }

                    showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);

                    slideButton.resetSlider();

                } else {

                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {

                hidePopView();
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

        redeemOffer();
    }
}
