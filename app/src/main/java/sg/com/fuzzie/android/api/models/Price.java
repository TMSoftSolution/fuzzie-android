package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Price {
    @SerializedName("value")
    private String mValue;
    @SerializedName("currency_code")
    private String mCurrencyCode;
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.mCurrencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.mCurrencySymbol = currencySymbol;
    }
}
