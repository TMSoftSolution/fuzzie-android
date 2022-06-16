package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joma on 10/17/17.
 */

public class Prize {

    @SerializedName("four_d")
    private String fourD;

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("name")
    private String name;

    @SerializedName("amount")
    private int amount;

    public Prize(String fourD, String identifier, String name, int amount){
        this.fourD = fourD;
        this.identifier = identifier;
        this.name = name;
        this.amount = amount;
    }

    public String getFourD() {
        return fourD;
    }

    public void setFourD(String fourD) {
        this.fourD = fourD;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
