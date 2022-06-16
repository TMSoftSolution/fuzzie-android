package sg.com.fuzzie.android.api.models;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Brand implements Comparable<Brand>{

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("background_image_url")
    private String mBackgroundImageUrl;
    private String mCustomImageUrl;
    @SerializedName("category")
    private String mCategory;
    @SerializedName("website")
    private String mWebsite;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("cash_back")
    private CashBack mCashBack;
    @SerializedName("facebook_url")
    private String mFacebookUrl;
    @SerializedName("instagram_url")
    private String mInstagramUrl;
    @SerializedName("twitter_url")
    private String mTwitterUrl;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("stores")
    private List<Stores> mStores;
    @SerializedName("terms_and_conditions_text")
    private List<String> mTermsAndConditionsList;
    @SerializedName("photos")
    private List<Photos> mPhotos;
    @SerializedName("gift_cards")
    private List<GiftCard> mGiftCards;
    @SerializedName("likers")
    private List<User> mLikers;
    @SerializedName("others_also_bought")
    private List<String> mOthersAlsoBought;
    @SerializedName("sub_category_id")
    private int mSubCategoryId;
    @SerializedName("category_ids")
    private List<String> mCategoryIds;
    @SerializedName("services")
    private List<Service> mServices;
    @SerializedName("service_categories")
    private List<ServiceCategory> mServiceCategory;
    @SerializedName("sold_out")
    private boolean soldOut;
    @SerializedName("liked")
    private boolean isLiked;
    @SerializedName("likers_count")
    private int likersCount;
    @SerializedName("wish_listed")
    private boolean wishListed;
    @SerializedName("options_for_gift_cards")
    private List<String> optionsForGiftCards;
    @SerializedName("new")
    private boolean isNew;
    @SerializedName("jackpot_coupons_present")
    private boolean jackpotEnabled;
    @SerializedName("power_up")
    private boolean powerUp;
    @SerializedName("tripadvisor")
    private TripAdvisor tripAdvisor;
    @SerializedName("jackpot_coupon_only")
    private boolean couponOnly;
    @SerializedName("jackpot_coupon_templates")
    private List<Coupon> coupons;
    @SerializedName("club_only_vouchers")
    private boolean clubOnly;
    @SerializedName("no_pin_redemption")
    private boolean noPinRedemption;

    private BrandLink brandLink;

    public Brand(Brand brand){

        this.mId = brand.getId();
        this.mName = brand.getName();
        this.mBackgroundImageUrl = brand.getBackgroundImageUrl();
        this.mCustomImageUrl = brand.getCustomImageUrl();
        this.mCategory = brand.getCategory();
        this.mWebsite = brand.getWebsite();
        this.mSlug = brand.getSlug();
        this.mCashBack = brand.getCashBack();
        this.mFacebookUrl = brand.getFacebookUrl();
        this.mInstagramUrl = brand.getInstagramUrl();
        this.mTwitterUrl = brand.getTwitterUrl();
        this.mDescription = brand.getDescription();
        this.mStores = brand.getStores();
        this.mTermsAndConditionsList = brand.getTermsAndConditionsList();
        this.mPhotos = brand.getPhotos();
        this.mGiftCards = brand.getGiftCards();
        this.mLikers = brand.getLikers();
        this.mOthersAlsoBought = brand.getOthersAlsoBought();
        this.mSubCategoryId = brand.getSubCategoryId();
        this.mCategoryIds = brand.getCategoryIds();
        this.mServices = brand.getServices();
        this.mServiceCategory = brand.getServiceCategory();
        this.soldOut = brand.isSoldOut();
        this.isLiked = brand.isLiked();
        this.likersCount = brand.getLikersCount();
        this.wishListed = brand.isWishListed();
        this.optionsForGiftCards = brand.getOptionsForGiftCards();
        this.isNew = brand.isNew();
        this.jackpotEnabled = brand.isJackpotEnabled();
        this.powerUp = brand.isPowerUp();
        this.tripAdvisor = brand.getTripAdvisor();
        this.couponOnly = brand.isCouponOnly();
        this.coupons = brand.getCoupons();
        this.brandLink = brand.getBrandLink();
        this.clubOnly = brand.isClubOnly();
        this.noPinRedemption = brand.isNoPinRedemption();
    }

    public static Brand fromJSON(String str) {
        return new Gson().fromJson(str, Brand.class);
    }

    public static String toJSON(Brand brand) {
        return new Gson().toJson(brand);
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getBackgroundImageUrl() {
        return mBackgroundImageUrl;
    }

    public void setBackgroundImageUrl(String mBackgroundImageUrl) {
        this.mBackgroundImageUrl = mBackgroundImageUrl;
    }

    public String getCustomImageUrl() {
        return mCustomImageUrl;
    }

    public void setCustomImageUrl(String mCustomImageUrl) {
        this.mCustomImageUrl = mCustomImageUrl;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String mSlug) {
        this.mSlug = mSlug;
    }

    public CashBack getCashBack() {
        return mCashBack;
    }

    public void setCashBack(CashBack mCashBack) {
        this.mCashBack = mCashBack;
    }

    public String getFacebookUrl() {
        return mFacebookUrl;
    }

    public void setFacebookUrl(String mFacebookUrl) {
        this.mFacebookUrl = mFacebookUrl;
    }

    public String getInstagramUrl() {
        return mInstagramUrl;
    }

    public void setInstagramUrl(String mInstagramUrl) {
        this.mInstagramUrl = mInstagramUrl;
    }

    public String getTwitterUrl() {
        return mTwitterUrl;
    }

    public void setTwitterUrl(String mTwitterUrl) {
        this.mTwitterUrl = mTwitterUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public List<Stores> getStores() {
        return mStores;
    }

    public void setStores(List<Stores> mStores) {
        this.mStores = mStores;
    }

    public List<String> getTermsAndConditionsList() {
        return mTermsAndConditionsList;
    }

    public void setTermsAndConditionsList(List<String> mTermsAndConditionsList) {
        this.mTermsAndConditionsList = mTermsAndConditionsList;
    }

    public List<Photos> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<Photos> mPhotos) {
        this.mPhotos = mPhotos;
    }

    public List<GiftCard> getGiftCards() {
        return mGiftCards;
    }

    public void setGiftCards(List<GiftCard> mGiftCards) {
        this.mGiftCards = mGiftCards;
    }

    public List<User> getLikers() {
        return mLikers;
    }

    public void setLikers(List<User> mLikers) {
        this.mLikers = mLikers;
    }

    public int getSubCategoryId() {
        return mSubCategoryId;
    }

    public void setSubCategoryId(int mSubCategoryId) {
        this.mSubCategoryId = mSubCategoryId;
    }

    public List<String> getCategoryIds() {
        return mCategoryIds;
    }

    public void setCategoryIds(List<String> mCategoryIds) {
        this.mCategoryIds = mCategoryIds;
    }

    public List<Service> getServices() {
        return mServices;
    }

    public void setServices(List<Service> mServices) {
        this.mServices = mServices;
    }

    public List<ServiceCategory> getServiceCategory() {
        return mServiceCategory;
    }

    public void setServiceCategory(List<ServiceCategory> mServiceCategory) {
        this.mServiceCategory = mServiceCategory;
    }

    public List<String> getOthersAlsoBought() {
        return mOthersAlsoBought;
    }

    public void setOthersAlsoBought(List<String> mOthersAlsoBought) {
        this.mOthersAlsoBought = mOthersAlsoBought;
    }

    public Boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getLikersCount() {
        return likersCount;
    }

    public void setLikersCount(int likersCount) {
        this.likersCount = likersCount;
    }


    @Override
    public int compareTo(@NonNull Brand o) {
        return mName.compareTo(o.getName());
    }

    public List<String> getOptionsForGiftCards() {
        return optionsForGiftCards;
    }

    public void setOptionsForGiftCards(List<String> optionsForGiftCards) {
        this.optionsForGiftCards = optionsForGiftCards;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isJackpotEnabled() {
        return jackpotEnabled;
    }

    public void setJackpotEnabled(boolean jackpotEnabled) {
        this.jackpotEnabled = jackpotEnabled;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    public boolean isWishListed() {
        return wishListed;
    }

    public void setWishListed(boolean wishListed) {
        this.wishListed = wishListed;
    }

    public TripAdvisor getTripAdvisor() {
        return tripAdvisor;
    }

    public BrandLink getBrandLink() {
        return brandLink;
    }

    public void setBrandLink(BrandLink brandLink) {
        this.brandLink = brandLink;
    }

    public boolean isCouponOnly() {
        return couponOnly;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public boolean isClubOnly() {
        return clubOnly;
    }

    public boolean isNoPinRedemption() {
        return noPinRedemption;
    }

}
