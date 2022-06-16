package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Gift {

    @SerializedName("id")
    private String id;
    @SerializedName("code")
    private String code;
    @SerializedName("used")
    private boolean used;
    @SerializedName("opened")
    private boolean opened;
    @SerializedName("expiring")
    private boolean expiring;
    @SerializedName("message")
    private String message;
    @SerializedName("expiration_date")
    private String expirationDate;
    @SerializedName("order_number")
    private String orderNumber;
    @SerializedName("saved_to_gift_box")
    private boolean savedToGiftBox;
    @SerializedName("redeemed_time")
    private String redeemedTime;
    @SerializedName("redeem_timer_started_at")
    private String redeemTimerStartedAt;
    @SerializedName("expired")
    private boolean expired;
    @SerializedName("gift_url")
    private String giftUrl;
    @SerializedName("checkout_gift_url")
    private String checkoutGiftUrl;
    @SerializedName("sent_time")
    private String sentTime;
    @SerializedName("short_code")
    private String shortCode;
    @SerializedName("redeemed_store")
    private Object redeemedStore;
    @SerializedName("brand")
    private Brand brand;
    @SerializedName("brand_id")
    private String brandId;
    @SerializedName("store")
    private Object store;
    @SerializedName("gift_card")
    private GiftCard giftCard;
    @SerializedName("service")
    private Service service;
    @SerializedName("coupons")
    private List<?> coupons;
    @SerializedName("image")
    private String imageUrl;
    @SerializedName("logo")
    private String logoUrl;
    @SerializedName("online")
    private Boolean online;
    @SerializedName("third_party_code")
    private String thirdPartyCode;
    @SerializedName("external_redeem_page")
    private String externalRedeemPage;
    @SerializedName("sold_out")
    private boolean soldOut;
    @SerializedName("gifted")
    private Boolean gifted;
    @SerializedName("sender")
    private Friend sender;
    @SerializedName("receiver")
    private Friend receiver;
    @SerializedName("received_gift_opened")
    private boolean receivedGiftOpened;
    @SerializedName("sent")
    private boolean sent;
    @SerializedName("delivered")
    private boolean delivered;
    @SerializedName("redemption_start_date")
    private String redeemStartDate;
    @SerializedName("terms")
    private List<String> terms;
    @SerializedName("type")
    private String type;
    @SerializedName("time_to_expire")
    private int timeToExpire;
    @SerializedName("jackpot_coupon_template")
    private Coupon coupon;
    @SerializedName("qr_code")
    private String qrCode;


    public static Gift fromJSON(String str) {
        return new Gson().fromJson(str, Gift.class);
    }

    public static String toJSON(Gift gift) {
        return new Gson().toJson(gift);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isExpiring() {
        return expiring;
    }

    public void setExpiring(boolean expiring) {
        this.expiring = expiring;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isSavedToGiftBox() {
        return savedToGiftBox;
    }

    public void setSavedToGiftBox(boolean savedToGiftBox) {
        this.savedToGiftBox = savedToGiftBox;
    }

    public String  getRedeemedTime() {
        return redeemedTime;
    }

    public void setRedeemedTime(String  redeemedTime) {
        this.redeemedTime = redeemedTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl) {
        this.giftUrl = giftUrl;
    }

    public String getCheckoutGiftUrl() {
        return checkoutGiftUrl;
    }

    public void setCheckoutGiftUrl(String checkoutGiftUrl) {
        this.checkoutGiftUrl = checkoutGiftUrl;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Object getRedeemedStore() {
        return redeemedStore;
    }

    public void setRedeemedStore(Object redeemedStore) {
        this.redeemedStore = redeemedStore;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Object getStore() {
        return store;
    }

    public void setStore(Object store) {
        this.store = store;
    }

    public GiftCard getGiftCard() {
        return giftCard;
    }

    public void setGiftCard(GiftCard giftCard) {
        this.giftCard = giftCard;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<?> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<?> coupons) {
        this.coupons = coupons;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getThirdPartyCode() {
        return thirdPartyCode;
    }

    public void setThirdPartyCode(String thirdPartyCode) {
        this.thirdPartyCode = thirdPartyCode;
    }

    public String getExternalRedeemPage() {
        return externalRedeemPage;
    }

    public void setExternalRedeemPage(String externalRedeemPage) {
        this.externalRedeemPage = externalRedeemPage;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public String getRedeemTimerStartedAt() {
        return redeemTimerStartedAt;
    }

    public void setRedeemTimerStartedAt(String redeemTimerStartedAt) {
        this.redeemTimerStartedAt = redeemTimerStartedAt;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean isGifted() {
        return gifted;
    }

    public void setGifted(boolean gifted) {
        this.gifted = gifted;
    }

    public Friend getSender() {
        return sender;
    }

    public void setSender(Friend sender) {
        this.sender = sender;
    }

    public Boolean isReceivedGiftOpened() {
        return receivedGiftOpened;
    }

    public void setReceivedGiftOpened(boolean receivedGiftOpened) {
        this.receivedGiftOpened = receivedGiftOpened;
    }

    public Friend getReceiver() {
        return receiver;
    }

    public void setReceiver(Friend receiver) {
        this.receiver = receiver;
    }

    public Boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getRedeemStartDate() {
        return redeemStartDate;
    }

    public void setRedeemStartDate(String redeemStartDate) {
        this.redeemStartDate = redeemStartDate;
    }

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimeToExpire() {
        return timeToExpire;
    }

    public void setTimeToExpire(int timeToExpire) {
        this.timeToExpire = timeToExpire;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public String getQrCode() {
        return qrCode;
    }
}
