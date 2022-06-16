package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;

import sg.com.fuzzie.android.api.models.Coupon;

import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 4/13/17.
 */

public class ShopTopBrandItem extends AbstractItem<ShopTopBrandItem, ShopTopBrandItem.ViewHolder> {

    Brand brand;

    public ShopTopBrandItem(Brand brand) {
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
        return R.id.item_shop_top_brand_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_shop_top_brand;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();
        CurrentUser_ currentUser = CurrentUser_.getInstance_(context);

        int cornerRadius = (int) ViewUtils.convertDpToPixel(3, context);
        Picasso.get()
                .load(brand.getCustomImageUrl())
                .placeholder(R.drawable.brands_placeholder)
                .transform(new RoundedCornersTransformation(cornerRadius, 0, RoundedCornersTransformation.CornerType.TOP))
                .into(viewHolder.ivBackground);

        if (brand.getCashBack().getPercentage() > 0) {

            if (brand.getCashBack().getPowerUpPercentage() > 0) {

                viewHolder.tvPowerUp.setText(FuzzieUtils.getFormattedPowerUpPercentage(brand.getCashBack().getPowerUpPercentage(), 1));
                ViewUtils.visible(viewHolder.tvPowerUp);

                if (currentUser.getCurrentUser().getPowerUpExpirationTime() != null || brand.isPowerUp()){

                    viewHolder.tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpActive));
                    viewHolder.tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lighting_accent_12dp, 0, 0, 0);
                    viewHolder.tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_small_blue));

                } else {

                    viewHolder.tvPowerUp.setTextColor(context.getResources().getColor(R.color.colorPowerUpInactive));
                    viewHolder.tvPowerUp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lighting_grey_12dp, 0, 0, 0);
                    viewHolder.tvPowerUp.setBackground(context.getResources().getDrawable(R.drawable.bg_powerup_small_white));

                }

            } else {

                ViewUtils.gone(viewHolder.tvPowerUp);
            }

            if (brand.getCashBack().getCashBackPercentage() > 0) {

                viewHolder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(brand.getCashBack().getCashBackPercentage(), 1));
                ViewUtils.visible(viewHolder.tvCashback);

            } else {

                ViewUtils.gone(viewHolder.tvCashback);
            }

        } else {

            ViewUtils.gone(viewHolder.tvCashback);
            ViewUtils.gone(viewHolder.tvPowerUp);

            if (brand.getBrandLink() != null && brand.getBrandLink().getType().equals("jackpot_coupon")){

                // show cashback if jackpot coupon has cashback value
                FuzzieData_ dataManager = FuzzieData_.getInstance_(context);
                Coupon coupon = dataManager.getCouponById(brand.getBrandLink().getCouponId());

                if (coupon != null && coupon.getCashBack() !=  null && coupon.getCashBack().getPercentage() > 0){

                    ViewUtils.visible(viewHolder.tvCashback);
                    viewHolder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(coupon.getCashBack().getPercentage(), 1));

                }

            }
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.ivBackground.setImageDrawable(null);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivBackground;
        protected TextView tvCashback;
        protected TextView tvPowerUp;


        public ViewHolder(View view) {
            super(view);
            this.ivBackground = (ImageView) view.findViewById(R.id.ivBackground);
            this.tvCashback = (TextView) view.findViewById(R.id.tvCashback);
            this.tvPowerUp = (TextView) view.findViewById(R.id.tvPowerUp);
        }
    }
}

