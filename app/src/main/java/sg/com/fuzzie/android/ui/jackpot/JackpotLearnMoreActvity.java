package sg.com.fuzzie.android.ui.jackpot;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

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
import sg.com.fuzzie.android.api.models.RedeemDetail;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.brand.RedeemDetailListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

/**
 * Created by joma on 10/13/17.
 */

@EActivity(R.layout.activity_jackpot_learn_more)
public class JackpotLearnMoreActvity extends BaseActivity {

    @Extra
    boolean fromRedPacket;

    @Extra
    boolean clubFaqs;

    @Extra
    boolean clubTerms;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.rvLearnMore)
    RecyclerView rvLearnMore;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    FastItemAdapter<RedeemDetailListItem> redeemDetailsAdapter;
    private List<RedeemDetail> redeemDetails;


    @AfterViews
    public void calledAfterViewInjection() {

        if (fromRedPacket){
            tvTitle.setText("LUCKY PACKETS FAQ");
        }

        redeemDetailsAdapter = new FastItemAdapter();

        rvLearnMore.setLayoutManager(new LinearLayoutManager(context));
        rvLearnMore.setAdapter(redeemDetailsAdapter);
        redeemDetailsAdapter.clear();

        if (clubTerms || clubFaqs){

            if (clubTerms){

                tvTitle.setText("TERMS & CONDITIONS");
                redeemDetails = dataManager.getClubHome().getTerms();

            } else {

                tvTitle.setText("FUZZIE CLUB FAQ");
                redeemDetails = dataManager.getClubHome().getFaqs();
            }

            showRedeemDetails();

        } else {

            showLoader();

            Call<List<RedeemDetail>> call = null;

            if (fromRedPacket){
                call = FuzzieAPI.APIService().getRedPacketLearnMore(currentUser.getAccesstoken());
            } else {
                call = FuzzieAPI.APIService().getJackpotLearnMore(currentUser.getAccesstoken());
            }
            call.enqueue(new Callback<List<RedeemDetail>>() {
                @Override
                public void onResponse(Call<List<RedeemDetail>> call, Response<List<RedeemDetail>> response) {

                    hideLoader();

                    if (response.code() == 200 && response.body() != null){

                        redeemDetails = response.body();
                        showRedeemDetails();

                    } else if (response.code() == 417){

                        logoutUser(currentUser, dataManager,  JackpotLearnMoreActvity.this);
                    }
                }

                @Override
                public void onFailure(Call<List<RedeemDetail>> call, Throwable t) {
                    hideLoader();
                }
            });
        }


    }

    private void showRedeemDetails(){

        if (redeemDetails == null) return;

        for (RedeemDetail redeemDetail : redeemDetails) {
            redeemDetailsAdapter.add(new RedeemDetailListItem(redeemDetail));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }
}
