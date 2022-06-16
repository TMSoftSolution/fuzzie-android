package sg.com.fuzzie.android.items.brand;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by nurimanizam on 26/12/16.
 */

public class BrandSearchItem  extends AbstractItem<BrandSearchItem, BrandSearchItem.ViewHolder> {

    Home home;
    Brand brand;

    public BrandSearchItem(Brand brand, Home home) {
        this.home = home;
        this.brand = brand;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_brand_search_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_brand_search;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(BrandSearchItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();

        viewHolder.tvBrandName.setText(brand.getName());

        if (home != null) {
            Category subCategory = FuzzieUtils.getSubCategory(home.getSubCategories(),brand.getSubCategoryId());
            viewHolder.tvCategory.setText(subCategory.getName());
        } else {
            viewHolder.tvCategory.setText("");
        }

        Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryRedIcon(brand.getSubCategoryId()));
        viewHolder.ivCategory.setImageDrawable(subCategoryIcon);

    }

    @Override
    public void unbindView(ViewHolder viewHolder){
        super.unbindView(viewHolder);

        viewHolder.ivCategory.setImageDrawable(null);
        viewHolder.tvBrandName.setText(null);
        viewHolder.tvCategory.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivCategory;
        protected TextView tvBrandName;
        protected TextView tvCategory;

        public ViewHolder(View view) {
            super(view);
            this.ivCategory = (ImageView) view.findViewById(R.id.ivCategory);
            this.tvBrandName = (TextView) view.findViewById(R.id.tvBrandName);
            this.tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        }
    }

}
