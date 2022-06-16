package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubStoreDetail {

    @SerializedName("nearby_stores")
    private List<ClubStore> nearbyStores;

    @SerializedName("related_stores")
    private List<ClubStore> relatedStores;

    @SerializedName("more_stores")
    private List<ClubStore> moreStores;

    @SerializedName("brand_id")
    private String brandId;


    public List<ClubStore> getNearbyStores() {
        return nearbyStores;
    }

    public List<ClubStore> getRelatedStores() {
        return relatedStores;
    }

    public List<ClubStore> getMoreStores() {
        return moreStores;
    }

    public String getBrandId() {
        return brandId;
    }
}
