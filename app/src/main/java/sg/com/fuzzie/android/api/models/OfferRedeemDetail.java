package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class OfferRedeemDetail {

    @SerializedName("redeemed_at")
    private String redeemedAt;

    @SerializedName("redeem_timer_started_at")
    private String redeemTimerStartedAt;

    @SerializedName("code")
    private String code;

    @SerializedName("redeemed_store_id")
    private String redeemedStoreId;

    @SerializedName("order_number")
    private String orderNumber;

    public String getRedeemedAt() {
        return redeemedAt;
    }

    public String getRedeemTimerStartedAt() {
        return redeemTimerStartedAt;
    }

    public String getCode() {
        return code;
    }

    public String getRedeemedStoreId() {
        return redeemedStoreId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
}
