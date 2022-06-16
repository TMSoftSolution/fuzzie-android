package sg.com.fuzzie.android.items.me;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

/**
 * Created by mac on 5/18/17.
 */

public class HeaderItem extends AbstractItem<HeaderItem, HeaderItem.ViewHolder> {


    public HeaderItem(){

    }

    @Override
    public int getType() {
        return R.id.item_header_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_header;
    }

    @Override
    public void bindView(HeaderItem.ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }

}

