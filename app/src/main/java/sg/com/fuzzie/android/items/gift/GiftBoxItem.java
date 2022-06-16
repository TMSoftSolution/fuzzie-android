package sg.com.fuzzie.android.items.gift;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Gift;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

/**
 * Created by nurimanizam on 16/12/16.
 */

public class GiftBoxItem extends AbstractItem<GiftBoxItem, GiftBoxItem.ViewHolder> {

    private Context context;

    private Gift gift;

    public GiftBoxItem(Gift gift) {
        this.gift = gift;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.item_giftbox_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_giftbox;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        context = viewHolder.itemView.getContext();

        if (gift.getImageUrl() != null && !gift.getImageUrl().equals("")){
            Picasso.get()
                    .load(gift.getImageUrl())
                    .placeholder(R.drawable.brands_placeholder)
                    .into(viewHolder.ivBackground);
        }

        if (gift.getGiftCard() != null) {
            viewHolder.tvBrandName.setText(gift.getGiftCard().getDisplayName());
            viewHolder.tvPrice.setText(FuzzieUtils.getFormattedValue(gift.getGiftCard().getDiscountedPrice(), 0.75f));
        } else if (gift.getService() != null) {
            viewHolder.tvBrandName.setText(gift.getService().getDisplayName());
            viewHolder.tvPrice.setText(FuzzieUtils.getFormattedValue(gift.getService().getDiscountedPrice(), 0.75f));
        }

        viewHolder.ivNew.setVisibility(View.GONE);
        viewHolder.ivGift.setVisibility(View.GONE);

        TimeUtils timeUtils = new TimeUtils();
        DateTimeFormatter parser = ISODateTimeFormat.dateTime();

       if (gift.isGifted() != null && gift.isGifted()){

           viewHolder.rlSenderInfo.setVisibility(View.VISIBLE);

           if (gift.isSent() != null && !gift.isSent()){

               viewHolder.tvSender.setText("Received from: ");
               viewHolder.tvSenderName.setText(gift.getSender().getName());
               if (gift.isOpened() != null && gift.isOpened()){
                   viewHolder.ivGiftLabel.setVisibility(View.GONE);
               } else {
                   viewHolder.ivGiftLabel.setVisibility(View.VISIBLE);
               }
               viewHolder.tvSenderState.setVisibility(View.GONE);

               if (gift.getRedeemedTime() != null){
                   if (gift.getRedeemTimerStartedAt() != null){
                       viewHolder.tvDate.setText("Redeemed " + timeUtils.timeAgo(parser.parseDateTime(gift.getRedeemTimerStartedAt()).getMillis()));
                       viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
                   } else {
                       viewHolder.tvDate.setText("Redeemed " + timeUtils.timeAgo(parser.parseDateTime(gift.getRedeemedTime()).getMillis()));
                       viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
                   }

               } else {
                   if (gift.getRedeemTimerStartedAt() != null){
                       viewHolder.tvDate.setText("Redeeming...");
                       viewHolder.tvDate.setTextColor(Color.parseColor("#FA3E3F"));

                   } else {
                       viewHolder.tvDate.setText("Bought " + timeUtils.timeAgo(parser.parseDateTime(gift.getSentTime()).getMillis()));
                       viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
                   }

               }

           } else {
               viewHolder.tvSender.setText("Send to: ");
               viewHolder.tvSenderName.setText(gift.getReceiver().getName());
               viewHolder.ivGiftLabel.setVisibility(View.GONE);
               viewHolder.tvSenderState.setVisibility(View.VISIBLE);
               if (gift.isDelivered() != null && gift.isDelivered()){
                   viewHolder.tvSenderState.setText("SENT");
                   viewHolder.tvSenderState.setBackground(context.getDrawable(R.drawable.bg_primary_radius_4dp));
               } else {
                   viewHolder.tvSenderState.setText("NOT SENT YET");
                   viewHolder.tvSenderState.setBackground(context.getDrawable(R.drawable.bg_lolly_radius_4dp));
               }

               viewHolder.tvDate.setText("Bought " + timeUtils.timeAgo(parser.parseDateTime(gift.getSentTime()).getMillis()));
               viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
           }

       } else {
           viewHolder.rlSenderInfo.setVisibility(View.GONE);
           viewHolder.ivGiftLabel.setVisibility(View.GONE);

           if (gift.getRedeemedTime() != null){
               if (gift.getRedeemTimerStartedAt() != null){
                   viewHolder.tvDate.setText("Redeemed " + timeUtils.timeAgo(parser.parseDateTime(gift.getRedeemTimerStartedAt()).getMillis()));
                   viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
               } else {
                   viewHolder.tvDate.setText("Redeemed " + timeUtils.timeAgo(parser.parseDateTime(gift.getRedeemedTime()).getMillis()));
                   viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
               }

           } else {
               if (gift.getRedeemTimerStartedAt() != null){
                   viewHolder.tvDate.setText("Redeeming...");
                   viewHolder.tvDate.setTextColor(Color.parseColor("#FA3E3F"));

               } else {
                   viewHolder.tvDate.setText("Bought " + timeUtils.timeAgo(parser.parseDateTime(gift.getSentTime()).getMillis()));
                   viewHolder.tvDate.setTextColor(Color.parseColor("#ADADAD"));
                   if (gift.isOpened() != null && !gift.isOpened() && !gift.isExpired()){
                       viewHolder.ivNew.setVisibility(View.VISIBLE);
                   }

               }

           }
       }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.ivBackground.setImageDrawable(null);
        holder.tvBrandName.setText(null);
        holder.tvPrice.setText(null);
        holder.tvDate.setText(null);
    }


    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlSenderInfo;
        TextView tvSender;
        TextView tvSenderName;
        TextView tvSenderState;
        ImageView ivGiftLabel;

        ImageView ivBackground;
        ImageView ivNew;
        ImageView ivGift;
        TextView tvBrandName;
        TextView tvPrice;
        TextView tvDate;

        public ViewHolder(View view) {
            super(view);

            this.rlSenderInfo = (RelativeLayout) view.findViewById(R.id.rlSenderInfo);
            this.tvSender = (TextView) view.findViewById(R.id.tvSender);
            this.tvSenderName = (TextView) view.findViewById(R.id.tvSenderName);
            this.tvSenderState = (TextView) view.findViewById(R.id.tvSenderState);
            this.ivGiftLabel = (ImageView) view.findViewById(R.id.ivGiftLabel);

            this.ivBackground = (ImageView) view.findViewById(R.id.ivBackground);
            this.ivNew = (ImageView) view.findViewById(R.id.ivNew);
            this.ivGift = (ImageView) view.findViewById(R.id.ivGift);
            this.tvBrandName = (TextView) view.findViewById(R.id.tvBrandName);
            this.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            this.tvDate = (TextView) view.findViewById(R.id.tvDate);
        }
    }

}
