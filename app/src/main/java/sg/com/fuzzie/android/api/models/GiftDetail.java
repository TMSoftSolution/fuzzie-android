package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/18/17.
 */

public class GiftDetail {

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("price")
    private double price;

    @SerializedName("cash_back_value")
    private double cashback;

    @SerializedName("cash_back_percentage")
    private double cashBackPercentage;

    @SerializedName("power_up_percentage")
    private double powerUpPercentage;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCashback() {
        return cashback;
    }

    public void setCashback(double cashback) {
        this.cashback = cashback;
    }

    public double getCashBackPercentage() {
        return cashBackPercentage;
    }

    public void setCashBackPercentage(double cashBackPercentage) {
        this.cashBackPercentage = cashBackPercentage;
    }

    public double getPowerUpPercentage() {
        return powerUpPercentage;
    }

    public void setPowerUpPercentage(double powerUpPercentage) {
        this.powerUpPercentage = powerUpPercentage;
    }

}
