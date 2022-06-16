package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.DecimalFormat;
import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Prize;

/**
 * Created by joma on 10/17/17.
 */

public class PrizeItem extends AbstractItem<PrizeItem, PrizeItem.ViewHolder>{

    private Prize prize;
    private int position;
    private boolean won;
    private boolean last;

    public PrizeItem(Prize prize, int position, boolean won, boolean last){
        this.prize = prize;
        this.position = position;
        this.won = won;
        this.last = last;
    }

    @Override
    public int getType() {
        return R.id.item_prize_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_prize;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();

        if (prize.getFourD().equals("")){
            holder.tvNotDrawYet.setVisibility(View.VISIBLE);
            holder.tvFourD.setVisibility(View.GONE);
            holder.tvWon.setVisibility(View.GONE);
        } else {
            holder.tvNotDrawYet.setVisibility(View.GONE);
            holder.tvFourD.setVisibility(View.VISIBLE);
            holder.tvFourD.setText(prize.getFourD());
            if (won){
                holder.tvFourD.setBackground(context.getDrawable(R.drawable.bg_jackpot_ticket_won));
                holder.tvWon.setVisibility(View.VISIBLE);
            } else {
                holder.tvFourD.setBackground(context.getDrawable(R.drawable.bg_jackpot_ticket_item_grey));
                holder.tvWon.setVisibility(View.GONE);
            }
        }

        holder.tvOrder.setText(String.valueOf(position + 1));
        if (position == 0){
            holder.tvOrder.setBackground(context.getDrawable(R.drawable.bg_prize_first));
        } else if (position == 1){
            holder.tvOrder.setBackground(context.getDrawable(R.drawable.bg_prize_second));
        } else if (position == 2){
            holder.tvOrder.setBackground(context.getDrawable(R.drawable.bg_prize_third));
        } else {
            holder.tvOrder.setBackground(context.getDrawable(R.drawable.bg_prize_normal));
        }

        holder.tvName.setText(prize.getName());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.tvAmount.setText("S$" + format.format(prize.getAmount()));

        if (last){
            holder.bottomDivider.setVisibility(View.VISIBLE);
        } else {
            holder.bottomDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvOrder;
        TextView tvAmount;
        TextView tvName;
        TextView tvNotDrawYet;
        TextView tvFourD;
        TextView tvWon;
        View bottomDivider;

        public ViewHolder(View itemView) {
            super(itemView);

            tvOrder = (TextView) itemView.findViewById(R.id.tvOrder);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvNotDrawYet = (TextView) itemView.findViewById(R.id.tvNotDrawYet);
            tvFourD = (TextView) itemView.findViewById(R.id.tvFourD);
            tvWon = (TextView) itemView.findViewById(R.id.tvWon);
            bottomDivider = (View) itemView.findViewById(R.id.bottomDivider);
        }
    }

    public void setPrize(Prize prize){
        this.prize = prize;
    }

    public void setWon(boolean won){
        this.won = won;
    }

}
