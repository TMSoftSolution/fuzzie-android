package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by movdev on 4/13/18.
 */

public class BrandLink {

    @SerializedName("type")
    private String type;

    @SerializedName("brand_id")
    private String brandId;

    @SerializedName("jackpot_coupon_template_id")
    private String couponId;

    @SerializedName("jackpot_coupon_template_ids")
    private List<String> couponIds;

    @SerializedName("title")
    private String title;

    @SerializedName("club_store_ids")
    private List<String> clubStoreIds;

    public String getType() {
        return type;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getCouponId() {
        return couponId;
    }

    public List<String> getCouponIds() {
        return couponIds;
    }

    public static BrandLink fromJSON(String brandLinkJson) {
        return new Gson().fromJson(brandLinkJson, BrandLink.class);
    }

    public static String toJSON(BrandLink brandLink) {
        return new Gson().toJson(brandLink);
    }

    public String getTitle() {
        return title;
    }

    public List<String> getClubStoreIds() {
        return clubStoreIds;
    }

}
