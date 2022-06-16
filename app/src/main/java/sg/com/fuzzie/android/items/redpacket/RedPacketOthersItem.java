package sg.com.fuzzie.android.items.redpacket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.ViewUtils;

/**
 * Created by mac on 2/28/18.
 */

public class RedPacketOthersItem extends AbstractItem<RedPacketOthersItem, RedPacketOthersItem.ViewHolder>{

    private RedPacket redPacket;
    private boolean isMe = false;

    public RedPacketOthersItem(RedPacket redPacket){
        this.redPacket = redPacket;
    }

    public RedPacketOthersItem(RedPacket redPacket, boolean isMe){
        this.redPacket = redPacket;
        this.isMe = isMe;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_red_packet_others;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_red_packet_others;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();

        if (redPacket.getUser().getAvatar() != null && !redPacket.getUser().getAvatar().equals("")){
            Picasso.get()
                    .load(redPacket.getUser().getAvatar())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .transform(new CropCircleTransformation())
                    .fit()
                    .into(holder.ivAvatar);
        }

        if (isMe){

            holder.tvName.setText(redPacket.getUser().getName() + " (you)");

        } else {

            holder.tvName.setText(redPacket.getUser().getName());

        }
        holder.tvCredits.setText(FuzzieUtils.getFormattedValue(redPacket.getValue(), 0.75f));
        holder.tvTicketCount.setText("X" + redPacket.getTicketCount());

        if (redPacket.isUsed() && redPacket.isChampion()){
            holder.ivChampion.setVisibility(View.VISIBLE);
            holder.ivAvatar.setBorderWidth((int)ViewUtils.convertDpToPixel(1, context));
        } else {
            holder.ivChampion.setVisibility(View.INVISIBLE);
            holder.ivAvatar.setBorderWidth(0);
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvName;
        TextView tvCredits;
        TextView tvTicketCount;
        ImageView ivChampion;

        public ViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCredits = (TextView) itemView.findViewById(R.id.tvCredits);
            tvTicketCount = (TextView) itemView.findViewById(R.id.tvTicketCount);
            ivChampion = (ImageView) itemView.findViewById(R.id.ivChampion);
        }
    }

}
