package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 2/27/18.
 */

public class RedPacket{

    @SerializedName("id")
    private String id;

    @SerializedName("user")
    private User user;

    @SerializedName("sender")
    private User sender;

    @SerializedName("value")
    private double value;

    @SerializedName("message")
    private String message;

    @SerializedName("created_at")
    private String createdTime;

    @SerializedName("used_at")
    private String usedTime;

    @SerializedName("used")
    private boolean used;

    @SerializedName("champion")
    private boolean champion;

    @SerializedName("red_packet_bundle_id")
    private String bundleId;

    @SerializedName("number_of_jackpot_tickets")
    private int ticketCount;

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public User getSender() {
        return sender;
    }

    public double getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUsedTime() {
        return usedTime;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isChampion() {
        return champion;
    }

    public String getBundleId() {
        return bundleId;
    }

    public int getTicketCount() {
        return ticketCount;
    }
}
