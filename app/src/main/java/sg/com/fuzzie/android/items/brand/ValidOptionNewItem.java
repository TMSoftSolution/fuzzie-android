package sg.com.fuzzie.android.items.brand;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

/**
 * Created by joma on 10/14/17.
 */

public class ValidOptionNewItem extends AbstractItem<ValidOptionNewItem, ValidOptionNewItem.ViewHolder> {

    String validOption;

    public ValidOptionNewItem(String validOption){
        this.validOption = validOption;
    }


    @Override
    public int getType() {
        return R.id.item_valid_option_new_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_valid_option_new;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ValidOptionNewItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        viewHolder.tvOption.setText(validOption);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOption;

        public ViewHolder(View view) {
            super(view);
            this.tvOption = (TextView) view.findViewById(R.id.tvOption);

        }
    }

}
