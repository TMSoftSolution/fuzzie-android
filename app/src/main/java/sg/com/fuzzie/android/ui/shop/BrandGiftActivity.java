package sg.com.fuzzie.android.ui.shop;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import sg.com.fuzzie.android.ui.club.ClubSubscribeActivity_;
import sg.com.fuzzie.android.ui.jackpot.PowerUpPackActivity_;
import sg.com.fuzzie.android.utils.HomeBrandsType;
import timber.log.Timber;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.FZUser;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.Photos;
import sg.com.fuzzie.android.api.models.TripAdvisor;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.gift.GiftCardItem;
import sg.com.fuzzie.android.items.me.LikerItem;
import sg.com.fuzzie.android.items.shop.ShopBrandsItem;
import sg.com.fuzzie.android.items.brand.ValidOptionNewItem;
import sg.com.fuzzie.android.ui.gift.GiftItActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotBrandCouponListActivity_;
import sg.com.fuzzie.android.ui.likes.LikerProfileActivity_;
import sg.com.fuzzie.android.ui.payments.CheckoutActivity_;
import sg.com.fuzzie.android.ui.payments.ShoppingBagActivity_;
import sg.com.fuzzie.android.utils.BlurBuilder;
import sg.com.fuzzie.android.utils.CenterLayoutManager;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.TripAdvisorRatingView;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESHED_SHOPPING_BAG;

/**
 * Created by nurimanizam on 17/12/16.
 */

@EActivity(R.layout.activity_brand_gift)
public class BrandGiftActivity extends BaseActivity {

    Brand brand;
    GiftCard giftCard;
    List<GiftCardItem> giftCardItemList;
    FastItemAdapter<GiftCardItem> giftAdapter;
    FastItemAdapter<ShopBrandsItem> alsoBoughtAdapter;
    FastItemAdapter<LikerItem> likerAdapter;
    FastItemAdapter<ValidOptionNewItem> validOptionAdapter;
    int selectedGiftCardPosition;

    BroadcastReceiver broadcastReceiver;

    @Extra
    String brandId;

    @Extra
    String brandExtra;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tvShoppingBag)
    TextView tvShoppingBag;

    @ViewById(R.id.vpPhotos)
    ViewPager vpPhotos;

    @ViewById(R.id.tlPhotosIndicator)
    TabLayout tlPhotosIndicator;

    @ViewById(R.id.tvSoldOut)
    TextView tvSoldOut;

    @ViewById(R.id.ivCategory)
    ImageView ivCategory;

    @ViewById(R.id.tvCategory)
    TextView tvCategory;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.rvGiftCards)
    RecyclerView rvGiftCards;

    @ViewById(R.id.btnAddToWishlist)
    LinearLayout btnAddToWishlist;

    @ViewById(R.id.tvCashback)
    TextView tvCashback;

    @ViewById(R.id.tvPowerUp)
    TextView tvPowerUp;

    @ViewById(R.id.tvEarn)
    TextView tvEarn;

    @ViewById(R.id.cvBuyIt)
    CardView cvBuyIt;

    @ViewById(R.id.cvGiftIt)
    CardView cvGiftIt;

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

    @ViewById(R.id.btnTripAdvisor)
    LinearLayout btnTripAdvisor;

    @ViewById(R.id.vTripAdvisorRating)
    TripAdvisorRatingView vTripAdvisorRating;

    @ViewById(R.id.tvTripAdvisorReviewCount)
    TextView tvTripAdvisorReviewCount;

    @ViewById(R.id.rvAlsoBought)
    RecyclerView rvAlsoBought;

    @ViewById(R.id.liker_count)
    AppCompatTextView tvLikers;

    @ViewById(R.id.liker_image)
    ImageView imgLiker;

    @ViewById(R.id.rvLikers)
    RecyclerView rvLikers;

    @ViewById(R.id.liker_more)
    ImageView ivLikerMore;

    @ViewById(R.id.empty)
    AppCompatTextView empty;

    @ViewById(R.id.llValidOptions)
    View llValidOptions;

    @ViewById(R.id.rvValidOption)
    RecyclerView rvValidOption;

    @ViewById(R.id.gifloader)
    GifImageView gifLoader;

    @ViewById(R.id.llParticipate)
    View llParticipate;

    @ViewById(R.id.ivJackpotBanner)
    ImageView ivJackpotBanner;

    @ViewById(R.id.cashContainer)
    View cashContainer;

    @ViewById(R.id.cashbackInfo)
    View cashbackInfo;

    @ViewById(R.id.tvCashbackPercentage)
    TextView tvCashbackPercentage;

    @ViewById(R.id.powerupInfo)
    View powerupInfo;

    @ViewById(R.id.tvPowerUpPercentage)
    TextView tvPowerUpPercentage;

    @ViewById(R.id.tvRedeemValid)
    TextView tvRedeemValid;

    @ViewById(R.id.vClubMember)
    View vClubMember;

    int likesCellCount;

    @AfterViews
    public void calledAfterViewInjection() {

        if (currentUser.getCurrentUser().getSettings().isDisplayGiftingPage() != null && currentUser.getCurrentUser().getSettings().isDisplayGiftingPage()){
            GiftingAvailabeActivity_.intent(context).start();
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (brandId == null) return;
        brand = dataManager.getBrandById(brandId);
        if (brand == null) return;
        brandExtra = Brand.toJSON(brand);

        if (brand.getGiftCards().size() == 1 || brand.getGiftCards().size() == 2) {
            giftCard = brand.getGiftCards().get(0);
            selectedGiftCardPosition = 0;
        } else if (brand.getGiftCards().size() == 3) {
            giftCard = brand.getGiftCards().get(1);
            selectedGiftCardPosition = 1;
        } else {
            giftCard = brand.getGiftCards().get(2);
            selectedGiftCardPosition = 2;
        }

        setBuyAndGiftIt(giftCard.isSoldOut());

        giftCardItemList = new ArrayList<>();

        vpPhotos.postDelayed(new Runnable() {
            @Override
            public void run() {
                vpPhotos.setBackground(null);
                vpPhotos.setAdapter(new ImageSlideAdapter(context, brand.getPhotos()));
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

        if (brand.isSoldOut() != null && brand.isSoldOut()) {
            tvSoldOut.setRotation(-20);
            tvSoldOut.setVisibility(View.VISIBLE);
            cvBuyIt.setEnabled(false);
        }

        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryWhiteIcon(brand.getSubCategoryId()));
        ivCategory.setImageDrawable(subCategoryIcon);

        if (dataManager.getHome() != null) {
            Category subCategory = FuzzieUtils.getSubCategory(dataManager.getHome().getSubCategories(),brand.getSubCategoryId());
            tvCategory.setText(subCategory.getName());
        } else {
            tvCategory.setText("");
        }

        tvTitle.setText(brand.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        refreshShoppingBag();

        tvBrandName.setText(giftCard.getDisplayName());

        if (brand.getOptionsForGiftCards() != null && brand.getOptionsForGiftCards().size() > 0){

            llValidOptions.setVisibility(View.VISIBLE);

            validOptionAdapter = new FastItemAdapter<>();
            rvValidOption.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvValidOption.setAdapter(validOptionAdapter);
            validOptionAdapter.clear();

            for (String option : brand.getOptionsForGiftCards()){
                validOptionAdapter.add(new ValidOptionNewItem(option));
            }
        } else {

            llValidOptions.setVisibility(View.GONE);
        }

        initCashbackInfo();

        giftAdapter = new FastItemAdapter<>();
        giftAdapter.withOnClickListener(new OnClickListener<GiftCardItem>() {
            @Override
            public boolean onClick(View v, IAdapter<GiftCardItem> adapter, GiftCardItem item, int position) {

                giftCard = item.getGiftCard();

                updateGiftCardDetails();

                for (GiftCardItem giftCardItem : giftCardItemList) {
                    if (giftCardItem.getGiftCard().getType().equals(giftCard.getType())) {
                        giftCardItem.setActive(true);
                    } else {
                        giftCardItem.setActive(false);
                    }
                }
                giftAdapter.notifyDataSetChanged();
                rvGiftCards.smoothScrollToPosition(position);
                selectedGiftCardPosition = position;
                return true;
            }
        });

        rvGiftCards.setLayoutManager(new CenterLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvGiftCards.smoothScrollToPosition(selectedGiftCardPosition);
        rvGiftCards.setAdapter(giftAdapter);

        giftAdapter.clear();
        for (GiftCard gc : brand.getGiftCards()) {

            GiftCardItem giftCardItem;
            if (gc.getType().equals(giftCard.getType())) {
                giftCardItem = new GiftCardItem(gc, true);
            } else {
                giftCardItem = new GiftCardItem(gc);
            }

            giftCardItemList.add(giftCardItem);
            giftAdapter.add(giftCardItem);

        }

        showRedeemValidInfo();
        updateWishlistButtonState();

        if (brand.isJackpotEnabled()){
            llParticipate.setVisibility(View.VISIBLE);
        }
        Picasso.get()
                .load(R.drawable.bg_jackpot_banner)
                .placeholder(R.drawable.bg_jackpot_banner)
                .into(ivJackpotBanner);

        if (brand.getFacebookUrl() == null) {
            ivFacebook.setVisibility(View.GONE);
        }

        if (brand.getTwitterUrl() == null) {
            ivTwitter.setVisibility(View.GONE);
        }

        if (brand.getInstagramUrl() == null) {
            ivInstagram.setVisibility(View.GONE);
        }

        if (brand.getWebsite() == null) {
            btnWebsite.setVisibility(View.GONE);
        }

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
                    tvBrandAboutShort.setVisibility(View.GONE);
                    tvBrandAbout.setVisibility(View.VISIBLE);
                    llSocialMedia.setVisibility(View.VISIBLE);
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
                    tvBrandAboutShort.setVisibility(View.VISIBLE);
                    tvBrandAbout.setVisibility(View.GONE);
                    llSocialMedia.setVisibility(View.GONE);
                }
            });
            ellAbout.collapse();
        } else {
            tvBrandAboutShort.setVisibility(View.GONE);
            tvBrandAbout.setVisibility(View.VISIBLE);
            llSocialMedia.setVisibility(View.VISIBLE);
        }

        updateTripAdvisorRatings();

        updateStoreLocationsState();

        alsoBoughtAdapter = new FastItemAdapter<>();
        rvAlsoBought.setLayoutManager(new LinearLayoutManager(context));
        rvAlsoBought.setAdapter(alsoBoughtAdapter);

        updateOtherAlsoBought();

        if (brand.getLikersCount() == 0){
            empty.setVisibility(View.VISIBLE);
            rvLikers.setVisibility(View.GONE);
            ivLikerMore.setVisibility(View.GONE);
        }  else {
            empty.setVisibility(View.GONE);
            rvLikers.setVisibility(View.VISIBLE);
            ivLikerMore.setVisibility(View.VISIBLE);
        }

        likerAdapter = new FastItemAdapter<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        rvLikers.setLayoutManager(linearLayoutManager);
        rvLikers.setAdapter(likerAdapter);
        likerAdapter.clear();

        updateLikers();

        likerAdapter.withOnClickListener(new OnClickListener<LikerItem>() {
            @Override
            public boolean onClick(View v, @NonNull IAdapter<LikerItem> adapter, @NonNull LikerItem item, int position) {
                User liker = item.getLiker();
                String userJson = User.toJSON(liker);
                LikerProfileActivity_.intent(context).userExtra(userJson).start();
                return true;
            }
        });

        tvLikers.setText(String.valueOf(brand.getLikersCount()));

        if (brand.isLiked()){
            imgLiker.setImageResource(R.drawable.ic_heart_fill);
        } else {
            imgLiker.setImageResource(R.drawable.ic_heart);
        }

        if (brand.isClubOnly()){

            ViewUtils.visible(vClubMember);

        } else {

            ViewUtils.gone(vClubMember);

        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BROADCAST_REFRESHED_SHOPPING_BAG)) {
                    refreshShoppingBag();
                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_REFRESHED_SHOPPING_BAG));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Click(R.id.ivShoppingBag)
    void shoppingBagButtonClicked() {
        ShoppingBagActivity_.intent(context).start();
    }

    @Click(R.id.tvLearnMore)
    void cashbackTextClicked() {
        ((FuzzieApplication_) getApplication()).lastActivityBitmap = BlurBuilder.blur(findViewById(android.R.id.content));
        CashbackInfoActivity_.intent(context).start();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Click(R.id.cvBuyIt)
    void buyButtonClicked() {

        ((FuzzieApplication_) getApplication()).lastActivityBitmap = BlurBuilder.blur(findViewById(android.R.id.content));
        dataManager.selectedBrand = brand;
        dataManager.selectedGiftCard = giftCard;

        if (brand.isClubOnly()){

            if (currentUser.getCurrentUser().getFuzzieClub() != null && currentUser.getCurrentUser().getFuzzieClub().isMembership()){

                CheckoutActivity_.intent(context).start();

            } else {

                showClubExclusiveAlert();

            }

        } else {

            CheckoutActivity_.intent(context).start();
        }

    }

    @Click(R.id.cvGiftIt)
    void giftItButtonClicked(){

        dataManager.selectedBrand = brand;
        dataManager.selectedGiftCard = giftCard;

        if (brand.isClubOnly()){

            if (currentUser.getCurrentUser().getFuzzieClub() != null && currentUser.getCurrentUser().getFuzzieClub().isMembership()){

                GiftItActivity_.intent(context).start();

            } else {

                showClubExclusiveAlert();

            }

        } else {

            GiftItActivity_.intent(context).start();
        }

    }

    @Click(R.id.ivFacebook)
    void facebookButtonClicked() {
        WebActivity_.intent(context).urlExtra(brand.getFacebookUrl()).titleExtra("FACEBOOK").start();
    }

    @Click(R.id.ivTwitter)
    void twitterButtonClicked() {
        WebActivity_.intent(context).urlExtra(brand.getTwitterUrl()).titleExtra("TWITTER").start();
    }

    @Click(R.id.ivInstagram)
    void instagramButtonClicked() {
        WebActivity_.intent(context).urlExtra(brand.getInstagramUrl()).titleExtra("INSTAGRAM").start();
    }

    @Click(R.id.btnWebsite)
    void websiteButtonClicked() {
        if (brand.getWebsite() != null){
            WebActivity_.intent(context).urlExtra(brand.getWebsite()).titleExtra("WEBSITE").start();
        }
    }

    @Click(R.id.btnTermsAndConditions)
    void termsButtonClicked() {
        BrandTermsActivity_.intent(context).brandId(brandId).start();
    }

    @Click(R.id.btnHowToRedeem)
    void howToRedeemButtonClicked() {
        BrandRedeemDetailsActivity_.intent(context).brandId(brandId).start();
    }

    @Click(R.id.btnStoreLocations)
    void storeLocationClicked() {
        BrandStoreLocationsActivity_.intent(context).brandId(brandId).start();
    }

    @Click(R.id.liker_set)
    void setLiked(){

        if (brand.isLiked()){

            likeRemoved();

            Call<Void> call = FuzzieAPI.APIService().removeLikeToBrand(currentUser.getAccesstoken(), brand.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.code() != 200){

                        likeAdded();
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    likeAdded();
                }
            });
        } else {

            likeAdded();

            Call<Void> call = FuzzieAPI.APIService().addLikeToBrand(currentUser.getAccesstoken(), brand.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.code() != 200){

                        likeRemoved();
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    likeRemoved();
                }
            });
        }
    }

    @Click(R.id.liker_more)
    void likerMoreButtonClicked(){
        if (brand.getLikers() != null){
            BrandLikersActivity_.intent(context).brandIdExtra(brand.getId()).start();
        }
    }

    private void initCashbackInfo(){

        if (giftCard == null) return;

        if (giftCard.getCashBack().getPercentage() > 0) {

            if (giftCard.getCashBack().getPowerUpPercentage() > 0) {

                tvPowerUp.setText(FuzzieUtils.getFormattedPowerUpPercentage(giftCard.getCashBack().getPowerUpPercentage(), 1));

                if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp()) {

                    tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpActive));
                    tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lighting_accent_16dp, 0, 0, 0);
                    tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_blue));

                    powerupInfo.setVisibility(View.GONE);
                    cashbackInfo.setBackground(context.getResources().getDrawable(R.drawable.bg_cash_back_info));


                } else {

                    tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpInactive));
                    tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ligthing_grey_16dp, 0, 0, 0);
                    tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_white));

                    tvPowerUpPercentage.setText(String.format("Get +%s%% Cashback", FuzzieUtils.getFormattedPercentage(giftCard.getCashBack().getPowerUpPercentage())));

                }

            } else {

                tvPowerUp.setVisibility(View.GONE);
                powerupInfo.setVisibility(View.GONE);
                cashbackInfo.setBackground(context.getResources().getDrawable(R.drawable.bg_cash_back_info));

            }

            if (giftCard.getCashBack().getCashBackPercentage() > 0) {

                tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(giftCard.getCashBack().getCashBackPercentage(), 1));

            } else {

                tvCashback.setVisibility(View.GONE);

            }

            if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp()) {

                tvCashbackPercentage.setText(String.format("%s%% Instant Cashback", FuzzieUtils.getFormattedPercentage(giftCard.getCashBack().getPercentage())));

            } else {

                tvCashbackPercentage.setText(String.format("%s%% Instant Cashback", FuzzieUtils.getFormattedPercentage(giftCard.getCashBack().getCashBackPercentage())));

            }

            updateGiftCardDetails();

        } else {

            cashContainer.setVisibility(View.GONE);
            tvCashback.setVisibility(View.GONE);
            tvPowerUp.setVisibility(View.GONE);
            cvBuyIt.setEnabled(false);
            cvGiftIt.setEnabled(false);

        }
    }

    void updateGiftCardDetails() {

        if (giftCard == null) return;

        if (giftCard.getCashBack().getPercentage() > 0) {

            if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp()){

                double cashback = Double.parseDouble(giftCard.getPrice().getValue()) * giftCard.getCashBack().getPercentage() / 100;
                tvEarn.setText(FuzzieUtils.getFormattedValue(cashback, 0.75f));

            } else {

                tvEarn.setText(FuzzieUtils.getFormattedValue(giftCard.getCashBack().getValue(), 0.75f));
            }

            setBuyAndGiftIt(giftCard.isSoldOut());

        }
    }

    private void showRedeemValidInfo(){

        if (giftCard == null) return;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date endDate = dateFormat.parse(giftCard.getRedeemEndDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endDate.getTime());

            Calendar now = Calendar.getInstance();
            long days = (calendar.getTimeInMillis() - now.getTimeInMillis()) / (24 * 60 * 60 * 1000);

            if (calendar.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY)){
                tvRedeemValid.setText("Valid for " + days + " days from purchase");
            } else {
                tvRedeemValid.setText("Valid for " + (days + 1) + " days from purchase");

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void refreshShoppingBag() {
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

    void likeAdded(){

        dataManager.updateBrand(brand, true);
        currentUser.likeBrand(brand.getId(), true);

        imgLiker.setImageResource(R.drawable.ic_heart_fill);
        tvLikers.setText(String.valueOf(brand.getLikersCount()));

        sendBroadcastLikeAdded();
    }

    void likeRemoved(){

        dataManager.updateBrand(brand, false);
        currentUser.likeBrand(brand.getId(), false);

        imgLiker.setImageResource(R.drawable.ic_heart);
        tvLikers.setText(String.valueOf(brand.getLikersCount()));

        sendBroadcastLikeRemoved();
    }

    void updateOtherAlsoBought(){
        Call<List<String>> call = FuzzieAPI.APIService().getAlsoBought(currentUser.getAccesstoken(), brand.getId());
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.code() == 200 && response.body() != null){
                    brand.setOthersAlsoBought(response.body());
                    alsoBoughtAdapter.add(new ShopBrandsItem(dataManager.getHome(), HomeBrandsType.OTHER_ALSO_BOUGHT,brand, currentUser.getAccesstoken()));
                    alsoBoughtAdapter.notifyAdapterDataSetChanged();
                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  BrandGiftActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
    }

    void updateLikers(){
        gifLoader.setVisibility(View.VISIBLE);
        Call<List<User>> call1 = FuzzieAPI.APIService().getLikes(currentUser.getAccesstoken(), brand.getId());
        call1.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200 & response.body() != null){
                    List<User> likes = new ArrayList<User>();
                    likes = response.body();
                    for (User user : likes){

                        if (user.getAvatar() == null){
                            user.setAvatar("");
                        }
                        user.setAvatarBear(FuzzieUtils.generateRandomAvatarId());
                    }

                    Collections.sort(likes, new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {

                            if (o1.getFriend().compareTo(o2.getFriend()) != 0){
                                return o1.getFriend().compareTo(o2.getFriend());
                            } else {
                                int comp = o1.getAvatar().compareTo(o2.getAvatar());
                                if (comp != 0){
                                    return comp;
                                } else {
                                    return o1.getAvatarBear().compareTo(o2.getAvatarBear());
                                }
                            }

                        }
                    });
                    Collections.reverse(likes);
                    brand.setLikers(likes);
                    if (ViewUtils.getLikesCellCount(getApplicationContext(), (int)(Math.log10(brand.getLikersCount())+1)) > brand.getLikers().size()){
                        likesCellCount = brand.getLikers().size();
                    } else {
                        likesCellCount = ViewUtils.getLikesCellCount(getApplicationContext(), (int)(Math.log10(brand.getLikersCount())+1));
                    }

                    for (int i = 0 ; i < likesCellCount ; i ++){
                        User liker = brand.getLikers().get(i);
                        LikerItem likerItem = new LikerItem(liker);
                        likerAdapter.add(likerItem);
                    }

                    likerAdapter.notifyAdapterDataSetChanged();
                    gifLoader.setVisibility(View.GONE);

                } else if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  BrandGiftActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Timber.e("Likes:" + t.getLocalizedMessage());
                gifLoader.setVisibility(View.GONE);
            }
        });
    }

    void updateTripAdvisorRatings() {
        FuzzieAPI.APIService().getTripAdvisorForBrand(currentUser.getAccesstoken(), brand.getId()).enqueue(new Callback<TripAdvisor>() {
            @Override
            public void onResponse(Call<TripAdvisor> call, final Response<TripAdvisor> response) {
                if (response.code() == 200 && response.body() != null && response.body().getReviewsCount() != null && response.body().getReviewsCount() != 0 && response.body().getLink() != null && !response.body().getLink().equals("")){
                    vTripAdvisorRating.setRating((int) response.body().getRating());
                    tvTripAdvisorReviewCount.setText(FuzzieUtils.fromHtml(String.format("(<b>%d</b> Review" + (response.body().getReviewsCount() > 1 ? "s" : "" ) + ")", response.body().getReviewsCount())));

                    btnTripAdvisor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            builder.setToolbarColor(getResources().getColor(R.color.primary));
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(BrandGiftActivity.this, Uri.parse(response.body().getLink()));
                        }
                    });

                    btnTripAdvisor.setVisibility(View.VISIBLE);
                } else {
                    btnTripAdvisor.setVisibility(View.GONE);

                }

                if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  BrandGiftActivity.this);
                }

            }

            @Override
            public void onFailure(Call<TripAdvisor> call, Throwable t) {
                btnTripAdvisor.setVisibility(View.GONE);
            }
        });
    }

    void updateStoreLocationsState() {
        if (brand.getStores().size() > 0) {
            findViewById(R.id.vStoreLocationsPreDivider).setVisibility(View.VISIBLE);
            findViewById(R.id.btnStoreLocations).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.vStoreLocationsPreDivider).setVisibility(View.GONE);
            findViewById(R.id.btnStoreLocations).setVisibility(View.GONE);
        }
    }

    @Click(R.id.btnAddToWishlist)
    void addToWishlistButtonClicked() {

        if (brand.isWishListed()) {

            removeWishList();

            FuzzieAPI.APIService().deleteFromWishlist(currentUser.getAccesstoken(), brand.getId()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    // if success do nothing
                    if (response.code() != 200){

                        addWishList();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    addWishList();
                }
            });

        } else {

            addWishList();

            FuzzieAPI.APIService().putToWishlist(currentUser.getAccesstoken(), brand.getId()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    // if success do nothing
                    if (response.code() != 200){

                        removeWishList();

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    removeWishList();
                }
            });

        }

    }

    private void addWishList(){

        brand.setWishListed(true);
        currentUser.wishListBrand(brand.getId(), true);
        updateWishlistButtonState();

        sendBroadcastForWishListBrand();
    }

    private void removeWishList(){

        brand.setWishListed(false);
        currentUser.wishListBrand(brand.getId(), false);
        updateWishlistButtonState();

        sendBroadcastForWishListBrand();
    }

    @Click(R.id.btnShareThis)
    void shareThisButtonClicked() {

                Picasso.get()
                .load(brand.getBackgroundImageUrl())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        String[] urlSplit = brand.getBackgroundImageUrl().split("/");
                        String filename = urlSplit[urlSplit.length - 1].split("\\?")[0];
                        String filepath = context.getExternalCacheDir() + "/" + filename;
//                        Timber.d(TAG, "File: " + filepath);

                        File file = new File(filepath);
                        try {
                            boolean created = file.createNewFile();
//                            Timber.d(TAG, "File Created: " + created);
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Timber.e("IOException", e.getLocalizedMessage());
                            Toast.makeText(context, "Unable to share", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String text = String.format("%s E-Gift Card: ", brand.getName());
                        if (giftCard.getCashBack().getPercentage() > 0) {
                            if (giftCard.getCashBack().getPowerUpPercentage() > 0) {
                                text += String.format("%.0f%% INSTANT Cashback + %.0f%% Bonus Cashback. Get it on FUZZIE now: http://fuzzie.com.sg/app", giftCard.getCashBack().getCashBackPercentage(), giftCard.getCashBack().getPowerUpPercentage());
                            } else {
                                text += String.format("%.0f%% INSTANT Cashback. Get it on FUZZIE now: http://fuzzie.com.sg/", giftCard.getCashBack().getCashBackPercentage());
                            }
                        }

                        String subject = String.format("%s E-Gift Card", brand.getName());

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, "Share this"));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        Toast.makeText(context, "Unable to share", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    void updateWishlistButtonState() {

        if (brand.isWishListed()) {
            AppCompatImageView icon = (AppCompatImageView) btnAddToWishlist.getChildAt(0);
            AppCompatTextView text = (AppCompatTextView) btnAddToWishlist.getChildAt(1);
            icon.setColorFilter(getResources().getColor(R.color.primary));
            text.setTextColor(getResources().getColor(R.color.primary));
            text.setText("Wishlisted");
        } else {
            AppCompatImageView icon = (AppCompatImageView) btnAddToWishlist.getChildAt(0);
            AppCompatTextView text = (AppCompatTextView) btnAddToWishlist.getChildAt(1);
            icon.setColorFilter(Color.parseColor("#8e8e8e"));
            text.setTextColor(Color.parseColor("#8e8e8e"));
            text.setText("Add to wishlist");
        }
    }

    @Click(R.id.ivJackpotBanner)
    void participateButtonClicked(){
        JackpotBrandCouponListActivity_.intent(context).brandId(brandId).start();
    }

    @Click(R.id.powerupInfo)
    void powerupButtonClicked(){
        PowerUpPackActivity_.intent(context).start();
    }

    private void sendBroadcastLikeAdded(){

        Intent intent = new Intent(Constants.BROADCAST_LIKE_ADDED_IN_BRAND);
        intent.putExtra("brandId", brand.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private void sendBroadcastLikeRemoved(){

        Intent intent = new Intent(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND);
        intent.putExtra("brandId", brand.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private void sendBroadcastForWishListBrand(){

        Intent intent = new Intent(Constants.BROADCAST_ADD_OR_REMOVE_WISHLIST_TO_BRAND);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static class ImageSlideAdapter extends PagerAdapter {
        Context context;

        List<Photos> photos;

        public ImageSlideAdapter(Context context, List<Photos> photos) {
            this.context = context;
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Picasso.get()
                    .load(photos.get(position).getUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .resize(context.getResources().getDisplayMetrics().widthPixels, (int) ViewUtils.convertDpToPixel(256, context))
                    .centerCrop()
                    .into(imageView);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void setBuyAndGiftIt(boolean soldOut){
        if (soldOut) {
            cvBuyIt.setEnabled(false);
            cvBuyIt.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
            cvGiftIt.setEnabled(false);
            cvGiftIt.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
        } else {
            cvBuyIt.setEnabled(true);
            cvBuyIt.setCardBackgroundColor(getResources().getColor(R.color.primary));
            cvGiftIt.setEnabled(true);
            cvGiftIt.setCardBackgroundColor(getResources().getColor(R.color.primary));
        }
    }

    @Override
    public void FZClubExclusiveDialogExploreButtonClicked() {
        super.FZClubExclusiveDialogExploreButtonClicked();

        ClubSubscribeActivity_.intent(context).start();
    }
}
