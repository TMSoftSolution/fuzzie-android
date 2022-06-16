package sg.com.fuzzie.android.ui.club;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;

import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.ClubStoreDetail;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.api.models.TripAdvisor;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.club.ClubOfferItem;
import sg.com.fuzzie.android.items.club.ClubStoreFinePrintItem;
import sg.com.fuzzie.android.items.club.ClubStoreItem;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.TripAdvisorRatingView;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_HOME_LOADED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CLUB_HOME_LOAD_FAIELD;

@EActivity(R.layout.activity_club_store)
public class ClubStoreActivity extends BaseActivity {

    BroadcastReceiver broadcastReceiver;

    private static final int REQUEST_CHANGE_STORE = 0;

    private ClubStore clubStore;
    private Store store;
    private Brand brand;
    private boolean isBookmarked;
    private List<ClubStore> nearbyStores;
    private List<ClubStore> relatedStores;

    FastItemAdapter<ClubOfferItem> offerAdapter;
    FastItemAdapter<ClubStoreItem> relatedStoresAdapter;
    FastItemAdapter<ClubStoreItem> nearStoresAdapter;
    FastItemAdapter<ClubStoreFinePrintItem> fineAdapter;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.vpPhotos)
    ViewPager vpPhotos;

    @ViewById(R.id.tlPhotosIndicator)
    TabLayout tlPhotosIndicator;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.ivCategory)
    ImageView ivCategory;

    @ViewById(R.id.tvCategory)
    TextView tvCategory;

    @ViewById(R.id.ellAbout)
    ExpandableRelativeLayout ellAbout;

    @ViewById(R.id.tvBrandAboutShort)
    TextView tvBrandAboutShort;

    @ViewById(R.id.tvBrandAbout)
    TextView tvBrandAbout;

    @ViewById(R.id.llSocialMedia)
    LinearLayout llSocialMedia;

    @ViewById(R.id.ivFacebook)
    ImageView ivFacebook;

    @ViewById(R.id.ivTwitter)
    ImageView ivTwitter;

    @ViewById(R.id.ivInstagram)
    ImageView ivInstagram;

    @ViewById(R.id.btnWebsite)
    Button btnWebsite;

    @ViewById(R.id.rvOffer)
    RecyclerView rvOffer;

    @ViewById(R.id.rvRelatedStores)
    RecyclerView rvRelatedStores;

    @ViewById(R.id.rvNearStores)
    RecyclerView rvNearStores;

    @ViewById(R.id.rvFinePrint)
    RecyclerView rvFinePrint;

    @ViewById(R.id.tvStoreName)
    TextView tvStoreName;

    @ViewById(R.id.tvStoreAddress)
    TextView tvStoreAddress;

    @ViewById(R.id.tvStoreBusinessTime)
    TextView tvStoreBusinessTime;

    @ViewById(R.id.tvLocation)
    TextView tvLocation;

    @ViewById(R.id.tvDistance)
    TextView tvDistance;

    @ViewById(R.id.btnTripAdvisor)
    LinearLayout btnTripAdvisor;

    @ViewById(R.id.vTripAdvisorRating)
    TripAdvisorRatingView vTripAdvisorRating;

    @ViewById(R.id.tvTripAdvisorReviewCount)
    TextView tvTripAdvisorReviewCount;

    @ViewById(R.id.tvOpened)
    TextView tvOpened;

    @ViewById(R.id.tvOpenState)
    TextView tvOpenState;

    @ViewById(R.id.ivBookmark)
    Button ivBookmark;

    @ViewById(R.id.tvStoreMore)
    TextView tvStoreMore;

    @ViewById(R.id.tvStoreMore1)
    TextView tvStoreMore1;

    @ViewById(R.id.cvCallStore)
    CardView cvCallStore;

    @ViewById(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar ;


    @AfterViews
    void callAfterViewInjection(){

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clubStore = dataManager.clubStore;

        if (clubStore == null) return;

        store = dataManager.getStoreById(clubStore.getStoreId());
        if (store == null) return;

        brand = dataManager.getBrandById(store.getBrandId());
        if (brand == null) return;

        tvTitle.setText(clubStore.getBrandName());
        dataManager.setClubMoreStores(null);

        isBookmarked = currentUser.getCurrentUser().getBookmakredStoreIds().contains(clubStore.getStoreId());

        offerAdapter = new FastItemAdapter<>();
        rvOffer.setLayoutManager(new LinearLayoutManager(context));
        rvOffer.setAdapter(offerAdapter);
        offerAdapter.withOnClickListener(new OnClickListener<ClubOfferItem>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter<ClubOfferItem> adapter, ClubOfferItem item, int position) {

                boolean isNotRedeemed = item.getOffer().getRedeemDetail() == null;
                boolean isIn24Hour = item.getOffer().getRedeemDetail() != null
                                    && item.getOffer().getRedeemDetail().getRedeemTimerStartedAt() != null
                                    && TimeUtils.isInRedeem(item.getOffer().getRedeemDetail().getRedeemTimerStartedAt());

                if (isNotRedeemed || isIn24Hour){

                    dataManager.offer = item.getOffer();
                    ClubOfferActivity_.intent(context).start();

                }

                return false;
            }
        });

        relatedStoresAdapter = new FastItemAdapter<>();
        rvRelatedStores.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvRelatedStores.setAdapter(relatedStoresAdapter);

        nearStoresAdapter = new FastItemAdapter<>();
        rvNearStores.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvNearStores.setAdapter(nearStoresAdapter);

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

        if (dataManager.getClubHome() == null){

            showLoader();

        } else {

            setUI();
        }
    }

    private void setUI(){

        showBrandInfo();


        if (clubStore.isOnline()){

            ViewUtils.gone(findViewById(R.id.physicalStoreInfo));
            ViewUtils.gone(findViewById(R.id.storeDetails));
            ViewUtils.gone(findViewById(R.id.otherInfos));

        } else {

            updateOpenState();
            showStoreDetails();
            updateStoreMoreCount();

        }

        showAboutBrand();
        showOffers();
        updateTripAdvisorRatings();


        if (isBookmarked){
            ivBookmark.setBackgroundResource(R.drawable.transition_bookmark_select);
        } else {
            ivBookmark.setBackgroundResource(R.drawable.transition_bookmark_unselect);
        }

        loadClubStoreDetail();

        handleAppBarScroll();
    }

    private void handleAppBarScroll (){

        // get the rate of scroll of the appBar set it as transparency for the toolbar
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                //  Drawable drawable = getDrawable(R.color.colorPrimary) ;

                // Vertical scroll
                float verticalScroll = - verticalOffset ;

                // Total scroll of the appbar
                float totalScroll = appBarLayout.getTotalScrollRange() ;

                // Get rate of scroll
                float scrollRate = verticalScroll /totalScroll ;

                // Get Alpha rate
                int alphaRate = Math.round(scrollRate * 255 ) ;

                if (totalScroll  - verticalScroll  <= mToolbar.getHeight() - 20 ){
                    alphaRate = 255 ;
                }

                if (totalScroll - verticalScroll <= (ivBookmark.getHeight()/2)){
                    ViewUtils.gone(ivBookmark);
                }else{
                    ViewUtils.visible(ivBookmark);
                }

                // mToolbar.getBackground().setAlpha(alphaRate);
                ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimary)) ;
                colorDrawable.setAlpha(alphaRate);

                mToolbar.setBackground(colorDrawable );

            }
        });
    }


    private void showBrandInfo(){

        vpPhotos.postDelayed(new Runnable() {
            @Override
            public void run() {
                vpPhotos.setBackground(null);
                vpPhotos.setAdapter(new BrandGiftActivity.ImageSlideAdapter(context, brand.getPhotos()));
                if (brand.getPhotos().size() == 1) {
                    tlPhotosIndicator.setVisibility(View.GONE);
                } else {
                    tlPhotosIndicator.setupWithViewPager(vpPhotos, true);
                    for (int i = 0; i < tlPhotosIndicator.getTabCount(); i++) {
                        View view = getLayoutInflater().inflate(R.layout.item_photo_indicator, tlPhotosIndicator, false);
                        tlPhotosIndicator.getTabAt(i).setCustomView(view);
                    }
                }
            }
        }, 500);


        tvBrandName.setText(brand.getName());

        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryBlackIcon(brand.getSubCategoryId()));
        ivCategory.setImageDrawable(subCategoryIcon);

        Category subCategory = FuzzieUtils.getSubCategory(dataManager.getHome().getSubCategories(),brand.getSubCategoryId());
        if (subCategory != null){
            tvCategory.setText(subCategory.getName());
        }
    }

    private void updateBookmark(){

        TransitionDrawable transition = (TransitionDrawable) ivBookmark.getBackground();

        if (isBookmarked){

            transition.startTransition(500);

        } else {

            transition.reverseTransition(500);
        }
    }

    private void updateOpenState(){

        if (clubStore.isClosingSoon()){

            tvOpened.setText("  CLOSING SOON ");
            tvOpened.setBackground(getResources().getDrawable(R.drawable.bg_store_close_soon));
            tvOpenState.setText(String.format("AT %s TODAY", clubStore.getCloseTime()));


        } else {


            if (clubStore.isOpen()){

                tvOpened.setText("  OPEN NOW ");
                tvOpened.setBackground(getResources().getDrawable(R.drawable.bg_store_opened));
                tvOpenState.setText(String.format("UNTIL %s TODAY", clubStore.getCloseTime()));

            } else {

                tvOpened.setText("  CLOSED  ");
                tvOpened.setBackground(getResources().getDrawable(R.drawable.bg_store_closed));
                tvOpenState.setText(String.format("OPEN AT %s TOMORROW", clubStore.getOpenTime()));
            }
        }

    }

    private void showAboutBrand(){

        tvBrandAbout.setText(brand.getDescription());
        if (brand.getDescription().length() > 180) {

            tvBrandAboutShort.setText(FuzzieUtils.fromHtml(brand.getDescription().substring(0, 180) + "&#133; <b><font color='#fa3e3f'>More</font></u>"));
            ellAbout.setClosePosition(300);

            ellAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ellAbout.toggle();
                }
            });

            ellAbout.setListener(new ExpandableLayoutListener() {
                @Override
                public void onAnimationStart() {
                }
                @Override
                public void onAnimationEnd() {

                }

                @Override
                public void onPreOpen() {

                    ViewUtils.gone(tvBrandAboutShort);
                    ViewUtils.visible(tvBrandAbout);
                    ViewUtils.visible(llSocialMedia);

                    ellAbout.setEnabled(false);

                }

                @Override
                public void onPreClose() {
                }

                @Override
                public void onOpened() {
                }

                @Override
                public void onClosed() {

                    ViewUtils.visible(tvBrandAboutShort);
                    ViewUtils.gone(tvBrandAbout);
                    ViewUtils.gone(llSocialMedia);
                }
            });

            ellAbout.collapse();

        } else {

            ViewUtils.gone(tvBrandAboutShort);
            ViewUtils.visible(tvBrandAbout);
            ViewUtils.visible(llSocialMedia);
        }
    }

    private void showStoreDetails(){

        tvStoreName.setText(store.getName());
        tvStoreAddress.setText(store.getAddress());
        tvStoreBusinessTime.setText(store.getBusinessHours());

        if (store.getPhone() == null || store.getPhone().equals("")){

            ViewUtils.gone(cvCallStore);
        }

        tvLocation.setText(store.getName());
        tvDistance.setText(clubStore.getDistance() != null ? String.format("%.2f km", clubStore.getDistance()) : "");

    }

    private void showOffers(){

        if (offerAdapter == null){

            offerAdapter = new FastItemAdapter<>();

        } else {

            offerAdapter.clear();
        }

        for (Offer offer : clubStore.getOffers()){

            offerAdapter.add(new ClubOfferItem(offer));
        }
    }

    private void loadClubStoreDetail(){

        Call<ClubStoreDetail> call = FuzzieAPI.APIService().getClubStoreDetail(currentUser.getAccesstoken(), clubStore.getStoreId());
        if (dataManager.myLocation != null){
            call = FuzzieAPI.APIService().getClubStoreDetail(currentUser.getAccesstoken(), dataManager.myLocation.getLatitude(), dataManager.myLocation.getLongitude(), clubStore.getStoreId());
        }

        call.enqueue(new Callback<ClubStoreDetail>() {
            @Override
            public void onResponse(Call<ClubStoreDetail> call, Response<ClubStoreDetail> response) {

                if (response.code() == 200 && response.body() != null){

                    relatedStores = response.body().getRelatedStores();
                    showRelatedStores();

                    nearbyStores = response.body().getNearbyStores();
                    showNearStores();

                    List<ClubStore> clubStores = response.body().getMoreStores();
                    clubStores.add(0, clubStore);
                    dataManager.setClubMoreStores(clubStores);
                    updateStoreMoreCount();
                }
            }

            @Override
            public void onFailure(Call<ClubStoreDetail> call, Throwable t) {

            }
        });
    }

    private void showRelatedStores(){

        if (relatedStores == null || relatedStores.size() == 0){

            ViewUtils.gone(findViewById(R.id.llRelatedStores));

        } else {

            ViewUtils.visible(findViewById(R.id.llRelatedStores));

            if (relatedStoresAdapter == null){

                relatedStoresAdapter = new FastItemAdapter<>();

            } else {

                relatedStoresAdapter.clear();
            }

            for (int i = 0 ; i < relatedStores.size() ; i ++){
                relatedStoresAdapter.add(new ClubStoreItem(relatedStores.get(i), this));
            }
        }

    }

    private void showNearStores(){

        if (nearbyStores == null || nearbyStores.size() == 0){

            ViewUtils.gone(findViewById(R.id.llNearStores));

        } else {

            ViewUtils.visible(findViewById(R.id.llNearStores));

            if (nearStoresAdapter == null){

                nearStoresAdapter = new FastItemAdapter<>();

            } else {

                nearStoresAdapter.clear();
            }

            for (int i = 0 ; i < nearbyStores.size() ; i ++){
                nearStoresAdapter.add(new ClubStoreItem(nearbyStores.get(i), this));
            }
        }

    }

    private void showFinePrint(){

        fineAdapter = new FastItemAdapter<>();
        rvFinePrint.setLayoutManager(new LinearLayoutManager(context));
        rvFinePrint.setAdapter(fineAdapter);
        for (int i = 0 ; i < 2 ; i ++){
            fineAdapter.add(new ClubStoreFinePrintItem());
        }
    }

    private void updateTripAdvisorRatings() {

        if (brand.getTripAdvisor() != null) {

            final TripAdvisor tripAdvisor = brand.getTripAdvisor();
            if (tripAdvisor.getReviewsCount() != null && tripAdvisor.getReviewsCount() != 0 && tripAdvisor.getLink() != null && !tripAdvisor.getLink().equals("")) {

                vTripAdvisorRating.setRating((int) tripAdvisor.getRating());
                tvTripAdvisorReviewCount.setText(FuzzieUtils.fromHtml(String.format("(<b>%d</b> Review" + (tripAdvisor.getReviewsCount() > 1 ? "s" : "" ) + ")", tripAdvisor.getReviewsCount())));

                btnTripAdvisor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(getResources().getColor(R.color.primary));
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(ClubStoreActivity.this, Uri.parse(tripAdvisor.getLink()));
                    }
                });

                ViewUtils.visible(btnTripAdvisor);

            } else {

                ViewUtils.gone(btnTripAdvisor);
            }

        } else {

            ViewUtils.gone(btnTripAdvisor);
        }
    }

    private void updateStoreMoreCount(){

        if (dataManager.getClubMoreStores() == null || dataManager.getClubMoreStores() .size() == 0){

            ViewUtils.gone(tvStoreMore);
            ViewUtils.gone(findViewById(R.id.btnStoreLocations));
            ViewUtils.gone(findViewById(R.id.storelocationDivider));

        } else {

            ViewUtils.visible(tvStoreMore);
            ViewUtils.visible(findViewById(R.id.btnStoreLocations));
            ViewUtils.visible(findViewById(R.id.storelocationDivider));

            tvStoreMore.setText(String.format("%d more stores", dataManager.getClubMoreStores().size()));
            tvStoreMore1.setText(String.format("%d more store locations", dataManager.getClubMoreStores().size()));
        }

    }

    @Click(R.id.ivReferral)
    void referalButtonClicked(){

        if (currentUser.getCurrentUser().getFuzzieClub().isMembership()) {

            ClubReferralActivity_.intent(context).start();

        } else {

            showFZAlert("JOIN THE CLUB", "Join the Club to activate your referral rewards.", "LEARN MORE", "Close", R.drawable.ic_bear_club);

        }
    }

    @Click(R.id.ivShare)
    void shareButtonClicked(){

        if (currentUser.getCurrentUser().getFuzzieClub().isMembership()) {

            ShareCompat.IntentBuilder.from(this) .setType("text/plain") .setText(String.format("Check out the Fuzzie Club for cool 1-for-1 deals and more from popular restaurants, attractions, spas and online shopping sites. I’m using it to save a lot of money. Use my code to get an extra $10 credits when you subscribe. %s www.fuzzie.com.sg/club", currentUser.getCurrentUser().getFuzzieClub().getReferralCode())).setSubject("Get free $10 credits on Fuzzie").startChooser();

        } else {

            showFZAlert("JOIN THE CLUB", "Join the Club to activate your referral rewards.", "LEARN MORE", "Close", R.drawable.ic_bear_club);

        }

    }

    @Click({R.id.tvStoreMore, R.id.btnStoreLocations})
    void storeMoreButtonClicked(){

        if (dataManager.getClubMoreStores()  == null) return;

        ClubStoresLocationActivity_.intent(context).startForResult(REQUEST_CHANGE_STORE);
    }

    @Click(R.id.ivBookmark)
    void bookmarkButtonClicked(){

        Call<Void> call = null;

        isBookmarked = !isBookmarked;
        updateBookmark();
        if (isBookmarked){

            Toast.makeText(this, "Store added to your favorites", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "Store removed from your favorites", Toast.LENGTH_SHORT).show();
        }
        currentUser.bookmarkStore(clubStore.getStoreId(), isBookmarked);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_CLUB_STORE_BOOKMARK_STATE_CHANGED));

        if (isBookmarked){

            call = FuzzieAPI.APIService().storeBookmarked(currentUser.getAccesstoken(), clubStore.getStoreId());

        } else {

            call = FuzzieAPI.APIService().storeUnBookmarked(currentUser.getAccesstoken(), clubStore.getStoreId());
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    @Click(R.id.tvViewAllRelatedStores)
    void relatedStoresViewAllButtonClicked(){

        if (relatedStores == null) return;

        dataManager.setOffers(null);
        dataManager.setTempStores(relatedStores);

        ClubStoreListActivity_.intent(context).flashMode(false).extraTitle("RELATED STORES").start();
    }

    @Click(R.id.tvViewAllNearbyStores)
    void nearbyStoresViewAllButtonClicked(){

        if (nearbyStores == null) return;

        dataManager.setOffers(null);
        dataManager.setTempStores(nearbyStores);

        ClubStoreListActivity_.intent(context).flashMode(false).extraTitle("NEAR THIS STORES").start();
    }

    @Click(R.id.cvCallStore)
    void callButtonClicked(){

        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + store.getPhone()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Click(R.id.cvDirection)
    void directionButtonClicked(){

        if (store == null) return;

//        String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", store.getLatitude(), store.getLongitude());
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", store.getLatitude(), store.getLongitude(), store.getAddress());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException ex)
        {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx)
            {
                Toast.makeText(this, "Please install a google map application", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_CHANGE_STORE){

                if (data != null){

                    if (data.getBooleanExtra("club_store_selected", false)){

                        clubStore = dataManager.clubStore;
                        store = dataManager.getStoreById(clubStore.getStoreId());

                        if (clubStore != null && store != null){

                            showOffers();
                            updateOpenState();
                            showStoreDetails();
                        }
                    }
                }
            }
        }
    }

    // FZAlertDialog Listener
    @Override
    public void FZAlertOkButtonClicked() {

        super.FZAlertOkButtonClicked();

        ClubSubscribeActivity_.intent(context).start();

    }

    @Override
    public void FZAlertCancelButtonClicked() {

        super.FZAlertCancelButtonClicked();
    }
}
