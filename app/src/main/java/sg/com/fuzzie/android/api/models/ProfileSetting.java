package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 5/25/17.
 */

public class ProfileSetting {

    @SerializedName("shares_likes_and_wish_list")
    private boolean shareLikeWishlist;

    @SerializedName("activate_push_notifications")
    private boolean activatePushNotifications;

    @SerializedName("enable_credit_reminders")
    private boolean enableCreditReminders;

    @SerializedName("enable_announcements")
    private boolean enableAnnouncements;

    @SerializedName("display_gifting_page")
    private boolean displayGiftingPage;

    @SerializedName("display_tutorial")
    private boolean displayTutorial;

    @SerializedName("jackpot_draw_notification")
    private boolean jackpotDrawNotification;

    @SerializedName("show_jackpot_update")
    private boolean showJackpotUpdatePage;

    @SerializedName("prompt_user_for_rating_the_app")
    private boolean promptUserForRatingTheApp;

    @SerializedName("show_jackpot_instructions")
    private boolean showJackpotInstructions;

    @SerializedName("first_red_packet_bundle_bought")
    private boolean boughtFirstRedPacketBundle;

    @SerializedName("show_lucky_packet_instructions")
    private boolean showRedPacketInstruction;

    @SerializedName("show_fuzzie_club_instructions")
    private boolean showFuzzieClubInstructions;

    public boolean isShareLikeWishlist() {
        return shareLikeWishlist;
    }

    public void setShareLikeWishlist(boolean shareLikeWishlist) {
        this.shareLikeWishlist = shareLikeWishlist;
    }

    public boolean isActivatePushNotifications() {
        return activatePushNotifications;
    }

    public void setActivatePushNotifications(boolean activatePushNotifications) {
        this.activatePushNotifications = activatePushNotifications;
    }

    public boolean isEnableCreditReminders() {
        return enableCreditReminders;
    }

    public void setEnableCreditReminders(boolean enableCreditReminders) {
        this.enableCreditReminders = enableCreditReminders;
    }

    public boolean isEnableAnnouncements() {
        return enableAnnouncements;
    }

    public void setEnableAnnouncements(boolean enableAnnouncements) {
        this.enableAnnouncements = enableAnnouncements;
    }

    public Boolean isDisplayGiftingPage() {
        return displayGiftingPage;
    }

    public void setDisplayGiftingPage(boolean displayGiftingPage) {
        this.displayGiftingPage = displayGiftingPage;
    }

    public Boolean isDisplayTutorial() {
        return displayTutorial;
    }

    public void setDisplayTutorial(boolean displayTutorial) {
        this.displayTutorial = displayTutorial;
    }

    public Boolean isJackpotDrawNotification() {
        return jackpotDrawNotification;
    }

    public void setJackpotDrawNotification(boolean jackpotDrawNotification) {
        this.jackpotDrawNotification = jackpotDrawNotification;
    }

    public boolean isPrompotUserForRatingTheApp() {
        return promptUserForRatingTheApp;
    }

    public void setPrompotUserForRatingTheApp(boolean promptUserForRatingTheApp) {
        this.promptUserForRatingTheApp = promptUserForRatingTheApp;
    }

    public boolean isShowJackpotInstructions() {
        return showJackpotInstructions;
    }

    public void setShowJackpotInstructions(boolean showJackpotInstructions) {
        this.showJackpotInstructions = showJackpotInstructions;
    }

    public Boolean isShowJackpotUpdatePage() {
        return showJackpotUpdatePage;
    }

    public void setShowJackpotUpdatePage(boolean showJackpotUpdatePage) {
        this.showJackpotUpdatePage = showJackpotUpdatePage;
    }

    public boolean isBoughtFirstRedPacketBundle() {
        return boughtFirstRedPacketBundle;
    }

    public boolean isShowRedPacketInstruction() {
        return showRedPacketInstruction;
    }

    public void setShowRedPacketInstruction(boolean showRedPacketInstruction) {
        this.showRedPacketInstruction = showRedPacketInstruction;
    }

    public boolean isShowFuzzieClubInstructions() {
        return showFuzzieClubInstructions;
    }

    public void setShowFuzzieClubInstructions(boolean showFuzzieClubInstructions) {
        this.showFuzzieClubInstructions = showFuzzieClubInstructions;
    }
}
