package sg.com.fuzzie.android.items.brand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
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
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 8/12/17.
 */

public class StoreInfoListItem extends AbstractItem<StoreInfoListItem, StoreInfoListItem.ViewHolder> {

    Context context;
    private Store store;
    private Brand brand;

    private BroadcastReceiver broadcastReceiver;

    public StoreInfoListItem(Store store){
        this.store = store;
    }

    public Brand getBrand(){
        return this.brand;
    }

    @Override
    public int getType() {
        return R.id.item_store_info_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_store_info;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();
        final CurrentUser_ currentUser = CurrentUser_.getInstance_(context);
        brand = FuzzieData_.getInstance_(context).getBrandById(store.getBrandId());

        if (brand.getBackgroundImageUrl() != null) {
            Picasso.get()
                    .load(brand.getBackgroundImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(holder.ivBackground);
        }

        holder.ivCategory.setImageResource(FuzzieUtils.getSubCategoryWhiteIcon(brand.getSubCategoryId()));
        holder.tvBrandName.setText(brand.getName());

        if (brand.getCashBack().getPercentage() > 0) {

            if (brand.getCashBack().getPowerUpPercentage() > 0) {

                ViewUtils.visible(holder.tvPowerUp);
                holder.tvPowerUp.setText(FuzzieUtils.getFormattedPowerUpPercentage(brand.getCashBack().getPowerUpPercentage(), 1));

                if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp()){

                    holder.tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpActive));
                    holder.tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lighting_accent_16dp, 0, 0, 0);
                    holder.tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_blue));
                    ViewUtils.visible(holder.tvPromo);


                } else {

                    holder.tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpInactive));
                    holder.tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ligthing_grey_16dp, 0, 0, 0);
                    holder.tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_white));
                    ViewUtils.gone(holder.tvPromo);

                }

            } else {

                ViewUtils.gone(holder.tvPromo);
                ViewUtils.gone(holder.tvPowerUp);
            }

            if (brand.getCashBack().getCashBackPercentage() > 0) {

                ViewUtils.visible(holder.tvCashback);
                holder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(brand.getCashBack().getCashBackPercentage(), 1));

            } else {

                ViewUtils.gone(holder.tvCashback);
            }
        } else {

            ViewUtils.gone(holder.tvCashback);
            ViewUtils.gone(holder.tvPowerUp);
            ViewUtils.gone(holder.tvPromo);
        }

        if (brand.isLiked()) {

            holder.ivLikes.setImageResource(R.drawable.ic_heart_fill);

        } else {

            holder.ivLikes.setImageResource(R.drawable.ic_heart);
        }

        holder.tvLikes.setText(String.valueOf(brand.getLikersCount()));

        if (FuzzieData_.getInstance_(context).myLocation != null) {

            ViewUtils.visible(holder.tvDistance);
            holder.tvDistance.setText(getDistance(store.getLatitude(), store.getLongitude()));
            holder.tvStoreName.setText(" - " + store.getName());

        } else {

            ViewUtils.gone(holder.tvDistance);
            holder.tvStoreName.setText(store.getName());

        }

        holder.llLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (brand.isLiked()){

                    sendBroadcaseLikeRemoved();

                    Call<Void> call = FuzzieAPI.APIService().removeLikeToBrand(currentUser.getAccesstoken(), brand.getId());
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

                    Call<Void> call = FuzzieAPI.APIService().addLikeToBrand(currentUser.getAccesstoken(), brand.getId());
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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().contains(Constants.BROADCAST_LIKE_ADDED_IN_BRAND)){
                    Bundle bundle = intent.getExtras();
                    String brandId = bundle.getString("brandId");
                    if (brand.getId().equals(brandId)){
                        likeAdded(holder);

                    }
                }

                if (intent.getAction().contains(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND)){
                    Bundle bundle = intent.getExtras();
                    String brandId = bundle.getString("brandId");
                    if (brand.getId().equals(brandId)){
                        likeRemoved(holder);

                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LIKE_ADDED_IN_BRAND));
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND));


    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);

        if (broadcastReceiver != null){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivBackground;
        ImageView ivCategory;
        TextView tvCashback;
        TextView tvPowerUp;
        TextView tvPromo;
        TextView tvBrandName;
        TextView tvDistance;
        TextView tvStoreName;
        TextView tvLikes;
        ImageView ivLikes;
        LinearLayout llLikes;

        public ViewHolder(View itemView) {
            super(itemView);

            ivBackground = (ImageView) itemView.findViewById(R.id.ivBackground);
            ivCategory = (ImageView) itemView.findViewById(R.id.ivCategory);
            tvCashback = (TextView) itemView.findViewById(R.id.tvCashback);
            tvPowerUp = (TextView) itemView.findViewById(R.id.tvPowerUp);
            tvPromo = (TextView) itemView.findViewById(R.id.tvPromo);
            tvBrandName = (TextView) itemView.findViewById(R.id.tvBrandName);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvStoreName = (TextView) itemView.findViewById(R.id.tvStoreName);
            tvLikes = (TextView) itemView.findViewById(R.id.tvLikesCount);
            ivLikes = (ImageView) itemView.findViewById(R.id.liker_image);
            llLikes = (LinearLayout) itemView.findViewById(R.id.llLikes);
        }
    }

    private String getDistance(double latitude, double longitude) {
        Location storeLcoation = new Location("Store");
        storeLcoation.setLatitude(latitude);
        storeLcoation.setLongitude(longitude);

        double distance = FuzzieData_.getInstance_(context).myLocation.distanceTo(storeLcoation) / 1000;

        return String.format("%.1f km", distance);
    }

    private void sendBroadcaseLikeAdded() {

        CurrentUser_.getInstance_(context).likeBrand(brand.getId(), true);
        FuzzieData_.getInstance_(context).updateBrand(brand, true);

        Intent intent = new Intent(Constants.BROADCAST_LIKE_ADDED_IN_BRAND);
        intent.putExtra("brandId", brand.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private void sendBroadcaseLikeRemoved() {

        CurrentUser_.getInstance_(context).likeBrand(brand.getId(), false);
        FuzzieData_.getInstance_(context).updateBrand(brand, false);

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
