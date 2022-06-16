package sg.com.fuzzie.android.ui.club;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.ClubHome;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.core.BaseFragment;

import sg.com.fuzzie.android.items.club.ClubCategoriesItem;
import sg.com.fuzzie.android.items.club.ClubEarnItem;
import sg.com.fuzzie.android.items.club.ClubJoinItem;
import sg.com.fuzzie.android.items.club.ClubStoresItem;
import sg.com.fuzzie.android.items.club.ClubTopBrandsItem;
import sg.com.fuzzie.android.items.shop.BannerItem;
import sg.com.fuzzie.android.items.shop.MiniBannerItem;
import sg.com.fuzzie.android.items.shop.ShopBrandsItem;
import sg.com.fuzzie.android.services.LocationService;
import sg.com.fuzzie.android.utils.ClubStoreType;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.HomeBrandsType;
import sg.com.fuzzie.android.utils.ViewUtils;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_HOME_LOADED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_HOME_LOAD_FAIELD;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_OFFER_REDEEMED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_SUBSCRIBE_SUCCESS;

/**
 * Created by nurimanizam on 14/12/16.
 */

@SuppressWarnings("unchecked")
@EFragment(R.layout.fragment_club)
public class ClubFragment extends BaseFragment {

    private boolean showClubMemberPop;
    private boolean showLocationSettingPop;

    FastItemAdapter adapter;
    LinearLayoutManager layoutManager;

    BroadcastReceiver broadcastReceiver;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.rvClub)
    RecyclerView rvClub;

    @ViewById(R.id.joinBanner)
    View joinBanner;

    @ViewById(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @AfterViews
    void calledAfterViewInjection() {

        checkLocationPermission();

        layoutManager = new LinearLayoutManager(context);
        adapter = new FastItemAdapter();
        rvClub.setLayoutManager(layoutManager);
        rvClub.setAdapter(adapter);
        rvClub.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!currentUser.getCurrentUser().getFuzzieClub().isMembership()){

                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    if (adapter.getAdapterItem(firstVisiblePosition) instanceof ClubJoinItem){

                        if (joinBanner.getVisibility() == View.VISIBLE){

                            ViewUtils.slideDown(joinBanner, 500);

                        }

                    } else {

                        if (joinBanner.getVisibility() == View.GONE || joinBanner.getVisibility() == View.INVISIBLE){

                            ViewUtils.slideUp(joinBanner, 500);

                        }
                    }
                }

            }
        });

        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                loadClub(true);
                loadOfferRedeemed();
            }
        });
        swipeContainer.setDistanceToTriggerSync(300);

        if (dataManager.getClubHome() == null){

            loadClub(true);

        } else {

            setClub();
        }

        loadOfferRedeemed();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_CLUB_SUBSCRIBE_SUCCESS)){

                    setClub();
                    if (joinBanner != null){

                        ViewUtils.gone(joinBanner);

                    }

                } else if (intent.getAction().equals(BROADCAST_CLUB_OFFER_REDEEMED)){

                    loadClub(false);
                }

            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_CLUB_SUBSCRIBE_SUCCESS));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_CLUB_OFFER_REDEEMED));
    }

    private void loadOfferRedeemed(){

        if (currentUser.getCurrentUser().getFuzzieClub().isMembership()){

            FuzzieAPI.APIService().getOfferRedeemed(currentUser.getAccesstoken()).enqueue(new Callback<List<Offer>>() {
                @Override
                public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {

                    if (response.code() == 200 && response.body() != null){

                        dataManager.setOfferRedeemed(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<Offer>> call, Throwable t) {

                }
            });
        }

    }

    private void loadClub(boolean showLoader){

        Call<ClubHome> call = FuzzieAPI.APIService().getClubHome(currentUser.getAccesstoken());

        if (dataManager.myLocation != null){

            call = FuzzieAPI.APIService().getClubHome(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude());

        }

        if (showLoader){
            showLoader();
        }

        call.enqueue(new Callback<ClubHome>() {
            @Override
            public void onResponse(Call<ClubHome> call, Response<ClubHome> response) {

                hideLoader();

                if (response.code() == 200 && response.body() != null){

                    dataManager.setClubHome(response.body());
                    setClub();

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_CLUB_HOME_LOADED));

                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager,  mActivity);

                } else {

                    String errorMessage = "Unknown error occurred: " + response.code();
                    showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_CLUB_HOME_LOAD_FAIELD));

                }
            }

            @Override
            public void onFailure(Call<ClubHome> call, Throwable t) {

                hideLoader();
                showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_CLUB_HOME_LOAD_FAIELD));

            }
        });


    }

    private void setClub(){

        if (dataManager.getHome() != null){

            ClubHome clubHome = dataManager.getClubHome();

            if (adapter == null){
                adapter = new FastItemAdapter();
            } else {
                adapter.clear();
            }

            if (currentUser.getCurrentUser().getFuzzieClub().isMembership()){
                adapter.add(new ClubEarnItem());

            } else {
                adapter.add(new ClubJoinItem());
            }

            if (clubHome.getBanners() != null && clubHome.getBanners().size() > 0){
                adapter.add(new BannerItem(clubHome.getBanners()));
            }

            if (clubHome.getBrandTypes() != null && clubHome.getBrandTypes().size() > 0){
                adapter.add(new ClubCategoriesItem(clubHome.getBrandTypes()));
            }

            // Temporarily disable flash sales offer
//            if (clubHome.getFlashOffers() != null && clubHome.getFlashOffers().size() > 0){
//                adapter.add(new ClubStoresItem(ClubStoreType.FLASH, clubHome.getFlashOffers(), null));
//            }

            if (clubHome.getNearStores() != null && clubHome.getNearStores().size() > 0){
                adapter.add(new ClubStoresItem(ClubStoreType.NEAR, null, clubHome.getNearStores()));
            }

            if (clubHome.getTopBrands() != null && clubHome.getTopBrands().size() > 0){
                adapter.add(new ClubTopBrandsItem(dataManager.getClubHome().getTopBrands(),context));
            }

            if (clubHome.getTrendingStores() != null && clubHome.getTrendingStores().size() > 0){
                adapter.add(new ClubStoresItem(ClubStoreType.TRENDING, null, clubHome.getTrendingStores()));
            }

            if (clubHome.getMiniBanners() != null && clubHome.getMiniBanners().size() > 0){
                adapter.add(new MiniBannerItem(clubHome.getMiniBanners(), true));
            }

            if (dataManager.getHome().getClubBrands() != null && dataManager.getHome().getClubBrands().size() > 0){
                ShopBrandsItem item = new ShopBrandsItem(dataManager.getHome(), HomeBrandsType.CLUB, currentUser.getAccesstoken());
                item.setFromClub(true);
                adapter.add(item);
            }

            if (clubHome.getNewStores() != null && clubHome.getNewStores().size() > 0){
                adapter.add(new ClubStoresItem(ClubStoreType.NEW, null, clubHome.getNewStores()));
            }
        }

    }

    @Click(R.id.joinBanner)
    void subscribeButtonClicked(){
        ClubSubscribeActivity_.intent(context).start();
    }

    @Click(R.id.ivReferral)
    void referalButtonClicked(){

        if (currentUser.getCurrentUser().getFuzzieClub().isMembership()){

            ClubReferralActivity_.intent(context).start();

        } else {

            showFZAlert("JOIN THE CLUB", "Join the Club to activate your referral rewards.", "LEARN MORE", "Close", R.drawable.ic_bear_club);
            showClubMemberPop = true;
        }
    }

    @Click(R.id.ivFavorite)
    void favoriteButtonClicked(){
        ClubFavoriteActivity_.intent(context).start();
    }

    @Click(R.id.ivSearch)
    void searchButtonClicked(){

        dataManager.setSearchClubStores(dataManager.getClubHome().getStores());
        dataManager.setSearchClubPlaces(dataManager.getClubHome().getPlaces());

        ClubSearchActivity_.intent(context).start();
    }

    private void startLocationService(){
        LocationService.getInstance().startLocationService(mActivity);
    }

    private void stopLocationService(){
        LocationService.getInstance().stopLocationService();
    }

    private void checkLocationPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST);
            } else {
                checkGPSEnabled();
            }

        } else {
            checkGPSEnabled();
        }

    }

    private void checkGPSEnabled(){
        final LocationManager mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationEnableAlert();
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    checkGPSEnabled();
                }
            }
        }

    }

    public void showLocationEnableAlert() {

        showFZAlert("Turn on your location", "Please enable location services for Fuzzie on your phone settings", "LATER", "GO TO SETTINGS", R.drawable.ic_my_location_white);
        showLocationSettingPop = true;
    }


    // FZAlertDialog Listener
    @Override
    public void FZAlertOkButtonClicked() {

        super.FZAlertOkButtonClicked();

        if (showClubMemberPop){

            ClubSubscribeActivity_.intent(context).start();
        }

        showClubMemberPop = false;
        showLocationSettingPop = false;
    }

    @Override
    public void FZAlertCancelButtonClicked() {

        super.FZAlertCancelButtonClicked();

        if (showLocationSettingPop){

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, Constants.GPS_SERVICES_ENABLE_REQUEST);

        }

        showClubMemberPop = false;
        showLocationSettingPop = false;
    }

}
