package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nurimanizam on 22/6/17.
 */

public class CreditCard {

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("title")
    private String title;

    @SerializedName("banner")
    private String banner;

    @SerializedName("banner_details_page")
    private String detailsBanner;

    @SerializedName("preview_copy")
    private String previewCopy;

    @SerializedName("details")
    private List<String> details;

    @SerializedName("bonus_title")
    private String bonusTitle;

    @SerializedName("bonus_body")
    private String bonusBody;

    @SerializedName("enable_bonus_section")
    private boolean enableBonus;

    @SerializedName("signup_url")
    private String signUpUrl;

    @SerializedName("terms")
    private String terms;

    public static CreditCard fromJSON(String str) {
        return new Gson().fromJson(str, CreditCard.class);
    }

    public static String toJSON(CreditCard creditCard) {
        return new Gson().toJson(creditCard);
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDetailsBanner() {
        return detailsBanner;
    }

    public void setDetailsBanner(String detailsBanner) {
        this.detailsBanner = detailsBanner;
    }

    public String getPreviewCopy() {
        return previewCopy;
    }

    public void setPreviewCopy(String previewCopy) {
        this.previewCopy = previewCopy;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public String getBonusTitle() {
        return bonusTitle;
    }

    public void setBonusTitle(String bonusTitle) {
        this.bonusTitle = bonusTitle;
    }

    public String getBonusBody() {
        return bonusBody;
    }

    public void setBonusBody(String bonusBody) {
        this.bonusBody = bonusBody;
    }

    public boolean isEnableBonus() {
        return enableBonus;
    }

    public void setEnableBonus(boolean enableBonus) {
        this.enableBonus = enableBonus;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getSignUpUrl() {
        return signUpUrl;
    }

    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }
}
