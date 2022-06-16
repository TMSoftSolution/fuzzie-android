package sg.com.fuzzie.android.ui.me;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import timber.log.Timber;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.me.FriendListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;

/**
 * Created by mac on 6/8/17.
 */

@EActivity(R.layout.activity_my_friends_list)
public class MyFriendsListActivity extends BaseActivity {

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rlSearBar)
    RelativeLayout rlSearchBar;

    @ViewById(R.id.rlMain)
    RelativeLayout rlMain;

    @ViewById(R.id.etSearch)
    EditText etSearch;

    @ViewById(R.id.tvCancel)
    TextView tvCancel;

    @ViewById(R.id.rlMainView)
    RelativeLayout rlMainView;

    @ViewById(R.id.llSortView)
    LinearLayout llSortView;

    @ViewById(R.id.tvSort)
    TextView tvSort;

    @ViewById(R.id.ivSort)
    ImageView ivSort;

    @ViewById(R.id.llVirtualSearch)
    LinearLayout llVirtualSearch;

    @ViewById(R.id.rvFriends)
    RecyclerView rvFriends;

    @ViewById(R.id.llSearchEmpty)
    LinearLayout llSearchEmpty;

    @ViewById(R.id.llFaceboook)
    LinearLayout llFaceboook;

    @ViewById(R.id.llFriendsEmpty)
    LinearLayout llFriendsEmpty;

    @ViewById(R.id.tvInvite)
    TextView tvInvite;

    @ViewById(R.id.ivBack)
    ImageView ivBack;

    static final int REQUEST_FINISH = 1;

    CallbackManager callbackManager;

    public static final int SORT_BIRTH = 0;
    public static final int SORT_NAME = 1;
    public static final String[] MONTHS = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
    public  ArrayList<String> birth = new ArrayList<String>();

    int sort = SORT_BIRTH;

    List<User> friends = new ArrayList<>();

    private SectionedRecyclerViewAdapter sectionAdapter;

    @AfterViews
    public void calledAfterViewInjection() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Timber.d("Facebook Access Token: " + AccessToken.getCurrentAccessToken().getToken());
                String facebookToken = AccessToken.getCurrentAccessToken().getToken();

                displayProgressDialog("Loading...");
                Call<List<User>> call = FuzzieAPI.APIService().getFacebookLinkedFuzzieUsers(currentUser.getAccesstoken(), facebookToken);
                call.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        dismissProgressDialog();
                        if (response.code() == 200 && response.body() != null){

                            Intent refreshIntent = new Intent(BROADCAST_REFRESH_USER);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

                            friends = response.body();
                            setFriendsList();

                        } else if (response.code() == 417){
                            logoutUser(currentUser, dataManager,  MyFriendsListActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Timber.e(t.getLocalizedMessage());
                        dismissProgressDialog();
                    }
                });
            }

            @Override
            public void onCancel() {
                Timber.d("Facebook Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                showFZAlert("Facebook Error", error.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
            }
        });

        sectionAdapter = new SectionedRecyclerViewAdapter();
        rvFriends.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvFriends.setAdapter(sectionAdapter);

        if (dataManager.goFriendsListFromHome){
            ivBack.setImageResource(R.drawable.ic_close_black_24dp);
            fetchFriends();
        } else {
            ivBack.setImageResource(R.drawable.ic_arrow_back);
            if (currentUser.getCurrentUser().getFacebookId() != null){
                if (dataManager.getHome() != null && dataManager.getHome().getFuzzieFriends() != null){
                    friends = dataManager.getHome().getFuzzieFriends();
                    setFriendsList();
                } else {
                    fetchFriends();
                }
            } else {
                tvTitle.setText("FIND YOUR FRIENDS");
                llFaceboook.setVisibility(View.VISIBLE);
                rlMainView.setVisibility(View.GONE);
            }
        }


    }

    private void fetchFriends(){
        displayProgressDialog("Loading...");
        Call<List<User>> call = FuzzieAPI.APIService().getMyFuzzieFriends(currentUser.getAccesstoken());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

                    friends = response.body();
                    dataManager.getHome().setFuzzieFriends(friends);
                    setFriendsList();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MyFriendsListActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

                Timber.e(t.getLocalizedMessage());
                dismissProgressDialog();

            }
        });

    }

    private void setFriendsList(){
        if (friends != null && friends.size() != 0){
            tvTitle.setText("MY FUZZIE FRIENDS");
            llFaceboook.setVisibility(View.GONE);
            rlMainView.setVisibility(View.VISIBLE);
            llFriendsEmpty.setVisibility(View.GONE);
            sortByBirth();
        } else {
            tvTitle.setText("MY FUZZIE FRIENDS");
            llFaceboook.setVisibility(View.GONE);
            rlMainView.setVisibility(View.GONE);
            llFriendsEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.llSort)
    void sortButtonClicked(){
        switch (sort){
            case SORT_BIRTH:
                sort = SORT_NAME;
                sortByName();
                break;
            case SORT_NAME:
                sort = SORT_BIRTH;
                sortByBirth();
                break;
        }
    }

    private void sortByBirth(){
        ivSort.setImageResource(R.drawable.ic_az);

        if (sectionAdapter == null){
            sectionAdapter = new  SectionedRecyclerViewAdapter();
        } else {
            sectionAdapter.removeAllSections();
        }

        if (birth == null){
            birth = new ArrayList<String>();
        } else {
            birth.clear();
        }

        for (int i = 0 ; i < 13 ; i ++){
            List<User> friendsWithBirth = getFriendsWithBirth(i, friends);

            if (friendsWithBirth.size() > 0){
                sectionAdapter.addSection(new FriendListItem(birth.get(i), friendsWithBirth));
            }
        }

        sectionAdapter.notifyDataSetChanged();
    }

    private void sortByName(){
        ivSort.setImageResource(R.drawable.ic_birthday);

        if (sectionAdapter == null){
            sectionAdapter = new  SectionedRecyclerViewAdapter();
        } else {
            sectionAdapter.removeAllSections();
        }

        for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
            List<User> friendsWithLetter = getFriendsWithLetter(alphabet, friends);

            if (friendsWithLetter.size() > 0) {
                sectionAdapter.addSection(new FriendListItem(String.valueOf(alphabet), friendsWithLetter));
            }
        }

        sectionAdapter.notifyDataSetChanged();
    }

    @Click(R.id.llVirtualSearch)
    void searchButtonClicked(){
        rlMain.setVisibility(View.GONE);
        rlSearchBar.setVisibility(View.VISIBLE);
        llSortView.setVisibility(View.GONE);
        tvInvite.setVisibility(View.GONE);
        etSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    @Click(R.id.tvCancel)
    void cancelClicked(){
        etSearch.setText("");
        rlMain.setVisibility(View.VISIBLE);
        rlSearchBar.setVisibility(View.GONE);
        llSortView.setVisibility(View.VISIBLE);
        tvInvite.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.llSortView.getWindowToken(), 0);
    }

    @AfterTextChange(R.id.etSearch)
    void searchEditTextChanged(){

        if (sectionAdapter == null){
            sectionAdapter = new  SectionedRecyclerViewAdapter();
        } else {
            sectionAdapter.removeAllSections();
        }

        List<User> friends = new ArrayList<>();

        for (User friend : this.friends) {
            if (friend.getName().toLowerCase().contains(etSearch.getText().toString().toLowerCase())) {
                friends.add(friend);
            }
        }

        if (friends.size() == 0){
            if (!etSearch.getText().toString().equals("")){
                llSearchEmpty.setVisibility(View.VISIBLE);
                rvFriends.setVisibility(View.GONE);
            } else {
                llSearchEmpty.setVisibility(View.GONE);
                rvFriends.setVisibility(View.VISIBLE);
            }

        } else {
            llSearchEmpty.setVisibility(View.GONE);
            rvFriends.setVisibility(View.VISIBLE);

            if (sort == SORT_NAME){
                for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
                    List<User> friendsWithLetter = getFriendsWithLetter(alphabet, friends);

                    if (friendsWithLetter.size() > 0) {
                        sectionAdapter.addSection(new FriendListItem(String.valueOf(alphabet), friendsWithLetter));
                    }
                }

                sectionAdapter.notifyDataSetChanged();

            } else if (sort == SORT_BIRTH){
                for (int i = 0 ; i <= MONTHS.length ; i ++){
                    List<User> friendsWithBirth = getFriendsWithBirth(i, friends);

                    if (friendsWithBirth.size() > 0){
                        sectionAdapter.addSection(new FriendListItem(birth.get(i), friendsWithBirth));
                    }
                }

                sectionAdapter.notifyDataSetChanged();
            }

        }
    }

    private List<User> getFriendsWithLetter(char letter, List<User> friends) {

        List<User> friendsWithLetter = new ArrayList<>();

        for (User friend : friends) {
            if (friend.getName().charAt(0) == letter) {
                friendsWithLetter.add(friend);
            }
        }

        Collections.sort(friendsWithLetter, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return friendsWithLetter;
    }

    private List<User> getFriendsWithBirth(int type, List<User> friends){

        List<User> friendsWithBirth = new ArrayList<>();

        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DateTime today = new DateTime(new Date());

        for (User friend : friends){

            if (friend.getBirthdate() != null && friend.getBirthdate() != ""){
                try {
                    DateTime dateObj = new DateTime(curFormatter.parse(friend.getBirthdate()));
                    if (type == 0){
                        if (!birth.contains("BIRTHDAYS TODAY")){
                            birth.add(type, "BIRTHDAYS TODAY");
                        }
                        if (today.getDayOfMonth() == dateObj.getDayOfMonth() && today.getMonthOfYear() == dateObj.getMonthOfYear()){
                            friendsWithBirth.add(friend);
                        }
                    } else if (type == 1){
                        if (!birth.contains("BIRTHDAYS THIS MONTH")){
                            birth.add(type, "BIRTHDAYS THIS MONTH");
                        }
                        if (today.getMonthOfYear() == dateObj.getMonthOfYear()){
                            if (today.getDayOfMonth() != dateObj.getDayOfMonth()){
                                friendsWithBirth.add(friend);
                            }
                        }
                    } else {
                        String title = "BIRTHDAYS IN " + MONTHS[(today.getMonthOfYear() + type - 2) % 12];
                        if (!birth.contains(title)){
                            birth.add(type, title);
                        }
                        switch (type){
                            case 2:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 1) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 3:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 2) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 4:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 3) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 5:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 4) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 6:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 5) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 7:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 6) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 8:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 7) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 9:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 8) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 10:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 9) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 11:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 10) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                            case 12:
                                if (dateObj.getMonthOfYear() % 12 == (today.getMonthOfYear() + 11) % 12){
                                    friendsWithBirth.add(friend);
                                }
                                break;
                        }
                    }

                } catch (ParseException ignored) {

                }
            }

        }

        return friendsWithBirth;
    }

    @Click(R.id.cvFacebook)
    void facebookButtonClicked(){
        Timber.d("Facebook Button Clicked");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_friends","user_birthday"));

    }

    @Click({R.id.cvInvite, R.id.tvInvite})
    void inviteButtonClicked(){
        InviteFriendsActivity_.intent(context).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FINISH) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }
}
