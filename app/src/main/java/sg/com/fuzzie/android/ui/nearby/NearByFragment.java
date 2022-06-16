package sg.com.fuzzie.android.ui.nearby;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseFragment;
import sg.com.fuzzie.android.items.brand.StoreInfoItem;
import sg.com.fuzzie.android.items.brand.StoreInfoListItem;
import sg.com.fuzzie.android.services.LocationService;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandSearchActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.views.autoloop.AutoScrollViewPager;
import sg.com.fuzzie.android.views.IndicatorView;

import static android.app.Activity.RESULT_OK;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_FILTER_DONE;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_LOCATION_UPDATED_FIRST;

/**
 * Created by mac on 7/10/17.
 */

@EFragment(R.layout.fragment_nearby)
public class NearByFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, ViewPager.OnPageChangeListener{

    private static final double mLat = 1.400270;
    private static final double mLng = 103.831959;

    BroadcastReceiver broadcastReceiver;

    private GoogleMap mMap;
    private Map<String, List<Store>> sortedStores;
    private List<Store> selectedStores;
    private Marker previousMarker;

    private FastItemAdapter<StoreInfoListItem> storeListAdapter;

    @InstanceState
    int instanceState;

    @ViewById(R.id.mapView)
    MapView mapView;

    @ViewById(R.id.ivFilterChecked)
    ImageView ivFilterChecked;

    @ViewById(R.id.liker_image)
    ImageView ivLikes;

    @ViewById(R.id.llStoreInfo)
    LinearLayout llStoreInfo;

    @ViewById(R.id.viewPager)
    AutoScrollViewPager viewPager;

    @ViewById(R.id.indicator)
    LinearLayout indicator;

    @ViewById(R.id.rlMap)
    RelativeLayout rlMap;

    @ViewById(R.id.llList)
    LinearLayout llList;

    @ViewById(R.id.rvStoreList)
    RecyclerView rvStoreList;

    private IndicatorView[] indicatorViews;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @AfterViews
    void calledAfterViewInjection()  {

        mapView.onCreate(new Bundle(instanceState));
        mapView.onResume();

        try{
            MapsInitializer.initialize(context);
        } catch (Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_FILTER_DONE)){
                    initMap();
                    initList();
                } else if (intent.getAction().equals(BROADCAST_LOCATION_UPDATED_FIRST)){
                    updateMyLocation();
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_FILTER_DONE));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_LOCATION_UPDATED_FIRST));

        hideStoreInfoView();

        storeListAdapter = new FastItemAdapter<>();
        rvStoreList.setLayoutManager(new LinearLayoutManager(context));
        rvStoreList.setAdapter(storeListAdapter);
        storeListAdapter.withOnClickListener(new OnClickListener<StoreInfoListItem>() {
            @Override
            public boolean onClick(View v, IAdapter<StoreInfoListItem> adapter, StoreInfoListItem item, int position) {
                Brand brand = item.getBrand();
                if (brand.getServices() != null && brand.getServices().size() > 0) {
                    if (brand.getServices().size() == 1 && (brand.getGiftCards() == null || brand.getGiftCards().size() == 0)) {
                        BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(brand.getServices().get(0))).start();
                    } else {
                        BrandServiceListActivity_.intent(context).brandId(brand.getId()).start();
                    }
                } else if (brand.getGiftCards() != null && brand.getGiftCards().size() > 0) {
                    BrandGiftActivity_.intent(context).brandId(brand.getId()).start();
                }
                return true;
            }
        });
    }

    private void startLocationService(){
        LocationService.getInstance().startLocationService(mActivity);
        if (mMap != null){
            updateMyLocation();
        }
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

    private void updateMyLocation(){
        if (mMap != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);
                }

            } else {
                mMap.setMyLocationEnabled(true);
            }
            if (dataManager.myLocation != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(dataManager.myLocation.getLatitude(),dataManager.myLocation.getLongitude())));
            }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        checkLocationPermission();

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        if (dataManager.myLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dataManager.myLocation.getLatitude(),dataManager.myLocation.getLongitude()), 14));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat,mLng), 10.3f));
        }

        updateMyLocation();

        initMap();
    }

    private void initMap(){

        if (mMap == null) return;

        mMap.clear();

        List<Store> stores1 = new ArrayList<>();
        if (dataManager.selectedSubCategoryIds.size() == 0 || dataManager.selectedSubCategoryIds.size() == dataManager.getSubCategoryIds().size()){
            stores1 = dataManager.getStores();
        } else {
            for (int i : dataManager.selectedSubCategoryIds){
                for (Store store : dataManager.getStores()){
                    if (store.getmSubCategoryId() == i){
                        stores1.add(store);
                    }
                }
            }
        }

        sortedStores = dataManager.getSortedStores(stores1);
        for (Map.Entry<String, List<Store>> entry : sortedStores.entrySet()){
            String key = entry.getKey();
            List<Store> stores = entry.getValue();
            if (stores.size() == 1){
                Store store = stores.get(0);
                LatLng latLng = new LatLng(store.getLatitude(), store.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerWhiteIcon(store.getmSubCategoryId()))))
                        .setTag(key);
            } else if (stores.size() > 1){
                Store store = stores.get(0);
                LatLng latLng = new LatLng(store.getLatitude(), store.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerWhiteStoreCounts(stores.size()))))
                        .setTag(key);
            }
        }

    }

    private void initList(){
        if (storeListAdapter == null){
            storeListAdapter = new FastItemAdapter<>();
        } else {
            storeListAdapter.clear();
        }

        List<Store> stores1 = new ArrayList<>();
        if (dataManager.selectedSubCategoryIds.size() == 0 || dataManager.selectedSubCategoryIds.size() == dataManager.getSubCategoryIds().size()){
            stores1 = dataManager.getStores();
        } else {
            for (int i : dataManager.selectedSubCategoryIds){
                for (Store store : dataManager.getStores()){
                    if (store.getmSubCategoryId() == i){
                        stores1.add(store);
                    }
                }
            }
        }

        if (dataManager.myLocation != null){
            Collections.sort(stores1, new Comparator<Store>() {
                @Override
                public int compare(Store o1, Store o2) {

                    Location location1 = new Location(o1.getName());
                    location1.setLatitude(o1.getLatitude());
                    location1.setLongitude(o1.getLongitude());

                    Location location2 = new Location(o2.getName());
                    location2.setLatitude(o2.getLatitude());
                    location2.setLongitude(o2.getLongitude());

                    float distance1 = dataManager.myLocation.distanceTo(location1);
                    float distance2 = dataManager.myLocation.distanceTo(location2);

                    return Float.compare(distance1, distance2);
                }
            });
        }

        for (Store store : stores1){
            storeListAdapter.add(new StoreInfoListItem(store));
        }
    }

    private void showMap(){
        rlMap.setVisibility(View.VISIBLE);
    }

    private void hideMap(){
        dataManager.isSelectedStore = false;
        hideStoreInfoView();

        if (previousMarker != null){
            unSelectMarker(previousMarker);
        }

        rlMap.setVisibility(View.INVISIBLE);
    }

    private void showList(){
        llList.setVisibility(View.VISIBLE);
        initList();
    }

    private void hideList(){
        if (storeListAdapter != null){
            storeListAdapter.clear();
        }
        llList.setVisibility(View.INVISIBLE);

    }

    private void showStoreInfoView(){

        llStoreInfo.setVisibility(View.VISIBLE);

        indicator.removeAllViews();
        indicatorViews = new IndicatorView[selectedStores.size()];
        if (selectedStores.size() == 1){
            indicator.setVisibility(View.GONE);
        } else {
            indicator.setVisibility(View.VISIBLE);
            for (int i = 0 ; i < selectedStores.size() ; i ++){
                indicatorViews[i] = new IndicatorView(context);
                indicatorViews[i].setDeltaX(3);
                indicator.addView(indicatorViews[i]);
            }
        }

        viewPager.setAdapter(new StoreInfoItem(context, selectedStores, true));
        viewPager.setInterval(3000);
        if (selectedStores.size() == 1){
            viewPager.stopAutoScroll();
            viewPager.setPagingEnabled(false);
            viewPager.removeOnPageChangeListener(this);

        } else {
            viewPager.startAutoScroll();
            viewPager.addOnPageChangeListener(this);
        }

    }

    private void hideStoreInfoView(){
        llStoreInfo.setVisibility(View.GONE);
        viewPager.setAdapter(null);
        viewPager.stopAutoScroll();
        viewPager.removeOnPageChangeListener(this);
        selectedStores = null;
    }

    @Click(R.id.rlFilter)
    void filterButtonClicked(){
        NearByFilterActivity_.intent(context).start();
    }

    @Click(R.id.ivMyLocation)
    void myLocationButtonClicked(){
        final LocationManager mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showLocationEnableAlert();
        } else {
            if (mMap != null){
                if (dataManager.myLocation != null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dataManager.myLocation.getLatitude(),dataManager.myLocation.getLongitude()), 14));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat,mLng), 10.3f));
                }
            }
        }
    }

    @CheckedChange({R.id.btnMap, R.id.btnList})
    void checkedChanged(boolean isChecked, CompoundButton button) {
        if (isChecked){
            if (button.getId() == R.id.btnMap){
                showMap();
                hideList();
            } else if (button.getId() == R.id.btnList){
                showList();
                hideMap();
            }
        }
    }

    @Click(R.id.ivSearch)
    void searchButtonClicked(){
        BrandSearchActivity_.intent(context).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        dataManager.isSelectedStore = false;
        updateFilterState();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (!dataManager.isSelectedStore){
            hideStoreInfoView();
        }
//        stopLocationService();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        moveCamera(marker);

        if (previousMarker != null && previousMarker != marker){
            unSelectMarker(previousMarker);
        }

        if (marker != previousMarker){
            selectMarker(marker);

            previousMarker = marker;
        }

        return true;
    }

    private void moveCamera(Marker marker){
        int mapViewHeight = mapView.getHeight();

        Projection projection = mMap.getProjection();

        LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude);
        Point markerScreenPosition = projection.toScreenLocation(markerLatLng);

        Point pointHalfScreenAbove = new Point(markerScreenPosition.x,
                markerScreenPosition.y + (mapViewHeight / 8));

        LatLng aboveMarkerLatLng = projection
                .fromScreenLocation(pointHalfScreenAbove);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(aboveMarkerLatLng), 500, null);
    }

    private void selectMarker(Marker marker){

        if (marker.getTag() != null){
            String key = (String) marker.getTag();
            if (sortedStores.containsKey(key)){
                List<Store> stores = sortedStores.get(key);
                if (stores.size() == 1){
                    Store store = stores.get(0);
                    marker.setIcon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerRedIcon(store.getmSubCategoryId())));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerRedStoreCounts(stores.size())));
                }

                selectedStores = stores;
                showStoreInfoView();
            }
        }
    }

    private void unSelectMarker(Marker marker){

        if (marker.getTag() != null){
            String key = (String) marker.getTag();
            if (sortedStores.containsKey(key)){
                List<Store> stores = sortedStores.get(key);
                if (stores.size() == 1){
                    Store store = stores.get(0);
                    marker.setIcon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerWhiteIcon(store.getmSubCategoryId())));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(FuzzieUtils.getMarkerWhiteStoreCounts(stores.size())));
                }
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

        dataManager.isSelectedStore = false;
        hideStoreInfoView();

        if (previousMarker != null){
            unSelectMarker(previousMarker);
        }
    }

    void updateFilterState(){
        if (dataManager.selectedSubCategoryIds.size() == 0){
            ivFilterChecked.setVisibility(View.GONE);
        } else {
            ivFilterChecked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GPS_SERVICES_ENABLE_REQUEST){
            if (resultCode == RESULT_OK){
                startLocationService();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition = position % selectedStores.size();
        for (int i = 0 ; i < selectedStores.size() ; i ++){
            if (i == realPosition){
                if (indicatorViews[i] != null){
                    indicatorViews[i].setSelect(true);
                }
            } else {
                if (indicatorViews[i] != null){
                    indicatorViews[i].setSelect(false);
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
