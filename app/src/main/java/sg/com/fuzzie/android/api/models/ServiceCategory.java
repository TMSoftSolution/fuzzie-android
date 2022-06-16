package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravindra on 04/11/16.
 */

public class ServiceCategory {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
