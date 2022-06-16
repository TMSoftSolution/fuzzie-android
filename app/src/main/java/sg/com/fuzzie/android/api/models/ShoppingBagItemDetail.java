package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nurimanizam on 29/12/16.
 */

public class ShoppingBagItemDetail {

    @SerializedName("gift_card")
    GiftCard giftCard;
    @SerializedName("service")
    Service service;

    public GiftCard getGiftCard() {
        return giftCard;
    }

    public void setGiftCard(GiftCard giftCard) {
        this.giftCard = giftCard;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
