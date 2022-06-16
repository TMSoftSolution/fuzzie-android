package sg.com.fuzzie.android.utils;

import android.location.Location;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import sg.com.fuzzie.android.api.models.Banner;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.api.models.BrandTypeDetail;
import sg.com.fuzzie.android.api.models.CardTemp;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.ClubHome;
import sg.com.fuzzie.android.api.models.ClubPlace;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Friend;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.api.models.OfferType;
import sg.com.fuzzie.android.api.models.Order;
import sg.com.fuzzie.android.api.models.PaymentMethod;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.api.models.RedPacketBundle;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.ShoppingBag;
import sg.com.fuzzie.android.api.models.ShoppingBagItem;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.api.models.Stores;
import timber.log.Timber;

/**
 * Created by nurimanizam on 19/12/16.
 */

@EBean(scope = EBean.Scope.Singleton)
public class FuzzieData {

    @Pref
    MyPrefs_ prefs;

    private Home home;
    private List<Gift> activeGifts;
    private List<Gift> usedGifts;
    private List<Gift> sentGifts;
    private ShoppingBag shoppingBag;
    private boolean isEditingShoppingBags;
    private ArrayList<ShoppingBagItem> selectedBagItems;
    private List<PaymentMethod> paymentMethods;
    private PaymentMethod selectedPaymentMethod;
    private List<Friend> friends;
    private Friend receiver;
    private Gift sentGift;
    private boolean activeGiftOnlineRedeemActivity;

    // Jackpot
    private List<JackpotResult> jackpotResults;
    private List<Coupon> coupons;
    private int issuedTicketCount;
    public String[] digits;

    // Gift Flow
    public Brand selectedBrand;
    public GiftCard selectedGiftCard;
    public Service selectedService;

    public Location myLocation;

    public Set<Integer> selectedSubCategoryIds;
    public boolean isSelectedStore;

    public boolean goFriendsListFromHome;

    // Orders
    private List<Order> orders;
    private Order selectedOrder;

    // Rate Page Showing
    public boolean isShowingRatePage;

    // Brand List Filter;
    public int selectedSortByIndex = 0;
    public Set<Integer> selectedBrandsCategoryIds;
    public Set<Integer> selectedBrandsSubCategoryIds;
    public List<Brand> selectedBrands;

    // Credit Card Temp
    public boolean cardTempAdded;
    public CardTemp cardTemp;

    // Lucky Packet
    public RedPacketBundle redPacketBundle;
    private List<RedPacketBundle> redPacketBundles;
    private List<RedPacket> redPackets;

    // Club
    private ClubHome clubHome;
    private List<Offer> offerRedeemed;
    private List<ClubStore> tempStores;     // save club stores temporarily.
    private List<Offer> offers;             // save offers temporarily.
    private BrandType brandType;            // save brand type temporarily.
    private BrandTypeDetail brandTypeDetail;
    public List<String> categoryFilters;
    public List<String> componentFilters;
    private List<ClubStore> brandTypeClubStores;
    private List<ClubPlace> brandTypeClubPlaces;
    private List<ClubStore> searchClubStores;
    private List<ClubPlace> searchClubPlaces;
    private List<ClubStore> clubMoreStores;

    public ClubStore clubStore;
    public Offer offer;


    public FuzzieData() {
        selectedBagItems = new ArrayList<ShoppingBagItem>();
        isEditingShoppingBags = false;

        selectedSubCategoryIds = new TreeSet<>();
        selectedBrandsCategoryIds = new TreeSet<>();
        selectedBrandsSubCategoryIds = new TreeSet<>();
    }


    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public ShoppingBag getShoppingBag() {
        return shoppingBag;
    }

    public void setShoppingBag(ShoppingBag shoppingBag) {
        this.shoppingBag = shoppingBag;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public PaymentMethod getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(PaymentMethod selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public boolean isEdittingBags() {
        return isEditingShoppingBags;
    }

    public void setEnableEditingBagItems(boolean state) {
        if(state == this.isEditingShoppingBags) {
            return;
        }
        this.isEditingShoppingBags = state;
    }

    public void emptyEdittingBags() {
        this.selectedBagItems.clear();
    }

    public void addSelectedItem(ShoppingBagItem item) {
        this.selectedBagItems.add(item);
        Timber.d("bag", "size" + this.selectedBagItems.size());
    }

    public void removeSelectedItem(ShoppingBagItem item) {
        if(this.selectedBagItems.contains(item)) {
            this.selectedBagItems.remove(item);
        }
        Timber.d("bag", "size" + this.selectedBagItems.size());
    }

    public ArrayList<ShoppingBagItem> getSelectedBagItems() {
        return selectedBagItems;
    }

    public List<Gift> getActiveGifts() {
        return activeGifts;
    }

    public void setActiveGifts(List<Gift> activeGifts) {
        this.activeGifts = activeGifts;
    }

    public List<Gift> getUsedGifts() {
        return usedGifts;
    }

    public void setUsedGifts(List<Gift> usedGifts) {
        this.usedGifts = usedGifts;
    }


    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public Friend getReceiver() {
        return receiver;
    }

    public void setReceiver(Friend receiver) {
        this.receiver = receiver;
    }

    public Gift getSentGift() {
        return sentGift;
    }

    public void setSentGift(Gift sentGift) {
        this.sentGift = sentGift;
    }

    public Banner getBannerById(int bannerId){
        Banner mBanner = null;

        if (getHome() != null && getHome().getBanners() != null){
            for (Banner banner : getHome().getBanners()){
                if (banner.getId() == bannerId){
                    mBanner = banner;
                    break;
                }
            }
        }

        return mBanner;
    }

    public Brand getBrandById(String brandId){
        Brand mBrand = null;

        if (getHome() != null && getHome().getBrands() != null){
            for (Brand brand : getHome().getBrands()){
                if (brand.getId().equals(brandId)){
                    mBrand = brand;
                    break;
                }
            }

        }

        return mBrand;
    }

    public void updateBrand(Brand brand, boolean like){

        if (like){

            brand.setLiked(true);
            brand.setLikersCount(brand.getLikersCount() + 1);

        } else {

            brand.setLiked(false);
            brand.setLikersCount(brand.getLikersCount() - 1);

        }
    }

    public Gift getGiftById(String giftId, boolean used){
        Gift gift = null;

        if (used){
            if (usedGifts != null){
                for (Gift gift1 : usedGifts){
                    if (gift1.getId().equals(giftId)){
                        gift = gift1;
                        break;
                    }
                }
            }
        } else {
            if (activeGifts != null){
                for (Gift gift1 : activeGifts){
                    if (gift1.getId().equals(giftId)){
                        gift = gift1;
                        break;
                    }
                }
            }
        }

        return gift;
    }

    public Gift getSentGiftById(String giftId){
        Gift gift = null;
        if (sentGifts != null){
            for (Gift gift1 : sentGifts){
                if (gift1.getId().equals(giftId)){
                    gift = gift1;
                    break;
                }
            }
        }
        return gift;
    }

    public void updateSentGift(Gift gift){
        if (sentGifts != null){
            for (int i = 0 ; i < sentGifts.size() ; i ++){
                Gift gift1 = sentGifts.get(i);
                if (gift1.getId().equals(gift.getId())){
                    sentGifts.set(i, gift);
                    break;
                }
            }
        }
    }

    public void updateActiveGift(Gift gift){
        if (activeGifts != null){
            for (int i = 0 ; i < activeGifts.size() ; i ++){
                Gift gift1 = activeGifts.get(i);
                if (gift1.getId().equals(gift.getId())){
                    activeGifts.set(i, gift);
                    break;
                }
            }
        }
    }

    public void removeActiveGift(Gift gift){

        if (activeGifts != null){

            activeGifts.remove(gift);
        }
    }

    public void removeUsedGift(Gift gift){

        if (usedGifts != null){

            usedGifts.remove(gift);
        }
    }

    public void setGiftAsOpened(String giftId){
        if (activeGifts != null){
            for (Gift gift : activeGifts){
                if (gift.getId().equals(giftId)){
                    gift.setOpened(true);
                }
            }
        }
    }

    public boolean isActiveGiftOnlineRedeemActivity() {
        return activeGiftOnlineRedeemActivity;
    }

    public void setActiveGiftOnlineRedeemActivity(boolean activeGiftOnlineRedeemActivity) {
        this.activeGiftOnlineRedeemActivity = activeGiftOnlineRedeemActivity;
    }

    public List<Store> getStores(){
        List<Store> stores = new ArrayList<>();
        if (home != null && home.getBrands() != null){
            for (Brand brand : home.getBrands()){
                if (brand.getStores() != null && brand.getStores().size() > 0){
                    for (Stores stores1 : brand.getStores()){
                        Store store = stores1.getStore();
                        store.setmSubCategoryId(brand.getSubCategoryId());
                        stores.add(store);
                    }
                }
            }
        }

        return stores;
    }

    public Map<String, List<Store>> getSortedStores(List<Store> stores){
        Map<String, List<Store>> sortedStores = new TreeMap<>();

        for (Store store : stores){
            String keyString = String.valueOf(store.getLatitude()) + "-" + String.valueOf(store.getLongitude());
            if (sortedStores.containsKey(keyString)){
                List<Store> stores1 = sortedStores.get(keyString);
                stores1.add(store);
            } else {
                List<Store> stores1 = new ArrayList<>();
                stores1.add(store);
                sortedStores.put(keyString, stores1);
            }
        }

        return sortedStores;
    }

    public Store getStoreById(String storeId){
        Store store = null;
        for (Store store1 : getStores()){
            if (store1.getId().equals(storeId)){
                store = store1;
                break;
            }
        }

        return store;
    }

    public Set<Integer> getSubCategoryIds(){
        Set<Integer> subCategoryIds = new TreeSet<>();

        for(Store store : getStores()){
            subCategoryIds.add(store.getmSubCategoryId());
        }

        return subCategoryIds;
    }

    public List<Category> getUsedSubCategoris(){
        List<Category> usedCategories = new ArrayList<>();

        Collections.sort(home.getSubCategories());
        for (int i : getSubCategoryIds()){
            usedCategories.add(home.getSubCategories().get(i-1));
        }

        return usedCategories;
    }


    public List<Gift> getSentGifts() {
        return sentGifts;
    }

    public void setSentGifts(List<Gift> sentGifts) {
        this.sentGifts = sentGifts;
    }

    public void resetFuzzieData(){
        setPaymentMethods(null);
        setActiveGifts(null);
        setUsedGifts(null);
        setSentGifts(null);
        setHome(null);
        setFriends(null);
        setShoppingBag(null);
        setFriends(null);
        setRedPackets(null);
        setRedPacketBundles(null);
        setJackpotResults(null);
        setCoupons(null);
        setClubHome(null);

    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    // Get Category List from Brands List
    public List<Category> getCategoriesFromBrands(List<Brand> brands){

        List<Category> categories = new ArrayList<>();
        Set<String> categorySet = new TreeSet<>();

        for (Brand brand : brands){
            for (String categoryId : brand.getCategoryIds()){
                categorySet.add(categoryId);
            }
        }

        for (String categoryId : categorySet){
            for (Category category : home.getCategories()){
                if (category.getId() == Integer.parseInt(categoryId)){
                    categories.add(category);
                    break;
                }
            }
        }

        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return categories;
    }

    // Get SubCategory List from Brands List
    public List<Category> getSubCategoriesFromBrands(List<Brand> brands){

        List<Category> subCategories = new ArrayList<>();
        Set<Integer> subCategorySet = new TreeSet<>();

        for (Brand brand : brands){
            subCategorySet.add(brand.getSubCategoryId());
        }

        for (int subCategoryId : subCategorySet){
            for (Category category : home.getSubCategories()){
                if (subCategoryId == category.getId()){
                    subCategories.add(category);
                    break;
                }
            }
        }

        Collections.sort(subCategories, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return subCategories;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public List<Coupon> getPowerUpPacks(){

        List<Coupon> powerUpPacks = new ArrayList<>();

        if (getCoupons() != null && getCoupons().size() != 0){
            for (Coupon coupon : getCoupons()){
                if (coupon.getPowerUpPack() != null){
                    powerUpPacks.add(coupon);
                }
            }
        }
        return powerUpPacks;
    }

    public int getIssuedTicketCount() {
        return issuedTicketCount;
    }

    public void setIssuedTicketCount(int issuedTicketCount) {
        this.issuedTicketCount = issuedTicketCount;
    }

    public Coupon getCouponById(String couponId){
        Coupon coupon = null;

        if (getCoupons() != null && getCoupons().size() != 0){
            for (Coupon coupon1 : getCoupons()){
                if (coupon1.getId().equals(couponId)){
                    coupon = coupon1;
                    break;
                }
            }
        }

        return coupon;
    }

    // Get SubCategory List from Coupon List
    public List<Category> getSubCategoriesFromCoupons(List<Coupon> coupons){

        List<Category> subCategories = new ArrayList<>();
        Set<Integer> subCategorySet = new TreeSet<>();

        for (Coupon coupon : coupons){
            subCategorySet.add(coupon.getSubCategoryId());
        }

        for (int subCategoryId : subCategorySet){
            for (Category category : home.getSubCategories()){
                if (subCategoryId == category.getId()){
                    subCategories.add(category);
                    break;
                }
            }
        }

        Collections.sort(subCategories, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return subCategories;
    }

    public Map<String , List<String>> getTicketsMap(String[] tickets){
        Map<String, List<String>> ticketsMap = new TreeMap<>();

        for (String string : tickets){

            if (ticketsMap.containsKey(string)){
                List<String> list = ticketsMap.get(string);
                list.add(string);
            } else {
                List<String> list = new ArrayList<>();
                list.add(string);
                ticketsMap.put(string, list);
            }
        }

        return ticketsMap;
    }

    public List<JackpotResult> getJackpotResults() {
        return jackpotResults;
    }

    public void setJackpotResults(List<JackpotResult> jackpotResults) {
        this.jackpotResults = jackpotResults;
    }

    public void setJackpotDrawId(String drawId){
        if (drawId != null){
            prefs.edit().jackpotDrawId().put(drawId).apply();
        } else {
            prefs.edit().jackpotDrawId().remove().apply();
        }
    }

    public String getJackpotDrawId(){
        return prefs.jackpotDrawId().get();
    }

    public List<Banner> getMiniBanners(String bannerType){

        List<Banner> banners = new ArrayList<>();

        if (home != null && home.getMiniBanners() != null && home.getMiniBanners().size() > 0){

            for (Banner banner : home.getMiniBanners()){

                if (banner.getBannerType() != null && banner.getBannerType().equals(bannerType)){
                    banners.add(banner);
                }
            }
        }

        return banners;
    }

    public List<RedPacketBundle> getRedPacketBundles() {
        return redPacketBundles;
    }

    public void setRedPacketBundles(List<RedPacketBundle> redPacketBundles) {
        this.redPacketBundles = redPacketBundles;
    }

    public List<RedPacket> getRedPackets() {
        return redPackets;
    }

    public void setRedPackets(List<RedPacket> redPackets) {
        this.redPackets = redPackets;
    }

    public RedPacket getRedPacketWithId(String id){

        if (redPackets != null && redPackets.size() > 0){
            for (RedPacket redPacket : redPackets){
                if (redPacket.getId().equals(id)){
                    return redPacket;
                }
            }
        }

        return null;
    }

    public RedPacketBundle getRedPacketBundleWithId(String id){

        if (redPacketBundles != null && redPacketBundles.size() > 0){
            for (RedPacketBundle redPacketBundle : redPacketBundles){
                if (redPacketBundle.getId().equals(id)){
                    return redPacketBundle;
                }
            }
        }

        return null;
    }

    public void replaceRedPacket(RedPacket redPacket){

        if (redPackets != null && redPackets.size() > 0){
            for (int i = 0 ; i < redPackets.size() ; i ++){
                RedPacket redPacket1 = redPackets.get(i);
                if (redPacket.getId().equals(redPacket1.getId())){
                    redPackets.set(i, redPacket);
                    break;
                }
            }
        }
    }

    public ClubHome getClubHome() {
        return clubHome;
    }

    public void setClubHome(ClubHome clubHome) {
        this.clubHome = clubHome;
    }

    public List<ClubStore> getTempStores() {
        return tempStores;
    }

    public void setTempStores(List<ClubStore> tempStores) {
        this.tempStores = tempStores;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public BrandType getBrandType() {
        return brandType;
    }

    public void setBrandType(BrandType brandType) {
        this.brandType = brandType;
    }

    public BrandTypeDetail getBrandTypeDetail() {
        return brandTypeDetail;
    }

    public void setBrandTypeDetail(BrandTypeDetail brandTypeDetail) {
        this.brandTypeDetail = brandTypeDetail;
    }

    public BrandType getBrandType(int brandTypeId){


        for (BrandType brandType : getClubHome().getBrandTypes()){

            if (brandType.getId() == brandTypeId){

                return brandType;
            }
        }

        return null;
    }

    public OfferType getOfferType(int offerId, List<OfferType> offerTypes){

        for (OfferType offerType : offerTypes){

            if (offerId == offerType.getId()){

                return offerType;
            }
        }

        return null;
    }

    public ClubStore getClubStore(String storeId, List<ClubStore> clubStores){

        if (clubStores == null) return null;

        for (ClubStore clubStore : clubStores){

            if (clubStore.getStoreId().equals(storeId)){

                return clubStore;
            }
        }

        return null;
    }

    public List<ClubStore> getBrandTypeClubStores() {
        return brandTypeClubStores;
    }

    public void setBrandTypeClubStores(List<ClubStore> brandTypeClubStores) {
        this.brandTypeClubStores = brandTypeClubStores;
    }

    public List<ClubPlace> getBrandTypeClubPlaces() {
        return brandTypeClubPlaces;
    }

    public void setBrandTypeClubPlaces(List<ClubPlace> brandTypeClubPlaces) {
        this.brandTypeClubPlaces = brandTypeClubPlaces;
    }

    public List<ClubStore> getSearchClubStores() {
        return searchClubStores;
    }

    public void setSearchClubStores(List<ClubStore> searchClubStores) {
        this.searchClubStores = searchClubStores;
    }

    public List<ClubPlace> getSearchClubPlaces() {
        return searchClubPlaces;
    }

    public void setSearchClubPlaces(List<ClubPlace> searchClubPlaces) {
        this.searchClubPlaces = searchClubPlaces;
    }

    public List<ClubStore> getClubMoreStores() {
        return clubMoreStores;
    }

    public void setClubMoreStores(List<ClubStore> clubMoreStores) {
        this.clubMoreStores = clubMoreStores;
    }

    public List<Offer> getOfferRedeemed() {
        return offerRedeemed;
    }

    public void setOfferRedeemed(List<Offer> offerRedeemed) {
        this.offerRedeemed = offerRedeemed;
    }
}
