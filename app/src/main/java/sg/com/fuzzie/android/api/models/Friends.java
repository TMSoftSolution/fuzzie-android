package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Friends {

    @SerializedName("friends")
    private List<Friend> mFriends;

    public List<Friend> getFriends() {
        return mFriends;
    }

    public void setFriends(List<Friend> friends) {
        this.mFriends = friends;
    }
}
