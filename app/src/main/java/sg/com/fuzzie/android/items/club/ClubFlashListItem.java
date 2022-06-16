package sg.com.fuzzie.android.items.club;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.ui.club.ClubOfferActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;

public class ClubFlashListItem extends AbstractItem<ClubFlashListItem, ClubFlashListItem.ViewHolder>{

    private Offer offer;

    public ClubFlashListItem(Offer offer){
        this.offer = offer;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_flash_list_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_flash_list;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        final FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        Picasso.get()
                .load(offer.getImageUrl())
                .placeholder(R.drawable.club_offer_placeholder)
                .into(holder.ivOffer);

        Brand brand = dataManager.getBrandById(offer.getBrandId());

        if (brand != null){

            Drawable subCategoryIcon = ContextCompat.getDrawable(context, FuzzieUtils.getSubCategoryWhiteIcon(brand.getSubCategoryId()));
            holder.ivCategory.setImageDrawable(subCategoryIcon);

            holder.tvCategory.setText(brand.getName());

        } else {

            holder.ivCategory.setImageDrawable(null);
            holder.tvCategory.setText("");

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.offer = offer;
                ClubOfferActivity_.intent(context).isFlashSaleOffer(true).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivOffer;
        ImageView ivCategory;
        TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            ivOffer = itemView.findViewById(R.id.ivOffer);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
