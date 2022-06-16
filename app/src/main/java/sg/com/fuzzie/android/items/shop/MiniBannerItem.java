package sg.com.fuzzie.android.items.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.WebActivity_;
import sg.com.fuzzie.android.api.models.Banner;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.ui.club.ClubReferralActivity_;
import sg.com.fuzzie.android.ui.club.ClubStoreActivity_;
import sg.com.fuzzie.android.ui.club.ClubStoresLocationActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotBrandCouponListActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotCouponActivity_;
import sg.com.fuzzie.android.ui.jackpot.PowerUpCouponActivity_;
import sg.com.fuzzie.android.ui.jackpot.PowerUpPackActivity_;
import sg.com.fuzzie.android.ui.me.InviteFriendsActivity_;
import sg.com.fuzzie.android.ui.redpacket.RedPacketActivity_;
import sg.com.fuzzie.android.ui.shop.BanksActivity_;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.ui.shop.BrandsListActivity_;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.views.autoloop.AutoScrollViewPager;
import sg.com.fuzzie.android.views.autoloop.RecyclingPagerAdapter;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;

/**
 * Created by mac on 4/27/17.
 */

public class MiniBannerItem extends AbstractItem<MiniBannerItem, MiniBannerItem.ViewHolder> {

    private List<Banner> banners;
    private boolean fromClub;

    public MiniBannerItem(List<Banner> banners){

        this.banners = banners;
        this.fromClub = false;

    }

    public MiniBannerItem(List<Banner> banners, boolean fromClub){

        this.banners = banners;
        this.fromClub = fromClub;

    }

    @Override
    public int getType() {
        return R.id.item_mini_banner_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_mini_banner;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(MiniBannerItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();

        boolean allowSwipe = (banners.size() != 1);

        viewHolder.viewPager.setAdapter(new MiniBannerItem.AutoPagerAdapter(context).setInfiniteLoop(allowSwipe));

        if (allowSwipe) {
            viewHolder.viewPager.setInterval(5000);
            viewHolder.viewPager.startAutoScroll();
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(MiniBannerItem.ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.viewPager.stopAutoScroll();
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private AutoScrollViewPager viewPager;

        public ViewHolder(View view) {
            super(view);

            this.viewPager = (AutoScrollViewPager) view.findViewById(R.id.viewPager);
        }

    }

    private class AutoPagerAdapter extends RecyclingPagerAdapter {

        Context context;

        private int size;
        private boolean isInfiniteLoop;

        public AutoPagerAdapter(Context context) {
            this.context = context;
            this.size = banners.size();
            isInfiniteLoop = false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_mini_banner_page, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_banner);

            Banner banner = banners.get(position % banners.size());

            Picasso.get().load(banner.getImage()).placeholder(R.drawable.brands_placeholder).into(imageView);
            container.addView(view);

            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Banner banner = banners.get(position % banners.size());
                    if (banner == null) return;

                    if (banner.getBannerType().equals("BrandsList")) {

                        if (banner.getBrandIds() != null){
                            String title = "";
                            if (banner.getTitle() != null){
                                title = banner.getTitle();
                            }

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<String>>(){}.getType();
                            BrandsListActivity_.intent(context).titleExtra(title).brandIdsExtra(gson.toJson(banner.getBrandIds(), type)).start();
                        }

                    } else if (banner.getBannerType().equals("Brand")) {

                        Home home = FuzzieData_.getInstance_(context).getHome();
                        Brand brand = null;
                        for (Brand aBrand : home.getBrands()) {
                            if (banner.getBrandId().equals(aBrand.getId())) {
                                brand = aBrand;
                                break;
                            }
                        }

                        if (brand != null) {

                            if (brand.getServices() != null && brand.getServices().size() > 0) {
                                if (brand.getServices().size() == 1 && (brand.getGiftCards() == null || brand.getGiftCards().size() == 0)) {
                                    BrandServiceActivity_.intent(context).brandId(brand.getId()).serviceExtra(Service.toJSON(brand.getServices().get(0))).start();
                                } else {
                                    BrandServiceListActivity_.intent(context).brandId(brand.getId()).start();
                                }
                            } else if (brand.getGiftCards() != null && brand.getGiftCards().size() > 0) {
                                BrandGiftActivity_.intent(context).brandId(brand.getId()).start();
                            }
                        }

                    } else if (banner.getBannerType().equals("Web-linked")) {

                        String bannerUrl = banner.getLink();
                        String title = banner.getTitle();
                        WebActivity_.intent(context).titleExtra(title).urlExtra(bannerUrl).start();

                    } else if (banner.getBannerType().equals("Referral Page")) {

                        if (fromClub){

                            ClubReferralActivity_.intent(context).start();

                        } else {

                            InviteFriendsActivity_.intent(context).start();

                        }

                    } else if (banner.getBannerType().equals("Bank")) {

                        BanksActivity_.intent(context).start();

                    } else if (banner.getBannerType().equals("PowerUpPacks")){

                        FuzzieData dataManager = FuzzieData_.getInstance_(context);

                        if (dataManager.getPowerUpPacks() != null && dataManager.getPowerUpPacks().size() > 0){

                            if (dataManager.getPowerUpPacks().size() == 1){

                                Coupon coupon = dataManager.getPowerUpPacks().get(0);
                                PowerUpCouponActivity_.intent(context).couponId(coupon.getId()).start();

                            } else {

                                PowerUpPackActivity_.intent(context).start();
                            }
                        }
                    }else if (banner.getBannerType().equals("JackpotGeneric")){

                        FuzzieData dataManager = FuzzieData_.getInstance_(context);

                        if (banner.getCouponIds() != null && banner.getCouponIds().size() > 0 && dataManager.getCoupons() != null && dataManager.getCoupons().size() > 0){

                            if (banner.getCouponIds().size() == 1){

                                String couponId = banner.getCouponIds().get(0);
                                Coupon coupon = dataManager.getCouponById(couponId);
                                if (coupon != null){
                                    if (coupon.getPowerUpPack() != null){

                                        PowerUpCouponActivity_.intent(context).couponId(couponId).start();

                                    } else {

                                        JackpotCouponActivity_.intent(context).couponId(couponId).start();

                                    }
                                }

                            } else {

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<String>>(){}.getType();

                                JackpotBrandCouponListActivity_.intent(context).couponIdsExtra(gson.toJson(banner.getCouponIds(), type)).start();

                            }
                        }

                    } else if (banner.getBannerType().equals("RedPacket")){

                        RedPacketActivity_.intent(context).start();

                    } else if (banner.getBannerType().equals("ClubHome")){

                        Intent intent = new Intent(BROADCAST_CHANGE_TAB);
                        intent.putExtra("tabId",R.id.tab_club);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    } else if (banner.getBannerType().equals("ClubBrand")){

                        Intent intent = new Intent(BROADCAST_CHANGE_TAB);
                        intent.putExtra("tabId",R.id.tab_club);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        if (banner.getClubStores() != null){

                            if (banner.getClubStores().size() == 1){

                                FuzzieData_.getInstance_(context).clubStore = banner.getClubStores().get(0);
                                ClubStoreActivity_.intent(context).start();

                            } else {

                                FuzzieData_.getInstance_(context).setClubMoreStores(banner.getClubStores());
                                ClubStoresLocationActivity_.intent(context).showStore(true).start();
                            }

                        }

                    } else if (banner.getBannerType().equals("ClubOffer")){

                        Intent intent = new Intent(BROADCAST_CHANGE_TAB);
                        intent.putExtra("tabId",R.id.tab_club);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }
                }
            });

            return view;
        }

        @Override
        public int getCount() {
            return isInfiniteLoop ? Integer.MAX_VALUE : banners.size();
        }

        private int getPosition(int position) {
            return isInfiniteLoop ? position % size : position;
        }

        /**
         * @return the isInfiniteLoop
         */
        public boolean isInfiniteLoop() {
            return isInfiniteLoop;
        }

        /**
         * @param isInfiniteLoop the isInfiniteLoop to set
         */
        public MiniBannerItem.AutoPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
            this.isInfiniteLoop = isInfiniteLoop;
            return this;
        }
    }


}
