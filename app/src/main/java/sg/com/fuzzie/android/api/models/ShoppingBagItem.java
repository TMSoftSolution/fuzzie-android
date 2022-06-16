package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nurimanizam on 28/12/16.
 */

public class ShoppingBagItem {

    @SerializedName("brand")
    private Brand brand;
    @SerializedName("item")
    private ShoppingBagItemDetail item;
    @SerializedName("cash_back")
    private CashBack cashback;
    @SerializedName("selling_price")
    private double sellingPrice;

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public ShoppingBagItemDetail getItem() {
        return item;
    }

    public void setItem(ShoppingBagItemDetail item) {
        this.item = item;
    }

    public CashBack getCashback() {
        return cashback;
    }

    public void setCashback(CashBack cashback) {
        this.cashback = cashback;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
