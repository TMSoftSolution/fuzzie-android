package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 6/25/17.
 */

public class TopBrand {

    @SerializedName("id")
    private String id;

    @SerializedName("image")
    private String image;

    @SerializedName("brand_id")
    private String brandId;

    @SerializedName("link_to")
    private BrandLink brandLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public BrandLink getBrandLink() {
        return brandLink;
    }
}
