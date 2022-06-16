package sg.com.fuzzie.android.items.payment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;


import java.util.List;

import sg.com.fuzzie.android.R;


/**
 * Created by nurimanizam on 23/6/17.
 */

public class AddCreditCardItem extends AbstractItem<AddCreditCardItem, AddCreditCardItem.ViewHolder> {

    public AddCreditCardItem() {
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_addcreditcard;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_addcreditcard;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(final AddCreditCardItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

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

