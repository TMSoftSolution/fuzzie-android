package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GiftCard {

    @SerializedName("type")
    private String mType;
    @SerializedName("price")
    private Price mPrice;
    @SerializedName("name")
    private String mName;
    @SerializedName("display_name")
    private String mDisplayName;
    @SerializedName("discounted_price")
    private double mDiscountedPrice;
    @SerializedName("cash_back")
    private CashBack mCashBack;
    @SerializedName("sold_out")
    private boolean mSoldOut;
    @SerializedName("redemption_start_date")
    private String redeemStartDate;
    @SerializedName("redemption_end_date")
    private String redeemEndDate;
    @SerializedName("redemption_type")
    private String redemptionType;

    public static GiftCard fromJSON(String str) {
        return new Gson().fromJson(str, GiftCard.class);
    }

    public static String toJSON(GiftCard giftCard) {
        return new Gson().toJson(giftCard);
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public Price getPrice() {
        return mPrice;
    }

    public void setPrice(Price price) {
        this.mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    public double getDiscountedPrice() {
        return mDiscountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.mDiscountedPrice = discountedPrice;
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
