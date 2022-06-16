package sg.com.fuzzie.android.items.redpacket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.RedPacket;
import sg.com.fuzzie.android.ui.redpacket.RedPacketOpenActivity_;
import sg.com.fuzzie.android.ui.redpacket.RedPacketReceiveDetailActivity_;
import sg.com.fuzzie.android.utils.TimeUtils;

import static sg.com.fuzzie.android.utils.Constants.REQUEST_RED_PACKET_OPEN;

/**
 * Created by mac on 2/27/18.
 */

public class RedPacketReceiveItem extends StatelessSection {

    private String headerTitle;
    private List<RedPacket> redPackets;

    public RedPacketReceiveItem(List<RedPacket> redPackets, String headerTitle){

        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_red_packet_receive)
                .headerResourceId(R.layout.item_red_packet_history_section)
                .build());

        this.redPackets = redPackets;
        this.headerTitle = headerTitle;
    }


    @Override
    public int getContentItemsTotal() {
        return redPackets.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;
        final Context context = viewHolder.itemView.getContext();

        final RedPacket redPacket = redPackets.get(position);

        if (redPacket.getSender().getAvatar() != null && !redPacket.getSender().getAvatar().equals("")){
            Picasso.get()
                    .load(redPacket.getSender().getAvatar())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .transform(new CropCircleTransformation())
                    .fit()
                    .into(viewHolder.ivAvatar);
        }

        viewHolder.tvName.setText(redPacket.getSender().getName());

        if (redPacket.isUsed()){

            viewHolder.redMark.setVisibility(View.INVISIBLE);
            viewHolder.btnOpen.setVisibility(View.GONE);
            viewHolder.ivArrow.setVisibility(View.VISIBLE);

        } else {

            viewHolder.redMark.setVisibility(View.VISIBLE);
            viewHolder.btnOpen.setVisibility(View.VISIBLE);
            viewHolder.ivArrow.setVisibility(View.GONE);

        }

        TimeUtils timeUtils = new TimeUtils();
        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        String createdTime = redPacket.getCreatedTime();
        if (createdTime != null && !createdTime.equals("")){
            viewHolder.tvDate.setText("Received " + timeUtils.timeAgo(parser.parseDateTime(createdTime).getMillis()));

        } else {
            viewHolder.tvDate.setText("");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!redPacket.isUsed()){
                    RedPacketOpenActivity_.intent(context).redPacketId(redPacket.getId()).startForResult(REQUEST_RED_PACKET_OPEN);
                } else {
                    RedPacketReceiveDetailActivity_.intent(context).redPacketId(redPacket.getId()).start();
                }
            }
        });

        viewHolder.btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedPacketOpenActivity_.intent(context).redPacketId(redPacket.getId()).startForResult(REQUEST_RED_PACKET_OPEN);
            }
        });


    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {

        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(headerTitle);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        HeaderViewHolder(View view) {

            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView tvName;
        TextView tvDate;
        Button btnOpen;
        View redMark;
        ImageView ivArrow;

        public ViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            btnOpen = (Button) itemView.findViewById(R.id.btnOpen);
            redMark = itemView.findViewById(R.id.redMark);
            ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
        }
    }
}
