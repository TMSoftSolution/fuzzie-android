package sg.com.fuzzie.android.ui.club;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import me.crosswall.lib.coverflow.core.PagerContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.adapter.ClubStoreInfoAdapter;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.api.models.BrandTypeDetail;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubSearchPlaceholderItem;
import sg.com.fuzzie.android.items.club.ClubStoresItem;
import sg.com.fuzzie.android.utils.ClubStoreType;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import timber.log.Timber;

@EActivity(R.layout.activity_club_category)
public class ClubCategoryActivity extends BaseActivity implements ClubStoreInfoAdapter.ClubStoreInfoAdapterListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private BrandType brandType;

    private List<ClubStore> nearStores;
    private List<ClubStore> trendingStores;
    private List<ClubStore> newStores;
    private List<Category> categories;
    private List<ClubStore> clubStores;

    private boolean isMapMode;
    private GoogleMap mMap;

    private List<Store> stores;
    private List<Marker> markers;
    private Marker previousMarker;
    private boolean markerClicked;  // This variable is used to prevent duplicated marker select.

    private static final double mLat = 1.400270;
    private static final double mLng = 103.831959;

    FastItemAdapter adapter;

    @InstanceState
    int instanceState;


    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.ivMode)
    ImageView ivMode;

    @ViewById(R.id.rvStores)
    RecyclerView rvStores;

    @ViewById(R.id.rlMap)
    View mapContainer;

    @ViewById(R.id.mapView)
    MapView mapView;

    @ViewById(R.id.pageContainer)
    PagerContainer pagerContainer;

    @AfterViews
    void callAfterViewInjection(){

        brandType = dataManager.getBrandType();
        if (brandType == null) return;

        loadClubStores();
        loadClubPlaces();
        loadDetails();

        tvTitle.setText(brandType.getName());

        adapter = new FastItemAdapter();
        rvStores.setLayoutManager(new LinearLayoutManager(context));
        rvStores.setAdapter(adapter);
        adapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter adapter, IItem item, int position) {

                if (item.getType() == R.id.item_club_search_placeholder_id){

                    dataManager.setSearchClubStores(dataManager.getBrandTypeClubStores());
                    dataManager.setSearchClubPlaces(dataManager.getBrandTypeClubPlaces());

                    ClubSearchActivity_.intent(context).start();
                }
                return false;
            }
        });

        mapView.onCreate(new Bundle(instanceState));
        mapView.onResume();
        try{
            MapsInitializer.initialize(context);
        } catch (Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        showMapOrStores();
    }

    private void loadDetails(){

        Call<BrandTypeDetail> call = FuzzieAPI.APIService().getBrandTypeDetail(currentUser.getAccesstoken(), brandType.getId());

        if (dataManager.myLocation != null){

            call = FuzzieAPI.APIService().getBrandTypeDetail(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), brandType.getId());

        }

        showLoader();
        call.enqueue(new Callback<BrandTypeDetail>() {
            @Override
            public void onResponse(Call<BrandTypeDetail> call, Response<BrandTypeDetail> response) {

                hideLoader();

                if (response.code() == 200 && response.body() != null){

                    dataManager.setBrandTypeDetail(response.body());

                    nearStores = response.body().getNearStores();
                    trendingStores = response.body().getTrendingStores();
                    newStores = response.body().getNewStores();
                    categories = response.body().getCategories();

                    updateDetails();
                }
            }

            @Override
            public void onFailure(Call<BrandTypeDetail> call, Throwable t) {

                hideLoader();
            }
        });
    }

    private void loadClubStores(){

        Call<List<ClubStore>> call = FuzzieAPI.APIService().getClubStores(currentUser.getAccesstoken(), brandType.getId());

        if (dataManager.myLocation != null){

            call = FuzzieAPI.APIService().getClubStores(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), brandType.getId());
        }

        call.enqueue(new Callback<List<ClubStore>>() {
            @Override
            public void onResponse(Call<List<ClubStore>> call, Response<List<ClubStore>> response) {

                if (response.code() == 200 && response.body() != null){

                    dataManager.setBrandTypeClubStores(response.body());
                    clubStores = response.body();
                    mappingStores();
                    updateMap();
                    initInfos();
                }
            }

            @Override
            public void onFailure(Call<List<ClubStore>> call, Throwable t) {

            }
        });
    }

    private void loadClubPlaces(){

        Call<List<ClubPlace>> call = FuzzieAPI.APIService().getClubPlaces(currentUser.getAccesstoken(), brandType.getId());

        if (dataManager.myLocation != null){

            call = FuzzieAPI.APIService().getClubPlaces(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), brandType.getId());
        }

        call.enqueue(new Callback<List<ClubPlace>>() {
            @Override
            public void onResponse(Call<List<ClubPlace>> call, Response<List<ClubPlace>> response) {

                if (response.code() == 200 && response.body() != null){

                    dataManager.setBrandTypeClubPlaces(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ClubPlace>> call, Throwable t) {

            }
        });
    }

    private void mappingStores(){

        if (clubStores == null) return;

        stores = new ArrayList<>();

        for (ClubStore clubStore : clubStores){

            Store store = dataManager.getStoreById(clubStore.getStoreId());
            if (store != null){
                stores.add(store);
            }
        }
    }

    private void showMapOrStores(){

        if (isMapMode){

            ivMode.setImageResource(R.drawable.ic_club_list);

            ViewUtils.gone(rvStores);
            ViewUtils.visible(mapContainer);
            ViewUtils.gone(pagerContainer);
            ViewUtils.gone(findViewById(R.id.space));

            mapView.onResume();
            myLocationButtonClicked();

        } else {

            ivMode.setImageResource(R.drawable.ic_club_location);

            ViewUtils.visible(rvStores);
            ViewUtils.gone(mapContainer);

            mapView.onPause();
        }
    }

    private void updateMap(){

        if (mMap == null) return;

        mMap.clear();

        if (stores == null) return;

        markers = new ArrayList<>();

        for (int i = 0 ; i < stores.size() ; i ++){

            Store store = stores.get(i);
            LatLng latLng = new LatLng(store.getLatitude(), store.getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerWhiteIcon(store.getmSubCategoryId()))));
            marker.setTag(i);

            markers.add(marker);
        }

    }

    private void showInfos(Marker marker){

        if (pagerContainer.getVisibility() != View.VISIBLE){

            ViewUtils.visible(pagerContainer);
            ViewUtils.visible(findViewById(R.id.space));

        }

        if (marker.getTag() != null){

            int position = (int)marker.getTag();

            if (position < stores.size()){

                pagerContainer.getViewPager().setCurrentItem(position, true);

            }
        }

    }

    private void initInfos(){

        ViewPager viewPager = pagerContainer.getViewPager();
        viewPager.setPageMargin(20);
        ClubStoreInfoAdapter adapter = new ClubStoreInfoAdapter(this, clubStores);
        adapter.setListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_IDLE){
                    Timber.e("Cover scrolled - " + index);

                    if (!markerClicked){

                        selectMarker(index);

                    }

                    markerClicked = false;
                }
            }
        });

    }

    private void selectMarker(Marker marker){

        if (marker.getTag() != null){

            int position = (int)marker.getTag();
            Store store = stores.get(position);
            marker.setIcon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerRedIcon(store.getmSubCategoryId())));

            moveCamera(marker);

        }
    }

    private void unSelectMarker(Marker marker){

        if (marker.getTag() != null){

            int position = (int)marker.getTag();
            Store store = stores.get(position);
            marker.setIcon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerWhiteIcon(store.getmSubCategoryId())));

        }
    }

    private void selectMarker(int position){

        if (mMap == null || markers == null) return;

        if (position < markers.size()){

            Marker marker = markers.get(position);

            if (previousMarker != null && previousMarker != marker){

                unSelectMarker(previousMarker);
            }

            if (marker != previousMarker){

                selectMarker(marker);
                previousMarker = marker;
            }
        }
    }

    private void moveCamera(Marker marker){

        Projection projection = mMap.getProjection();

        LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude);
        Point markerScreenPosition = projection.toScreenLocation(markerLatLng);

        Point pointHalfScreenAbove = new Point(markerScreenPosition.x, markerScreenPosition.y);

        LatLng aboveMarkerLatLng = projection
                .fromScreenLocation(pointHalfScreenAbove);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(aboveMarkerLatLng), 500, null);
    }

    private void updateDetails(){

        if (adapter == null){

            adapter = new FastItemAdapter();

        } else {

            adapter.clear();
        }

        adapter.add(new ClubSearchPlaceholderItem());

        if (nearStores != null && nearStores.size() > 0){

            adapter.add(new ClubStoresItem(ClubStoreType.NEAR, null, nearStores));

        }

        if (categories != null && categories.size() > 0){

            adapter.add(new ClubStoresItem(ClubStoreType.POPULAR_CATEGORY, categories));

        }

        if (trendingStores != null && trendingStores.size() > 0){

            adapter.add(new ClubStoresItem(ClubStoreType.TRENDING, null, trendingStores));

        }

        if (newStores != null && newStores.size() > 0){

            adapter.add(new ClubStoresItem(ClubStoreType.NEW, null, newStores));

        }
    }

    @Click(R.id.ivMode)
    void modeButtonClicked(){

        isMapMode = !isMapMode;

        showMapOrStores();

    }

    @Click(R.id.ivMyLocation)
    void myLocationButtonClicked(){

        if (mMap != null){
            if (dataManager.myLocation != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dataManager.myLocation.getLatitude(),dataManager.myLocation.getLongitude()), 14));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat,mLng), 10.3f));
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Override
    public void itemClicked(ClubStore clubStore) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                mMap.setMyLocationEnabled(true);
            }

        } else {

            mMap.setMyLocationEnabled(true);
        }

        updateMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        markerClicked = true;
        showInfos(marker);

        if (previousMarker != null && previousMarker != marker){

            unSelectMarker(previousMarker);
        }

        if (marker != previousMarker){

            selectMarker(marker);
            previousMarker = marker;
        }

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
