package sg.com.fuzzie.android.items.brand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Stores;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by nurimanizam on 18/12/16.
 */

public class BrandListItem extends AbstractItem<BrandListItem, BrandListItem.ViewHolder> {

    private Home home;
    private Brand brand;
    private String accessToken;

    private Context context;
    private CurrentUser_ currentUser;
    private FuzzieData_ dataManager;

    private BroadcastReceiver broadcastReceiver;

    public BrandListItem(Home home, Brand brand) {
        this.home = home;
        this.brand = brand;
    }

    public BrandListItem(Brand brand){
        this.brand = brand;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_brand_list_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_brand_list;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(final BrandListItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        context = viewHolder.itemView.getContext();
        currentUser = CurrentUser_.getInstance_(context);
        dataManager = FuzzieData_.getInstance_(context);

        Picasso.get()
                .load(brand.getBackgroundImageUrl())
                .placeholder(R.drawable.brands_placeholder)
                .into(viewHolder.ivBackground);

        viewHolder.tvBrandName.setText(brand.getName());
        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryWhiteIcon(brand.getSubCategoryId()));
        viewHolder.ivCategory.setImageDrawable(subCategoryIcon);

        if (brand.getCashBack().getPercentage() > 0) {
            if (brand.getCashBack().getPowerUpPercentage() > 0) {

                ViewUtils.visible(viewHolder.tvPowerUp);
                viewHolder.tvPowerUp.setText(FuzzieUtils.getFormattedPowerUpPercentage(brand.getCashBack().getPowerUpPercentage(), 1));

                if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp()){

                    viewHolder.tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpActive));
                    viewHolder.tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lighting_accent_16dp, 0, 0, 0);
                    viewHolder.tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_blue));

                } else {

                    viewHolder.tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpInactive));
                    viewHolder.tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ligthing_grey_16dp, 0, 0, 0);
                    viewHolder.tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_white));

                }

            } else {

                ViewUtils.gone(viewHolder.tvPowerUp);
            }

            if (brand.getCashBack().getCashBackPercentage() > 0) {

                ViewUtils.visible(viewHolder.tvCashback);
                viewHolder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(brand.getCashBack().getCashBackPercentage(), 1));

            } else {

                ViewUtils.gone(viewHolder.tvCashback);
            }

        } else {

            ViewUtils.gone(viewHolder.tvCashback);
            ViewUtils.gone(viewHolder.tvPowerUp);

            if (brand.getBrandLink() != null && brand.getBrandLink().getType().equals("jackpot_coupon")){

                // show cashback if jackpot coupon has cashback value
                Coupon coupon = dataManager.getCouponById(brand.getBrandLink().getCouponId());

                if (coupon != null && coupon.getCashBack() !=  null && coupon.getCashBack().getPercentage() > 0){

                    ViewUtils.visible(viewHolder.tvCashback);
                    viewHolder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(coupon.getCashBack().getPercentage(), 1));

                }

            }
        }

        if (brand.getCashBack().getPowerUpPercentage() > 0.0 && (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp())){

            ViewUtils.visible(viewHolder.ivPromo);
            if (brand.isNew()){

                viewHolder.ivPromo.setImageResource(R.drawable.ic_brand_new_label);

            } else {

                viewHolder.ivPromo.setImageResource(R.drawable.ic_brand_promo_label);
            }

        } else {

            if (brand.isNew()){

                ViewUtils.visible(viewHolder.ivPromo);
                viewHolder.ivPromo.setImageResource(R.drawable.ic_brand_new_label);

            } else {

                ViewUtils.gone(viewHolder.ivPromo);
            }
        }

        if (brand.getStores().size() == 0){

            viewHolder.tvPrice.setText("Online");

        } else {

            if (brand.getStores().size() == 1){

                Stores stores = brand.getStores().get(0);
                viewHolder.tvPrice.setText(stores.getStore().getName());

            } else {

                viewHolder.tvPrice.setText(String.valueOf(brand.getStores().size()) + " Stores");
            }
        }

        if (brand.isLiked()){

            viewHolder.ivLikes.setImageResource(R.drawable.ic_heart_fill);

        } else {

            viewHolder.ivLikes.setImageResource(R.drawable.ic_heart);
        }

        viewHolder.tvLikes.setText(String.valueOf(brand.getLikersCount()));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().contains(Constants.BROADCAST_LIKE_ADDED_IN_BRAND)){
                    Bundle bundle = intent.getExtras();
                    String brandId = bundle.getString("brandId");
                    if (brand.getId().equals(brandId)){
                        likeAdded(viewHolder);

                    }
                }

                if (intent.getAction().contains(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND)){
                    Bundle bundle = intent.getExtras();
                    String brandId = bundle.getString("brandId");
                    if (brand.getId().equals(brandId)){
                        likeRemoved(viewHolder);

                    }
                }

            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LIKE_ADDED_IN_BRAND));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND));

        accessToken = currentUser.getAccesstoken();

        viewHolder.llLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (brand.isLiked()){

                    sendBroadcaseLikeRemoved();

                    Call<Void> call = FuzzieAPI.APIService().removeLikeToBrand(accessToken, brand.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {


                            if (response.code() != 200){

                                sendBroadcaseLikeAdded();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            sendBroadcaseLikeAdded();
                        }
                    });
                } else {

                    sendBroadcaseLikeAdded();

                    Call<Void> call = FuzzieAPI.APIService().addLikeToBrand(accessToken, brand.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.code() != 200){

                                sendBroadcaseLikeRemoved();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            sendBroadcaseLikeRemoved();
                        }
                    });
                }
            }
        });

        viewHolder.tvSoldOut.setText("SOLD OUT");
        if (brand.isSoldOut() != null && brand.isSoldOut()) {

            viewHolder.tvSoldOut.setRotation(-20);
            ViewUtils.visible(viewHolder.tvSoldOut);

        } else {

            ViewUtils.gone(viewHolder.tvSoldOut);
        }

        if (brand.isClubOnly()){

            ViewUtils.visible(viewHolder.ivClubMember);

        } else {

            ViewUtils.gone(viewHolder.ivClubMember);
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder viewHolder){
        super.unbindView(viewHolder);

        if (broadcastReceiver != null){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        }
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivBackground;
        protected ImageView ivCategory;
        protected TextView tvCashback;
        protected TextView tvPowerUp;
        protected TextView tvBrandName;
        protected TextView tvPrice;
        protected LinearLayout llLikes;
        protected ImageView ivLikes;
        protected TextView tvLikes;
        protected TextView tvSoldOut;
        protected ImageView ivPromo;
        protected ImageView ivClubMember;

        public ViewHolder(View view) {
            super(view);
            this.ivBackground = view.findViewById(R.id.ivBackground);
            this.ivCategory = view.findViewById(R.id.ivCategory);
            this.tvCashback =  view.findViewById(R.id.tvCashback);
            this.tvPowerUp =  view.findViewById(R.id.tvPowerUp);
            this.tvBrandName =  view.findViewById(R.id.tvBrandName);
            this.tvPrice =  view.findViewById(R.id.tvPrice);
            this.llLikes = view.findViewById(R.id.llLikes);
            this.tvLikes =  view.findViewById(R.id.tvLikesCount);
            this.ivLikes = view.findViewById(R.id.liker_image);
            this.tvSoldOut =  view.findViewById(R.id.tvSoldOut);
            this.ivPromo = view.findViewById(R.id.ivPromo);
            this.ivClubMember = view.findViewById(R.id.ivClubMember);

        }
    }

    private void sendBroadcaseLikeAdded(){

        currentUser.likeBrand(brand.getId(), true);
        dataManager.updateBrand(brand, true);

        Intent intent = new Intent(Constants.BROADCAST_LIKE_ADDED_IN_BRAND);
        intent.putExtra("brandId", brand.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private void sendBroadcaseLikeRemoved(){

        currentUser.likeBrand(brand.getId(), false);
        dataManager.updateBrand(brand, false);

        Intent intent = new Intent(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND);
        intent.putExtra("brandId", brand.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private void likeAdded(ViewHolder viewHolder){

        if (brand == null) return;

        viewHolder.ivLikes.setImageResource(R.drawable.ic_heart_fill);
        viewHolder.tvLikes.setText(String.valueOf(brand.getLikersCount()));
    }

    private void likeRemoved(ViewHolder viewHolder){

        if (brand == null) return;

        viewHolder.ivLikes.setImageResource(R.drawable.ic_heart);
        viewHolder.tvLikes.setText(String.valueOf(brand.getLikersCount()));
    }


}
