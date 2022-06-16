package sg.com.fuzzie.android.ui.club;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import me.crosswall.lib.coverflow.core.PagerContainer;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.adapter.ClubStoreInfoAdapter;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubStoreLocationItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import timber.log.Timber;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_HOME_LOADED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_HOME_LOAD_FAIELD;

@EActivity(R.layout.activity_club_stores_location)
public class ClubStoresLocationActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, ClubStoreInfoAdapter.ClubStoreInfoAdapterListener {

    BroadcastReceiver broadcastReceiver;

    FastItemAdapter<ClubStoreLocationItem> adapter;
    private boolean isMapMode;
    private GoogleMap mMap;

    private List<ClubStore> clubStores;
    private List<Store> stores;
    private List<Marker> markers;
    private Marker previousMarker;
    private boolean markerClicked;  // This variable is used to prevent duplicated marker select.

    private static final double mLat = 1.400270;
    private static final double mLng = 103.831959;


    @InstanceState
    int instanceState;

    @Extra
    boolean showStore;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.rvStores)
    RecyclerView rvStores;

    @ViewById(R.id.rlMap)
    View mapContainer;

    @ViewById(R.id.mapView)
    MapView mapView;

    @ViewById(R.id.ivMode)
    ImageView ivMode;

    @ViewById(R.id.pageContainer)
    PagerContainer pagerContainer;

    @AfterViews
    void callAfterViewInjection(){

        if (dataManager.getClubMoreStores() == null) return;
        clubStores = dataManager.getClubMoreStores();

        mappingStores();

        mapView.onCreate(new Bundle(instanceState));
        mapView.onResume();

        try{
            MapsInitializer.initialize(context);
        } catch (Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        adapter = new FastItemAdapter<>();
        rvStores.setLayoutManager(new LinearLayoutManager(context));
        rvStores.setAdapter(adapter);
        adapter.withOnClickListener(new OnClickListener<ClubStoreLocationItem>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter<ClubStoreLocationItem> adapter, ClubStoreLocationItem item, int position) {

                ClubStore clubStore = item.getClubStore();
                chooseStore(clubStore);

                return true;
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_CLUB_HOME_LOADED)){

                    hideLoader();
                    setUI();

                } else if (intent.getAction().equals(BROADCAST_CLUB_HOME_LOAD_FAIELD)){

                    finish();
                }

            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_CLUB_HOME_LOADED));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_CLUB_HOME_LOAD_FAIELD));

        showList();
        if (dataManager.getClubHome() == null){

            showLoader();

        } else {

            setUI();
        }
    }

    private void setUI(){

        initList();
        initInfos();
    }

    private void mappingStores(){

        stores = new ArrayList<>();

        for (ClubStore clubStore : clubStores){

            Store store = dataManager.getStoreById(clubStore.getStoreId());
            if (store != null){
                stores.add(store);
            }
        }
    }

    private void initList(){

        showList();

        if (adapter == null){
            adapter = new FastItemAdapter<>();
        } else {
            adapter.clear();
        }

        for (int i = 0 ; i < clubStores.size() ; i ++){

            Store store = dataManager.getStoreById(clubStores.get(i).getStoreId());
            if (store != null){

                adapter.add(new ClubStoreLocationItem(clubStores.get(i), store));

            }
        }

    }

    private void showList(){

        ViewUtils.visible(rvStores);
        ViewUtils.gone(mapContainer);

    }

    private void showMap(){

        ViewUtils.gone(rvStores);
        ViewUtils.visible(mapContainer);
        ViewUtils.gone(pagerContainer);

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

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.ivMode)
    void modeButtonClicked(){

        isMapMode = !isMapMode;

        if (isMapMode){

            showMap();
            ivMode.setImageResource(R.drawable.ic_club_list);

        } else {

            showList();
            ivMode.setImageResource(R.drawable.ic_club_location);
        }
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

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void itemClicked(ClubStore clubStore) {

        chooseStore(clubStore);
    }

    private void chooseStore(ClubStore clubStore){

        dataManager.clubStore = clubStore;

        if (showStore){

            ClubStoreActivity_.intent(context).start();

        } else {

            Intent intent = new Intent();
            intent.putExtra("club_store_selected", true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
