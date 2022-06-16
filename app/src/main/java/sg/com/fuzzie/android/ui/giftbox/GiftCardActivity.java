package sg.com.fuzzie.android.ui.giftbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.items.brand.ValidOptionNewItem;
import sg.com.fuzzie.android.services.LocationService;
import sg.com.fuzzie.android.ui.club.LocationEnableActivity_;
import sg.com.fuzzie.android.utils.Constants;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.Photos;
import sg.com.fuzzie.android.api.models.TripAdvisor;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.ui.shop.BrandRedeemDetailsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandStoreLocationsActivity_;
import sg.com.fuzzie.android.ui.shop.BrandTermsActivity_;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.TripAdvisorRatingView;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_GIFTBOX_ACTIVE;
import static sg.com.fuzzie.android.utils.Constants.REQUEST_GIFT_MARK_AS_UNREDEEMED;

/**
 * Created by mac on 5/29/17.
 */

@EActivity(R.layout.activity_gift_card)
public class GiftCardActivity extends BaseActivity {

    @Extra
    String giftId;

    @Extra
    boolean used;

    Gift gift;
    Brand brand;
    GiftCard giftCard;
    boolean showFirstRedeem;

    FastItemAdapter<ValidOptionNewItem> validOptionAdapter;

    @Bean
    CurrentUser currentUser;

    @Bean
    FuzzieData dataManager;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.vpPhotos)
    ViewPager vpPhotos;

    @ViewById(R.id.tlPhotosIndicator)
    TabLayout tlPhotosIndicator;

    @ViewById(R.id.ivCategory)
    ImageView ivCategory;

    @ViewById(R.id.tvCategory)
    TextView tvCategory;

    @ViewById(R.id.tvBrandName)
    TextView tvBrandName;

    @ViewById(R.id.tvPrice)
    TextView tvPrice;

    @ViewById(R.id.llValidOptions)
    View llValidOptions;

    @ViewById(R.id.rvValidOption)
    RecyclerView rvValidOption;

    @ViewById(R.id.cvRedeem)
    CardView cvRedeem;

    @ViewById(R.id.tvRedeem)
    TextView tvRedeem;

    @ViewById(R.id.llOnlineValid)
    LinearLayout llOnlineValid;

    @ViewById(R.id.tvOnlineValid)
    TextView tvOnlineValid;

    @ViewById(R.id.tvOnlineValidValue)
    TextView tvOnlineValidValue;

    @ViewById(R.id.tvValid)
    TextView tvValid;

    @ViewById(R.id.tvValidValue)
    TextView tvValidValue;

    @ViewById(R.id.tvOrderNumber)
    TextView tvOrderNumber;

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

    @ViewById(R.id.llSenderInfo)
    LinearLayout llSenderInfo;

    @ViewById(R.id.ivSenderAvatar)
    CircleImageView ivSenderAvatar;

    @ViewById(R.id.liker_facebook)
    ImageView ivFacebookIcon;

    @ViewById(R.id.tvSenderName)
    TextView tvSenderName;

    @ViewById(R.id.tvMessage)
    TextView tvMessage;

    @ViewById(R.id.llRedeemStartDate)
    View llRedeemStartDate;

    @ViewById(R.id.tvRedeemStartDate)
    TextView tvRedeemStartDate;

    @AfterViews
    public void calledAfterViewInjection(){

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (giftId == null) return;

        gift = dataManager.getGiftById(giftId, used);
        if (gift == null) return;

        brand = gift.getBrand();

        giftCard = gift.getGiftCard();

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

        tvBrandName.setText(brand.getName() + " E-Gift Card");
        tvPrice.setText(giftCard.getPrice().getCurrencySymbol() + giftCard.getPrice().getValue());

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

        updateGift();

        updateSenderInfo();

        updateMarkAsUnredeemedState();

        tvOrderNumber.setText(String.valueOf(gift.getOrderNumber()));

    }

    private void updateGift(){

        if (gift.getRedeemTimerStartedAt() != null){

            llRedeemStartDate.setVisibility(View.GONE);
            cvRedeem.setVisibility(View.VISIBLE);

            cvRedeem.setEnabled(true);
            tvRedeem.setText("VIEW CODE");
            cvRedeem.setCardBackgroundColor(Color.parseColor("#FA3E3F"));
            tvValid.setText("Redeemed on:");

            if (gift.getRedeemedTime() != null){

                tvValidValue.setText(TimeUtils.dateTimeDDMMYYYYHMA(gift.getRedeemedTime()));

                llOnlineValid.setVisibility(View.VISIBLE);
                tvOnlineValid.setText("Valid till:");
                tvOnlineValidValue.setText(TimeUtils.dateTimeDDMMYYYY(gift.getExpirationDate()));

            } else {

                tvValidValue.setText(TimeUtils.dateTimeDDMMYYYYHMA(gift.getRedeemTimerStartedAt()));

            }

            if (gift.isExpired()){

                cvRedeem.setEnabled(false);
                cvRedeem.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
                tvRedeem.setText("EXPIRED");

            }

        } else {

            tvRedeem.setText("REDEEM");

            if (gift.getRedeemedTime() != null){

                llRedeemStartDate.setVisibility(View.GONE);
                cvRedeem.setVisibility(View.VISIBLE);
                tvRedeem.setText("REDEEMED");

                cvRedeem.setEnabled(false);
                cvRedeem.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
                tvValid.setText("Redeemed on:");
                tvValidValue.setText(TimeUtils.dateTimeDDMMYYYYHMA(gift.getRedeemedTime()));

            } else {

                if (gift.isExpired()){

                    llRedeemStartDate.setVisibility(View.GONE);
                    cvRedeem.setVisibility(View.VISIBLE);

                    cvRedeem.setEnabled(false);
                    cvRedeem.setCardBackgroundColor(getResources().getColor(R.color.loblolly));
                    tvRedeem.setText("EXPIRED");

                } else {

                    Date now = new Date();
                    Date startDate = null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                    try {
                        startDate = dateFormat.parse(gift.getRedeemStartDate());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (startDate != null && startDate.after(now)){

                        llRedeemStartDate.setVisibility(View.VISIBLE);
                        cvRedeem.setVisibility(View.GONE);

                        tvRedeemStartDate.setText("Redemption starts on " + TimeUtils.redeemStartEndFormat(gift.getRedeemStartDate()));

                    } else {

                        llRedeemStartDate.setVisibility(View.GONE);
                        cvRedeem.setVisibility(View.VISIBLE);

                        cvRedeem.setEnabled(true);
                        cvRedeem.setCardBackgroundColor(Color.parseColor("#FA3E3F"));

                        tvRedeem.setText("REDEEM");
                    }
                }

                tvValid.setText("Valid till:");
                tvValidValue.setText(TimeUtils.dateTimeDDMMYYYY(gift.getExpirationDate()));
            }
        }
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
                            customTabsIntent.launchUrl(GiftCardActivity.this, Uri.parse(response.body().getLink()));
                        }
                    });

                    btnTripAdvisor.setVisibility(View.VISIBLE);
                } else {
                    btnTripAdvisor.setVisibility(View.GONE);

                }

                if (response.code() == 417){
                    logoutUser(currentUser, dataManager,  GiftCardActivity.this);
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

    void updateSenderInfo(){

        if (gift.isGifted() != null && gift.isGifted()){
            if (gift.getSender() != null){
                llSenderInfo.setVisibility(View.VISIBLE);
                if (gift.getSender().getAvatar() != null && !gift.getSender().getAvatar().equals("")){
                    ivSenderAvatar.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(gift.getSender().getAvatar())
                            .placeholder(R.drawable.profile_image_placeholder)
                            .fit()
                            .into(ivSenderAvatar);
                    ivFacebookIcon.setVisibility(View.VISIBLE);
                } else {
                    ivSenderAvatar.setVisibility(View.GONE);
                    ivFacebookIcon.setVisibility(View.GONE);
                }

                if (gift.getSender().getName() != null){
                    tvSenderName.setText(gift.getSender().getName());
                }

            }

            if (gift.getMessage() != null){
                tvMessage.setText(gift.getMessage());
            }
        } else {
            llSenderInfo.setVisibility(View.GONE);
        }
    }

    void updateMarkAsUnredeemedState(){
        if (gift.getRedeemTimerStartedAt() != null){
            findViewById(R.id.llMarkAsUnredeemed).setVisibility(View.VISIBLE);
            findViewById(R.id.vMarkAsUnredeemedDivider).setVisibility(View.VISIBLE);
            } else {
            findViewById(R.id.llMarkAsUnredeemed).setVisibility(View.GONE);
            findViewById(R.id.vMarkAsUnredeemedDivider).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
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

        if (gift != null && gift.getTerms() != null){

            StringBuilder sb = new StringBuilder();
            for (String terms: gift.getTerms()) {
                sb.append(terms);
                sb.append("\n");
            }

            BrandTermsActivity_.intent(context).termsExtra(sb.toString()).start();
        }
    }

    @Click(R.id.btnHowToRedeem)
    void howToRedeemButtonClicked() {
        BrandRedeemDetailsActivity_.intent(context).brandId(brand.getId()).start();
    }

    @Click(R.id.btnStoreLocations)
    void storeLocationClicked() {
        BrandStoreLocationsActivity_.intent(context).brandId(brand.getId()).start();
    }

    @Click(R.id.llMarkAsUnredeemed)
    void markAsUnredeemedButtonClicked(){
        displayProgressDialog("Processing...");
        Call<PayResponse> call = FuzzieAPI.APIService().markAsUnredeemed(currentUser.getAccesstoken(), giftId);
        call.enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null && response.body().getGift() != null){

                    if (used){

                        used = false;

                        dataManager.removeUsedGift(gift);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_GIFTBOX_ACTIVE));

                    } else {

                        dataManager.updateActiveGift(response.body().getGift());

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED));

                    }

                    gift = response.body().getGift();

                    updateGift();
                    updateMarkAsUnredeemedState();

                    showPopView("SUCCESS!", "", R.drawable.bear_good);

                } else if (response.code() == 417){

                    logoutUser(currentUser, dataManager, GiftCardActivity.this);

                } else {

                    String errorMessage = "Unknown error occurred: " + response.code();

                    if (response.code() != 500 && response.errorBody() != null) {
                        try {
                            String jsonString = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
                            if (jsonObject != null && jsonObject.get("error") != null && !jsonObject.get("error").isJsonNull()) {
                                errorMessage = jsonObject.get("error").getAsString();
                            } else if (jsonObject != null && jsonObject.get("message") != null && !jsonObject.get("message").isJsonNull()) {
                                errorMessage = jsonObject.get("message").getAsString();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    showFZAlert("Oops!", errorMessage, "OK", "", R.drawable.ic_bear_dead_white);
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    @Click(R.id.cvRedeem)
    void redeemButtonClicked(){

        Timber.e("Redeem button clicked.");
        if (gift == null || brand == null) return;

        if (!gift.isOnline() && brand.isNoPinRedemption()){

            checkLocationPermission();

        } else {

            if (gift.getThirdPartyCode() != null && gift.getThirdPartyCode().length() > 0) {

                showRedeemConfirm();

            } else {

                if ((giftCard.getRedemptionType().equals("qr_code") || giftCard.getRedemptionType().equals("bar_code")) && gift.getQrCode() != null){

                    showRedeemConfirm();

                } else {

                    goGiftRedeemPage();

                }
            }
        }
    }


    private void showRedeemConfirm(){

        if (gift.getRedeemTimerStartedAt() != null){

            goGiftRedeemPage();

        } else {

            showFirstRedeem = true;
            SpannableString spannablecontent=new SpannableString("Your card will be shifted to the Used section in 24 hours. You can still access and use your code after the countdown.");
            spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    49,58, 0);
            showFZAlert("READY TO REDEEM?", spannablecontent, "REDEEM NOW", "CANCEL", R.drawable.ic_bear_baby_white);
        }
    }

    private void goGiftRedeemPage(){

        if (gift.isOnline()){

            GiftRedeemOnlineActivity_.intent(context).giftId(giftId).used(used).startForResult(REQUEST_GIFT_MARK_AS_UNREDEEMED);

        } else {

            GiftRedeemPhysicalActivity_.intent(context).giftId(giftId).used(used).startForResult(REQUEST_GIFT_MARK_AS_UNREDEEMED);
        }
    }

    /*
    *  FZAlertDialogListener
    * */

    @Override
    public void FZAlertOkButtonClicked() {
        super.FZAlertOkButtonClicked();

        if (showFirstRedeem){

            showFirstRedeem = false;
            displayProgressDialog("Loading...");

            Call<PayResponse> call = FuzzieAPI.APIService().markGiftOpened(currentUser.getAccesstoken(), gift.getId());
            call.enqueue(new Callback<PayResponse>() {
                @Override
                public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {

                    dismissProgressDialog();

                    if (response.code() == 200 && response.body() != null && response.body().getGift() != null) {

                        gift = response.body().getGift();

                        dataManager.updateActiveGift(gift);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED));

                        updateGift();
                        updateMarkAsUnredeemedState();

                        goGiftRedeemPage();

                    } else if (response.code() == 417){
                        logoutUser(currentUser, dataManager,  GiftCardActivity.this);
                    } else {
                        showFZAlert("Oops!", "Error occurred. Please contact support.", "OK", "", R.drawable.ic_bear_dead_white);
                    }

                }

                @Override
                public void onFailure(Call<PayResponse> call, Throwable t) {
                    dismissProgressDialog();
                    showFZAlert("Oops!", t.getLocalizedMessage(), "OK", "", R.drawable.ic_bear_dead_white);
                }

            });
        }

    }

    @Override
    public void FZAlertCancelButtonClicked() {
        super.FZAlertCancelButtonClicked();
        showFirstRedeem = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == REQUEST_GIFT_MARK_AS_UNREDEEMED){

                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private static class ImageSlideAdapter extends PagerAdapter {
        Context context;

        List<Photos> photos;

        ImageSlideAdapter(Context context, List<Photos> photos) {
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void checkLocationPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST);

            } else {

                checkGPSEnabled();
            }

        } else {

            checkGPSEnabled();
        }

    }

    private void checkGPSEnabled(){

        final LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert mLocationManager != null;
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            startLocationService();

            goGPSRedemptionPage();

        } else {

            LocationEnableActivity_.intent(context).start();
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }
    }

    private void startLocationService(){
        LocationService.getInstance().startLocationService(this);
    }

    private void goGPSRedemptionPage(){

        GiftStoreLocationVerifyActivity_.intent(context).giftId(giftId).start();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    checkGPSEnabled();
                }
            }
        }

    }
}
