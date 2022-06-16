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
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.BrandType;
import sg.com.fuzzie.android.api.models.ClubStore;
import sg.com.fuzzie.android.ui.club.ClubStoreActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;

public class ClubStoreListItem extends AbstractItem<ClubStoreListItem, ClubStoreListItem.ViewHolder>{

    private ClubStore clubStore;

    public ClubStoreListItem(ClubStore clubStore){
        this.clubStore = clubStore;
    }

    public ClubStore getClubStore() {
        return clubStore;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_store_list_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_store_list;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();
        final FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        Brand brand = dataManager.getBrandById(clubStore.getBrandId());

        if (brand != null && brand.getBackgroundImageUrl() != null && !brand.getBackgroundImageUrl().equals("")){

            Picasso.get()
                    .load(brand.getBackgroundImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(holder.ivStore);
        }

        holder.tvBrandName.setText(clubStore.getBrandName());
        holder.tvStoreName.setText(clubStore.getStoreName());
        holder.tvDistance.setText(clubStore.getDistance() != null && clubStore.getDistance() != 0 ? String.format("%.2f km", clubStore.getDistance()) : "");

        BrandType brandType = dataManager.getBrandType(clubStore.getBrandTypeId());

        if (brandType != null && brandType.getBlackIcon() != null){

            Picasso.get()
                    .load(brandType.getBlackIcon())
                    .into(holder.ivCategory);
        }

        if (clubStore.getFilterComponents().size() == 0){

            holder.tvCategory.setText("");

        } else {

            int count = Math.min(2, clubStore.getFilterComponents().size());
            StringBuilder str = new StringBuilder("");
            for (int i = 0 ; i < count ; i ++){

                str.append(clubStore.getFilterComponents().get(i).getName()).append(" ");
            }

            holder.tvCategory.setText(str.toString());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.clubStore = clubStore;
                ClubStoreActivity_.intent(holder.itemView.getContext()).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStore;
        TextView tvBrandName;
        TextView tvStoreName;
        TextView tvCategory;
        TextView tvDistance;
        ImageView ivCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            ivStore = itemView.findViewById(R.id.ivStore);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivCategory = itemView.findViewById(R.id.ivCategory);
        }
    }
}
