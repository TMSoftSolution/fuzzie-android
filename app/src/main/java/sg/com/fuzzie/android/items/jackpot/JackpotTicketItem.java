package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;

/**
 * Created by joma on 10/19/17.
 */

public class JackpotTicketItem extends AbstractItem<JackpotTicketItem, JackpotTicketItem.ViewHolder> {

    private List<String> list;

    public JackpotTicketItem(List<String > list){
        this.list = list;
    }

    @Override
    public int getType() {
        return R.id.item_jackpot_ticket_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_jackpot_ticket;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();

        holder.tvTicket.setText(list.get(0));

        if (list.size() > 1){
            holder.tvTicketCount.setVisibility(View.VISIBLE);
            holder.tvTicketCount.setText(String.valueOf(list.size()) + "X");
        } else {
            holder.tvTicketCount.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTicket;
        TextView tvTicketCount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTicket = (TextView) itemView.findViewById(R.id.tvTicket);
            tvTicketCount = (TextView) itemView.findViewById(R.id.tvTicketCount);
        }
    }

}
