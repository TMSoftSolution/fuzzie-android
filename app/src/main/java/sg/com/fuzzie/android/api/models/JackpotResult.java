package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joma on 10/17/17.
 */

public class JackpotResult {

    @SerializedName("id")
    private String id;

    @SerializedName("prizes")
    private List<Prize> prizes;

    @SerializedName("my_combinations")
    private List<List<String>> combinations;

    @SerializedName("draw_time")
    private String drawDateTime;

    @SerializedName("streaming_url")
    private String streamingUrl;

    @SerializedName("state")
    private String state;

    @SerializedName("current_winning")
    private Prize currentWinning;

    @SerializedName("next_prize")
    private Prize nextPrize;

    @SerializedName("watchers_count")
    private double watchersCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Prize> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
    }

    public List<List<String>> getCombinations() {
        return combinations;
    }

    public void setCombinations(List<List<String>> combinations) {
        this.combinations = combinations;
    }

    public String getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDrawDateTime() {
        return drawDateTime;
    }

    public void setDrawDateTime(String drawDateTime) {
        this.drawDateTime = drawDateTime;
    }

    public Prize getCurrentWinning() {
        return currentWinning;
    }

    public void setCurrentWinning(Prize currentWinning) {
        this.currentWinning = currentWinning;
    }

    public double getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(double watchersCount) {
        this.watchersCount = watchersCount;
    }

    public Prize getNextPrize() {
        return nextPrize;
    }

    public void setNextPrize(Prize nextPrize) {
        this.nextPrize = nextPrize;
    }
}
