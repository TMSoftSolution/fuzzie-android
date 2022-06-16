package sg.com.fuzzie.android.items.brand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;


/**
 * Created by mac on 4/27/17.
 */

public class CashbackItem extends AbstractItem<CashbackItem, CashbackItem.ViewHolder> {

    Brand brand;

    public CashbackItem(Brand brand){
        this.brand = brand;
    }

    @Override
    public int getType() {
        return R.id.item_cashback_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_cashback;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(CashbackItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();
        CurrentUser_ currentUser = CurrentUser_.getInstance_(context);

        if (brand.getCashBack().getPercentage() > 0) {

            if (brand.getCashBack().getPowerUpPercentage() > 0) {

                viewHolder.tvPowerUp.setText(FuzzieUtils.getFormattedPowerUpPercentage(brand.getCashBack().getPowerUpPercentage(), 1));
                ViewUtils.visible(viewHolder.tvPowerUp);

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

                viewHolder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(brand.getCashBack().getCashBackPercentage(), 1));
                ViewUtils.visible(viewHolder.tvCashback);

            } else {

                ViewUtils.gone(viewHolder.tvCashback);
            }

        } else {

            ViewUtils.gone(viewHolder.tvCashback);
            ViewUtils.gone(viewHolder.tvPowerUp);

        }


    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvCashback;
        protected TextView tvPowerUp;

        public ViewHolder(View view) {
            super(view);
            this.tvCashback = (TextView) view.findViewById(R.id.tvCashback);
            this.tvPowerUp = (TextView) view.findViewById(R.id.tvPowerUp);
        }
    }

}
