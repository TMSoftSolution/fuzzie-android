package sg.com.fuzzie.android.ui.me;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.me.ReferralItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 6/4/17.
 */

@EActivity(R.layout.activity_referrals)
public class ReferralsActivity extends BaseActivity {

    private SectionedRecyclerViewAdapter referralAdapter;
    List<User> users = new ArrayList<User>();

    @Extra
    boolean isClubReferral;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.rvReferrals)
    RecyclerView rvReferrals;

    @ViewById(R.id.emptyView)
    View emptyView;

    @AfterViews
    public void calledAfterViewInjection() {

        referralAdapter = new SectionedRecyclerViewAdapter();
        rvReferrals.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvReferrals.setAdapter(referralAdapter);

        getReferralsUsers();

        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                getReferralsUsers();
            }
        });

    }

    private void setReferralAdapter(){

        if (users == null || users.size() == 0){

            emptyView.setVisibility(View.VISIBLE);
            rvReferrals.setVisibility(View.GONE);

        } else {

            if (referralAdapter == null){
                referralAdapter =  new SectionedRecyclerViewAdapter();
            } else {
                referralAdapter.removeAllSections();
            }

            if (getAwardedUsers().size() != 0){
                referralAdapter.addSection(new ReferralItem("SUCCESSFUL REFERRALS", getAwardedUsers(), isClubReferral));
            }
            if (getPendingUsers().size() != 0){
                referralAdapter.addSection(new ReferralItem(isClubReferral ? "PENDING FIRST PURCHASE" :"PENDING MINIMUM SPEND", getPendingUsers(), isClubReferral));
            }

            referralAdapter.notifyDataSetChanged();

//            Collections.sort(users, new Comparator<User>() {
//                @Override
//                public int compare(User o1, User o2) {
//                    if (o1.getFriend().compareTo(o2.getFriend()) != 0){
//                        return o1.getFriend().compareTo(o2.getFriend());
//                    } else {
//                        int comp = o1.getAvatar().compareTo(o2.getAvatar());
//                        if (comp != 0){
//                            return comp;
//                        } else {
//                            return o1.getAvatarBear().compareTo(o2.getAvatarBear());
//                        }
//                    }
//                }
//            });
//            Collections.reverse(users);

        }

    }

    private List<User> getAwardedUsers(){
        List<User> mUsers = new ArrayList<>();
        for (User user : users){
            if (user.getStatus().equals("referer_awarded_bonus")){
                mUsers.add(user);
            }
        }

        return mUsers;
    }

    private List<User> getPendingUsers(){
        List<User> mUsers = new ArrayList<>();
        for (User user : users){
            if (user.getStatus().equals("signed_up")){
                mUsers.add(user);
            }
        }
        return mUsers;
    }

    // Get Referrals Users
    private void getReferralsUsers(){

        displayProgressDialog("Loading...");

        Call<List<User>> call = FuzzieAPI.APIService().getReferralsUsers(currentUser.getAccesstoken());

        if (isClubReferral){

            call = FuzzieAPI.APIService().getClubReferralsUsers(currentUser.getAccesstoken());

        }

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

                    users = response.body();
                    for (User user : users){
                        if (user.getAvatar() == null){
                            user.setAvatar("");
                        }
                        user.setAvatarBear(FuzzieUtils.generateRandomAvatarId());
                    }
                    setReferralAdapter();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  ReferralsActivity.this);
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

                dismissProgressDialog();

            }
        });

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }
}
