package sg.com.fuzzie.android.api.models;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category implements Comparable<Category>{

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("picture")
    private String picture;

    @SerializedName("brands")
    private List<String> brands;

    @SerializedName("offers")
    private List<String> offers;

    public static Category fromJSON(String str) {
        return new Gson().fromJson(str, Category.class);
    }

    public static String toJSON(Category category) {
        return new Gson().toJson(category);
    }

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public List<String> getOffers() {
        return offers;
    }

    @Override
    public int compareTo(@NonNull Category o) {
        return ((Integer)id).compareTo((Integer)o.getId());
    }

}
