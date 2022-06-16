package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Component {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
