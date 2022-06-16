package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 2/12/18.
 */

public class PowerUpPack {

    @SerializedName("number_of_codes")
    private int numberOfCounts;

    @SerializedName("time_to_expire")
    private int timeToExpire;

    @SerializedName("expiration_date")
    private String expirationDate;

    @SerializedName("image")
    private String image;

    @SerializedName("sequence")
    private int sequence;

    @SerializedName("name")
    private String name;

    public int getNumberOfCounts() {
        return numberOfCounts;
    }

    public void setNumberOfCounts(int numberOfCounts) {
        this.numberOfCounts = numberOfCounts;
    }

    public int getTimeToExpire() {
        return timeToExpire;
    }

    public void setTimeToExpire(int timeToExpire) {
        this.timeToExpire = timeToExpire;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getImage() {
        return image;
    }

    public int getSequence() {
        return sequence;
    }

    public String getName() {
        return name;
    }
}
