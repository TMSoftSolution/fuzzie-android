package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ActivateCode {
    @SerializedName("type")
    private String type;
    @SerializedName("brand_name")
    private String brandName;
    @SerializedName("cash_back")
    private int cashBack;
    @SerializedName("gift_type")
    private String giftType;
    @SerializedName("gift_title")
    private String giftTitle;
    @SerializedName("time_to_expire")
    private int timeToExpire;
    @SerializedName("credits")
    private int credits;
    @SerializedName("message")
    private int message;

    public static ActivateCode fromJSON(String activateCode) {
        return new Gson().fromJson(activateCode, ActivateCode.class);
    }

    public static String toJSON(ActivateCode home) {
        return new Gson().toJson(home);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getCashBack() {
        return cashBack;
    }

    public void setCashBack(int cashBack) {
        this.cashBack = cashBack;
    }

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType;
    }

    public String getGiftTitle() {
        return giftTitle;
    }

    public void setGiftTitle(String giftTitle) {
        this.giftTitle = giftTitle;
    }

    public int getTimeToExpire() {
        return timeToExpire;
    }

    public void setTimeToExpire(int timeToExpire) {
        this.timeToExpire = timeToExpire;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}
