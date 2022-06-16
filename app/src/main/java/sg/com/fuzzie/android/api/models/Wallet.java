package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravindra on 14/11/16.
 */

public class Wallet {

    @SerializedName("balance")
    private Double balance;
    @SerializedName("expiration_date")
    private String expirationDate;

    @SerializedName("encashable_balance")
    private double cashableBalance;

    public Double getBalance() {
        return balance;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public double getCashableBalance() {
        return cashableBalance;
    }
}
