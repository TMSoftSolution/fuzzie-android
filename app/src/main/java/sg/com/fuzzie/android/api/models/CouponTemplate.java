package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joma on 10/13/17.
 */

public class CouponTemplate {

    @SerializedName("coupons")
    private List<Coupon> coupons;

    @SerializedName("issued_tickets_count")
    private int issuedTicketCount;

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public int getIssuedTicketCount() {
        return issuedTicketCount;
    }

    public void setIssuedTicketCount(int issuedTicketCount) {
        this.issuedTicketCount = issuedTicketCount;
    }
}
