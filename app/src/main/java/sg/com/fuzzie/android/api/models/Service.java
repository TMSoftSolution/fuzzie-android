package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ravindra on 04/11/16.
 */

public class Service {

    @SerializedName("type")
    private String type;
    @SerializedName("price")
    private Price price;
    @SerializedName("name")
    private String name;
    @SerializedName("discounted_price")
    private double discountedPrice;
    @SerializedName("kind")
    private String kind;
    @SerializedName("valid_with_in_store_promo")
    private boolean validWithInStorePromo;
    @SerializedName("promotional_price")
    private int promotionalPrice;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("terms_and_conditions_text")
    private List<String> mTermsAndConditionsList;
    @SerializedName("description")
    private String description;
    @SerializedName("on_promotion")
    private boolean onPromotion;
    @SerializedName("service_category_ids")
    private List<Integer> serviceCategoryIds;
    @SerializedName("cash_back")
    private CashBack mCashBack;
    @SerializedName("sold_out")
    private boolean mSoldOut;
    @SerializedName("display_name")
    private String mDisplayName;
    @SerializedName("redemption_start_date")
    private String redeemStartDate;
    @SerializedName("redemption_end_date")
    private String redeemEndDate;
    @SerializedName("redemption_type")
    private String redemptionType;

    public static Service fromJSON(String str) {
        return new Gson().fromJson(str, Service.class);
    }

    public static String toJSON(Service service) {
        return new Gson().toJson(service);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isValidWithInStorePromo() {
        return validWithInStorePromo;
    }

    public void setValidWithInStorePromo(boolean validWithInStorePromo) {
        this.validWithInStorePromo = validWithInStorePromo;
    }

    public int getPromotionalPrice() {
        return promotionalPrice;
    }

    public void setPromotionalPrice(int promotionalPrice) {
        this.promotionalPrice = promotionalPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isOnPromotion() {
        return onPromotion;
    }

    public void setOnPromotion(boolean onPromotion) {
        this.onPromotion = onPromotion;
    }

    public List<Integer> getServiceCategoryIds() {
        return serviceCategoryIds;
    }

    public void setServiceCategoryIds(List<Integer> serviceCategoryIds) {
        this.serviceCategoryIds = serviceCategoryIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CashBack getCashBack() {
        return mCashBack;
    }

    public void setCashBack(CashBack mCashBack) {
        this.mCashBack = mCashBack;
    }

    public Boolean isSoldOut() {
        return mSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.mSoldOut = soldOut;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public List<String> getTermsAndConditionsList() {
        return mTermsAndConditionsList;
    }

    public void setTermsAndConditionsList(List<String> mTermsAndConditionsList) {
        this.mTermsAndConditionsList = mTermsAndConditionsList;
    }

    public String getRedeemStartDate() {
        return redeemStartDate;
    }

    public String getRedeemEndDate() {
        return redeemEndDate;
    }

    public String getRedemptionType() {
        return redemptionType;
    }
}
