package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

public class Stores {
    @SerializedName("store")
    private Store mStore;

    public Store getStore() {
        return mStore;
    }

    public void setStore(Store store) {
        this.mStore = store;
    }
}
