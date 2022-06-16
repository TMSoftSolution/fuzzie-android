package sg.com.fuzzie.android.api;


import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import sg.com.fuzzie.android.api.models.BrandTypeDetail;
import sg.com.fuzzie.android.api.models.ClubHome;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.ClubStoreDetail;
import sg.com.fuzzie.android.api.models.CouponTemplate;
import sg.com.fuzzie.android.api.models.Friend;
import sg.com.fuzzie.android.api.models.Bank;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.api.models.LocationVerifyResult;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.api.models.Order;
import sg.com.fuzzie.android.api.models.PayResponse;
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.api.models.PowerUpPreview;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.api.models.RedPacketBundle;
import sg.com.fuzzie.android.api.models.RedeemDetail;
import sg.com.fuzzie.android.api.models.TripAdvisor;
import sg.com.fuzzie.android.api.models.User;
import sg.com.fuzzie.android.api.models.JackpotResults;

import static sg.com.fuzzie.android.utils.Constants.JACKPOT_LIVE_DRAW_URL;

/**
 * Created by nurimanizam on 10/12/16.
 */

public interface FuzzieAPIService {

    /* Version API */

    @GET("app/version/android")
    Call<JsonObject> getCurrentStoreVersion();


    /* Auth API */
    @Multipart
    @POST("user/email_register")
    Call<JsonObject> signUpViaEmail(@Part("email") RequestBody email,
                                     @Part("first_name") RequestBody firstName,
                                     @Part("last_name") RequestBody lastName,
                                     @Part("password") RequestBody password,
                                     @Part("password_confirmation") RequestBody passwordConfirmation,
                                     @Part("phone") RequestBody phone,
                                     @Part("referred_by_code") RequestBody referralCode,
                                     @Part("gender") RequestBody gender,
                                     @Part("birthdate") RequestBody birthdate,
                                     @Part MultipartBody.Part image
                                     );


    @FormUrlEncoded
    @POST("user/phone_login")
    Call<JsonObject> loginViaPhone(@Field("phone") String phone,
                               @Field("otp") String otp);

    @FormUrlEncoded
    @POST("user/phone_login_send_otp")
    Call<Void> checkPhoneRegistered(@Field("phone") String phone);

    @POST("user")
    Call<JsonObject> loginViaFacebook(@Header("X-Facebook-Token") String facebookToken);

    @FormUrlEncoded
    @POST("user/facebook_register")
    Call<JsonObject> signUpViaFacebook(@Header("X-Facebook-Token") String facebookToken,
                                       @Field("email") String email,
                                       @Field("first_name") String firstName,
                                       @Field("last_name") String lastName,
                                       @Field("phone") String phone,
                                       @Field("referred_by_code") String referralCode,
                                       @Field("gender") String gender,
                                       @Field("birthdate") String birthdate);

    @FormUrlEncoded
    @POST("api/users/validate_referral_code")
    Call<JsonObject> validateReferralCode(@Field("code") String code);

    @FormUrlEncoded
    @POST("api/phone/{code}/verify")
    Call<JsonObject> verifyOTPCode(@Header("X-Fuzzie-Token") String accesstoken,
                                   @Path("code") String OTPCode,
                                   @Field("mobile") boolean isMobile);

    @POST("api/phone/resend_otp")
    Call<Object> resendOTPCode(@Header("X-Fuzzie-Token") String accesstoken);

    @FormUrlEncoded
    @POST("password/forgot")
    Call<JsonObject> resetPassword(@Field("email_or_phone") String email);

    /* User API */

    @GET("user")
    Call<JsonObject> getMyDetails(@Header("X-Fuzzie-Token") String accesstoken);

    @FormUrlEncoded
    @POST("api/device_tokens")
    Call<JsonObject> registerFcm(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Field("device_token") String token);

    @FormUrlEncoded
    @PUT("api/accounts/update_email")
    Call<JsonObject>updateEmail(@Header("X-Fuzzie-Token") String accesstoken,
                                @Field("email") String email);

    @FormUrlEncoded
    @PUT("api/accounts/update_first_name")
    Call<JsonObject>updateFirstName(@Header("X-Fuzzie-Token") String accesstoken,
                                    @Field("first_name") String firstName);

    @FormUrlEncoded
    @PUT("api/accounts/update_last_name")
    Call<JsonObject>updateLastName(@Header("X-Fuzzie-Token") String accesstoken,
                                    @Field("last_name") String lastName);

    @FormUrlEncoded
    @PUT("api/accounts/update_gender")
    Call<JsonObject>updateGender(@Header("X-Fuzzie-Token") String accesstoken,
                                    @Field("gender") String gender);

    @FormUrlEncoded
    @PUT("api/accounts/update_birthday")
    Call<JsonObject>updateBirthday(@Header("X-Fuzzie-Token") String accesstoken,
                                    @Field("birthdate") String birthday);

    @FormUrlEncoded
    @PUT("api/accounts/update_password")
    Call<Void>updateUserPassword(@Header("X-Fuzzie-Token") String accesstoken,
                                 @Field("current_password") String currentPassword,
                                 @Field("new_password") String newPassword);


    @Multipart
    @POST("user/profile_picture")
    Call<JsonObject> uploadProfilePhoto(@Header("X-Fuzzie-Token") String accesstoken,
                                        @Part MultipartBody.Part image);

    @DELETE("api/accounts/delete_profile_picture")
    Call<JsonObject> deleteProfilePhoto(@Header("X-Fuzzie-Token") String accesstoken);

    @FormUrlEncoded
    @POST("api/user_settings")
    Call<Void> updatePrivacyShareLikeWishlistState(@Header("X-Fuzzie-Token") String accesstoken,
                                       @Field("settings[shares_likes_and_wish_list]") boolean show);

    @FormUrlEncoded
    @POST("api/user_settings")
    Call<Void> setDisplayGiftingPage(@Header("X-Fuzzie-Token") String accesstoken,
                                     @Field("settings[display_gifting_page]") boolean show);

    @FormUrlEncoded
    @POST("api/user_settings")
    Call<Void> setShowFuzzieClubInstructions(@Header("X-Fuzzie-Token") String accesstoken,
                                     @Field("settings[show_fuzzie_club_instructions]") boolean show);

    @FormUrlEncoded
    @POST("api/user_settings")
    Call<Void> setJackpotDrawNotification(@Header("X-Fuzzie-Token") String accesstoken,
                                       @Field("settings[jackpot_draw_notification]") boolean show);

    @FormUrlEncoded
    @POST("api/user_settings")
    Call<Void> setShowJackpotInstructions(@Header("X-Fuzzie-Token") String accesstoken,
                                          @Field("settings[show_jackpot_instructions]") boolean show);


    /* Brand API */

    @GET("api/v9/home")
    Call<Home> getHomeData(@Header("X-Fuzzie-Token") String accesstoken);

    /* Others Also Bought API*/
    @GET("api/brands/{brandId}/others_also_bought")
    Call<List<String>> getAlsoBought(@Header("X-Fuzzie-Token") String accesstoken,
                                     @Path("brandId") String brandId);

    /* Get Likes API*/
    @GET("api/brands/{brandId}/likers")
    Call<List<User>> getLikes(@Header("X-Fuzzie-Token") String accesstoken,
                             @Path("brandId") String brandId);
    
    @PUT("api/liked_list/{brandId}")
    Call<Void> addLikeToBrand(@Header("X-Fuzzie-Token") String accesstoken,
                                      @Path("brandId") String brandId);

    @DELETE("api/liked_list/{brandId}")
    Call<Void> removeLikeToBrand(@Header("X-Fuzzie-Token") String accesstoken,
                              @Path("brandId") String brandId);

    /* Shopping Bag API */

    @GET("api/shopping_bag/items")
    Call<JsonObject> getShoppingBag(@Header("X-Fuzzie-Token") String accesstoken);

    @FormUrlEncoded
    @POST("api/shopping_bag/add")
    Call<JsonObject> addToShoppingBag(@Header("X-Fuzzie-Token") String accesstoken,
                                      @Field("id") String itemId);

    @HTTP(method = "DELETE", path = "api/shopping_bag/remove", hasBody = true)
    Call<JsonObject> removeItemsFromShoppingBag(@Header("X-Fuzzie-Token") String accesstoken,
                                           @Body JsonObject itemIds);


    /* Bank API */
    @GET("api/banks")
    Call<List<Bank>> getBanks(@Header("X-Fuzzie-Token") String accesstoken);

    /* Promo Code API */

    @GET("api/promo_codes/{code}/validate")
    Call<JsonObject> validatePromoCode(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("code") String code);

    @GET("api/promo_codes/{code}/validate")
    Call<JsonObject> validatePromoCode(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("code") String code,
            @Query("payment_method_token") String paymentMethodToken);

    /* Payment API */
    @GET("api/rdp/list_cards")
    Call<List<PaymentMethod>> getPaymentMethods(@Header("X-Fuzzie-Token") String accesstoken);

    @FormUrlEncoded
    @POST("api/rdp/add_card")
    Call<JsonObject> addPaymentMethod(
            @Header("X-Fuzzie-Token") String token,
            @Field("card_number") String cardNumber,
            @Field("exp_date") String expDate,
            @Field("cvv2") String cvv2);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/rdp/delete_card", hasBody = true)
    Call<ResponseBody> deletePaymentMethod(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Field("token") String paymentToken);

    @FormUrlEncoded
    @POST("gifts")
    Call<PayResponse> buyGift(@Header("X-Fuzzie-Token") String token,
                              @Field("message") String message,
                              @Field("gift_card_type") String giftType,
                              @Field("wallet") double creditsToUse,
                              @Field("rdp[token]") String paymentToken,
                              @Field("payment_mode") String paymentMode,
                              @Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("gifts")
    Call<PayResponse> buyGift(@Header("X-Fuzzie-Token") String token,
                              @Field("message") String message,
                              @Field("gift_card_type") String giftType,
                              @Field("wallet") double creditsToUse,
                              @Field("rdp[card_number]") String cardNumber,
                              @Field("rdp[exp_date]") String expDate,
                              @Field("rdp[cvv2]") String cvv2,
                              @Field("payment_mode") String paymentMode,
                              @Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("gifts")
    Call<PayResponse> buyGiftWithNets(@Header("X-Fuzzie-Token") String token,
                                      @Field("message") String message,
                                      @Field("gift_card_type") String giftType,
                                      @Field("wallet") double creditsToUse,
                                      @Field("netspay[payment_reference]") String paymentReference,
                                      @Field("payment_mode") String paymentMode,
                                      @Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("api/shopping_bag/checkout")
    Call<PayResponse> checkoutShoppingBag(@Header("X-Fuzzie-Token") String token,
                                         @Field("message") String message,
                                         @Field("wallet") double creditsToUse,
                                         @Field("rdp[token]") String paymentToken,
                                         @Field("payment_mode") String paymentMode,
                                         @Field("promo_code") String promoCode);
    @FormUrlEncoded
    @POST("api/shopping_bag/checkout")
    Call<PayResponse> checkoutShoppingBag(@Header("X-Fuzzie-Token") String token,
                                          @Field("message") String message,
                                          @Field("wallet") double creditsToUse,
                                          @Field("rdp[card_number]") String cardNumber,
                                          @Field("rdp[exp_date]") String expDate,
                                          @Field("rdp[cvv2]") String cvv2,
                                          @Field("payment_mode") String paymentMode,
                                          @Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("api/shopping_bag/checkout")
    Call<PayResponse> checkoutShoppingBagWithNets(@Header("X-Fuzzie-Token") String token,
                                                  @Field("message") String message,
                                                  @Field("wallet") double creditsToUse,
                                                  @Field("netspay[payment_reference]") String paymentReference,
                                                  @Field("payment_mode") String paymentMode,
                                                  @Field("promo_code") String promoCode);
    /* Code Activate API */
    @POST("api/codes/{code}/redeem")
    Call<JsonObject> activateCode(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("code") String code);

    @FormUrlEncoded
    @POST("api/codes/{code}/redeem")
    Call<JsonObject> activateCode(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("code") String code,
            @Field("force") boolean force);

    /* Redeem API */

    @POST("api/power_up_code_gifts/{giftId}/redeem")
    Call<JsonObject> redeemPowerUpGiftCard(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("giftId") String giftId);

    /* Gift Box API */

    @FormUrlEncoded
    @POST("gifts/{giftId}/redeem")
    Call<PayResponse> redeemGift(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("giftId") String giftId,
            @Field("pin") String pin);

    /*
     *  Gift Redeem wtih GPS
     * */
    @POST("gifts/{gift_id}/redeem")
    Call<PayResponse> redeemGiftWithGPS(@Header("X-Fuzzie-Token") String accesstoken,
                                   @Header("X-Latitude") double latitude,
                                   @Header("X-Longitude") double longitude,
                                   @Path("gift_id") String giftId);

    @PUT("api/v2/gifts/{giftId}/mark_as_opened")
    Call<PayResponse> markGiftOpened(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("giftId") String giftId);

    @PUT("api/v2/gifts/{giftId}/mark_as_used")
    Call<PayResponse> markGiftRedeemed(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("giftId") String giftId);


    /* TripAdvisor API */
    @GET("/api/brands/{brandId}/tripadvisor")
    Call<TripAdvisor> getTripAdvisorForBrand(@Header("X-Fuzzie-Token") String accesstoken, @Path("brandId") String brandId);

    /* Brand Redeem Details API */
    @GET("/api/redeem_details/{brandId}")
    Call<List<RedeemDetail>> getRedeemDetails(@Header("X-Fuzzie-Token") String accesstoken, @Path("brandId") String brandId);

    /* Brand Redeem Details API */
    @GET("/api/redeem_details/power_up_code")
    Call<List<RedeemDetail>> getRedeemDetails(@Header("X-Fuzzie-Token") String accesstoken);

    /* Wishlist API */

    @PUT("/wish_list/{brandId}")
    Call<JsonObject> putToWishlist(@Header("X-Fuzzie-Token") String accesstoken, @Path("brandId") String brandId);

    @DELETE("/wish_list/{brandId}")
    Call<JsonObject> deleteFromWishlist(@Header("X-Fuzzie-Token") String accesstoken, @Path("brandId") String brandId);

    /* Referrals API */
    @GET("api/referrals")
    Call<JsonObject> getReferrals(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    * New API for liked list and wish list
    */

    /* Get a user's wish list */
    @GET("/api/v2/wish_listed_brands/{userId}")
    Call<List<String>> getUserWishlistedBrands(@Header("X-Fuzzie-Token") String accesstoken, @Path("userId") String userId);

    /* Get a user's liked list */
    @GET("/api/v2/liked_brands/{userId}")
    Call<List<String>> getUserLikedBrands(@Header("X-Fuzzie-Token") String accesstoken, @Path("userId") String userId);

    /* Get my active gift box*/
    @GET("/api/v2/my_gifts/active")
    Call<List<Gift>> getActiveGifts(@Header("X-Fuzzie-Token") String accesstoken,
                                    @Query("start") int start,
                                    @Query("offset") int offset);

    /* Get my used gift box*/
    @GET("/api/v2/my_gifts/used")
    Call<List<Gift>> getUsedGifts(@Header("X-Fuzzie-Token") String accesstoken,
                                  @Query("start") int start,
                                  @Query("offset") int offset);

    /* Get my sent gift box*/
    @GET("/api/v2/my_gifts/sent")
    Call<List<Gift>> getSentGifts(@Header("X-Fuzzie-Token") String accesstoken,
                                  @Query("start") int start,
                                  @Query("offset") int offset);

    /*
    *  Get referrals user list
    * */
    @GET("api/referrals/users")
    Call<List<User>> getReferralsUsers(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    *  Get My Fuzzie Friends List
    * */
    @GET("api/friends/fuzzie")
    Call<List<User>> getMyFuzzieFriends(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    *  Get Facebook Account Link Fuzzie User
    * */
    @POST("api/accounts/link")
    Call<List<User>> getFacebookLinkedFuzzieUsers(@Header("X-Fuzzie-Token") String accesstoken,
                                                  @Header("X-Facebook-Token") String facebooktoken);

    /*
    *  Get Facebook whole friends list
    * */
    @GET("api/friends/facebook")
    Call<List<Friend>> getFacebookFriends(@Header("X-Fuzzie-Token") String accesstoken);

    @POST("api/accounts/connect")
    Call<List<Friend>> getFacebookConnects(@Header("X-Fuzzie-Token") String accesstoken,
                                           @Header("X-Facebook-Token") String facebooktoken);

    /*
    * Gift It API
    **/

    @FormUrlEncoded
    @POST("gifts")
    Call<PayResponse> giftIt(@Header("X-Fuzzie-Token") String token,
                             @Field("message") String message,
                             @Field("gift_card_type") String giftType,
                             @Field("wallet") double creditsToUse,
                             @Field("gifted") boolean gifted,
                             @Field("friend_name") String friendName,
                             @Field("rdp[token]") String paymentToken,
                             @Field("payment_mode") String paymentMode,
                             @Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("gifts")
    Call<PayResponse> giftIt(@Header("X-Fuzzie-Token") String token,
                             @Field("message") String message,
                             @Field("gift_card_type") String giftType,
                             @Field("wallet") double creditsToUse,
                             @Field("gifted") boolean gifted,
                             @Field("friend_name") String friendName,
                             @Field("rdp[card_number]") String cardNumber,
                             @Field("rdp[exp_date]") String expDate,
                             @Field("rdp[cvv2]") String cvv2,
                             @Field("payment_mode") String paymentMode,
                             @Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("gifts")
    Call<PayResponse> giftItWithNets(@Header("X-Fuzzie-Token") String token,
                                     @Field("message") String message,
                                     @Field("gift_card_type") String giftType,
                                     @Field("wallet") double creditsToUse,
                                     @Field("gifted") boolean gifted,
                                     @Field("friend_name") String friendName,
                                     @Field("netspay[payment_reference]") String paymentReference,
                                     @Field("payment_mode") String paymentMode,
                                     @Field("promo_code") String promoCode);

    /*
    * Send Gift with Email
    * */
    @FormUrlEncoded
    @POST("api/gifts/{gift_id}/notify")
    Call<Void> sendGiftWithEmail(@Header("X-Fuzzie-Token") String token,
                                 @Path("gift_id") String giftId,
                                 @Field("email") String email,
                                 @Field("copy_to_self") boolean copy);

    /*
    * Mark received gift as opened
    * */

    @PUT("api/v2/gifts/{gift_id}/mark_received_gift_as_opened")
    Call<PayResponse> markAsOpened(@Header("X-Fuzzie-Token") String token,
                                              @Path("gift_id") String giftId);

    /*
    * Mark online gift as unredeemed
    * */
    @PUT("api/v2/gifts/{gift_id}/mark_as_unredeemed")
    Call<PayResponse> markAsUnredeemed(@Header("X-Fuzzie-Token") String token,
                                      @Path("gift_id") String giftId);

    /*
    * Reset Unopened tickets count
    * */
    @POST("api/new_items/reset_tickets")
    Call<JsonObject> resetUnopenedTicketsCount(@Header("X-Fuzzie-Token") String token);

    /*
     * Reset Unopened tickets count
     * */
    @POST("api/new_items/reset_gifts")
    Call<JsonObject> resetUnopenedGiftsCount(@Header("X-Fuzzie-Token") String token);

    /*
    * Confirm gifted gift
    * */
    @POST("api/gifts/{gift_id}/add_to_gift_box")
    Call<Void> confirmGift(@Header("X-Fuzzie-Token") String token,
                                 @Path("gift_id") String giftId);

    /*
    *  Get User with id
    * */
    @GET("api/accounts/{user_id}/info")
    Call<User> getUserInfoWithId(@Header("X-Fuzzie-Token") String token,
                                 @Path("user_id") String userId);

    /*
    * Gift Edit
    * */

    @FormUrlEncoded
    @PUT("api/gifts/{gift_id}")
    Call<Gift> updateGiftInfo(@Header("X-Fuzzie-Token") String token,
                              @Path("gift_id") String giftId,
                              @Field("friend_name") String name,
                              @Field("message") String message);

    /*
    * Set Delivered
    * */
    @PUT("api/v2/gifts/{gift_id}/mark_as_delivered")
    Call<PayResponse> markAsDelivered(@Header("X-Fuzzie-Token") String token,
                                     @Path("gift_id") String giftId);

    /*
    * Get Orders
    * */
    @GET("api/v3/orders")
    Call<List<Order>> getOrders(@Header("X-Fuzzie-Token") String token,
                                @Query("page") int page,
                                @Query("limit") int limit);

    /*
    * Call Rate push after press share button
    * */
    @POST("api/users/share_code")
    Call<Void> callRateApp(@Header("X-Fuzzie-Token") String token);

    /*
    * Top Up Purchase
    * */
    @FormUrlEncoded
    @POST("api/credits/top_up")
    Call<JsonObject> purchaseTopUp(@Header("X-Fuzzie-Token") String token,
                                    @Field("top_up_value") double value,
                                    @Field("rdp[token]") String paymentToken,
                                    @Field("payment_mode") String paymentMode,
                                    @Field("promo_code") String promoCode);
    @FormUrlEncoded
    @POST("api/credits/top_up")
    Call<JsonObject> purchaseTopUp(@Header("X-Fuzzie-Token") String token,
                                            @Field("top_up_value") double value,
                                            @Field("rdp[card_number]") String cardNumber,
                                            @Field("rdp[exp_date]") String expDate,
                                            @Field("rdp[cvv2]") String cvv2,
                                            @Field("payment_mode") String paymentMode,
                                            @Field("promo_code") String promoCode);

    /*
    * Get Coupon Template
    * */
    @GET("api/jackpot/coupon_templates/all")
    Call<CouponTemplate> getCouponTemplates(@Header("X-Fuzzie-Token") String token);

    @GET("api/power_up_preview_brands")
    Call<PowerUpPreview>getPowerUpPreview(@Header("X-Fuzzie-Token") String token);

    /*
    * Jackpot Learn More
    * */
    @GET("/api/jackpot/learn_more")
    Call<List<RedeemDetail>> getJackpotLearnMore(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    * Coupon Purchase
    * */
    @FormUrlEncoded
    @POST("api/v2/jackpot/coupons")
    Call<PayResponse> purchaseCoupon(@Header("X-Fuzzie-Token") String accesstoken,
                                    @Field("coupon_template_id") String couponId,
                                    @Field("promo_code") String promoCode);

    /*
    * Lock Tickets
    * */
    @FormUrlEncoded
    @POST("api/jackpot/tickets/set")
    Call<JsonObject> lockTickets(@Header("X-Fuzzie-Token") String accesstoken,
                                 @Field("four_d_numbers[]") ArrayList<String> four_d_numbers);

    /*
    * Get Jackpot Result
    * */
    @GET("api/jackpot/results")
    Call<JackpotResults> getJackpotResult(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    * Get Coupon Template with BrandId
    * */
    @GET("api/brands/{brand_id}/jackpot_coupon_templates")
    Call<CouponTemplate> getCouponTemplatesWithBrandId(@Header("X-Fuzzie-Token") String token,
                                                       @Path("brand_id") String brandId);

    /*
    * Get Jackpot Live Draw Result
    * */
    @GET(JACKPOT_LIVE_DRAW_URL + "/{draw_id}")
    Call<JackpotResult> getJackpotLiveDrawResult(@Path("draw_id") String drawId);

    /*
    * Get Jackpot Issued tickets count.
    * */
    @GET("api/jackpot/tickets/global_count")
    Call<JsonObject> getIssuedTicketsCount(@Header("X-Fuzzie-Token") String token);

    /*
    * Don't show any rate pop on the same day or foreever
    * */
    @POST("api/rate_app/may_be_later")
    Call<Void> receiveRateNotificationAfterDay(@Header("X-Fuzzie-Token") String token);

    @POST("api/rate_app/i_will_rate")
    Call<Void> dontSendRateNotification(@Header("X-Fuzzie-Token") String token);

    /*
    * Lucky Packet Purchase
    * */
    @FormUrlEncoded
    @POST("api/red_packet_bundles")
    Call<PayResponse> purchaseRedPacket(@Header("X-Fuzzie-Token") String accesstoken,
                                        @Field("message") String message,
                                        @Field("number_of_red_packets") int quantity,
                                        @Field("number_of_jackpot_tickets") int ticketCount,
                                        @Field("split_type") String splitType,
                                        @Field("value") double value);

    /*
    * Lucky Packet Bundle with ID
    * */
    @GET("api/red_packet_bundles/{id}")
    Call<RedPacketBundle> getRedPacketBundle(@Header("X-Fuzzie-Token") String accesstoken,
                                             @Path("id") String id);

     /*
    * Lucky Packet Bundles
    * */
    @GET("api/red_packet_bundles")
    Call<List<RedPacketBundle>> getRedPacketBundles(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    * Lucky Packets
    * */
    @GET("api/red_packets")
    Call<List<RedPacket>> getRedPackets(@Header("X-Fuzzie-Token") String accesstoken);

    /*
    * Lucky Packets Array that opened for special bundle
    * */
    @GET("api/red_packet_bundles/{id}/assigned_red_packets")
    Call<List<RedPacket>> getRedPackets(@Header("X-Fuzzie-Token") String accesstoken,
                                             @Path("id") String id);

    /*
    * Lucky Packet Open
    * */
    @POST("api/red_packets/{id}/open")
    Call<RedPacket> openRedPacket(@Header("X-Fuzzie-Token") String accesstoken,
                                        @Path("id") String id);

    /*
    * Lucky Packet Confirm
    * */
    @POST("api/red_packet_bundles/{id}/use")
    Call<RedPacket> confirmRedPacket(@Header("X-Fuzzie-Token") String accesstoken,
                                  @Path("id") String id);

    /*
    * Send Lucky Packets vai Email
    * */
    @FormUrlEncoded
    @POST("api/red_packet_bundles/{id}/send_by_email")
    Call<Void> sendRedPackets(@Header("X-Fuzzie-Token") String token,
                                 @Path("id") String redPacketBundleId,
                                 @Field("email") String email,
                                 @Field("copy_to_self") boolean copy);
    /*
    * Lucky Packet Learn More
    * */
    @GET("/api/red_packets/learn_more")
    Call<List<RedeemDetail>> getRedPacketLearnMore(@Header("X-Fuzzie-Token") String accesstoken);

    /*
     * Club Subscribe
     * */
    @FormUrlEncoded
    @POST("api/fuzzie_club/membership/renew")
    Call<JsonObject> clubSubscribe(@Header("X-Fuzzie-Token") String token,
                                   @Field("wallet") double value,
                                   @Field("rdp[token]") String paymentToken,
                                   @Field("payment_mode") String paymentMode,
                                   @Field("promo_code") String promoCode,
                                   @Field("referral_code") String referralCode);
    @FormUrlEncoded
    @POST("api/fuzzie_club/membership/renew")
    Call<JsonObject> clubSubscribe(@Header("X-Fuzzie-Token") String token,
                                   @Field("wallet") double value,
                                   @Field("rdp[card_number]") String cardNumber,
                                   @Field("rdp[exp_date]") String expDate,
                                   @Field("rdp[cvv2]") String cvv2,
                                   @Field("payment_mode") String paymentMode,
                                   @Field("promo_code") String promoCode,
                                   @Field("referral_code") String referralCode);
    @FormUrlEncoded
    @POST("api/fuzzie_club/membership/renew")
    Call<JsonObject> clubSubscribeWithNets(@Header("X-Fuzzie-Token") String token,
                                           @Field("wallet") double value,
                                           @Field("netspay[payment_reference]") String paymentReference,
                                           @Field("payment_mode") String paymentMode,
                                           @Field("promo_code") String promoCode,
                                           @Field("referral_code") String referralCode);

    /*
     * Club Home
     * */
    @GET("api/fuzzie_club/home")
    Call<ClubHome> getClubHome(@Header("X-Fuzzie-Token") String accesstoken,
                               @Header("X-Latitude") double latitude,
                               @Header("X-Longitude") double longitude);

    @GET("api/fuzzie_club/home")
    Call<ClubHome> getClubHome(@Header("X-Fuzzie-Token") String accesstoken);

    /*
     * Club Bookmark
     * */
    @PUT("api/fuzzie_club/stores/{store_id}/bookmark")
    Call<Void> storeBookmarked(@Header("X-Fuzzie-Token") String token,
                               @Path("store_id") String storeId);

    @PUT("api/fuzzie_club/stores/{store_id}/unbookmark")
    Call<Void> storeUnBookmarked(@Header("X-Fuzzie-Token") String token,
                                 @Path("store_id") String storeId);

    /*
     * Club Store Details
     * */
    @GET("api/fuzzie_club/stores/{store_id}")
    Call<ClubStoreDetail> getClubStoreDetail(@Header("X-Fuzzie-Token") String token,
                                             @Path("store_id") String storeId);

    @GET("api/fuzzie_club/stores/{store_id}")
    Call<ClubStoreDetail> getClubStoreDetail(@Header("X-Fuzzie-Token") String token,
                                             @Header("X-Latitude") double latitude,
                                             @Header("X-Longitude") double longitude,
                                             @Path("store_id") String storeId);

    /*
     * Club Brand Type Details
     * */
    @GET("api/fuzzie_club/brand_types/{brand_type_id}/details")
    Call<BrandTypeDetail> getBrandTypeDetail(@Header("X-Fuzzie-Token") String token,
                                             @Path("brand_type_id") int brandTypeId);

    @GET("api/fuzzie_club/brand_types/{brand_type_id}/details")
    Call<BrandTypeDetail> getBrandTypeDetail(@Header("X-Fuzzie-Token") String token,
                                             @Header("X-Latitude") double latitude,
                                             @Header("X-Longitude") double longitude,
                                             @Path("brand_type_id") int brandTypeId);

    /*
     * Get Club Stores under brand type
     * */
    @GET("api/fuzzie_club/brand_types/{brand_type_id}/stores")
    Call<List<ClubStore>> getClubStores(@Header("X-Fuzzie-Token") String token,
                                        @Path("brand_type_id") int brandTypeId);

    @GET("api/fuzzie_club/brand_types/{brand_type_id}/stores")
    Call<List<ClubStore>> getClubStores(@Header("X-Fuzzie-Token") String token,
                                             @Header("X-Latitude") double latitude,
                                             @Header("X-Longitude") double longitude,
                                             @Path("brand_type_id") int brandTypeId);

    /*
     * Get Club Places under brand type
     * */
    @GET("api/fuzzie_club/brand_types/{brand_type_id}/places")
    Call<List<ClubPlace>> getClubPlaces(@Header("X-Fuzzie-Token") String token,
                                        @Path("brand_type_id") int brandTypeId);

    @GET("api/fuzzie_club/brand_types/{brand_type_id}/places")
    Call<List<ClubPlace>> getClubPlaces(@Header("X-Fuzzie-Token") String token,
                                        @Header("X-Latitude") double latitude,
                                        @Header("X-Longitude") double longitude,
                                        @Path("brand_type_id") int brandTypeId);

    /* Club Referrals API */
    @GET("api/fuzzie_club/referrals")
    Call<JsonObject> getClubReferrals(@Header("X-Fuzzie-Token") String accesstoken);

    /*
     *  Get Club referrals user list
     * */
    @GET("api/fuzzie_club/referrals/users")
    Call<List<User>> getClubReferralsUsers(@Header("X-Fuzzie-Token") String accesstoken);

    /*
     *  Club Offer Redeem
     * */
    @FormUrlEncoded
    @POST("api/fuzzie_club/offers/{offer_id}/redeem")
    Call<Offer> redeemOffer(@Header("X-Fuzzie-Token") String accesstoken,
                                       @Path("offer_id") String offerId,
                                       @Field("pin") String pin);

    /*
     *  Club Offer Redeem wtih GPS
     * */
    @POST("api/fuzzie_club/offers/{offer_id}/redeem")
    Call<Offer> redeemOfferWithGPS(@Header("X-Fuzzie-Token") String accesstoken,
                                              @Header("X-Latitude") double latitude,
                                              @Header("X-Longitude") double longitude,
                                              @Path("offer_id") String offerId);

    /*
     * Get Offer list redeemed
     * */
    @GET("api/fuzzie_club/offer_coupons")
    Call<List<Offer>> getOfferRedeemed(@Header("X-Fuzzie-Token") String token);


    /*
     * Location Verify
     * */
    @GET("api/stores/check_nearest")
    Call<LocationVerifyResult> locationVerify(@Header("X-Fuzzie-Token") String accesstoken,
                                              @Header("X-Latitude") double latitude,
                                              @Header("X-Longitude") double longitude,
                                              @Query("store_id") String storeId);

    /*
    *   Club Promo Code API
    * */

    @GET("api/fuzzie_club/promo_codes/{code}/validate")
    Call<JsonObject> validateClubPromoCode(
            @Header("X-Fuzzie-Token") String accesstoken,
            @Path("code") String code);

    /*
     *   Club Online Offer Mark as opened
     * */
    @POST("api/fuzzie_club/offers/{offer_id}/online_redeem")
    Call<Offer>onlineClubOfferRedeem(@Header("X-Fuzzie-Token") String accesstoken,
                                       @Path("offer_id") String offerId);

}
