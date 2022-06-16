package sg.com.fuzzie.android.ui.club;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubOfferRedeemedItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

@EActivity(R.layout.activity_club_setting)
public class ClubSettingActivity extends BaseActivity {

    FastItemAdapter<ClubOfferRedeemedItem> redeemAdapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.rvRedeem)
    RecyclerView rvRedeem;

    @ViewById(R.id.ivAvatar)
    ImageView ivAvatar;

    @ViewById(R.id.tvName)
    TextView tvName;

    @ViewById(R.id.tvMember)
    TextView tvMember;

    @ViewById(R.id.tvMemberShip)
    TextView tvMemberShip;

    @ViewById(R.id.tvRedeemCount)
    TextView tvRedeemCount;

    @AfterViews
    void callAfterViewInjection(){

        setUI();

    }

    private void setUI(){

        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                loadOfferRedeemed();
            }
        });
        swipeContainer.setDistanceToTriggerSync(300);

        redeemAdapter = new FastItemAdapter<>();

        rvRedeem.setLayoutManager(new LinearLayoutManager(context));
        rvRedeem.setAdapter(redeemAdapter);

        showProfileInfo();

        if (dataManager.getOfferRedeemed() != null){

            showOffersRedeemed();

        } else {

            loadOfferRedeemed();
        }

    }

    private void showProfileInfo(){

        tvName.setText(String.format("%s %s", currentUser.getCurrentUser().getFirstName(), currentUser.getCurrentUser().getLastName()));
        Picasso.get()
                .load(currentUser.getCurrentUser().getProfileImage())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(ivAvatar);

        String expDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date subDate = null;
        try {
            subDate = dateFormat.parse(currentUser.getCurrentUser().getFuzzieClub().getSubscriptionDate());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM yyyy");
        expDate = timeFormat.format(subDate);

        tvMember.setText(String.format("Member since %s", expDate));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(subDate);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        expDate = timeFormat.format(calendar.getTime());
        tvMemberShip.setText(expDate);

    }

    private void showOffersRedeemed(){

        tvRedeemCount.setText(String.format("OFFER REDEEMED (%d)", dataManager.getOfferRedeemed().size()));

        if (redeemAdapter == null){

            redeemAdapter = new FastItemAdapter<>();

        } else {

            redeemAdapter.clear();
        }

        for (Offer offer : dataManager.getOfferRedeemed()){

            redeemAdapter.add(new ClubOfferRedeemedItem(offer));
        }
    }

    private void loadOfferRedeemed(){

        FuzzieAPI.APIService().getOfferRedeemed(currentUser.getAccesstoken()).enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {

                if (response.code() == 200 && response.body() != null){

                    dataManager.setOfferRedeemed(response.body());

                    showOffersRedeemed();
                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {

            }
        });

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvExtend)
    void extendButtonClicked(){
        ClubSubscribePaymentActivity_.intent(context).start();
    }
}
