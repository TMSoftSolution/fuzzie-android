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
import sg.com.fuzzie.android.api.models.Offer;
import sg.com.fuzzie.android.api.models.OfferType;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

public class ClubOfferItem extends AbstractItem<ClubOfferItem, ClubOfferItem.ViewHolder>{

    private Offer offer;

    public ClubOfferItem(Offer offer){
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_club_offer_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_offer;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (offer == null) return;

        Picasso.get()
                .load(offer.getImageUrl())
                .placeholder(R.drawable.club_offer_placeholder)
                .into(holder.ivStore);

        holder.tvOfferName.setText(offer.getName());

        FuzzieData_ dataManager = FuzzieData_.getInstance_(holder.itemView.getContext());
        BrandType brandType = dataManager.getBrandType(offer.getBrandTypeId());

        if (brandType != null){

            OfferType offerType =  dataManager.getOfferType(offer.getOfferTypeId(), brandType.getOfferTypes());

            if (offerType != null){

                holder.tvOfferType.setText(offerType.getName());

            }
        }

        if (offer.getRedeemDetail() != null){

            if (offer.isOnline() && offer.getRedeemDetail().getRedeemTimerStartedAt() != null && TimeUtils.isInRedeem(offer.getRedeemDetail().getRedeemTimerStartedAt())){

                ViewUtils.visible(holder.tvSaving);
                ViewUtils.gone(holder.tvRedeemDate);
                ViewUtils.gone(holder.vShadow);

                holder.tvSaving.setText(String.format("Estimated savings: S$%.2f", offer.getEstSave()).replace(".00", ""));

            } else {

                ViewUtils.gone(holder.tvSaving);
                ViewUtils.visible(holder.tvRedeemDate);
                ViewUtils.visible(holder.vShadow);

                holder.tvRedeemDate.setText(String.format("Redeemed %s", TimeUtils.dateTimeFormat(offer.getRedeemDetail().getRedeemedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", "d MMM")));

            }



        } else {

            ViewUtils.visible(holder.tvSaving);
            ViewUtils.gone(holder.tvRedeemDate);
            ViewUtils.gone(holder.vShadow);

            holder.tvSaving.setText(String.format("Estimated savings: S$%.2f", offer.getEstSave()).replace(".00", ""));
        }


    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStore;
        TextView tvOfferName;
        TextView tvOfferType;
        TextView tvSaving;
        TextView tvRedeemDate;
        View vShadow;

        public ViewHolder(View itemView) {
            super(itemView);

            ivStore = itemView.findViewById(R.id.ivStore);
            tvOfferName = itemView.findViewById(R.id.tvOfferName);
            tvOfferType = itemView.findViewById(R.id.tvOfferType);
            tvSaving = itemView.findViewById(R.id.tvSaving);
            tvRedeemDate = itemView.findViewById(R.id.tvRedeemDate);
            vShadow = itemView.findViewById(R.id.vShadow);
        }
    }
}
