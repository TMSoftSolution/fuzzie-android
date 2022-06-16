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
import sg.com.fuzzie.android.api.models.BrandType;

public class ClubFilterItem extends AbstractItem<ClubFilterItem, ClubFilterItem.ViewHolder>{

    private BrandType brandType;
    private boolean checked;

    public ClubFilterItem(BrandType brandType){
        this.brandType = brandType;
    }

    public ClubFilterItem(BrandType brandType, boolean checked){
        this.brandType = brandType;
        this.checked = checked;
    }

    public BrandType getBrandType() {
        return brandType;
    }

    public boolean isChecked(){
        return checked;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_filter_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_filter;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (checked){

            holder.llCategory.setBackgroundResource(R.drawable.bg_club_filter_category_icon_selected);

            if (brandType.getWhiteIcon() != null){

                Picasso.get()
                        .load(brandType.getWhiteIcon())
                        .into(holder.ivCategory);
                holder.ivCategory.setAlpha(1.0f);

            }


        } else {

            holder.llCategory.setBackgroundResource(R.drawable.bg_club_fliter_category_icon);

            if (brandType.getBlackIcon() != null){

                Picasso.get()
                        .load(brandType.getBlackIcon())
                        .into(holder.ivCategory);
                holder.ivCategory.setAlpha(0.4f);

            }


        }

        holder.tvCategory.setText(brandType.getName());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        View llCategory;
        ImageView ivCategory;
        TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            llCategory = itemView.findViewById(R.id.llCategory);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
