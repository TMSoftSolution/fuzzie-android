package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("gift_details")
    private List<GiftDetail> giftDetails;

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("total_cash_back")
    private double totalCashback;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("paid_with_credits")
    private double paidWithCredits;

    @SerializedName("paid_with_card")
    private double paidWithCard;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("type")
    private String type;

    @SerializedName("payment_instrument_type")
    private String paymentInstrumentType;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<GiftDetail> getGiftDetails() {
        return giftDetails;
    }

    public void setGiftDetails(List<GiftDetail> giftDetails) {
        this.giftDetails = giftDetails;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public double getTotalCashback() {
        return totalCashback;
    }

    public void setTotalCashback(double totalCashback) {
        this.totalCashback = totalCashback;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPaidWithCredits() {
        return paidWithCredits;
    }

    public void setPaidWithCredits(double paidWithCredits) {
        this.paidWithCredits = paidWithCredits;
    }

    public double getPaidWithCard() {
        return paidWithCard;
    }

    public void setPaidWithCard(double paidWithCard) {
        this.paidWithCard = paidWithCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentInstrumentType() {
        return paymentInstrumentType;
    }

    public void setPaymentInstrumentType(String paymentInstrumentType) {
        this.paymentInstrumentType = paymentInstrumentType;
    }
}
