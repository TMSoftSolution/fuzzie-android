package sg.com.fuzzie.android.items.filter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 9/3/17.
 */

public class BrandListRefineItem extends AbstractItem<BrandListRefineItem, BrandListRefineItem.ViewHolder> {

    private Category category;
    private ViewHolder viewHolder;

    public BrandListRefineItem(Category category){
        this.category = category;
    }

    public Category getCategory(){
        return this.category;
    }

    public ViewHolder getViewHolder(){
        return viewHolder;
    }

    @Override
    public int getType() {
        return R.id.id_brand_list_refine;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_brand_list_refine;
    }

    @Override
    public void bindView(final BrandListRefineItem.ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        viewHolder = holder;
        Context context = holder.itemView.getContext();
        FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        Picasso.get()
                .load(FuzzieUtils.getSubCategoryRedIcon(category.getId()))
                .placeholder(FuzzieUtils.getSubCategoryRedIcon(category.getId()))
                .into(holder.ivSubCategory);

        holder.tvSubCategory.setText(category.getName());

        holder.updateCheck(dataManager.selectedBrandsSubCategoryIds.contains(category.getId()));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivSubCategory;
        TextView tvSubCategory;
        ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);

            ivSubCategory = (ImageView) itemView.findViewById(R.id.ivSubCategory);
            tvSubCategory = (TextView) itemView.findViewById(R.id.tvSubCategory);
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
