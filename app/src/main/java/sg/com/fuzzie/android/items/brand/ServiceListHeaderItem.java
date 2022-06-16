package sg.com.fuzzie.android.items.brand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

/**
 * Created by nurimanizam on 24/12/16.
 */

public class ServiceListHeaderItem extends AbstractItem<ServiceListHeaderItem, ServiceListHeaderItem.ViewHolder> {

    String title;

    public ServiceListHeaderItem(String title) {
        this.title = title;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_service_list_header_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_service_list_header;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ServiceListHeaderItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();
        viewHolder.tvHeaderTitle.setText(title);

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvHeaderTitle;

        public ViewHolder(View view) {
            super(view);
            this.tvHeaderTitle = (TextView) view.findViewById(R.id.tvHeaderTitle);
        }
    }
}
