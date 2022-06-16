package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Store {

    @SerializedName("id")
    private String mId;
    @SerializedName("brand_id")
    private String mBrandId;
    @SerializedName("name")
    private String mName;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("longitude")
    private double mLongitude;
    @SerializedName("latitude")
    private double mLatitude;
    @SerializedName("accepts_gift_redemption")
    private boolean mAcceptsGiftRedemption;
    @SerializedName("business_hours")
    private String mBusinessHours;
    @SerializedName("encrypted_pin")
    private String mEncryptedPin;
    @SerializedName("pin")
    private String mPin;
    @SerializedName("no_pin_redemption")
    private boolean noPinRedemption;

    private int mSubCategoryId;

    public static Store fromJSON(String str) {
        return new Gson().fromJson(str, Store.class);
    }

    public static String toJSON(Store store) {
        return new Gson().toJson(store);
    }

    public String getBrandId() {
        return mBrandId;
    }

    public void setBrandId(String brandId) {
        this.mBrandId = brandId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public boolean isAcceptsGiftRedemption() {
        return mAcceptsGiftRedemption;
    }

    public void setAcceptsGiftRedemption(boolean acceptsGiftRedemption) {
        this.mAcceptsGiftRedemption = acceptsGiftRedemption;
    }

    public String getBusinessHours() {
        return mBusinessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.mBusinessHours = businessHours;
    }

    public String getEncryptedPin() {
        return mEncryptedPin;
    }

    public void setEncryptedPin(String encryptedPin) {
        this.mEncryptedPin = encryptedPin;
    }

    public String getPin() {
        return mPin;
    }

    public void setPin(String pin) {
        this.mPin = pin;
    }

    public int getmSubCategoryId() {
        return mSubCategoryId;
    }

    public void setmSubCategoryId(int mSubCategoryId) {
        this.mSubCategoryId = mSubCategoryId;
    }

    public boolean isNoPinRedemption() {
        return noPinRedemption;
    }
}
