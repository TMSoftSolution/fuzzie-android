package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class PaymentMethod {

    @SerializedName("token")
    private String token;

    @SerializedName("card_type")
    private String cardType;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("last_4")
    private String last4;

    @SerializedName("first_6")
    private String first6;

    @SerializedName("image_url")
    private String imageUrl;

    public PaymentMethod(){
        token = "";
        cardType = "";
        createdAt = "";
        first6 = "";
        last4 = "";
        imageUrl = "";
    }

    public static PaymentMethod fromJSON(String str) {
        return new Gson().fromJson(str, PaymentMethod .class);
    }

    public static String toJSON(PaymentMethod  method) {
        return new Gson().toJson(method);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFirst6() {
        return first6;
    }

    public void setFirst6(String first6) {
        this.first6 = first6;
    }

}
