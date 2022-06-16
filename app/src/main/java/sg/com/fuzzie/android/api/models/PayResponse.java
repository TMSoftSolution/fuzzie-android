package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joma on 1/13/18.
 */

public class PayResponse {

    @Expose
    @SerializedName("cash_back")
    private double cashBack;

    @Expose
    @SerializedName("number_of_gifts")
    private int giftCounts;

    @Expose
    @SerializedName("type")
    private String type;

    @Expose
    @SerializedName("gift")
    private Gift gift;

    @Expose
    @SerializedName("coupon")
    private Gift coupon;

    @Expose
    @SerializedName("gifts")
    private List<Gift> gifts;

    @Expose
    @SerializedName("red_packet_bundle")
    private RedPacketBundle redPacketBundle;

    @Expose
    @SerializedName("awarded_jackpot_tickets_count")
    private int awardedTicketsCount;

    public double getCashBack() {
        return cashBack;
    }

    public int getGiftCounts() {
        return giftCounts;
    }

    public String getType() {
        return type;
    }


    public Gift getGift() {
        return gift;
    }

    public Gift getCoupon() {
        return coupon;
    }

    public List<Gift> getGifts() {
        return gifts;
    }

    public RedPacketBundle getRedPacketBundle() {
        return redPacketBundle;
    }

    public int getAwardedTicketsCount() {
        return awardedTicketsCount;
    }
}
