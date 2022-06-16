package sg.com.fuzzie.android.api.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joma on 10/13/17.
 */

public class Coupon implements Comparable<Coupon>{

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    @SerializedName("brand_id")
    private String brandId;

    @SerializedName("cash_back")
    private CashBack cashBack;

    @SerializedName("photos")
    private List<Photos> photos;

    @SerializedName("stores")
    private List<Stores> stores;

    @SerializedName("price")
    private Price price;

    @SerializedName("description")
    private String description;

    @SerializedName("terms")
    private List<String> terms;

    @SerializedName("options")
    private List<String> options;

    @SerializedName("number_of_jackpot_tickets")
    private int ticketCount;

    @SerializedName("sold_out")
    private boolean soldOut;

    @SerializedName("redemption_start_date")
    private String redeemStartDate;

    @SerializedName("redemption_end_date")
    private String redeemEndDate;

    @SerializedName("power_up_pack")
    private PowerUpPack powerUpPack;

    private String brandName;
    private int subCategoryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public CashBack getCashBack() {
        return cashBack;
    }

    public void setCashBack(CashBack cashBack) {
        this.cashBack = cashBack;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public List<Stores> getStores() {
        return stores;
    }

    public void setStores(List<Stores> stores) {
        this.stores = stores;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String>  getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    @Override
    public int compareTo(@NonNull Coupon o) {
        return 0;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public String getRedeemStartDate() {
        return redeemStartDate;
    }

    public void setRedeemStartDate(String redeemStartDate) {
        this.redeemStartDate = redeemStartDate;
    }

    public String getRedeemEndDate() {
        return redeemEndDate;
    }

    public void setRedeemEndDate(String redeemEndDate) {
        this.redeemEndDate = redeemEndDate;
    }

    public double getPricePerTicket() {

        double price = Double.parseDouble(this.price.getValue());
        double cashbackPrice = this.cashBack.getValue();

        return (price -  cashbackPrice) / this.ticketCount;
    }

    public PowerUpPack getPowerUpPack() {
        return powerUpPack;
    }

    public void setPowerUpPack(PowerUpPack powerUpPack) {
        this.powerUpPack = powerUpPack;
    }
}
