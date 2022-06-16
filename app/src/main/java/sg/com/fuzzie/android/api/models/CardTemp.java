package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 2/4/18.
 */

public class CardTemp {

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("exp_date")
    private String expDate;

    @SerializedName("cvv")
    private String cvv;


    public CardTemp(String cardNumber, String expDate, String cvv){

        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.cvv = cvv;

    }

    public static CardTemp fromJSON(String str){
        return new Gson().fromJson(str, CardTemp.class);
    }

    public static String toJSON(CardTemp cardTemp){
        return new Gson().toJson(cardTemp);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
