package sg.com.fuzzie.android.items.gift;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Gift;

/**
 * Created by mac on 2/12/18.
 */

public class GiftBoxPowerUpCardItem extends AbstractItem<GiftBoxPowerUpCardItem, GiftBoxPowerUpCardItem.ViewHolder>{

    private Gift gift;

    public GiftBoxPowerUpCardItem(Gift gift){
        this.gift = gift;
    }

    public Gift getGift(){
        return this.gift;
    }

    public void setGift(Gift gift){
        this.gift = gift;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_gift_box_power_up_card;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_gift_box_power_up_card;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.ivBackground.setImageResource(R.drawable.power_up_card_placeholder);

        if (gift.getTimeToExpire() == 1){
            holder.tvBrandName.setText("Power Up Card - 1 Hour");
        } else{
            holder.tvBrandName.setText(String.format("Power Up Card - %d Hours", gift.getTimeToExpire()));

        }

        if (gift.isOpened() || gift.isExpired()){
            holder.ivNew.setVisibility(View.GONE);
        } else {
            holder.ivNew.setVisibility(View.VISIBLE);
        }

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {


        ImageView ivBackground;
        ImageView ivNew;
        TextView tvBrandName;

        public ViewHolder(View view) {
            super(view);

            this.ivBackground = (ImageView) view.findViewById(R.id.ivBackground);
            this.ivNew = (ImageView) view.findViewById(R.id.ivNew);
            this.tvBrandName = (TextView) view.findViewById(R.id.tvBrandName);
        }
    }
}
