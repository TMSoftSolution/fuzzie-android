package sg.com.fuzzie.android.items.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 8/9/17.
 */

public class OrderHistoryHeaderItem extends AbstractItem<OrderHistoryHeaderItem, OrderHistoryHeaderItem.ViewHolder> {

    public OrderHistoryHeaderItem(){

    }

    @Override
    public int getType() {
        return R.id.item_order_history_header_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_order_history_header;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();
        CurrentUser currentUser = CurrentUser_.getInstance_(context);

        holder.tvTotalCashback.setText(FuzzieUtils.getFormattedValue(context, currentUser.getCurrentUser().getCashbackEarned(), 0.5f));

        Picasso.get()
                .load(R.drawable.history_background)
                .placeholder(R.drawable.history_background)
                .into(holder.ivBackground);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBackground;
        TextView tvTotalCashback;

        public ViewHolder(View view) {
            super(view);

            ivBackground = (ImageView) view.findViewById(R.id.ivBackground);
            tvTotalCashback = (TextView) view.findViewById(R.id.tvTotalCashback);
        }
    }

}