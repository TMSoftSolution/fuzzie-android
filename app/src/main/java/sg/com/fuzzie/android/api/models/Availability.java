package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Availability {

    @SerializedName("available_now")
    private boolean availableNow;

    @SerializedName("available_slots")
    private String availableSlots;

    public boolean isAvailableNow() {
        return availableNow;
    }

    public String getAvailableSlots() {
        return availableSlots;
    }
}
