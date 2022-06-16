package sg.com.fuzzie.android.items.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.GiftDetail;
import sg.com.fuzzie.android.api.models.Order;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 8/4/17.
 */

public class OrderItem extends AbstractItem<OrderItem, OrderItem.ViewHolder> {

    private GiftDetail giftDetail;
    private boolean showDivider;
    private Order order;

    public OrderItem(GiftDetail giftDetail, boolean showDivider){
        this.giftDetail = giftDetail;
        this.showDivider = showDivider;
    }

    public OrderItem(Order order){
        this.order = order;
    }

    @Override
    public int getType() {
        return R.id.item_order_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_order;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (giftDetail != null){

            holder.tvBrandName.setText(giftDetail.getDisplayName());
            holder.tvPrice.setText(FuzzieUtils.getFormattedValue(giftDetail.getPrice(), 0.77f));


            if (giftDetail.getCashBackPercentage() > 0){

                ViewUtils.visible(holder.tvCashback);
                holder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(giftDetail.getCashBackPercentage(), 1));

            } else {

                ViewUtils.gone(holder.tvCashback);
            }

            if (giftDetail.getPowerUpPercentage() > 0){

                ViewUtils.visible(holder.tvPowerUp);
                holder.tvPowerUp.setText(FuzzieUtils.getFormattedPowerUpPercentage(giftDetail.getPowerUpPercentage(), 1));

            } else {

                ViewUtils.gone(holder.tvPowerUp);
            }

            holder.tvCashbackEarned.setText(FuzzieUtils.getFormattedValue(giftDetail.getCashback(), 0.77f));

            if (showDivider){

                ViewUtils.visible(holder.divider);

            } else {

                ViewUtils.invisible(holder.divider);
            }

        } else if (order != null){

            if (order.getType().equals("TopUp")){

                holder.tvBrandName.setText("Credits top up");

            } else if (order.getType().equals("RedPacket")){

                holder.tvBrandName.setText("Lucky Packet");
            }

            holder.tvPrice.setText(FuzzieUtils.getFormattedValue(order.getTotalPrice(), 0.77f));

            holder.tvPowerUp.setVisibility(View.GONE);

            if (order.getTotalCashback() > 0){

                ViewUtils.visible(holder.tvCashback);
                holder.tvCashback.setText(FuzzieUtils.getFormattedCashbackPercentage(order.getTotalCashback() / order.getTotalPrice() * 100, 1));

                ViewUtils.visible(holder.tvCashbackEarned);
                holder.tvCashbackEarned.setText(FuzzieUtils.getFormattedValue(order.getTotalCashback(), 0.77f));

            } else {

                ViewUtils.gone(holder.tvCashback);
                ViewUtils.gone(holder.tvCashbackEarned);
            }

            ViewUtils.gone(holder.divider);
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBrandName;
        TextView tvPrice;
        TextView tvCashback;
        TextView tvPowerUp;
        TextView tvCashbackEarned;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tvBrandName = (TextView) itemView.findViewById(R.id.tvBrandName);
            this.tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            this.tvCashback = (TextView) itemView.findViewById(R.id.tvCashback);
            this.tvPowerUp = (TextView) itemView.findViewById(R.id.tvPowerUp);
            this.tvCashbackEarned = (TextView) itemView.findViewById(R.id.tvCashbackEarned);
            this.divider = (View) itemView.findViewById(R.id.divider);
        }
    }
}
