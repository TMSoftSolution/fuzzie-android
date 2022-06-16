package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrandTypeDetail {

    @SerializedName("trending_stores")
    private List<ClubStore> trendingStores;

    @SerializedName("nearby_stores")
    private List<ClubStore> nearStores;

    @SerializedName("new_stores")
    private List<ClubStore> newStores;

    @SerializedName("popular_categories")
    private List<Category> categories;

    public List<ClubStore> getTrendingStores() {
        return trendingStores;
    }

    public List<ClubStore> getNearStores() {
        return nearStores;
    }

    public List<ClubStore> getNewStores() {
        return newStores;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
