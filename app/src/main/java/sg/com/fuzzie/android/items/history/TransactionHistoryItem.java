package sg.com.fuzzie.android.items.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Order;
import sg.com.fuzzie.android.ui.me.history.OrderDetailsActivity_;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

/**
 * Created by mac on 8/4/17.
 */

public class TransactionHistoryItem extends AbstractItem<TransactionHistoryItem, TransactionHistoryItem.ViewHolder> {

    private Order order;

    public TransactionHistoryItem(Order order){
        this.order = order;
    }

    public Order getOrder(){
        return this.order;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_transaction_history_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_transaction_history;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvOrderDate.setText(TimeUtils.dateTimeForOrderHistory(order.getCreatedAt()));
        holder.tvOrderNumber.setText("#" + order.getOrderNumber());

        holder.tvTotal.setText(FuzzieUtils.getFormattedValue(order.getTotalPrice(), 0.77f));
        holder.tvCashback.setText(FuzzieUtils.getFormattedValue(order.getTotalCashback(), 0.77f));

        if (order.getTotalCashback() > 0){
            holder.llCashback.setVisibility(View.VISIBLE);
        } else {
            holder.llCashback.setVisibility(View.INVISIBLE);
        }

        final Context context = holder.itemView.getContext();
        final FuzzieData dataManager = FuzzieData_.getInstance_(context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.setSelectedOrder(order);
                OrderDetailsActivity_.intent(context).start();
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderDate;
        TextView tvOrderNumber;
        TextView tvTotal;
        TextView tvCashback;
        LinearLayout llCashback;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tvOrderDate = (TextView) itemView.findViewById(R.id.tvOrderDate);
            this.tvOrderNumber = (TextView) itemView.findViewById(R.id.tvOrderNumber);
            this.tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);
            this.tvCashback = (TextView) itemView.findViewById(R.id.tvCashback);
            this.llCashback = (LinearLayout) itemView.findViewById(R.id.llCashback);
        }
    }
}
