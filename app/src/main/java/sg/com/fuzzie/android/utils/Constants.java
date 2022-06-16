package sg.com.fuzzie.android.utils;

/**
 * Created by nurimanizam on 15/12/16.
 */

public class Constants {

//    public static final String API_BASE_URL = "https://api.fuzzie.com.sg/";
    public static final String API_BASE_URL = "http://beta.fuzzie.com.sg/";

//    public static final String JACKPOT_LIVE_DRAW_URL = "http://fuzzie-assets.s3.amazonaws.com/jackpot/result";
    public static final String JACKPOT_LIVE_DRAW_URL = "http://fuzzie-assets.s3.amazonaws.com/jackpot/result/beta";

    // NetsPay Production Constant
    public static final String NETSPAY_API_KEY_ID = "3961c3bf-cb8d-48a5-b634-92cf929b78d2";
    public static final String NETSPAY_SECRET_KEY = "9e9bff70-4580-405e-a711-fcdf6dac2f59";
    public static final String NETSPAY_MID = "UMID_868456002";
    public static final String NETSPAY_INCOMING_REQUEST_IP= "52.128.22.41";
    public static final String NETSPAY_S2S = "https://api.fuzzie.com.sg/api/netspay/s2s";

    // NetsPay Sandbox Constant
//    public static final String NETSPAY_API_KEY_ID = "fa9b51d6-412f-4f4c-87e3-92b9ce3a4084";
//    public static final String NETSPAY_SECRET_KEY = "9905c100-c94f-4327-afd5-4d8f818062f2";
//    public static final String NETSPAY_MID = "UMID_877767002";
//    public static final String NETSPAY_INCOMING_REQUEST_IP= "118.201.98.241:9605";
//    public static final String NETSPAY_S2S = "http://beta.fuzzie.com.sg/api/netspay/s2s";

    public static final String BROADCAST_REFRESH_USER = "BROADCAST_REFRESH_USER";
    public static final String BROADCAST_REFRESHED_USER = "BROADCAST_REFRESHED_USER";

    public static final String BROADCAST_REFRESH_HOME = "BROADCAST_REFRESH_HOME";
    public static final String BROADCAST_REFRESHED_HOME = "BROADCAST_REFRESHED_HOME";

    public static final String BROADCAST_REFRESH_SHOPPING_BAG = "BROADCAST_REFRESH_SHOPPING_BAG";
    public static final String BROADCAST_REFRESHED_SHOPPING_BAG = "BROADCAST_REFRESHED_SHOPPING_BAG";

    public static final String BROADCAST_RELAYOUT_SHOPPING_BAG = "BROADCAST_RELAYOUT_SHOPPING_BAG";
    public static final String BROADCAST_CHANGED_SELETED_SHOPPING_BAG = "BROADCAST_CHANGED_SELETED_SHOPPING_BAG";

    public static final String BROADCAST_REFRESH_GIFTBOX_ACTIVE = "BROADCAST_REFRESH_GIFTBOX_ACTIVE";
    public static final String BROADCAST_REFRESHED_GIFTBOX_ACTIVE = "BROADCAST_REFRESHED_GIFTBOX_ACTIVE";
    public static final String BROADCAST_REFRESH_GIFTBOX_USED = "BROADCAST_REFRESH_GIFTBOX_USED";
    public static final String BROADCAST_REFRESHED_GIFTBOX_USED = "BROADCAST_REFRESHED_GIFTBOX_USED";
    public static final String BROADCAST_REFRESH_GIFTBOX_SENT = "BROADCAST_REFRESH_GIFTBOX_SENT";
    public static final String BROADCAST_REFRESHED_GIFTBOX_SENT = "BROADCAST_REFRESHED_GIFTBOX_SENT";
    public static final String BROADCAST_UPDATED_SENT_GIFT = "BROADCAST_UPDATED_SENT_GIFT";
    public static final String BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED = "BROADCAST_ONLINE_ACTIVE_GIFT_REDEEM_STATE_CHANGED";
    public static final String BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED = "BROADCAST_ONLINE_ACTIVE_GIFT_REDEEMED";

    public static final String BROADCAST_CHANGE_TAB = "BROADCAST_CHANGE_TAB";

    public static final String BROADCAST_REFRESH_PAYMENT_METHODS = "BROADCAST_REFRESH_PAYMENT_METHODS";

    public static final String BROADCAST_LIKE_ADDED_IN_BRAND = "BROADCAST_LIKE_ADDED_IN_BRAND";
    public static final String BROADCAST_LIKE_REMOVED_IN_BRAND = "BROADCAST_LIKE_REMOVED_IN_BRAND";

    public static final String BROADCAST_ADD_OR_REMOVE_WISHLIST_TO_BRAND = "BROADCAST_ADD_OR_REMOVE_WISHLIST_TO_BRAND";

    public static final String BROADCAST_HOME_LIKE_CLICKED = "BROADCAST_HOME_LIKE_CLICKED";

    public static final String BROADCAST_NOTIFICATION_WISHLIST = "BROADCAST_NOTIFICATION_WISHLIST";
    public static final String BROADCAST_NOTIFICATION_WISHLIST_WITH_ID = "BROADCAST_NOTIFICATION_WISHLIST_WITH_ID";
    public static final String BROADCAST_NOTIFICATION_FRIENDS = "BROADCAST_NOTIFICATION_FRIENDS";
    public static final String BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS = "BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS";
    public static final String BROADCAST_NOTIFICATION_RATE_APP = "BROADCAST_NOTIFICATION_RATE_APP";

    public static final String BROADCAST_FILTER_DONE = "BROADCAST_FILTER_DONE";

    public static final String BROADCAST_JACKPOT_RESULT_REFRESH = "BROADCAST_JACKPOT_RESULT_REFRESH";
    public static final String BROADCAST_JACKPOT_RESULT_REFRESHED = "BROADCAST_JACKPOT_RESULT_REFRESHED";
    public static final String BROADCAST_JACKPOT_LIVE_NOTIFICATION = "BROADCAST_JACKPOT_LIVE_NOTIFICATION";
    public static final String BROADCAST_JACKPOT_IS_START = "BROADCAST_JACKPOT_IS_START";
    public static final String BROADCAST_JACKPOT_IS_END = "BROADCAST_JACKPOT_IS_END";
    public static final String BROADCAST_JACKPOT_IS_WON = "BROADCAST_JACKPOT_IS_WON";

    public static final String BROADCAST_LOCATION_UPDATED_FIRST = "BROADCAST_LOCATION_UPDATED_FIRST";

    public static final String BROADCAST_CLUB_HOME_LOADED = "BROADCAST_CLUB_HOME_LOADED";
    public static final String BROADCAST_CLUB_HOME_LOAD_FAIELD = "BROADCAST_CLUB_HOME_LOAD_FAIELD";
    public static final String BROADCAST_CLUB_SUBSCRIBE_SUCCESS = "BROADCAST_CLUB_SUBSCRIBE_SUCCESS";
    public static final String BROADCAST_CLUB_OFFER_REDEEMED = "BROADCAST_CLUB_OFFER_REDEEMED";
    public static final String BROADCAST_CLUB_STORE_BOOKMARK_STATE_CHANGED = "BROADCAST_CLUB_STORE_BOOKMARK_STATE_CHANGED";

    public static final long COUNTDOWN_TIME =               24 * 3600 * 1000;
//    public static final long COUNTDOWN_TIME =               2 * 60 * 1000;
    public static final long REDEEM_TIMER_INTERVAL          = 1000;
    public static final long JACKPOT_DRAW_TIME_INTERVAL     = 1000;

    public static final int REQUEST_TOP_UP_PAYMENT          = 1000;
    public static final int REQUEST_RED_PACKET_OPEN         = 2000;
    public static final int REQUEST_RED_PACKET_ADD_MESSAGE  = 3000;
    public static final int REQUEST_RED_PACKET_ADD_CREDITS  = 3001;
    public static final int REQUEST_RED_PACKET_ADD_TICKETS  = 3002;

    public static final int REQUEST_PHOTO_PICKER            = 4000;

    public static final int REQUEST_GIFT_MARK_AS_UNREDEEMED = 5000;

    public static final int GIFTBOX_OFFSET                  = 10;

    public static final String NETSPAY_APP_PACKAGE_NAME     = "com.nets.netspay";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST   = 9000;
    public static final int GPS_SERVICES_ENABLE_REQUEST        = 9001;
    public static final int LOCATION_PERMISSION_REQUEST        = 9002;


    public interface ScheduledNotificationId{

        int NOTIFICATION_ID_JACKPOT_LIVE                    = 3000;
        int NOTIFICATION_ID_JACKPOT_REMAINDER               = 3001;
        int NOTIFICATION_ID_MY_BIRTHDAY                     = 3002;
        int NOTIFICATION_ID_FRIEND_BIRTHDAY                 = 3003;
    }

    public interface AlarmReceiverAction{

        String INTENT_JACKPOT                               = "fuzzie.com.sg.JACKPOT_NOTIFICATION";
        String INTENT_BIRTHDAY                              = "fuzzie.com.sg.BIRTHDAY_NOTIFICATION";
    }
}
