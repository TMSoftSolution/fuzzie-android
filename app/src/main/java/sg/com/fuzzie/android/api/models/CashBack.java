package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class CashBack {
    @SerializedName("percentage")
    private double percentage;
    @SerializedName("cash_back_percentage")
    private double cashBackPercentage;
    @SerializedName("power_up_percentage")
    private double powerUpPercentage;
    @SerializedName("value")
    private double value;

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
