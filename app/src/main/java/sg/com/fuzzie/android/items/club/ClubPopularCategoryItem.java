package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.ui.club.ClubSubCategoryActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;

public class ClubPopularCategoryItem extends AbstractItem<ClubPopularCategoryItem, ClubPopularCategoryItem.ViewHolder>{

    private Category category;

    public ClubPopularCategoryItem(Category category){
        this.category = category;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_popular_category_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_popular_category;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (category.getPicture() != null && !category.getPicture().equals("")){

            Picasso.get()
                    .load(category.getPicture())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(holder.ivCategory);
        }

        holder.tvCategory.setText(category.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FuzzieData_ dataManager = FuzzieData_.getInstance_(holder.itemView.getContext());
                dataManager.categoryFilters = new ArrayList<>();
                dataManager.categoryFilters.add(category.getName());

                ClubSubCategoryActivity_.intent(holder.itemView.getContext()).titleExtra(dataManager.getBrandType().getName()).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivCategory;
        TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
