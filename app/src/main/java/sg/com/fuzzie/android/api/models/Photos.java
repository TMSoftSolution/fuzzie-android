package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Photos {
    @SerializedName("url")
    private String mUrl;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }
}
