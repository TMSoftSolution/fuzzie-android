package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class ReferralRewards {

    @SerializedName("total_credits_earned")
    private double credits;

    @SerializedName("total_jackpot_tickets_earned")
    private int tickets;

    public double getCredits() {
        return credits;
    }

    public int getTickets() {
        return tickets;
    }
}
