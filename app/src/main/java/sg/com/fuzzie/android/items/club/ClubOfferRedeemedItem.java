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
import sg.com.fuzzie.android.api.models.Brand;
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

public class ClubOfferRedeemedItem extends AbstractItem<ClubOfferRedeemedItem, ClubOfferRedeemedItem.ViewHolder>{

    private Offer offer;

    public ClubOfferRedeemedItem(Offer offer){

        this.offer = offer;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_offer_redeemed_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_offer_redeemed;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (offer.getImageUrl() != null && !offer.getImageUrl().equals("")){

            Picasso.get()
                    .load(offer.getImageUrl())
                    .placeholder(R.drawable.categories_placeholder)
                    .into(holder.ivStore);
        }

        Brand brand = FuzzieData_.getInstance_(holder.itemView.getContext()).getBrandById(offer.getBrandId());
        holder.tvBrandName.setText(brand != null ? brand.getName() : "");

        holder.tvOfferName.setText(offer.getName());

        if (offer.getRedeemDetail() != null && offer.getRedeemDetail().getRedeemedAt() != null){
            holder.tvRedeemDate.setText(String.format("Redeemed %s", TimeUtils.dateTimeFormat(offer.getRedeemDetail().getRedeemedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", "d MMM")));
        } else {
            holder.tvRedeemDate.setText("");
        }
        holder.tvPrice.setText(FuzzieUtils.getFormattedValue(offer.getEstSave(), 1));

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStore;
        TextView tvOfferName;
        TextView tvBrandName;
        TextView tvRedeemDate;
        TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            ivStore = itemView.findViewById(R.id.ivStore);
            tvOfferName = itemView.findViewById(R.id.tvOfferName);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvRedeemDate = itemView.findViewById(R.id.tvRedeemDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
