package sg.com.fuzzie.android.ui.club;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

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
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.api.models.OfferType;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.services.LocationService;
import sg.com.fuzzie.android.ui.shop.BrandTermsActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.ViewUtils;


@EActivity(R.layout.activity_club_offer)
public class ClubOfferActivity extends BaseActivity {

    private boolean showOnlineOfferConfirm;
    private boolean showClubMembershipConfirm;

    private Offer offer;
    private ClubStore clubStore;
    private Brand brand;

    @Extra
    boolean isFlashSaleOffer;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvRule)
    TextView tvRule;

    @ViewById(R.id.cvContinue)
    CardView cvContinue;

    @ViewById(R.id.ivCover)
    ImageView ivCover;

    @ViewById(R.id.tvOfferType)
    TextView tvOfferType;

    @ViewById(R.id.tvOfferName)
    TextView tvOfferName;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvStoreName)
    TextView tvStoreName;

    @ViewById(R.id.tvEstSaving)
    TextView tvEstSaving;

    @ViewById(R.id.tvStoreAddress)
    TextView tvStoreAddress;

    @ViewById(R.id.tvAvailableTime)
    TextView tvAvailableTime;

    @ViewById(R.id.cvRedeem)
    CardView cvRedeem;

    @ViewById(R.id.vBackground)
    ImageView vBackground;

    @ViewById(R.id.tvRedeem)
    TextView tvRedeem;

    @AfterViews
    void afterViewInjection(){

        offer = dataManager.offer;
        if (offer == null) return;

        if (isFlashSaleOffer) {

            if (offer.getClubStores() != null && offer.getClubStores().size() > 0){

                clubStore = offer.getClubStores().get(0);

            }

        } else {

            clubStore = dataManager.clubStore;
        }

        if (clubStore == null) return;

        brand = dataManager.getBrandById(offer.getBrandId());
        if (brand == null) return;

        tvRule.setPaintFlags(tvRule.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (offer.getImageUrl() != null && !offer.getImageUrl().equals("")){

            Picasso.get()
                    .load(offer.getImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(ivCover);
        }

        tvOfferName.setText(offer.getName());

        tvBrandName.setText(clubStore.getBrandName());
        tvEstSaving.setText(String.format("Estimated savings: S$%.2f", offer.getEstSave()).replace(".00", ""));

        if (offer.isOnline()){

            ViewUtils.gone(findViewById(R.id.storeInfo));

        } else {

            BrandType brandType = dataManager.getBrandType(offer.getBrandTypeId());

            if (brandType != null){

                OfferType offerType =  dataManager.getOfferType(offer.getOfferTypeId(), brandType.getOfferTypes());

                if (offerType != null){

                    tvOfferType.setText(offerType.getName());

                }

            }

            updateStoreInfo();
            tvAvailableTime.setText(offer.getAvailability().getAvailableSlots());
        }


        updateRedeemButton();

    }

    private void updateStoreInfo(){

        tvStoreName.setText(clubStore.getStoreName());

        Store store = dataManager.getStoreById(clubStore.getStoreId());
        tvStoreAddress.setText(store != null ? store.getAddress() : "" );

    }

    private void updateRedeemButton(){

        if (offer.getAvailability().isAvailableNow()){

            cvRedeem.setCardBackgroundColor(Color.parseColor("#FA3E3F"));
            cvRedeem.setEnabled(true);

            if (offer.isOnline() && offer.getRedeemDetail() != null && offer.getRedeemDetail().getRedeemTimerStartedAt() != null){

                tvRedeem.setText("VIEW CODE");

            } else {

                tvRedeem.setText("REDEEM");
            }

        } else {

            cvRedeem.setCardBackgroundColor(Color.parseColor("#D3D3D3"));
            cvRedeem.setEnabled(false);

        }
    }


    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvRedeem)
    void redeemButtonClicked(){

        if (currentUser.getCurrentUser().getFuzzieClub().isMembership()){

            if (offer.isOnline()){

                if (offer.getRedeemDetail() != null && offer.getRedeemDetail().getRedeemTimerStartedAt() != null){

                    dataManager.offer = offer;
                    ClubOfferOnlineRedeemActivity_.intent(context).start();

                } else {

                    showFZAlert("READY TO REDEEM?", "After redeeming, your item will be marked as used in 24 hours and you won't be able to access the code thereafter.", "YES, REDEEM NOW", "No, cancel", R.drawable.ic_bear_dead_white);
                    showOnlineOfferConfirm = true;
                }


            } else {

                if (brand.isNoPinRedemption()){

                    checkLocationPermission();

                } else {

                    dataManager.clubStore = clubStore;
                    dataManager.offer = offer;

                    ClubOfferRedeemActivity_.intent(context).start();
                }

            }

        } else {

            showFZAlert("JOIN THE CLUB", "This offer is available for Fuzzie Club members only. Join the Club now to access all offers!", "LEARN MORE", "Close", R.drawable.ic_bear_club);

        }
    }

    @Click(R.id.tvRule)
    void ruleButtonClicked(){

        BrandTermsActivity_.intent(context).titleExtra("RULES OF USE").termsExtra(currentUser.getCurrentUser().getFuzzieClub().getRulesOfUse()).start();
    }

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (showClubMembershipConfirm){

            showClubMembershipConfirm = false;
            ClubSubscribeActivity_.intent(context).start();

        } else if (showOnlineOfferConfirm){

            showOnlineOfferConfirm = false;
            markAsOpened();
        }
    }

    private void markAsOpened(){

        displayProgressDialog("Loading...");

        FuzzieAPI.APIService().onlineClubOfferRedeem(currentUser.getAccesstoken(), offer.getId()).enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {

                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null){

                    offer = response.body();
                    dataManager.offer = offer;

                    updateRedeemButton();

                    ClubOfferOnlineRedeemActivity_.intent(context).start();

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_CLUB_OFFER_REDEEMED));


                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager, ClubOfferActivity.this);

                } else if (response.code() == 412){

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

                } else {

                    showFZAlert("Oops!", "Unknown error occurred: " + response.code(), "OK", "", R.drawable.ic_bear_dead_white);

                }
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {

                dismissProgressDialog();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

            }
        });
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();

        showClubMembershipConfirm = false;
        showOnlineOfferConfirm = false;
    }

    private void checkLocationPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST);

            } else {

                checkGPSEnabled();
            }

        } else {

            checkGPSEnabled();
        }

    }

    private void checkGPSEnabled(){

        final LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert mLocationManager != null;
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            startLocationService();

            goClubLocationConfirm();

        } else {

            LocationEnableActivity_.intent(context).start();
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }
    }

    private void startLocationService(){
        LocationService.getInstance().startLocationService(this);
    }

    private void goClubLocationConfirm(){

        dataManager.clubStore = clubStore;
        dataManager.offer = offer;

        ClubLocationConfirmActivity_.intent(context).start();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    checkGPSEnabled();
                }
            }
        }

    }

}
