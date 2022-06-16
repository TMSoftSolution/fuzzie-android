package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nurimanizam on 22/6/17.
 */

public class Bank {

    @SerializedName("name")
    private String name;
    @SerializedName("banner")
    private String banner;

    @SerializedName("credit_cards")
    private List<CreditCard> creditCards;

    public static Bank fromJSON(String str) {
        return new Gson().fromJson(str, Bank.class);
    }

    public static String toJSON(Bank bank) {
        return new Gson().toJson(bank);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

}