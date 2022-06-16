package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faruq on 03/02/17.
 */

public class RedeemDetail {

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("image")
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
