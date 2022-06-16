package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
/**
 * Created by joma on 10/15/17.
 */

public class TicketItem extends AbstractItem<TicketItem, TicketItem.ViewHolder>{

    private List<String> digit;
    private boolean won;

    public TicketItem(List<String> digit){
        this.digit = digit;
    }

    public TicketItem(List<String> digit, boolean won){
        this.digit = digit;
        this.won = won;
    }


    @Override
    public int getType() {
        return R.id.item_ticket_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_ticket;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();

        if (digit.size() > 1){
            holder.tvTicketCount.setVisibility(View.VISIBLE);
            holder.tvTicketCount.setText(digit.size() + "X");
        } else {
            holder.tvTicketCount.setVisibility(View.GONE);
        }

        holder.tvTicket.setText(digit.get(0));
        if (won){
            holder.tvTicket.setBackground(context.getDrawable(R.drawable.bg_jackpot_ticket_won));
        } else {
            holder.tvTicket.setBackground(context.getDrawable(R.drawable.bg_jackpot_ticket_item_grey));
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
