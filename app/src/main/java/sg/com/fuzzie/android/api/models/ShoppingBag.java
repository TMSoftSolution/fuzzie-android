package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nurimanizam on 28/12/16.
 */

public class ShoppingBag {

    @SerializedName("items")
    private List<ShoppingBagItem> items;
    @SerializedName("total_cash_back")
    private double totalCashBack;
    @SerializedName("total_discount")
    private double totalDiscount;
    @SerializedName("total_price")
    private double totalPrice;

    public static ShoppingBag fromJSON(String str) {
        return new Gson().fromJson(str, ShoppingBag.class);
    }

    public static String toJSON(ShoppingBag bag) {
        return new Gson().toJson(bag);
    }

    public List<ShoppingBagItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingBagItem> items) {
        this.items = items;
    }

    public double getTotalCashBack() {
        return totalCashBack;
    }

    public void setTotalCashBack(double totalCashBack) {
        this.totalCashBack = totalCashBack;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
