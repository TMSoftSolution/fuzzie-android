package sg.com.fuzzie.android.items.brand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Service;
import sg.com.fuzzie.android.api.models.Store;
import sg.com.fuzzie.android.ui.shop.BrandGiftActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceActivity_;
import sg.com.fuzzie.android.ui.shop.BrandServiceListActivity_;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;
import sg.com.fuzzie.android.views.autoloop.RecyclingPagerAdapter;

/**
 * Created by mac on 7/17/17.
 */

public class StoreInfoItem extends RecyclingPagerAdapter {

    Context context;
    private List<Store> stores;
    private boolean isInfiniteLoop;

    public StoreInfoItem(Context context, List<Store> stores, boolean isInfiniteLoop) {
        this.context = context;
        this.stores = stores;
        this.isInfiniteLoop = isInfiniteLoop;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup container) {

        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_store_info, container, false);

            holder = new ViewHolder();

            holder.ivBackground = (ImageView) convertView.findViewById(R.id.ivBackground);
            holder.ivCategory = (ImageView) convertView.findViewById(R.id.ivCategory);
            holder.tvCashback = (TextView) convertView.findViewById(R.id.tvCashback);
            holder.tvPowerUp = (TextView) convertView.findViewById(R.id.tvPowerUp);
            holder.tvPromo = (TextView) convertView.findViewById(R.id.tvPromo);
            holder.tvBrandName = (TextView) convertView.findViewById(R.id.tvBrandName);
            holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            holder.tvStoreName = (TextView) convertView.findViewById(R.id.tvStoreName);
            holder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikesCount);
            holder.ivLikes = (ImageView) convertView.findViewById(R.id.liker_image);
            holder.llLikes = (LinearLayout) convertView.findViewById(R.id.llLikes);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Store store = stores.get(position % stores.size());
        final Brand brand = FuzzieData_.getInstance_(context).getBrandById(store.getBrandId());
        final CurrentUser_ currentUser = CurrentUser_.getInstance_(context);

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
                holder.tvPowerUp.setText("+" + String.format("%.0f", brand.getCashBack().getPowerUpPercentage()) + "%");

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
                holder.tvCashback.setText(String.format("%.0f", brand.getCashBack().getCashBackPercentage()) + "%");
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FuzzieData_.getInstance_(context).isSelectedStore = true;
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
        });

        holder.llLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (brand.isLiked()){

                    likeRemoved(brand, holder);

                    Call<Void> call = FuzzieAPI.APIService().removeLikeToBrand(currentUser.getAccesstoken(), brand.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.code() != 200){

                                likeAdded(brand, holder);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            likeAdded(brand, holder);
                        }
                    });

                } else {

                    likeAdded(brand, holder);

                    Call<Void> call = FuzzieAPI.APIService().addLikeToBrand(currentUser.getAccesstoken(), brand.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.code() != 200){

                                likeRemoved(brand, holder);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            likeRemoved(brand, holder);
                        }
                    });
                }
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return isInfiniteLoop ? Integer.MAX_VALUE : stores.size();
    }

    public class ViewHolder {

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

    }


    private String getDistance(double latitude, double longitude) {
        Location storeLcoation = new Location("Store");
        storeLcoation.setLatitude(latitude);
        storeLcoation.setLongitude(longitude);

        double distance = FuzzieData_.getInstance_(context).myLocation.distanceTo(storeLcoation) / 1000;

        return String.format("%.1f km", distance);
    }

    private void sendBroadcaseLikeAdded(String brandId) {

        Intent intent = new Intent(Constants.BROADCAST_LIKE_ADDED_IN_BRAND);
        intent.putExtra("brandId", brandId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendBroadcaseLikeRemoved(String brandId) {

        Intent intent = new Intent(Constants.BROADCAST_LIKE_REMOVED_IN_BRAND);
        intent.putExtra("brandId", brandId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private void likeAdded(Brand brand, ViewHolder holder){

        CurrentUser_.getInstance_(context).likeBrand(brand.getId(), true);
        FuzzieData_.getInstance_(context).updateBrand(brand, true);

        holder.ivLikes.setImageResource(R.drawable.ic_heart_fill);
        holder.tvLikes.setText(String.valueOf(brand.getLikersCount()));

        sendBroadcaseLikeAdded(brand.getId());
    }

    private void likeRemoved(Brand brand, ViewHolder holder){

        CurrentUser_.getInstance_(context).likeBrand(brand.getId(), false);
        FuzzieData_.getInstance_(context).updateBrand(brand, false);

        holder.ivLikes.setImageResource(R.drawable.ic_heart);
        holder.tvLikes.setText(String.valueOf(brand.getLikersCount()));

        sendBroadcaseLikeRemoved(brand.getId());
    }

}
