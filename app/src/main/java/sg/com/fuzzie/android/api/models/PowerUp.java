package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class PowerUp {
    @SerializedName("percentage")
    private String mPercentage;

    public String getPercentage() {
        return mPercentage;
    }

    public void setPercentage(String percentage) {
        this.mPercentage = percentage;
    }
}
