package sg.com.fuzzie.android.items.redpacket;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.utils.FuzzieUtils;

/**
 * Created by mac on 2/27/18.
 */

public class RedPacketTotalLuckItem extends StatelessSection {

    private double credits;
    private int ticketCount;

    public  RedPacketTotalLuckItem(double credits, int ticketCount){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_red_packet_total_luck)
                .build());
        this.credits = credits;
        this.ticketCount = ticketCount;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.tvCredits.setText(FuzzieUtils.getFormattedValue(credits, 0.75f));
        viewHolder.tvTicketCount.setText("X" + ticketCount);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCredits;
        TextView tvTicketCount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCredits = (TextView) itemView.findViewById(R.id.tvCredits);
            tvTicketCount = (TextView) itemView.findViewById(R.id.tvTicketCount);

        }
    }
}
