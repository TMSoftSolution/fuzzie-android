package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubPlace {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("store_ids")
    private List<String> storeIds;

    @SerializedName("distance")
    private double distance;

    public static ClubPlace fromJSON(String str) {
        return new Gson().fromJson(str, ClubPlace.class);
    }

    public static String toJSON(ClubPlace clubPlace) {
        return new Gson().toJson(clubPlace);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getStoreIds() {
        return storeIds;
    }

    public Double getDistance() {
        return distance;
    }

}
