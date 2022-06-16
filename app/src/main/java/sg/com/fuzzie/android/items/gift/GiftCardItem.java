package sg.com.fuzzie.android.items.gift;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.GiftCard;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by nurimanizam on 19/12/16.
 */

public class GiftCardItem extends AbstractItem<GiftCardItem, GiftCardItem.ViewHolder> {

    private GiftCard giftCard;
    private Boolean active;

    public GiftCardItem(GiftCard giftCard) {
        this.giftCard = giftCard;
        this.active = false;
    }

    public GiftCardItem(GiftCard giftCard, Boolean active) {
        this.giftCard = giftCard;
        this.active = active;
    }

    public GiftCard getGiftCard() {
        return giftCard;
    }

    public void setGiftCard(GiftCard giftCard) {
        this.giftCard = giftCard;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_giftcard_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_giftcard;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(GiftCardItem.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();

        viewHolder.tvGiftCardPrice.setText(giftCard.getPrice().getCurrencySymbol() + giftCard.getPrice().getValue());

        if (giftCard.isSoldOut() != null && giftCard.isSoldOut()) {
            viewHolder.tvGiftCardSoldOut.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvGiftCardSoldOut.setVisibility(View.GONE);
        }

        if (active) {
            Drawable activeBackground = ContextCompat.getDrawable(context, R.drawable.bg_giftcard);
            viewHolder.tvGiftCardPrice.setBackground(activeBackground);
            viewHolder.tvGiftCardPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf"));
            viewHolder.tvGiftCardPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            viewHolder.tvGiftCardPrice.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.tvGiftCardPrice.setHeight((int) ViewUtils.convertDpToPixel(40, context));
            viewHolder.tvGiftCardPrice.setWidth((int) ViewUtils.convertDpToPixel(75, context));
        } else {
            Drawable inactiveBackground = ContextCompat.getDrawable(context, R.drawable.bg_giftcard_inactive);
            viewHolder.tvGiftCardPrice.setBackground(inactiveBackground);
            viewHolder.tvGiftCardPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_reg.ttf"));
            viewHolder.tvGiftCardPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            viewHolder.tvGiftCardPrice.setTextColor(Color.parseColor("#B5B5B5"));
            viewHolder.tvGiftCardPrice.setHeight((int) ViewUtils.convertDpToPixel(40, context));
            viewHolder.tvGiftCardPrice.setWidth((int) ViewUtils.convertDpToPixel(75, context));
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvGiftCardPrice;

        protected TextView tvGiftCardSoldOut;

        public ViewHolder(View view) {
            super(view);
            this.tvGiftCardPrice = (TextView) view.findViewById(R.id.tvGiftCardPrice);
            this.tvGiftCardSoldOut = (TextView) view.findViewById(R.id.tvGiftCardSoldOut);
        }
    }

}
