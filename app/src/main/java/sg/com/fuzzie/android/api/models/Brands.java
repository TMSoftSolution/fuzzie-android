package sg.com.fuzzie.android.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Brands {
    @SerializedName("brand")
    private Brand mBrand;

    public static Brands fromJSON(String str) {
        return new Gson().fromJson(str, Brands.class);
    }

    public static String toJSON(Brands brands) {
        return new Gson().toJson(brands);
    }

    public Brand getBrand() {
        return mBrand;
    }

    public void setBrand(Brand brand) {
        this.mBrand = brand;
    }
}
