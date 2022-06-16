package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class FuzzieClub {

    @SerializedName("membership")
    private boolean membership;

    @SerializedName("subscription_date")
    private String subscriptionDate;

    @SerializedName("referral_code")
    private String referralCode;

    @SerializedName("membership_price")
    private double membershipPrice;

    @SerializedName("referral_bonus_for_referer")
    private double bonusForReferer;

    @SerializedName("referral_bonus_for_subscriber")
    private double bonusForSubscriber;

    @SerializedName("savings")
    private double savings;

    @SerializedName("rules_of_use")
    private String rulesOfUse;

    public boolean isMembership() {
        return membership;
    }

    public String getSubscriptionDate() {
        return subscriptionDate;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public double getMembershipPrice() {
        return membershipPrice;
    }

    public double getBonusForReferer() {
        return bonusForReferer;
    }

    public double getBonusForSubscriber() {
        return bonusForSubscriber;
    }

    public double getSavings() {
        return savings;
    }

    public String getRulesOfUse() {
        return rulesOfUse;
    }
}
