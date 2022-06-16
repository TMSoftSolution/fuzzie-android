package sg.com.fuzzie.android.api.models;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubStore implements Comparable<ClubStore> {

    @SerializedName("id")
    private String storeId;

    @SerializedName("brand_id")
    private String brandId;

    @SerializedName("offers")
    private List<Offer> offers;

    @SerializedName("distance")
    private double distance;

    @SerializedName("open_now")
    private boolean open;

    @SerializedName("filter_components")
    private List<Component> filterComponents;

    @SerializedName("categories")
    private List<Category> categories;

    @SerializedName("brand_type_id")
    private int brandTypeId;

    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("store_name")
    private String storeName;

    @SerializedName("online_brand")
    private boolean online;

    @SerializedName("closing_soon")
    private boolean closingSoon;

    @SerializedName("open_time")
    private String openTime;

    @SerializedName("close_time")
    private String closeTime;

    public static ClubStore fromJSON(String str) {
        return new Gson().fromJson(str, ClubStore.class);
    }

    public static String toJSON(ClubStore clubStore) {
        return new Gson().toJson(clubStore);
    }

    public String getStoreId() {
        return storeId;
    }

    public String getBrandId() {
        return brandId;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public Double getDistance() {
        return distance;
    }

    public boolean isOpen() {
        return open;
    }

    public List<Component> getFilterComponents() {
        return filterComponents;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public int getBrandTypeId() {
        return brandTypeId;
    }

    @Override
    public int compareTo(@NonNull ClubStore o) {

        return storeId.compareTo(o.storeId);
    }

    public String getBrandName() {
        return brandName;
    }

    public String getStoreName() {
        return storeName;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isClosingSoon() {
        return closingSoon;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }
}
