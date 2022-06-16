package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.api.models.User;

/**
 * Created by mac on 2/27/18.
 */

public class RedPacketBundle {

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("user")
    private User user;

    @SerializedName("value")
    private double value;

    @SerializedName("message")
    private String message;

    @SerializedName("split_type")
    private String splitType;

    @SerializedName("number_of_red_packets")
    private int redPacketsCount;

    @SerializedName("red_packets")
    private List<RedPacket> redPackets;

    @SerializedName("created_at")
    private String createdTime;

    @SerializedName("all_packets_opened")
    private boolean allPacketsOpened;

    @SerializedName("opened_packets_count")
    private int openedRedPacketsCount;

    @SerializedName("unopened_packets_count")
    private int unOpenedRedPacketsCount;

    @SerializedName("assigned_packets_count")
    private int assignedRedPacketsCount;

    @SerializedName("unassigned_packets_count")
    private int unAssignedRedPacketsCount;

    @SerializedName("number_of_jackpot_tickets")
    private int ticketCount;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public User getUser() {
        return user;
    }

    public double getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public String getSplitType() {
        return splitType;
    }

    public int getRedPacketsCount() {
        return redPacketsCount;
    }

    public List<RedPacket> getRedPackets() {
        return redPackets;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public boolean isAllPacketsOpened() {
        return allPacketsOpened;
    }

    public int getOpenedRedPacketsCount() {
        return openedRedPacketsCount;
    }

    public int getUnOpenedRedPacketsCount() {
        return unOpenedRedPacketsCount;
    }

    public int getAssignedRedPacketsCount() {
        return assignedRedPacketsCount;
    }

    public int getUnAssignedRedPacketsCount() {
        return unAssignedRedPacketsCount;
    }

    public int getTicketCount() {
        return ticketCount;
    }
}
