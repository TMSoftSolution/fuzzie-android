package sg.com.fuzzie.android.items.filter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.utils.FuzzieData_;

/**
 * Created by mac on 9/3/17.
 */

public class CategoryRefineItem extends AbstractItem<CategoryRefineItem, CategoryRefineItem.ViewHolder>{

    private Category category;
    private ViewHolder viewHolder;

    public CategoryRefineItem(Category category){
        this.category = category;
    }

    public Category getCategory(){
        return this.category;
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    @Override
    public int getType() {
        return R.id.id_category_refine;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_refine_category;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        viewHolder = holder;

        holder.tvCategory.setText(category.getName());

        Context context = holder.itemView.getContext();
        FuzzieData_ dataManager = FuzzieData_.getInstance_(context);
        holder.updateCheck(dataManager.selectedBrandsCategoryIds.contains(category.getId()));

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvCategory;
        ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
            ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
        }

        public void updateCheck(boolean checked){
            if (checked){
                ivCheck.setVisibility(View.VISIBLE);
            } else {
                ivCheck.setVisibility(View.INVISIBLE);
            }
        }

    }

}
