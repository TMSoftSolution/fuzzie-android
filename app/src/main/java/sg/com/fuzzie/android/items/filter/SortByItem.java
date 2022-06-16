package sg.com.fuzzie.android.items.filter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

/**
 * Created by mac on 9/3/17.
 */

public class SortByItem extends AbstractItem<SortByItem, SortByItem.ViewHolder> {

    private String sort;
    private boolean checked;

    public SortByItem(String sort, boolean checked){
        this.sort = sort;
        this.checked = checked;
    }

    @Override
    public int getType() {
        return R.id.id_sort_by;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_sort_by;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvSort.setText(sort);
        holder.updateCheck(checked);

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvSort;
        ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSort = (TextView) itemView.findViewById(R.id.tvSort);
            ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
        }

        void updateCheck(boolean checked){
            if (checked){
                ivCheck.setVisibility(View.VISIBLE);
            } else {
                ivCheck.setVisibility(View.INVISIBLE);
            }
        }

    }

}
