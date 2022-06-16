package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.ui.club.ClubCategoryActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;

public class ClubCategoryItem extends AbstractItem<ClubCategoryItem, ClubCategoryItem.ViewHolder>{

    private BrandType brandType;

    public ClubCategoryItem(BrandType brandType){

        this.brandType = brandType;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_category_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_category;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();

        holder.tvCategory.setText(brandType.getName());

        if (brandType.getRedIcon() != null){

            Picasso.get()
                    .load(brandType.getRedIcon())
                    .into(holder.ivCategory);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FuzzieData_.getInstance_(context).setBrandType(brandType);
                ClubCategoryActivity_.intent(context).start();
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
