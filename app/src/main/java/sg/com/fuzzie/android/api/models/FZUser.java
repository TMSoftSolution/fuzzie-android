package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nurimanizam on 10/12/16.
 */

public class FZUser {

    @SerializedName("fuzzie_token")
    private String fuzzieToken;

    @SerializedName("id")
    private String fuzzieId;

    @SerializedName("facebook_id")
    private String facebookId;

    @SerializedName("name")
    private String name;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("birthdate")
    private String birthday;

    @SerializedName("birthdate_formatted")
    private String birthdate_formatted;

    @SerializedName("gender")
    private String gender;

    @SerializedName("avatar")
    private String profileImage;

    @SerializedName("wallet")
    private Wallet wallet;

    @SerializedName("cash_back_earned")
    private double cashbackEarned;

    @SerializedName("power_up_expiration_time")
    private String powerUpExpirationTime;

    @SerializedName("wish_list_ids")
    private ArrayList<String> wishListIds;

    @SerializedName("liked_list_ids")
    private ArrayList<String> likedListIds;

    @SerializedName("referral_code")
    private String referralCode;

    @SerializedName("referral_url")
    private String referralUrl;

    @SerializedName("settings")
    private ProfileSetting settings;

    @SerializedName("current_jackpot_tickets_count")
    private int currentJackpotTicketsCount = 0;

    @SerializedName("next_jackpot_tickets_count")
    private int nextJackpotTicketsCount = 0;

    @SerializedName("available_jackpot_tickets_count")
    private int availableJackpotTicketsCount;

    @SerializedName("jackpot_tickets_expiration_date")
    private String jackpotTicketsExpirationDate;

    @SerializedName("active_gift_count")
    private int activeGiftCount;

    @SerializedName("unopened_red_packets_count")
    private int unOpenedRedPacketCount;

    @SerializedName("opened_red_packets_count")
    private int openedRedPacketCount;

    @SerializedName("unopened_gifts_count")
    private int unOpenedGiftCount;

    @SerializedName("unopened_tickets_count")
    private int unOpenedTicketCount;

    @SerializedName("bookmarked_store_ids")
    private List<String> bookmakredStoreIds;

    @SerializedName("fuzzie_club")
    private FuzzieClub fuzzieClub;

    @SerializedName("referral_rewards")
    private ReferralRewards referralRewards;

    public FZUser() {

    }

    public static FZUser fromJSON(String str) {
        return new Gson().fromJson(str, FZUser.class);
    }

    public static String toJSON(FZUser user) {
        return new Gson().toJson(user);
    }

    public String getFuzzieToken() {
        return fuzzieToken;
    }

    public void setFuzzieToken(String fuzzieToken) {
        this.fuzzieToken = fuzzieToken;
    }

    public String getFuzzieId() {
        return fuzzieId;
    }

    public void setFuzzieId(String fuzzieId) {
        this.fuzzieId = fuzzieId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public double getCashbackEarned() {
        return cashbackEarned;
    }

    public void setCashbackEarned(double cashbackEarned) {
        this.cashbackEarned = cashbackEarned;
    }

    public String getPowerUpExpirationTime() {
        return powerUpExpirationTime;
    }

    public void setPowerUpExpirationTime(String powerUpExpirationTime) {
        this.powerUpExpirationTime = powerUpExpirationTime;
    }

    public ArrayList<String> getWishListIds() {
        return wishListIds;
    }

    public void setWishListIds(ArrayList<String> wishListIds) {
        this.wishListIds = wishListIds;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferralUrl() {
        return referralUrl;
    }

    public void setReferralUrl(String referralUrl) {
        this.referralUrl = referralUrl;
    }

    public String getBirthdate_formatted() {
        return birthdate_formatted;
    }

    public void setBirthdate_formatted(String birthdate_formatted) {
        this.birthdate_formatted = birthdate_formatted;
    }

    public ArrayList<String> getLikedListIds() {
        return likedListIds;
    }

    public void setLikedListIds(ArrayList<String> likedListIds) {
        this.likedListIds = likedListIds;
    }

    public ProfileSetting getSettings() {
        return settings;
    }

    public void setSettings(ProfileSetting settings) {
        this.settings = settings;
    }

    public int getCurrentJackpotTicketsCount() {
        return currentJackpotTicketsCount;
    }

    public void setCurrentJackpotTicketsCount(int currentJackpotTicketsCount) {
        this.currentJackpotTicketsCount = currentJackpotTicketsCount;
    }

    public int getNextJackpotTicketsCount() {
        return nextJackpotTicketsCount;
    }

    public void setNextJackpotTicketsCount(int nextJackpotTicketsCount) {
        this.nextJackpotTicketsCount = nextJackpotTicketsCount;
    }

    public int getAvailableJackpotTicketsCount() {
        return availableJackpotTicketsCount;
    }

    public String getJackpotTicketsExpirationDate() {
        return jackpotTicketsExpirationDate;
    }

    public int getActiveGiftCount() {
        return activeGiftCount;
    }

    public void setActiveGiftCount(int activeGiftCount) {
        this.activeGiftCount = activeGiftCount;
    }

    public int getUnOpenedRedPacketCount() {
        return unOpenedRedPacketCount;
    }

    public void setUnOpenedRedPacketCount(int unOpenedRedPacketCount) {
        this.unOpenedRedPacketCount = unOpenedRedPacketCount;
    }

    public int getOpenedRedPacketCount() {
        return openedRedPacketCount;
    }

    public int getUnOpenedGiftCount() {
        return unOpenedGiftCount;
    }

    public void setUnOpenedGiftCount(int unOpenedGiftCount) {
        this.unOpenedGiftCount = unOpenedGiftCount;
    }

    public int getUnOpenedTicketCount() {
        return unOpenedTicketCount;
    }

    public void setUnOpenedTicketCount(int unOpenedTicketCount) {
        this.unOpenedTicketCount = unOpenedTicketCount;
    }

    public List<String> getBookmakredStoreIds() {
        return bookmakredStoreIds;
    }

    public void setBookmakredStoreIds(List<String> bookmakredStoreIds) {
        this.bookmakredStoreIds = bookmakredStoreIds;
    }

    public FuzzieClub getFuzzieClub() {
        return fuzzieClub;
    }

    public ReferralRewards getReferralRewards() {
        return referralRewards;
    }
}
