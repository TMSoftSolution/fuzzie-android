package sg.com.fuzzie.android.items.redpacket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.RedPacketBundle;
import sg.com.fuzzie.android.ui.redpacket.RedPacketSentDetailActivity_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

/**
 * Created by mac on 2/27/18.
 */

public class RedPacketSentItem extends StatelessSection {

    private String headerTitle;
    private List<RedPacketBundle> redPacketBundles;

    public RedPacketSentItem(List<RedPacketBundle> redPacketBundles, String headerTitle){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_red_packet_sent)
                .headerResourceId(R.layout.item_red_packet_history_section)
                .build());
        this.redPacketBundles = redPacketBundles;
        this.headerTitle = headerTitle;
    }

    @Override
    public int getContentItemsTotal() {
        return this.redPacketBundles.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;
        final Context context = viewHolder.itemView.getContext();

        final RedPacketBundle redPacketBundle = redPacketBundles.get(position);

        int count = redPacketBundle.getRedPacketsCount();
        if (count > 1){
            viewHolder.tvCount.setText(String.format("%d Lucky Packets", count));
        } else {
            viewHolder.tvCount.setText(String.format("%d Lucky Packet", count));
        }

        TimeUtils timeUtils = new TimeUtils();
        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        String createdTime = redPacketBundle.getCreatedTime();
        if (createdTime != null && !createdTime.equals("")){
            viewHolder.tvDate.setText("Bought " + timeUtils.timeAgo(parser.parseDateTime(createdTime).getMillis()));

        } else {
            viewHolder.tvDate.setText("");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedPacketSentDetailActivity_.intent(context).bundleId(redPacketBundle.getId()).start();
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

        private final TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCount;
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCount = (TextView) itemView.findViewById(R.id.tvCount);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
