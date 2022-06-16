package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Sender {

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
