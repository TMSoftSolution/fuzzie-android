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

public class ClubStoreItem extends AbstractItem<ClubStoreItem, ClubStoreItem.ViewHolder>{

    private ClubStore clubStore;
    private Brand brand;
    private BrandType brandType;
    private Context context;

    public ClubStoreItem(ClubStore clubStore, Context context){

        this.clubStore = clubStore;
        this.context = context;

        FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        brand = dataManager.getBrandById(clubStore.getBrandId());
        brandType = dataManager.getBrandType(clubStore.getBrandTypeId());
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_store_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_store;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (brand != null && brand.getBackgroundImageUrl() != null && !brand.getBackgroundImageUrl().equals("")){

            Picasso.get()
                    .load(brand.getBackgroundImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(holder.ivStore);

        }

        holder.tvBrandName.setText(clubStore.getBrandName());
        holder.tvStoreName.setText(clubStore.getStoreName());
        holder.tvDistance.setText(clubStore.getDistance() != null && clubStore.getDistance() != 0 ? String.format("%.2f km", clubStore.getDistance()) : "");

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
                FuzzieData_.getInstance_(context).clubStore = clubStore;
                ClubStoreActivity_.intent(context).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStore;
        TextView tvBrandName;
        TextView tvStoreName;
        TextView tvDistance;
        ImageView ivCategory;
        TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            ivStore = itemView.findViewById(R.id.ivStore);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
