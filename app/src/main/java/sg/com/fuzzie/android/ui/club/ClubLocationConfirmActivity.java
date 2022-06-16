package sg.com.fuzzie.android.ui.club;

import android.content.Intent;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.LocationVerifyResult;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_club_location_confirm)
public class ClubLocationConfirmActivity extends BaseActivity {

    private static final int REQUEST_CHANGE_STORE               = 0;

    private ClubStore clubStore;
    private Offer offer;
    private Store store;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @Extra
    boolean isFlashSaleOffer;

    @ViewById(R.id.tvStoreName)
    TextView tvStoreName;

    @ViewById(R.id.tvStoreAddress)
    TextView tvStoreAddress;


    @AfterViews
    void callAfterViewInjection(){

        clubStore = dataManager.clubStore;
        offer = dataManager.offer;

        if (clubStore == null || offer == null) return;

        updateStoreInfo();
    }

    private void updateStoreInfo(){

        tvStoreName.setText(clubStore.getStoreName());

        store = dataManager.getStoreById(clubStore.getStoreId());
        tvStoreAddress.setText(store != null ? store.getAddress() : "" );
    }

    private void finishWithTransition(){

        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);
    }

    @Click(R.id.ivClose)
    void closeButtonClicked(){

        finishWithTransition();
    }

    @Click(R.id.cvContinue)
    void continueButtonClicked(){

        if (dataManager.myLocation == null || store == null) return;

        showLocationVerifyDialog();

        FuzzieAPI.APIService().locationVerify(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), store.getId()).enqueue(new Callback<LocationVerifyResult>() {
            @Override
            public void onResponse(Call<LocationVerifyResult> call, Response<LocationVerifyResult> response) {

                hideLocationVerifyDialog();

                if (response.code() == 200 && response.body() != null && response.body().isNearest()){

                    ClubOfferRedeemActivity_.intent(context).start();

                    finish();

                } else {

                    showLocationNoMatchDialog();
                }
            }

            @Override
            public void onFailure(Call<LocationVerifyResult> call, Throwable t) {

                hideLocationVerifyDialog();
                showLocationNoMatchDialog();
            }
        });
    }

    @Click(R.id.btnChange)
    void changeButtonClicked(){

        changeStoreLocation();
    }


    private void changeStoreLocation(){

        if (isFlashSaleOffer){

            if (offer.getClubStores() != null && offer.getClubStores().size() > 0){

                dataManager.setClubMoreStores(offer.getClubStores());
            }
        }

        if (dataManager.getClubMoreStores()  == null) return;

        ClubStoresLocationActivity_.intent(context).startForResult(REQUEST_CHANGE_STORE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_CHANGE_STORE){

                if (data != null){

                    if (data.getBooleanExtra("club_store_selected", false)){

                        clubStore = dataManager.clubStore;
                        updateStoreInfo();
                    }
                }
            }
        }
    }

    @Override
    public void FZLocationNoMatchDialogChangeButtonClicked() {
        super.FZLocationNoMatchDialogChangeButtonClicked();

        changeStoreLocation();
    }

    @Override
    public void onBackPressed() {

        finishWithTransition();
    }
}
