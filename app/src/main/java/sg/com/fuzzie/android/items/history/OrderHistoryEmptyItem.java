package sg.com.fuzzie.android.items.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import sg.com.fuzzie.android.R;

/**
 * Created by joma on 10/30/17.
 */

public class OrderHistoryEmptyItem  extends AbstractItem<OrderHistoryEmptyItem, OrderHistoryEmptyItem.ViewHolder> {

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_order_history_empty_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_order_history_empty;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }
}
