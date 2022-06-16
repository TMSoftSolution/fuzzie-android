package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Offer {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("estimated_savings")
    private double estSave;

    @SerializedName("brand_id")
    private String brandId;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("store_ids")
    private List<String> storeIds;

    @SerializedName("offer_type_id")
    private int offerTypeId;

    @SerializedName("brand_type_id")
    private int brandTypeId;

    @SerializedName("max_redemptions")
    private int maxRedemption;

    @SerializedName("sold_out")
    private boolean soldOut;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("availability_options")
    private Availability availability;

    @SerializedName("redemption_details")
    private OfferRedeemDetail redeemDetail;

    @SerializedName("online_brand")
    private boolean online;

    @SerializedName("external_redeem_page")
    private String externalRedeemPage;

    @SerializedName("stores")               // For Flash Sales Offer
    private List<ClubStore> clubStores;

    public static Offer fromJSON(String str) {
        return new Gson().fromJson(str, Offer.class);
    }

    public static String toJSON(Offer offer) {
        return new Gson().toJson(offer);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getStoreIds() {
        return storeIds;
    }

    public double getEstSave() {
        return estSave;
    }

    public int getOfferTypeId() {
        return offerTypeId;
    }

    public int getBrandTypeId() {
        return brandTypeId;
    }

    public int getMaxRedemption() {
        return maxRedemption;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public String getEndDate() {
        return endDate;
    }

    public List<ClubStore> getClubStores() {
        return clubStores;
    }

    public Availability getAvailability() {
        return availability;
    }

    public OfferRedeemDetail getRedeemDetail() {
        return redeemDetail;
    }

    public boolean isOnline() {
        return online;
    }

    public String getExternalRedeemPage() {
        return externalRedeemPage;
    }
}
