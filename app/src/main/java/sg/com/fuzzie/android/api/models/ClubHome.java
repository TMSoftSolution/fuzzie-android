package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubHome {

    @SerializedName("trending_stores")
    private List<ClubStore> trendingStores;

    @SerializedName("nearby_stores")
    private List<ClubStore> nearStores;

    @SerializedName("new_stores")
    private List<ClubStore> newStores;

    @SerializedName("flash_sale_offers")
    private List<Offer> flashOffers;

    @SerializedName("top_brands")
    private List<TopBrand> topBrands;

    @SerializedName("categories")
    private List<Category> categories;

    @SerializedName("brand_types")
    private List<BrandType> brandTypes;

    @SerializedName("stores")
    private List<ClubStore> stores;

    @SerializedName("places")
    private List<ClubPlace> places;

    @SerializedName("banners")
    private List<Banner> banners;

    @SerializedName("mini_banners")
    private List<Banner> miniBanners;

    @SerializedName("faqs")
    private List<RedeemDetail> faqs;

    @SerializedName("terms")
    private List<RedeemDetail> terms;

    public List<ClubStore> getTrendingStores() {
        return trendingStores;
    }

    public List<ClubStore> getNearStores() {
        return nearStores;
    }

    public List<ClubStore> getNewStores() {
        return newStores;
    }

    public List<Offer> getFlashOffers() {
        return flashOffers;
    }

    public List<TopBrand> getTopBrands() {
        return topBrands;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<BrandType> getBrandTypes() {
        return brandTypes;
    }

    public List<ClubStore> getStores() {
        return stores;
    }

    public List<ClubPlace> getPlaces() {
        return places;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public List<Banner> getMiniBanners() {
        return miniBanners;
    }

    public List<RedeemDetail> getFaqs() {
        return faqs;
    }

    public List<RedeemDetail> getTerms() {
        return terms;
    }
}
