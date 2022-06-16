package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Coupon;
import sg.com.fuzzie.android.ui.jackpot.PowerUpCouponActivity_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by movdev on 3/25/18.
 */

public class PowerUpItem extends AbstractItem<PowerUpItem, PowerUpItem.ViewHolder> {

    private Context context;
    private Coupon coupon;

    public PowerUpItem(Coupon coupon){
        this.coupon = coupon;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_power_up_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_power_up;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();

        holder.tvCouponName.setText(coupon.getPowerUpPack().getName());

        if (coupon.getPowerUpPack().getNumberOfCounts() == 1){
            holder.tvCardCount.setText("1x Power Up Card");
        } else {
            holder.tvCardCount.setText(String.format("%dx Power Up Cards", coupon.getPowerUpPack().getNumberOfCounts()));
        }

        if (coupon.getTicketCount() == 1){
            holder.tvTicketCount.setText("1x Free Jackpot Ticket");
        } else {
            holder.tvTicketCount.setText(String.format("%dx Free Jackpot Tickets", coupon.getTicketCount()));
        }

        double price = Double.parseDouble(coupon.getPrice().getValue());
        holder.tvPrice.setText(FuzzieUtils.getFormattedValue(price, 0.625f));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerUpCouponActivity_.intent(context).couponId(coupon.getId()).start();
            }
        });

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPrice;
        TextView tvCouponName;
        TextView tvTicketCount;
        TextView tvCardCount;

        public ViewHolder(View view) {
            super(view);

            tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            tvCouponName = (TextView) view.findViewById(R.id.tvCouponName);
            tvTicketCount = (TextView) view.findViewById(R.id.tvTicketCount);
            tvCardCount = (TextView) view.findViewById(R.id.tvCardCount);
        }
    }

}
