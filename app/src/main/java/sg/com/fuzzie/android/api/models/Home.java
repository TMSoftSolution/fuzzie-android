package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Home {

    @SerializedName("max_banners")
    private int mMaxBanners;
    @SerializedName("brands")
    private List<Brand> mBrands;
    @SerializedName("categories")
    private List<Category> mCategories;
    @SerializedName("sub_categories")
    private List<Category> mSubCategories;
    @SerializedName("recommended_brands")
    private List<String> mRecommendedBrands;
    @SerializedName("new_brands")
    private List<TopBrand> mNewBrands;
    @SerializedName("club_brands")
    private List<TopBrand> mClubBrands;
    @SerializedName("popular_brands")
    private List<String> mPopularBrands;
    @SerializedName("top_brands")
    private List<TopBrand> mTopBrands;
    @SerializedName("near_brands")
    private List<String> mNearBrands;
    @SerializedName("hottest_brands")
    private List<String> mHottestBrands;
    @SerializedName("most_wishlisted_brands")
    private List<String> mMostWishlistedBrands;
    @SerializedName("most_cashback_brands")
    private List<String> mMostCashbackBrands;
    @SerializedName("stories")
    private List<Story> mStories;
    @SerializedName("banners")
    private List<Banner> mBanners;
    @SerializedName("mini_banners")
    private List<Banner> mMiniBanners;
    @SerializedName("mini_banner_locations")
    private List<String>mMiniBannerLocations;
    @SerializedName("fuzzie_friends")
    private List<User> fuzzieFriends;
    @SerializedName("jackpot")
    private Jackpot jackpot;
    @SerializedName("jackpot_tickets_to_be_given_with_red_packet_bundle")
    private int jackpotTicketsCountForFirstRedPacketSending;

    public static Home fromJSON(String homejson) {
        return new Gson().fromJson(homejson, Home.class);
    }

    public static String toJSON(Home home) {
        return new Gson().toJson(home);
    }

    public int getMaxBanners() {
        return mMaxBanners;
    }

    public void setMaxBanners(int maxBanners) {
        this.mMaxBanners = maxBanners;
    }

    public List<Brand> getBrands() {
        return mBrands;
    }

    public void setBrands(List<Brand> brands) {
        this.mBrands = brands;
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        this.mCategories = categories;
    }

    public List<String> getRecommendedBrands() {
        return mRecommendedBrands;
    }

    public void setRecommendedBrands(List<String> mRecommendedBrands) {
        this.mRecommendedBrands = mRecommendedBrands;
    }

    public List<String> getPopularBrands() {
        return mPopularBrands;
    }

    public void setPopularBrands(List<String> mPopularBrands) {
        this.mPopularBrands = mPopularBrands;
    }

    public List<TopBrand> getTopBrands() {
        return mTopBrands;
    }

    public void setTopBrands(List<TopBrand> topBrands) {
        this.mTopBrands = topBrands;
    }

    public List<String> getNearBrands() {
        return mNearBrands;
    }

    public void setNearBrands(List<String> mNearBrands) {
        this.mNearBrands = mNearBrands;
    }

    public List<String> getHottestBrands() {
        return mHottestBrands;
    }

    public void setHottestBrands(List<String> mHottestBrands) {
        this.mHottestBrands = mHottestBrands;
    }

    public List<String> getMostWishlistedBrands() {
        return mMostWishlistedBrands;
    }

    public void setMostWishlistedBrands(List<String> mMostWishlistedBrands) {
        this.mMostWishlistedBrands = mMostWishlistedBrands;
    }

    public List<String> getMostCashbackBrands() {
        return mMostCashbackBrands;
    }

    public void setMostCashbackBrands(List<String> mMostCashbackBrands) {
        this.mMostCashbackBrands = mMostCashbackBrands;
    }

    public List<Story> getStories() {
        return mStories;
    }

    public void setStories(List<Story> mStories) {
        this.mStories = mStories;
    }

    public List<Category> getSubCategories() {
        if (mSubCategories == null) {
            mSubCategories = new ArrayList<>();
        }
        return mSubCategories;
    }

    public void setSubCategories(List<Category> mSubCategories) {
        this.mSubCategories = mSubCategories;
    }

    public List<Banner> getBanners() {
        return mBanners;
    }

    public void setBanners(List<Banner> mBanners) {
        this.mBanners = mBanners;
    }

    public List<Banner> getMiniBanners() {
        return mMiniBanners;
    }

    public void setMiniBanners(List<Banner> mMiniBanners) {
        this.mMiniBanners = mMiniBanners;
    }

    public List<TopBrand> getNewBrands() {
        return mNewBrands;
    }

    public void setNewBrands(List<TopBrand> mNewBrands) {
        this.mNewBrands = mNewBrands;
    }

    public List<String> getMiniBannerLocations() {
        return mMiniBannerLocations;
    }

    public void setMiniBannerLocations(List<String> mMiniBannerLocations) {
        this.mMiniBannerLocations = mMiniBannerLocations;
    }

    public List<User> getFuzzieFriends() {
        return fuzzieFriends;
    }

    public void setFuzzieFriends(List<User> fuzzieFriends) {
        this.fuzzieFriends = fuzzieFriends;
    }

    public Jackpot getJackpot() {
        return jackpot;
    }

    public void setJackpot(Jackpot jackpot) {
        this.jackpot = jackpot;
    }

    public int getJackpotTicketsCountForFirstRedPacketSending() {
        return jackpotTicketsCountForFirstRedPacketSending;
    }

    public List<TopBrand> getClubBrands() {
        return mClubBrands;
    }

    public void setmClubBrands(List<TopBrand> mClubBrands) {
        this.mClubBrands = mClubBrands;
    }
}
