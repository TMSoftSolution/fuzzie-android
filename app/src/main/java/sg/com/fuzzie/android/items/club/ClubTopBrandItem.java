package sg.com.fuzzie.android.items.club;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;

public class ClubTopBrandItem extends AbstractItem<ClubTopBrandItem, ClubTopBrandItem.ViewHolder>{

    private Brand brand;

    public ClubTopBrandItem(Brand brand){
        this.brand = brand;
    }

    public Brand getBrand() {
        return brand;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_top_brand_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_top_brand;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Picasso.get()
                .load(brand.getCustomImageUrl())
                .placeholder(R.drawable.brands_placeholder)
                .into(holder.ivBrand);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivBrand;

        public ViewHolder(View itemView) {
            super(itemView);

            ivBrand = itemView.findViewById(R.id.ivBrand);
        }
    }
}
