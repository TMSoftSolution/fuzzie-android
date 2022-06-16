package sg.com.fuzzie.android.ui.shop;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.RedeemDetail;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.RedeemDetailListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by faruq on 03/02/17.
 */

@EActivity(R.layout.activity_brand_redeem_details)
public class BrandRedeemDetailsActivity extends BaseActivity {

    Brand brand;

    FastItemAdapter<RedeemDetailListItem> redeemDetailsAdapter;

    @Extra
    String brandId;

    @Extra
    boolean isPowerUpCardMode;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvHowToRedeem)
    RecyclerView rvHowToRedeem;

    @AfterViews
    public void calledAfterViewInjection() {

        redeemDetailsAdapter = new FastItemAdapter();

        rvHowToRedeem.setLayoutManager(new LinearLayoutManager(context));
        rvHowToRedeem.setAdapter(redeemDetailsAdapter);
        redeemDetailsAdapter.clear();

        showLoader();

        Call<List<RedeemDetail>> call;

        if (isPowerUpCardMode){

            call = FuzzieAPI.APIService().getRedeemDetails(currentUser.getAccesstoken());

        } else {

            call = FuzzieAPI.APIService().getRedeemDetails(currentUser.getAccesstoken(), brandId);

        }

        call.enqueue(new Callback<List<RedeemDetail>>() {
            @Override
            public void onResponse(Call<List<RedeemDetail>> call, Response<List<RedeemDetail>> response) {
                hideLoader();

                if (response.code() == 200 && response.body() != null){
                    for (RedeemDetail redeemDetail : response.body()) {
                        redeemDetailsAdapter.add(new RedeemDetailListItem(redeemDetail));
                    }
                    redeemDetailsAdapter.notifyAdapterDataSetChanged();
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  BrandRedeemDetailsActivity.this);
                }


            }

            @Override
            public void onFailure(Call<List<RedeemDetail>> call, Throwable t) {
                hideLoader();
            }
        });
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

}
