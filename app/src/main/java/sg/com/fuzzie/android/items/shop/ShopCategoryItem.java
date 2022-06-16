package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by nurimanizam on 16/12/16.
 */

public class ShopCategoryItem extends AbstractItem<ShopCategoryItem, ShopCategoryItem.ViewHolder> {

    Category category;

    public ShopCategoryItem(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_category_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_home_category;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ShopCategoryItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();

        int cornerRadius = (int) ViewUtils.convertDpToPixel(3, context);
        Picasso.get()
                .load(category.getPicture())
                .placeholder(R.drawable.categories_placeholder)
                .transform(new RoundedCornersTransformation(cornerRadius, 0, RoundedCornersTransformation.CornerType.ALL))
                .into(viewHolder.ivBackground);

        if (category.getName() == null || category.getName().equals("")){

            viewHolder.tvName.setText("");
            viewHolder.ivGradient.setVisibility(View.INVISIBLE);

        } else {

            viewHolder.tvName.setText(category.getName());
            viewHolder.ivGradient.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.ivBackground.setImageDrawable(null);
        holder.tvName.setText(null);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected RoundedImageView ivBackground;
        protected TextView tvName;
        protected ImageView ivGradient;

        public ViewHolder(View view) {
            super(view);
            this.ivBackground = (RoundedImageView) view.findViewById(R.id.ivBackground);
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.ivGradient = (ImageView) view.findViewById(R.id.ivGradient);
        }
    }
}
