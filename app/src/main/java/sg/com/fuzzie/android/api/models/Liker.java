package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Liker {

    @SerializedName("user")
    private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
