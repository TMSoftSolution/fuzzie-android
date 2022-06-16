package sg.com.fuzzie.android.ui.shop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sg.com.fuzzie.android.api.models.JackpotResults;
import sg.com.fuzzie.android.utils.HomeBrandsType;
import timber.log.Timber;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.MainActivity;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.CouponTemplate;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.api.models.Friend;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.api.models.ProfileSetting;
import sg.com.fuzzie.android.core.BaseFragment;
import sg.com.fuzzie.android.items.shop.BannerItem;
import sg.com.fuzzie.android.items.shop.CreditBarItem;
import sg.com.fuzzie.android.items.shop.MiniBannerItem;
import sg.com.fuzzie.android.items.shop.ShopBrandsItem;
import sg.com.fuzzie.android.items.shop.ShopCategoriesItem;
import sg.com.fuzzie.android.items.shop.ShopJackpotItem;
import sg.com.fuzzie.android.items.shop.ShopJackpotTeasingItem;
import sg.com.fuzzie.android.items.shop.ShopUpcomingBirthdatesItem;
import sg.com.fuzzie.android.ui.me.MyFriendsListActivity_;
import sg.com.fuzzie.android.ui.payments.EmptyBagActivity_;
import sg.com.fuzzie.android.ui.payments.ShoppingBagActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FZAlarmManager;
import sg.com.fuzzie.android.utils.FZTimer_;
import sg.com.fuzzie.android.utils.FuzzieData;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_HOME_LIKE_CLICKED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_START;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESHED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_HOME;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_SHOPPING_BAG;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_USER;

/**
 * Created by nurimanizam on 15/12/16.
 */

@SuppressWarnings("unchecked")
@EFragment(R.layout.fragment_shop)
public class ShopFragment extends BaseFragment implements ShopUpcomingBirthdatesItem.ShopUpcomingBirthdatesItemListener{

    Home home;
    FastItemAdapter shopAdapter;
    CreditBarItem creditBarItem;

    BroadcastReceiver broadcastReceiver;
    CallbackManager callbackManager;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvShoppingBag)
    TextView tvShoppingBag;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @ViewById(R.id.rvShop)
    RecyclerView rvShop;

    @ViewById(R.id.llPowerUpTimer)
    LinearLayout llPowerUpTimer;

    @ViewById(R.id.tvPowerUpTimer)
    TextView tvPowerUpTimer;

    @ViewById(R.id.rlEmpty)
    RelativeLayout rlEmpty;

    @ViewById(R.id.id_fragment_shop)
    View container;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if (currentUser != null
                    && currentUser.getCurrentUser() != null
                    && currentUser.getCurrentUser().getPowerUpExpirationTime() != null
                    && currentUser.getCurrentUser().getPowerUpExpirationTime().length() > 0
                    && tvPowerUpTimer != null
                    && llPowerUpTimer != null) {

                llPowerUpTimer.setVisibility(View.VISIBLE);

                DateTimeFormatter parser = ISODateTimeFormat.dateTime();

                DateTime now = new DateTime();
                DateTime expiryDate = parser.parseDateTime(currentUser.getCurrentUser().getPowerUpExpirationTime());

                long milliseconds = expiryDate.getMillis() - now.getMillis();
                int hours = (int) (milliseconds/3600000);
                int mins = (int) (milliseconds/60000);
                mins %= 60;
                int secs = (int) (milliseconds/1000);
                secs %= 60;

                tvPowerUpTimer.setText(String.format("%02d:%02d:%02d",hours,mins,secs));

//                Timber.d("Power Up Timer: " + milliseconds);
            } else if (llPowerUpTimer != null) {
                llPowerUpTimer.setVisibility(View.GONE);
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        timerHandler.postDelayed(timerRunnable, 0);
        refreshShoppingBag();
        refreshCredits();
        if (dataManager.goFriendsListFromHome){
            setUI();
            dataManager.goFriendsListFromHome = false;
        }
        if (home != null){
            FZTimer_.getInstance_(context).startTimer();

            if (home.getJackpot() != null){
                DateTimeFormatter parser = ISODateTimeFormat.dateTime();
                DateTime now = new DateTime();
                DateTime drawTime = parser.parseDateTime(home.getJackpot().getDrawTime());

                long leftTime = drawTime.getMillis() - now.getMillis();
                if (leftTime <= 0){
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_IS_START));
                }
            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        if (home != null){
            FZTimer_.getInstance_(context).stopTimer();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @AfterViews
    public void calledAfterViewInjection() {

        shopAdapter = new FastItemAdapter();
        rvShop.setLayoutManager(new LinearLayoutManager(context));
        rvShop.setAdapter(shopAdapter);

        refreshCredits();
        refreshShoppingBag();
        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                fetchUserProfile();
                loadHomeData(true);
                loadFacebookFriends();
            }
        });
        swipeContainer.setDistanceToTriggerSync(300);

        if (dataManager.getHome() != null) {
            home = dataManager.getHome();
        }

        if (home == null) {
            loadHomeData(true);
        } else {
            setUI();
        }

        if (dataManager.getFriends() == null){
            loadFacebookFriends();
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_REFRESHED_USER)) {
                    refreshCredits();
                }

                if (intent.getAction().equals(BROADCAST_REFRESHED_HOME)) {
                    home = dataManager.getHome();
                    setUI();
                }

                if (intent.getAction().equals(BROADCAST_REFRESHED_SHOPPING_BAG)) {
                    refreshShoppingBag();
                }

                if (intent.getAction().equals(Constants.BROADCAST_JACKPOT_IS_START)){
                    if (dataManager.getHome() != null && dataManager.getHome().getJackpot() != null){
                        dataManager.getHome().getJackpot().setLive(true);
                        updateJackpot();
                    }
                }

                if (intent.getAction().equals(Constants.BROADCAST_JACKPOT_IS_END)){
                    updateJackpot();
                    loadHomeData(false);
                }

            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_USER));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_REFRESHED_SHOPPING_BAG));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_REFRESHED_HOME));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_JACKPOT_IS_START));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_JACKPOT_IS_END));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_JACKPOT_IS_WON));

        facebookConnect();

    }

    private void setUI(){

        if (home == null) return;

        shopAdapter.clear();
        if (currentUser.getCurrentUser() != null && currentUser.getCurrentUser().getWallet().getBalance() > 0){
            creditBarItem = new CreditBarItem(currentUser);
            shopAdapter.add(creditBarItem);
        }

        if (home.getBanners() != null && home.getBanners().size() > 0){
            shopAdapter.add(new BannerItem(home.getBanners()));
        }

        if (home.getRecommendedBrands() != null && home.getRecommendedBrands().size() > 0){
            shopAdapter.add(new ShopBrandsItem(home, HomeBrandsType.RECOMMENDED, currentUser.getAccesstoken()));
        }

        if (home.getClubBrands() != null && home.getClubBrands().size() > 0){
            shopAdapter.add(new ShopBrandsItem(home, HomeBrandsType.CLUB, currentUser.getAccesstoken()));
        }

        if (dataManager.getMiniBanners("Referral Page").size() > 0){
            shopAdapter.add(new MiniBannerItem(dataManager.getMiniBanners("Referral Page")));
        }

        if (dataManager.getMiniBanners("RedPacket").size() > 0){
            shopAdapter.add(new MiniBannerItem(dataManager.getMiniBanners("RedPacket")));
        }

        if (dataManager.getMiniBanners("PowerUpPacks").size() > 0){
            shopAdapter.add(new MiniBannerItem(dataManager.getMiniBanners("PowerUpPacks")));
        }

        if (home.getJackpot().isEnabled()){
            shopAdapter.add(new ShopJackpotItem(home));
        } else {
            shopAdapter.add(new ShopJackpotTeasingItem());
        }

        if (home.getPopularBrands() != null && home.getPopularBrands().size() > 0){
            shopAdapter.add(new ShopBrandsItem(home, HomeBrandsType.TRENDING, currentUser.getAccesstoken()));
        }

        if (currentUser.getCurrentUser().getFacebookId() == null || (currentUser.getCurrentUser().getFacebookId() != null && home.getFuzzieFriends() != null && home.getFuzzieFriends().size() != 0)){
            shopAdapter.add(new ShopUpcomingBirthdatesItem(home, this));
        }

        if (home.getNewBrands() != null && home.getNewBrands().size() > 0){
            shopAdapter.add(new ShopBrandsItem(home, HomeBrandsType.NEW, currentUser.getAccesstoken()));
        }

        if (home.getTopBrands() != null && home.getTopBrands().size() > 0){
            shopAdapter.add(new ShopBrandsItem(home, HomeBrandsType.TOP, currentUser.getAccesstoken()));
        }


        if (home.getCategories() != null && home.getCategories().size() > 0){
            shopAdapter.add(new ShopCategoriesItem(home));
        }

        ((MainActivity)mActivity).updateWalletBadge();

        ProfileSetting profileSetting = currentUser.getCurrentUser().getSettings();

//        if (profileSetting.isShowFuzzieClubInstructions()){
//            updateShowFuzzieClubInstructions();
//            showCoachMarkDialog();
//        }

        if (profileSetting.isJackpotDrawNotification() && home != null && home.getJackpot().isEnabled() && home.getJackpot().getDrawTime() != null && !home.getJackpot().getDrawTime().equals("")){
            FZAlarmManager.getInstance().scheduleJackpotLiveDrawNotification(context);
        }

        if (home != null && home.getJackpot().isEnabled() && home.getJackpot().getDrawTime() != null && !home.getJackpot().getDrawTime().equals("")){
            FZAlarmManager.getInstance().scheduleJackpotRemainderNotification(context);
        }
    }

    private void facebookConnect(){
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Timber.d("Facebook Access Token: " + AccessToken.getCurrentAccessToken().getToken());
                String facebookToken = AccessToken.getCurrentAccessToken().getToken();

                FZUser user = currentUser.getCurrentUser();
                user.setFacebookId(AccessToken.getCurrentAccessToken().getUserId());
                currentUser.setCurrentUser(user);

                displayProgressDialog("Saving...");
                Call<List<User>> call = FuzzieAPI.APIService().getFacebookLinkedFuzzieUsers(currentUser.getAccesstoken(), facebookToken);
                call.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        dismissProgressDialog();
                        if (response.code() == 200 && response.body() != null){
                            dataManager.goFriendsListFromHome = true;
                            MyFriendsListActivity_.intent(context).start();

                        } else if (response.code() == 417){
                            logoutUser(currentUser, dataManager,  mActivity);
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
            }
        });
    }

    @Click(R.id.ivSearch)
    void searchButtonClicked() {
        Timber.d("Search Button Clicked");
        if (home != null) {
            BrandSearchActivity_.intent(context).start();
        }
    }

    @Click(R.id.ivLike)
    void likeButtonClicked(){
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_HOME_LIKE_CLICKED));
    }

    @Click(R.id.llShoppingBag)
    void shoppingBagButtonClicked() {
        if (dataManager.getShoppingBag() != null) {
            if(dataManager.getShoppingBag().getItems() != null) {
                if(dataManager.getShoppingBag().getItems().size() > 0 ) {
                    Timber.d("Bag","Size: " + dataManager.getShoppingBag().getItems().size());
                    ShoppingBagActivity_.intent(context).start();
                    return;
                }
            }
        }
        EmptyBagActivity_.intent(context).start();
    }

    @Click(R.id.cvRefresh)
    void refreshButtonClicked(){
        fetchUserProfile();
        loadHomeData(true);
        loadFacebookFriends();
    }

    void refreshCredits() {
        if (currentUser.getCurrentUser() != null) {
            if (currentUser.getCurrentUser().getWallet().getBalance() > 0.0){
                if (shopAdapter != null && shopAdapter.getItemCount() != 0 && shopAdapter.getAdapterItem(0) != null && shopAdapter.getAdapterItem(0) instanceof CreditBarItem){
                    shopAdapter.notifyAdapterItemChanged(0);
                } else if (shopAdapter != null && shopAdapter.getItemCount() != 0 && shopAdapter.getAdapterItem(0) != null && !(shopAdapter.getAdapterItem(0) instanceof CreditBarItem)){
                    setUI();
                }

            } else {
                if (shopAdapter.getAdapterItemCount() != 0){
                    if (shopAdapter.getAdapterItem(0) instanceof CreditBarItem){
                        shopAdapter.remove(0);
                    }
                }

            }
        } else {
            if (shopAdapter.getAdapterItemCount() != 0){
                if (shopAdapter.getAdapterItem(0) instanceof CreditBarItem){
                    shopAdapter.remove(0);
                }
            }
        }
    }

    void refreshShoppingBag() {
        if (tvShoppingBag == null) return;
        if (dataManager.getShoppingBag() != null) {
            if (dataManager.getShoppingBag().getItems().size() > 0) {
                tvShoppingBag.setVisibility(View.VISIBLE);
                tvShoppingBag.setText(String.format("%d", dataManager.getShoppingBag().getItems().size()));
            } else {
                tvShoppingBag.setVisibility(View.GONE);
            }
        } else {
            tvShoppingBag.setVisibility(View.GONE);
        }
    }

    private void updateJackpot(){
        if (shopAdapter != null && shopAdapter.getItemCount() != 0){
            for (int i = 0 ; i < shopAdapter.getItemCount() ; i ++){
                if (shopAdapter.getAdapterItem(i) != null && shopAdapter.getAdapterItem(i) instanceof ShopJackpotItem){
                    shopAdapter.notifyAdapterItemChanged(i);
                }
            }
        }
    }

    void loadHomeData(boolean showLoader) {

        if (rlEmpty != null){
            rlEmpty.setVisibility(View.GONE);
        }
        if (swipeContainer != null && showLoader){
            swipeContainer.setVisibility(View.VISIBLE);
        }

        Call<Home> call = FuzzieAPI.APIService().getHomeData(currentUser.getAccesstoken());
        showLoader();

        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(Call<Home> call, Response<Home> response) {
                hideLoader();

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }

                if (response.code() == 200 && response.body() != null) {

                    dataManager.setHome(response.body());

                    if (dataManager.getHome().getJackpot() != null){

                        // save next draw time to user preference
                        currentUser.setJackpotLiveDrawDateTime(dataManager.getHome().getJackpot().getDrawTime());

                        // save draw id to user preference
                        dataManager.setJackpotDrawId(dataManager.getHome().getJackpot().getDrawId());
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESHED_HOME));

                    loadCouponTemplates();
                    loadJackpotResult();

                    // Schedule Friends Birthday Notification
                    if (dataManager.getHome().getFuzzieFriends() != null && dataManager.getHome().getFuzzieFriends().size() > 0){

                        FZAlarmManager.getInstance().scheduleFriendsBirthdayNotification(context, dataManager.getHome().getFuzzieFriends());
                    }

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  mActivity);
                } else {
                    if (rlEmpty != null){
                        rlEmpty.setVisibility(View.VISIBLE);
                    }
                    if (swipeContainer != null){
                        swipeContainer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Home> call, Throwable t) {
                hideLoader();
                Timber.d("Home Data Error: " + t.getLocalizedMessage());
                if (rlEmpty != null){
                    rlEmpty.setVisibility(View.VISIBLE);
                }

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                    swipeContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadFacebookFriends(){
        Call<List<Friend>> call = FuzzieAPI.APIService().getFacebookFriends(currentUser.getAccesstoken());
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                if (response.code() == 200 && response.body() != null){
                    dataManager.setFriends(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                if (t.getLocalizedMessage() != null) Timber.d(t.getLocalizedMessage());
            }
        });
    }

    public void fetchUserProfile() {
        Call<JsonObject> call = FuzzieAPI.APIService().getMyDetails(currentUser.getAccesstoken());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("user") != null) {

                    FZUser user = FZUser.fromJSON(response.body().getAsJsonObject("user").toString());
                    currentUser.setCurrentUser(user);

                    Intent intent = new Intent(Constants.BROADCAST_REFRESHED_USER);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  mActivity);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void loadCouponTemplates(){

        Call<CouponTemplate> call = FuzzieAPI.APIService().getCouponTemplates(currentUser.getAccesstoken());
        call.enqueue(new Callback<CouponTemplate>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(@NonNull Call<CouponTemplate> call, @NonNull Response<CouponTemplate> response) {

                if (response.code() == 200 && response.body() != null){
                    fetchCoupons(response.body());

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager, mActivity);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CouponTemplate> call, @NonNull Throwable t) {

                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void fetchCoupons(CouponTemplate couponTemplate){

        dataManager.setIssuedTicketCount(couponTemplate.getIssuedTicketCount());

        List<Coupon> templates = new ArrayList<Coupon>();
        for (Coupon coupon : couponTemplate.getCoupons()){

            if (coupon.getPowerUpPack() != null){

                coupon.setBrandName("Fuzzie");
                coupon.setSubCategoryId(32);

            } else {

                Brand brand = dataManager.getBrandById(coupon.getBrandId());
                if (brand != null){
                    coupon.setBrandName(brand.getName());
                    coupon.setSubCategoryId(brand.getSubCategoryId());
                } else {
                    coupon.setBrandName(" ");
                    coupon.setSubCategoryId(30);
                }

            }

            templates.add(coupon);
        }

        dataManager.setCoupons(templates);

    }

    private void loadJackpotResult(){
        FuzzieAPI.APIService().getJackpotResult(currentUser.getAccesstoken()).enqueue(new Callback<JackpotResults>() {
            @Override
            public void onResponse(Call<JackpotResults> call, Response<JackpotResults> response) {
                if (response.code() == 200 && response.body() != null){
                    dataManager.setJackpotResults(response.body().getResults());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_RESULT_REFRESHED));

                }
            }

            @Override
            public void onFailure(Call<JackpotResults> call, Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void coachMarkDialogClicked() {
        hideCoachMarkDialog();
    }

    @Override
    public void coachMarkDialogTryButtonClicked() {

        Intent intent = new Intent(BROADCAST_CHANGE_TAB);
        intent.putExtra("tabId", R.id.tab_club);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        hideCoachMarkDialog();
    }

    private void updateShowFuzzieClubInstructions(){

        FZUser user = currentUser.getCurrentUser();
        user.getSettings().setShowFuzzieClubInstructions(false);
        currentUser.setCurrentUser(user);

        Call<Void> call = FuzzieAPI.APIService().setShowFuzzieClubInstructions(currentUser.getAccesstoken(), false);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    // ShopUpcomingBirthdatesItemListener
    @Override
    public void facebookConnectButtonClicked() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_friends","user_birthday"));
    }
}
