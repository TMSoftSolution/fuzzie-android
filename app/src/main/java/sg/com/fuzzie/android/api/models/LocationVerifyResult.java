package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class LocationVerifyResult {

    @SerializedName("is_nearest")
    private boolean nearest;

    public boolean isNearest() {
        return nearest;
    }
}
