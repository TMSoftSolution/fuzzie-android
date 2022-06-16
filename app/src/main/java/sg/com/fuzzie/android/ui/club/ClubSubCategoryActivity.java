package sg.com.fuzzie.android.ui.club;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
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
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

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
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Component;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubFilterTagItem;
import sg.com.fuzzie.android.items.club.ClubStoreListItem;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import timber.log.Timber;

@EActivity(R.layout.activity_club_sub_category)
public class ClubSubCategoryActivity extends BaseActivity implements ClubFilterTagItem.ClubFilterTagItemListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, ClubStoreInfoAdapter.ClubStoreInfoAdapterListener {

    private final int REQUEST_FILTER = 1000;

    private List<ClubStore> clubStores;
    private List<ClubStore> filteredStores;

    private boolean isMapMode;
    private GoogleMap mMap;

    private List<Store> stores;
    private List<Marker> markers;
    private Marker previousMarker;
    private boolean markerClicked;  // This variable is used to prevent duplicated marker select.

    private static final double mLat = 1.400270;
    private static final double mLng = 103.831959;

    FastItemAdapter<ClubStoreListItem> storeAdapter;
    FastItemAdapter<ClubFilterTagItem> tagAdapter;

    @Extra
    String titleExtra;

    @InstanceState
    int instanceState;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

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

    @ViewById(R.id.rvTag)
    RecyclerView rvTag;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvFilterCount)
    TextView tvFilterCount;

    @AfterViews
    void callAfterViewInjection(){

        tvTitle.setText(titleExtra);

        if (dataManager.categoryFilters == null){

            dataManager.categoryFilters = new ArrayList<>();

        }

        dataManager.componentFilters = new ArrayList<>();

        clubStores = dataManager.getBrandTypeClubStores();
        if (clubStores == null) return;

        filterStores();
        mappingStores();

        storeAdapter = new FastItemAdapter<>();
        rvStores.setLayoutManager(new LinearLayoutManager(context));
        rvStores.setAdapter(storeAdapter);

        updateStores();

        mapView.onCreate(new Bundle(instanceState));
        mapView.onResume();
        try{
            MapsInitializer.initialize(context);
        } catch (Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        initInfos();
        showMapOrStores();

        tagAdapter = new FastItemAdapter<>();
        rvTag.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvTag.setAdapter(tagAdapter);

        showTag();


    }

    private void filterStores(){

        List<ClubStore> categoryFilteredStores = new ArrayList<>();

        if (dataManager.categoryFilters.size() == 0){

            categoryFilteredStores = clubStores;

        } else {

            for (ClubStore clubStore : clubStores) {

                for (Category category : clubStore.getCategories()){

                    if (dataManager.categoryFilters.contains(category.getName())){

                        categoryFilteredStores.add(clubStore);
                        break;
                    }
                }
            }

        }

        List<ClubStore> componentFilteredStores = new ArrayList<>();

        if (dataManager.componentFilters.size() == 0){

            componentFilteredStores = categoryFilteredStores;

        } else {

            for (ClubStore clubStore : categoryFilteredStores){

                for (Component component : clubStore.getFilterComponents()){

                    if (dataManager.componentFilters.contains(component.getName())){

                        componentFilteredStores.add(clubStore);
                    }
                }
            }
        }

        filteredStores = componentFilteredStores;

    }

    private void mappingStores(){

        stores = new ArrayList<>();

        for (ClubStore clubStore : filteredStores){

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

    private void updateStores(){

        if (storeAdapter == null){

            storeAdapter = new FastItemAdapter<>();

        } else {

            storeAdapter.clear();

        }

        for (ClubStore clubStore : filteredStores){
            storeAdapter.add(new ClubStoreListItem(clubStore));
        }
    }

    private void showTag(){

        if (tagAdapter == null){

            tagAdapter = new FastItemAdapter<>();

        } else {

            tagAdapter.clear();

        }

        for (String tag : dataManager.categoryFilters){
            tagAdapter.add(new ClubFilterTagItem(tag, this));

        }

        for (String tag : dataManager.componentFilters){
            tagAdapter.add(new ClubFilterTagItem(tag, this));
        }

        showHideTagView();
    }

    private void showHideTagView(){

        if (tagAdapter.getAdapterItemCount() == 0){

            ViewUtils.gone(findViewById(R.id.tagView));
            ViewUtils.gone(tvFilterCount);

        } else {

            ViewUtils.visible(findViewById(R.id.tagView));
            ViewUtils.visible(tvFilterCount);
            tvFilterCount.setText(String.valueOf(tagAdapter.getAdapterItemCount()));

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
        ClubStoreInfoAdapter adapter = new ClubStoreInfoAdapter(this, filteredStores);
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

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.ivFilter)
    void filterButtonClicked(){
        ClubSubCategoryFilterActivity_.intent(context).startForResult(REQUEST_FILTER);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Click(R.id.ivMode)
    void modeButtonClicked(){

        isMapMode = !isMapMode;

        showMapOrStores();

    }

    @Click(R.id.cvSearch)
    void searchButtonClicked(){

        dataManager.setSearchClubStores(filteredStores);
        dataManager.setSearchClubPlaces(dataManager.getBrandTypeClubPlaces());

        ClubSearchActivity_.intent(context).start();
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
    public void tagRemove(int position, String tagName) {

        tagAdapter.remove(position);

        if (position < dataManager.categoryFilters.size()){

            dataManager.categoryFilters.remove(tagName);

        } else {

            dataManager.componentFilters.remove(tagName);

        }

        showHideTagView();

        filterStores();
        mappingStores();
        initInfos();

        updateStores();
        updateMap();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_FILTER){

                showTag();

                filterStores();
                mappingStores();
                initInfos();

                updateStores();
                updateMap();
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
    public void itemClicked(ClubStore clubStore) {

        dataManager.clubStore = clubStore;
        ClubStoreActivity_.intent(context).start();

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
