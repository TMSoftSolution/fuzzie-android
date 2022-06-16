package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Banner {

    @SerializedName("id")
    private int id;
    @SerializedName("banner_type")
    private String bannerType;
    @SerializedName("header")
    private String header;
    @SerializedName("sub_header")
    private String subHeader;
    @SerializedName("image")
    private String image;
    @SerializedName("link")
    private String link;
    @SerializedName("story_id")
    private int storyId;
    @SerializedName("brand_id")
    private String brandId;
    @SerializedName("power_up_campaign_id")
    private String powerUpCampaignId;
    @SerializedName("sequence")
    private int sequence;
    @SerializedName("general_page")
    private GeneralPage generalPage;
    @SerializedName("brand_ids")
    private List<String> brandIds;
    @Expose
    @SerializedName("jackpot_coupon_template_id")
    private String couponId;

    @SerializedName("jackpot_coupon_templates")
    private List<String> couponIds;

    @SerializedName("title")
    private String title;

    // For Mini Banner
    @SerializedName("position")
    private int position;
    @SerializedName("locations")
    private List<String>locations;

    // For Club
    @SerializedName("club_brand_stores")
    private List<ClubStore> clubStores;
    @SerializedName("club_offer")
    private Offer offer;
    @SerializedName("club_brand_online")
    private boolean clubBrandOnline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBannerType() {
        return bannerType;
    }

    public void setBannerType(String bannerType) {
        this.bannerType = bannerType;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public Object getPowerUpCampaignId() {
        return powerUpCampaignId;
    }

    public void setPowerUpCampaignId(String powerUpCampaignId) {
        this.powerUpCampaignId = powerUpCampaignId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<String> getBrandIds() {
        return brandIds;
    }

    public void setBrandIds(List<String> brandIds) {
        this.brandIds = brandIds;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public GeneralPage getGeneralPage() {
        return generalPage;
    }

    public void setGeneralPage(GeneralPage generalPage) {
        this.generalPage = generalPage;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public List<String> getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(List<String> couponIds) {
        this.couponIds = couponIds;
    }

    public String getTitle() {
        return title;
    }

    public List<ClubStore> getClubStores() {
        return clubStores;
    }

    public Offer getOffer() {
        return offer;
    }

    public boolean isClubBrandOnline() {
        return clubBrandOnline;
    }
}
