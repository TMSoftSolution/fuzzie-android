package sg.com.fuzzie.android.ui.giftbox;

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
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.LocationVerifyResult;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.shop.BrandStoreLocationsActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_gift_store_location_verify)
public class GiftStoreLocationVerifyActivity extends BaseActivity {

    private static final int REQUEST_CHANGE_STORE               = 0;

    private Store store;
    private Brand brand;

    @Extra
    String giftId;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvStoreName)
    TextView tvStoreName;

    @ViewById(R.id.tvStoreAddress)
    TextView tvStoreAddress;

    @AfterViews
    void callAfterViewInjection(){

        if (giftId ==null || giftId.equals("")) return;

        Gift gift = dataManager.getGiftById(giftId, false);
        if (gift == null) return;

        brand = gift.getBrand();
        if (brand == null) return;

        store = brand.getStores().get(0).getStore();
        if (store == null) return;

        updateStoreInfo();
    }

    private void updateStoreInfo(){

        tvStoreName.setText(store.getName());
        tvStoreAddress.setText(store.getAddress());
    }

    private void finishWithTransition(){

        finish();
        overridePendingTransition(R.anim.stay, R.anim.slid_out_up);
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

                    GiftRedeemGPSActivity_.intent(context).giftId(giftId).storeNameExtra(store.getName()).start();

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

        BrandStoreLocationsActivity_.intent(context).brandId(brand.getId()).clickable(true).startForResult(REQUEST_CHANGE_STORE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_CHANGE_STORE){

                if (data != null){

                    String storeString = data.getStringExtra("extra_store");

                    if (storeString != null && !storeString.equals("")){

                        store = Store.fromJSON(storeString);
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

    @Click(R.id.ivClose)
    void closeButtonClicked(){

        finishWithTransition();
    }
}
