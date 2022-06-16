package sg.com.fuzzie.android;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.services.LocationService;
import sg.com.fuzzie.android.ui.redpacket.RedPacketActivity_;
import sg.com.fuzzie.android.ui.redpacket.RedPacketHistoryActivity_;
import sg.com.fuzzie.android.ui.update.ForceUpdateActivity_;
import sg.com.fuzzie.android.utils.FZAlarmManager;
import timber.log.Timber;

import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.api.models.ShoppingBag;
import sg.com.fuzzie.android.api.models.JackpotResults;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.auth.LandingActivity_;
import sg.com.fuzzie.android.ui.auth.TutorialActivity_;
import sg.com.fuzzie.android.ui.club.ClubFragment_;
import sg.com.fuzzie.android.ui.club.ClubTempFragment_;
import sg.com.fuzzie.android.ui.giftbox.GiftBoxActivity_;
import sg.com.fuzzie.android.ui.giftbox.WalletFragment_;
import sg.com.fuzzie.android.ui.jackpot.JackpotHomeActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotIsLiveActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotLiveActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotTicketsActivity_;
import sg.com.fuzzie.android.ui.likes.LikerProfileActivity_;
import sg.com.fuzzie.android.ui.me.InviteFriendsActivity_;
import sg.com.fuzzie.android.ui.me.MeFragment_;
import sg.com.fuzzie.android.ui.me.MyFriendsListActivity_;
import sg.com.fuzzie.android.ui.nearby.NearByFragment_;
import sg.com.fuzzie.android.ui.rate.RateExperienceActivity_;
import sg.com.fuzzie.android.ui.shop.ShopFragment_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FZTimer;
import sg.com.fuzzie.android.utils.FuzzieData;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_HOME_LIKE_CLICKED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_END;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_START;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_IS_WON;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_LIVE_NOTIFICATION;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESH;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESHED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_FRIENDS;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_RATE_APP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_WISHLIST;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_WISHLIST_WITH_ID;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_GIFTBOX_ACTIVE;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_GIFTBOX_SENT;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_GIFTBOX_USED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_HOME;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_ACTIVE;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_SENT;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_USED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_HOME;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_PAYMENT_METHODS;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_SHOPPING_BAG;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;
import static sg.com.fuzzie.android.utils.Constants.COUNTDOWN_TIME;
import static sg.com.fuzzie.android.utils.Constants.GIFTBOX_OFFSET;
import static sg.com.fuzzie.android.utils.Constants.REDEEM_TIMER_INTERVAL;

/**
 * Created by nurimanizam on 14/12/16.
 */

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements FZTimer.OnTaskRunListener {


    BroadcastReceiver broadcastReceiver;

    private Fragment shopFragment;
    private Fragment nearbyFragment;
    private Fragment clubFragment;
    private Fragment walletFragment;
    private Fragment meFragment;

    private SharedPreferences mPrefs;

    public boolean fromLike = false;
    public boolean selectWishList = false;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @Bean
    FZTimer fzTimer;

    @ViewById(R.id.contentFrame)
    FrameLayout contentFrame;

    @ViewById(R.id.bottomBar)
    BottomBar bottomBar;

    @AfterViews
    public void calledAfterViewInjection() {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        fzTimer.setInterval(REDEEM_TIMER_INTERVAL);
        fzTimer.setOnTaskRunListener(this);

        if (currentUser.isLoggedIn()) {
            mPrefs.edit().putBoolean("loggedin", true).apply();
            if(!mPrefs.getBoolean("tutorial_shown", false)){
                showTutorialView();
                mPrefs.edit().putBoolean("tutorial_shown", true).apply();
                return;
            }
        } else {
            showLandingView();
            return;
        }

        checkStoreVersion();
        checkPlayServices();
        updateFCMToken();

        shopFragment = ShopFragment_.builder().build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFrame, shopFragment).commitAllowingStateLoss();

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                if (tabId != R.id.tab_nearby){
                    dataManager.selectedSubCategoryIds = new TreeSet<Integer>();
                }

                Fragment fragment = null;

                if (tabId == R.id.tab_shop) {
                    if (shopFragment == null) {
                        shopFragment = ShopFragment_.builder().build();
                    }
                    fragment = shopFragment;

                } else if (tabId == R.id.tab_nearby) {

                    nearbyFragment = NearByFragment_.builder().build();
                    fragment = nearbyFragment;

                } else if (tabId == R.id.tab_club) {

                    if (clubFragment == null) {
                        clubFragment = ClubFragment_.builder().build();
//                        clubFragment = ClubTempFragment_.builder().build();
                    }
                    fragment = clubFragment;

                } else if (tabId == R.id.tab_wallet) {

                    if (walletFragment == null) {
                        walletFragment = WalletFragment_.builder().build();
                    }

                    fragment = walletFragment;

                } else if (tabId == R.id.tab_me) {

                    meFragment = MeFragment_.builder().fromLike(fromLike).selectWishList(selectWishList).build();
                    fragment = meFragment;

                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commitAllowingStateLoss();
                }
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_REFRESH_HOME)) {
                    loadHomeData();
                }

                if (intent.getAction().equals(BROADCAST_CHANGE_TAB)) {
                    int tabId = intent.getIntExtra("tabId", 0);
                    if (tabId != 0) {
                        bottomBar.selectTabWithId(tabId);
                    }
                }

                if (intent.getAction().equals(BROADCAST_REFRESH_USER)) {
                    fetchUserProfile();
                }

                if (intent.getAction().equals(BROADCAST_REFRESH_PAYMENT_METHODS)) {
                    fetchPaymentMethods();
                }

                if (intent.getAction().equals(BROADCAST_REFRESH_SHOPPING_BAG)) {
                    fetchShoppingBag();
                }

                if (intent.getAction().equals(BROADCAST_HOME_LIKE_CLICKED)){
                    likeClicked();
                }

                if (intent.getAction().equals(BROADCAST_NOTIFICATION_WISHLIST_WITH_ID)){
                    String userId = intent.getStringExtra("user_id");
                    if (!userId.equals("")){
                        goFriendWishList(userId);
                    }
                }

                if (intent.getAction().equals(BROADCAST_NOTIFICATION_WISHLIST)){
                    goMyWishList();
                }

                if (intent.getAction().equals(BROADCAST_NOTIFICATION_FRIENDS)){
                    goFriendsPage();
                }

                if (intent.getAction().equals(BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS)){
                    goGiftBoxForFriends();
                }

                if (intent.getAction().equals(BROADCAST_NOTIFICATION_RATE_APP)){
                    goRatePage();
                }

                if (intent.getAction().equals(BROADCAST_REFRESHED_GIFTBOX_ACTIVE) || intent.getAction().equals(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED)){
                    checkRedeeming();
                }

                if (intent.getAction().equals(BROADCAST_REFRESH_GIFTBOX_ACTIVE)){
                    loadActiveGifts();
                }

                if (intent.getAction().equals(BROADCAST_REFRESH_GIFTBOX_USED)){
                    loadUsedGifts();
                }

                if (intent.getAction().equals(BROADCAST_REFRESH_GIFTBOX_SENT)){
                    loadSentGifts();
                }

                if (intent.getAction().equals(BROADCAST_JACKPOT_RESULT_REFRESH) || intent.getAction().equals(BROADCAST_JACKPOT_IS_WON) || intent.getAction().equals(BROADCAST_JACKPOT_IS_END)){
                    loadJackpotResult();
                }

                if (intent.getAction().equals(BROADCAST_JACKPOT_LIVE_NOTIFICATION)){
                    dataManager.getHome().getJackpot().setLive(true);
                    showJackpotIsLivePage();
            }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_HOME));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_CHANGE_TAB));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_USER));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_HOME_LIKE_CLICKED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_NOTIFICATION_WISHLIST_WITH_ID));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_NOTIFICATION_WISHLIST));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_NOTIFICATION_FRIENDS));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_SHOPPING_BAG));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_NOTIFICATION_RATE_APP));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESHED_GIFTBOX_ACTIVE));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_GIFTBOX_ACTIVE));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_GIFTBOX_USED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_GIFTBOX_SENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_RESULT_REFRESH));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_LIVE_NOTIFICATION));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_JACKPOT_IS_END));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_REFRESH_PAYMENT_METHODS));

        loadBackgroundData();

        handleExtra(getIntent().getExtras());
        handleData(getIntent().getData());

        FZAlarmManager.getInstance().scheduleMyBirthdayNotification(context);

        checkLocationPermission();
    }

    void showTutorialView() {
        Intent intent = new Intent(this, TutorialActivity_.class);
        startActivity(intent);
        finish();
    }

    void showLandingView() {
        Intent intent = new Intent(this, LandingActivity_.class);
        startActivity(intent);
        finish();
    }

    void loadBackgroundData() {

        fetchUserProfile();
        fetchPaymentMethods();
        fetchShoppingBag();
        loadActiveGifts();
        loadUsedGifts();
        loadSentGifts();
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

                    updateWalletBadge();

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    void fetchPaymentMethods() {

        Call<List<PaymentMethod>> call = FuzzieAPI.APIService().getPaymentMethods(currentUser.getAccesstoken());
        call.enqueue(new Callback<List<PaymentMethod>>() {
            @Override
            public void onResponse(Call<List<PaymentMethod>> call, Response<List<PaymentMethod>> response) {

                Timber.d("Payment Method Loaded: " + response.code());

                if (response.code() == 200 && response.body() != null) {

                    dataManager.setPaymentMethods(response.body());
                    if (dataManager.getPaymentMethods().size() > 0){

                        dataManager.setSelectedPaymentMethod(dataManager.getPaymentMethods().get(0));

                    } else {

                        dataManager.setSelectedPaymentMethod(null);

                    }

                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<PaymentMethod>> call, Throwable t) {
                Timber.d("Payment Method Error: " + t.getLocalizedMessage());
            }
        });
    }

    void fetchShoppingBag() {
        Call<JsonObject> call = FuzzieAPI.APIService().getShoppingBag(currentUser.getAccesstoken());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonObject("shopping_bag") != null) {

                    ShoppingBag bag = ShoppingBag.fromJSON(response.body().getAsJsonObject("shopping_bag").toString());
                    dataManager.setShoppingBag(bag);

                    Intent intent = new Intent(Constants.BROADCAST_REFRESHED_SHOPPING_BAG);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.d("Shopping Bag Error: " + t.getLocalizedMessage());
            }
        });
    }

    void updateFCMToken() {
        if (FirebaseInstanceId.getInstance().getToken() != null) {
            String FCMToken = FirebaseInstanceId.getInstance().getToken();
            Timber.d("FCMToken" + FCMToken);
            Call<JsonObject> call = FuzzieAPI.APIService().registerFcm(currentUser.getAccesstoken(), FCMToken);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Timber.d("FCM Registration Loaded: " + response.code());
                    if (response.code() == 417){
                        logoutUser(currentUser, dataManager,  MainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Timber.d("FCM Registration Error: " + t.getLocalizedMessage());
                }
            });
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void checkStoreVersion() {
        Call<JsonObject> call = FuzzieAPI.APIService().getCurrentStoreVersion();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200
                        && response.body() != null
                        && response.body().getAsJsonPrimitive("version") != null
                        && response.body().getAsJsonPrimitive("force") != null) {

                    int storeVersion = response.body().getAsJsonPrimitive("version").getAsInt();
                    boolean force = response.body().getAsJsonPrimitive("force").getAsBoolean();

                    if (force && BuildConfig.VERSION_CODE < storeVersion) {

                        ForceUpdateActivity_.intent(context).start();

                    }

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.d("Version Check Error: " + t.getLocalizedMessage());
            }
        });
    }

    @SupposeUiThread
    protected void likeClicked(){
        fromLike = true;
        bottomBar.selectTabWithId(R.id.tab_me);
    }

    private void confirmGift(String giftId){

        Call<Void> call = FuzzieAPI.APIService().confirmGift(currentUser.getAccesstoken(), giftId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_GIFTBOX_ACTIVE));
                    goGiftBox(0);

                } else if (response.code() == 401 || response.code() == 403){

                    if (response.errorBody() != null){
                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            showAlert("OOPS!", jsonObject.get("message").getAsString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void confirmRedPacket(String redPacketBundleId){

        FuzzieAPI.APIService().confirmRedPacket(currentUser.getAccesstoken(), redPacketBundleId).enqueue(new Callback<RedPacket>() {
            @Override
            public void onResponse(Call<RedPacket> call, Response<RedPacket> response) {

                if (response.code() == 200 && response.body() != null){

                    if (dataManager.getRedPackets() != null){

                        dataManager.getRedPackets().add(response.body());
                    }

                    // Increase Unopened Lucky Packet Count
                    currentUser.changeUnopenedRedPacketCount(1);
                    updateWalletBadge();
                    goLuckyPacketHistoryPage(0);

                } else {

                    String errorMessage = "Unknown error occurred: " + response.code();
                    if (response.code() != 500 && response.errorBody() != null) {
                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()) {
                                errorMessage = jsonObject.get("error").getAsString();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    showFZAlert("OOPS!", errorMessage, "GOT IT", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<RedPacket> call, Throwable t) {

            }
        });
    }

    public void updateWalletBadge(){
        if (currentUser != null){
            FZUser user = currentUser.getCurrentUser();
            bottomBar.getTabWithId(R.id.tab_wallet).setBadgeCount(user.getUnOpenedGiftCount() + user.getUnOpenedRedPacketCount() + user.getUnOpenedTicketCount());
        }
    }

    private void goFriendsPage(){
        MyFriendsListActivity_.intent(context).start();
    }

    private void goGiftBoxForFriends(){
        bottomBar.selectTabWithId(R.id.tab_wallet);
        goGiftBox(2);

    }

    private void goMyWishList(){
        selectWishList = true;
        bottomBar.selectTabWithId(R.id.tab_me);
    }

    private void goFriendWishList(String userId){
        LikerProfileActivity_.intent(context).userIdExtra(userId).selectWishList(true).start();
    }

    private void goFriendLike(String userId){
        LikerProfileActivity_.intent(context).userIdExtra(userId).selectWishList(false).start();
    }

    private void goRatePage(){
        if (!dataManager.isShowingRatePage){
            RateExperienceActivity_.intent(context).start();
            dataManager.isShowingRatePage = true;
        }
    }

    private void showJackpotIsLivePage(){
        JackpotIsLiveActivity_.intent(context).start();
    }

    private void goJackpotHome(){
        bottomBar.selectTabWithId(R.id.tab_shop);
        JackpotHomeActivity_.intent(context).start();
    }

    private void goGiftBox(int selectedTab){
        GiftBoxActivity_.intent(context).selectedTab(selectedTab).start();
    }

    private void goLuckyPacketHistoryPage(int selectedTab){
        RedPacketHistoryActivity_.intent(context).selectedTab(selectedTab).start();
    }

    private void goLuckyPacketHistoryPage(int selectedTab, String bundleId, boolean goSentDetailsPage){
        RedPacketHistoryActivity_.intent(context).selectedTab(selectedTab).bundleId(bundleId).goSentDetailsPage(goSentDetailsPage).start();
    }

    private void goJackpotLiveDrawPage(){
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_IS_START));
        JackpotLiveActivity_.intent(context).start();
    }

    private void goJackpotTickets(){
        JackpotTicketsActivity_.intent(context).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkRedeeming();
        updateWalletBadge();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    private void loadJackpotResult(){
        FuzzieAPI.APIService().getJackpotResult(currentUser.getAccesstoken()).enqueue(new Callback<JackpotResults>() {
            @Override
            public void onResponse(Call<JackpotResults> call, Response<JackpotResults> response) {
                if (response.code() == 200 && response.body() != null){
                    hideLoader();
                    dataManager.setJackpotResults(response.body().getResults());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_RESULT_REFRESHED));

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JackpotResults> call, Throwable t) {
                hideLoader();
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void loadActiveGifts(){

        Call<List<Gift>> call = FuzzieAPI.APIService().getActiveGifts(currentUser.getAccesstoken(), 1, GIFTBOX_OFFSET);
        call.enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {


                if (response.code() == 200 && response.body() != null){

                    dataManager.setActiveGifts(response.body());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESHED_GIFTBOX_ACTIVE));

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }


    private void loadUsedGifts(){

        Call<List<Gift>> call = FuzzieAPI.APIService().getUsedGifts(currentUser.getAccesstoken(), 1, GIFTBOX_OFFSET);
        call.enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                if (response.code() == 200 && response.body() != null){

                    dataManager.setUsedGifts(response.body());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESHED_GIFTBOX_USED));

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    private void loadSentGifts(){
        Call<List<Gift>> call = FuzzieAPI.APIService().getSentGifts(currentUser.getAccesstoken(), 1, GIFTBOX_OFFSET);
        call.enqueue(new Callback<List<Gift>>() {
            @Override
            public void onResponse(Call<List<Gift>> call, Response<List<Gift>> response) {

                if (response.code() == 200 && response.body() != null){

                    dataManager.setSentGifts(response.body());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESHED_GIFTBOX_SENT));

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Gift>> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }
        });
    }

    void loadHomeData() {

        Call<Home> call = FuzzieAPI.APIService().getHomeData(currentUser.getAccesstoken());

        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(Call<Home> call, Response<Home> response) {

                if (response.code() == 200 && response.body() != null) {

                    dataManager.setHome(response.body());

                    if (dataManager.getHome().getJackpot() != null){

                        // save next draw time to user preference
                        currentUser.setJackpotLiveDrawDateTime(dataManager.getHome().getJackpot().getDrawTime());

                        // save draw id to user preference
                        dataManager.setJackpotDrawId(dataManager.getHome().getJackpot().getDrawId());
                    }
                    
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESHED_HOME));

                    // Schedule Friends Birthday Notification
                    if (dataManager.getHome().getFuzzieFriends() != null && dataManager.getHome().getFuzzieFriends().size() > 0) {

                        FZAlarmManager.getInstance().scheduleFriendsBirthdayNotification(context, dataManager.getHome().getFuzzieFriends());
                    }

                }
            }

            @Override
            public void onFailure(Call<Home> call, Throwable t) {
                Timber.d("Home Data Error: " + t.getLocalizedMessage());

            }
        });
    }

    private void checkRedeeming(){
        if (dataManager.getActiveGifts() != null){
            if (getRedeemingGifts().size() > 0){
                redeemTimerStart();
            } else {
                redeemTimerStop();
            }
        }
    }

    private void redeemTimerStart(){
        fzTimer.startTimer();
        Timber.e("Timer is running...");
    }

    private void redeemTimerStop(){
        fzTimer.stopTimer();
        Timber.e("Timer is stopped...");
    }

    private List<Gift> getRedeemingGifts(){
        List<Gift> gifts = new ArrayList<>();

        if (dataManager.getActiveGifts() != null){
            for (Gift gift : dataManager.getActiveGifts()){
                if (gift.getRedeemTimerStartedAt() != null && gift.getRedeemedTime() == null){
                    gifts.add(gift);
                    Timber.e("Gift Id " + gift.getId());
                }
            }
        }

        return gifts;
    }

    void redeemGift(final Gift gift) {

        Call<PayResponse> call = FuzzieAPI.APIService().markGiftRedeemed(currentUser.getAccesstoken(), gift.getId());
        call.enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                if (response.code() == 200 && response.body() != null && response.body().getGift() != null) {

                    dataManager.removeActiveGift(gift);
                    if (dataManager.getUsedGifts() != null){
                        dataManager.getUsedGifts().add(0, response.body().getGift());
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED));

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

            }

        });
    }

    // FZTimer Listener
    @Override
    public void onTaskRun(long past_time, String rendered_time) {

        Timber.e(rendered_time);

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        DateTime now = new DateTime();

        if (getRedeemingGifts().size() > 0){

            for (Gift gift : getRedeemingGifts()){
                DateTime redeemStartTime = parser.parseDateTime(gift.getRedeemTimerStartedAt());
                if (now.getMillis() - redeemStartTime.getMillis() >= COUNTDOWN_TIME && !dataManager.isActiveGiftOnlineRedeemActivity()){
                    redeemGift(gift);
                }
            }

        } else {

            redeemTimerStop();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleExtra(intent.getExtras());

    }

    private void handleExtra(Bundle bundle){

        if (bundle != null){

            if (bundle.getString("to") != null){

                if (bundle.getString("to").equals("referrals")){

                    InviteFriendsActivity_.intent(context).start();

                } else if (bundle.getString("to").equals("friends")){

                    goFriendsPage();

                } else if (bundle.getString("to").equals("wishlist")){

                    if (bundle.getString("user_id") != null && !bundle.getString("user_id").equals("")){

                        String userId = bundle.getString("user_id");
                        goFriendWishList(userId);

                    } else {

                        goMyWishList();

                    }

                } else if (bundle.getString("to").equals("giftbox_for_friends")){

                    goGiftBoxForFriends();

                } else if (bundle.getString("to").equals("rate_app")){

                    goRatePage();

                }  else if (bundle.getString("to").equals("lucky_packets")){

                    bottomBar.selectTabWithId(R.id.tab_wallet);

                    if (bundle.getString("bundle_id") != null){

                        goLuckyPacketHistoryPage(1, bundle.getString("bundle_id"), true);

                    } else {

                        goLuckyPacketHistoryPage(0);

                    }
                }

            } else if (bundle.getBoolean("jackpot_live")){

                goJackpotLiveDrawPage();

            } else if (bundle.getBoolean("my_birthday")){

                goMyWishList();

            } else if (bundle.getBoolean("friend_birthday")){

                String userId = bundle.getString("user_id", "");
                if (!userId.equals("")){
                    goFriendLike(userId);
                }
            } else if (bundle.getBoolean("go_jackpot_coupon_list")){

                goJackpotHome();

            } else if (bundle.getBoolean("go_gift_box")){

                goGiftBox(bundle.getInt("selected_tab", 0));

            }  else if (bundle.getBoolean("jackpot_live")){

                goJackpotLiveDrawPage();

            } else if (bundle.getBoolean("go_jackpot_tickets")){

                goJackpotTickets();

            } else if (bundle.getBoolean("go_red_packet_history_page")){

                bottomBar.selectTabWithId(R.id.tab_wallet);
                goLuckyPacketHistoryPage(bundle.getInt("selected_tab", 1));

            }
        }
    }

    private void handleData(Uri data){

        if (data != null && data.isHierarchical()) {

            String uri = this.getIntent().getDataString();
            Timber.e("Deep link clicked " + uri);

            if (data.getScheme().equals("fuzzie")){

                String type = data.getQueryParameter("type");
                String id = data.getQueryParameter("id");

                if (type != null && id != null){

                    if (type.equals("gift")){

                        confirmGift(id);
                        bottomBar.selectTabWithId(R.id.tab_wallet);

                    } else if (type.equals("red_packet")){

                        confirmRedPacket(id);
                        bottomBar.selectTabWithId(R.id.tab_wallet);

                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    checkGPSEnabled();
                }
            }
        }

    }

    private void checkLocationPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST);
            } else {
                checkGPSEnabled();
            }

        } else {
            checkGPSEnabled();
        }

    }

    private void checkGPSEnabled(){
        final LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationEnableAlert();
        } else {
            startLocationService();
        }
    }

    private void startLocationService(){
        LocationService.getInstance().startLocationService(this);
    }

    private void stopLocationService(){
        LocationService.getInstance().stopLocationService();
    }

    public void showLocationEnableAlert() {
        showFZAlert("Turn on your location", "Please enable location services for Fuzzie on your phone settings", "LATER", "GO TO SETTINGS", R.drawable.ic_my_location_white);
    }


    // FZAlertDialog Listener
    @Override
    public void FZAlertOkButtonClicked() {
        hideFZAlert();
    }

    @Override
    public void FZAlertCancelButtonClicked() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, Constants.GPS_SERVICES_ENABLE_REQUEST);
        hideFZAlert();
    }
}
