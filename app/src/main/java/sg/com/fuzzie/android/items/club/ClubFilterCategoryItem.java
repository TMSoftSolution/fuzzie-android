package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.utils.ViewUtils;

public class ClubFilterCategoryItem extends AbstractItem<ClubFilterCategoryItem, ClubFilterCategoryItem.ViewHolder>{

    private boolean showEffect = false;
    private Category category;

    public ClubFilterCategoryItem(Category category){

        this.category = category;
    }

    public ClubFilterCategoryItem(Category category, boolean showEffect){

        this.category = category;
        this.showEffect = showEffect;
    }


    public void setShowEffect(boolean showEffect){
        this.showEffect = showEffect;
    }

    public boolean isShowEffect(){
        return this.showEffect;
    }

    public Category getCategory() {
        return category;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_filter_category_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_filter_category;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (showEffect) {

            ViewUtils.visible(holder.vEffect);

        } else {

            ViewUtils.gone(holder.vEffect);

        }

        if (category.getPicture() != null && !category.getPicture().equals("")){

            Picasso.get()
                    .load(category.getPicture())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(holder.ivCategory);
        }

        holder.tvCategory.setText(category.getName());

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivCategory;
        TextView tvCategory;
        View vEffect;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            vEffect = itemView.findViewById(R.id.vEffect);
        }
    }
}
