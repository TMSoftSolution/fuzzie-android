package sg.com.fuzzie.android.api.models;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class User implements Comparable<User>{

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("facebook_id")
    private Object facebookId;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("birthdate")
    private String birthdate;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("brand_id")
    private String brandId;
    @SerializedName("gender")
    private String gender;
    @SerializedName("enabled_birthday_notifications")
    private boolean enabledBirthdayNotifications;
    @SerializedName("friend")
    private boolean friend;
    @SerializedName("wallet")
    private Wallet wallet;
    @SerializedName("fuzzie_token")
    private String fuzzieToken;
    @SerializedName("citizen_number")
    private Object citizenNumber;
    @SerializedName("wish_list_ids")
    private ArrayList<String> wishListIds;
    @SerializedName("placeholder")
    private String placeHolderImage;
    @SerializedName("status")
    private String status;

    private Integer avatarBear;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Object facebookId) {
        this.facebookId = facebookId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isEnabledBirthdayNotifications() {
        return enabledBirthdayNotifications;
    }

    public void setEnabledBirthdayNotifications(boolean enabledBirthdayNotifications) {
        this.enabledBirthdayNotifications = enabledBirthdayNotifications;
    }

    public Boolean getFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getFuzzieToken() {
        return fuzzieToken;
    }

    public void setFuzzieToken(String fuzzieToken) {
        this.fuzzieToken = fuzzieToken;
    }

    public Object getCitizenNumber() {
        return citizenNumber;
    }

    public void setCitizenNumber(Object citizenNumber) {
        this.citizenNumber = citizenNumber;
    }

    public List<String> getWishListIds() {
        return wishListIds;
    }

    public void setWishListIds(ArrayList<String> wishListIds) {
        this.wishListIds = wishListIds;
    }

    public static User fromJSON(String str) {
        return new Gson().fromJson(str, User.class);
    }

    public static String toJSON(User user) {
        return new Gson().toJson(user);
    }

    public String getPlaceHolderImage() {
        return placeHolderImage;
    }

    public void setPlaceHolderImage(String placeHolderImage) {
        this.placeHolderImage = placeHolderImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAvatarBear() {
        return avatarBear;
    }

    public void setAvatarBear(int avatarBear) {
        this.avatarBear = avatarBear;
    }

    @Override
    public int compareTo(@NonNull User o) {
        return avatarBear.compareTo(o.avatarBear);
    }
}
