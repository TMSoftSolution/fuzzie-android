package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("name")
    private String name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("fuzzie_id")
    private String fuzzieId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFuzzieId() {
        return fuzzieId;
    }

    public void setFuzzieId(String fuzzieId) {
        this.fuzzieId = fuzzieId;
    }
}
