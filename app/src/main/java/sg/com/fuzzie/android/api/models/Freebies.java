package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Freebies {
    @SerializedName("type")
    private String mType;

    @SerializedName("object")
    private Object mObject;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        this.mObject = object;
    }

    public static class Object {
        @SerializedName("id")
        private String mId;
        @SerializedName("name")
        private String mName;
        @SerializedName("banner")
        private String mBanner;

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            this.mId = id;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            this.mName = name;
        }

        public String getBanner() {
            return mBanner;
        }

        public void setBanner(String banner) {
            this.mBanner = banner;
        }
    }
}
