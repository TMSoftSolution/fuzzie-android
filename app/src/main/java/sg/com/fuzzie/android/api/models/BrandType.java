package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrandType {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image_red")
    private String redIcon;

    @SerializedName("image_black")
    private String blackIcon;

    @SerializedName("image_white")
    private String whiteIcon;

    @SerializedName("categories")
    private List<Category> categories;

    @SerializedName("filters")
    private List<Filter> filters;

    @SerializedName("offer_types")
    private List<OfferType> offerTypes;

    public static BrandType fromJSON(String str) {
        return new Gson().fromJson(str, BrandType.class);
    }

    public static String toJSON(BrandType brandType) {
        return new Gson().toJson(brandType);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRedIcon() {
        return redIcon;
    }

    public String getBlackIcon() {
        return blackIcon;
    }

    public String getWhiteIcon() {
        return whiteIcon;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public List<OfferType> getOfferTypes() {
        return offerTypes;
    }

}
