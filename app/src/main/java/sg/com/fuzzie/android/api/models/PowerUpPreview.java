package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by movdev on 3/26/18.
 */

public class PowerUpPreview {

    @SerializedName("brand_ids")
    private List<String> brandIds;

    public List<String> getBrandIds() {
        return brandIds;
    }
}
