package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Filter {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("components")
    private List<Component> components;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Component> getComponents() {
        return components;
    }
}
