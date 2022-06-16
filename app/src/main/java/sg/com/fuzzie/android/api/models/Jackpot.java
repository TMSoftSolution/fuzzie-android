package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 11/27/17.
 */

public class Jackpot {

    @Expose
    @SerializedName("draw_id")
    private String drawId;

    @Expose
    @SerializedName("draw_time")
    private String drawTime;

    @Expose
    @SerializedName("next_draw_id")
    private String nextDrawId;

    @Expose
    @SerializedName("next_draw_time")
    private String nextDrawTime;

    @Expose
    @SerializedName("enabled")
    private boolean enabled = false;

    @Expose
    @SerializedName("draw_state")
    private boolean live = false;

    @Expose
    @SerializedName("number_of_tickets_per_week")
    private int numberOfTicketsPerWeek;

    public String getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(String drawTime) {
        this.drawTime = drawTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public int getNumberOfTicketsPerWeek() {
        return numberOfTicketsPerWeek;
    }

    public void setNumberOfTicketsPerWeek(int numberOfTicketsPerWeek) {
        this.numberOfTicketsPerWeek = numberOfTicketsPerWeek;
    }

    public String getDrawId() {
        return drawId;
    }

    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }

    public String getNextDrawTime() {
        return nextDrawTime;
    }

    public void setNextDrawTime(String nextDrawTime) {
        this.nextDrawTime = nextDrawTime;
    }

    public String getNextDrawId() {
        return nextDrawId;
    }

    public void setNextDrawId(String nextDrawId) {
        this.nextDrawId = nextDrawId;
    }
}
