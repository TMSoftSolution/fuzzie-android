package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nurimanizam on 25/12/16.
 */

public class ValidWith {

    @SerializedName("valid_with_in_store_promo")
    private boolean inStorePromo;

    @SerializedName("valid_with_discount_codes")
    private boolean discountCodes;

    @SerializedName("valid_with_repeat_customers")
    private boolean repeatCustomers;

    @SerializedName("valid_with_cashback_sites")
    private boolean cashbackSites;

    public boolean isInStorePromo() {
        return inStorePromo;
    }

    public void setInStorePromo(boolean inStorePromo) {
        this.inStorePromo = inStorePromo;
    }

    public boolean isDiscountCodes() {
        return discountCodes;
    }

    public void setDiscountCodes(boolean discountCodes) {
        this.discountCodes = discountCodes;
    }

    public boolean isRepeatCustomers() {
        return repeatCustomers;
    }

    public void setRepeatCustomers(boolean repeatCustomers) {
        this.repeatCustomers = repeatCustomers;
    }

    public boolean isCashbackSites() {
        return cashbackSites;
    }

    public void setCashbackSites(boolean cashbackSites) {
        this.cashbackSites = cashbackSites;
    }

}
