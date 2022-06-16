package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.jackpot.JackpotHomeActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotTicketSetActivity_;
import sg.com.fuzzie.android.utils.CurrentUser_;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import sg.com.fuzzie.android.utils.TimeUtils;

/**
 * Created by mac on 1/22/18.
 */

public class JackpotTicketsUseItem extends AbstractItem<JackpotTicketsUseItem, JackpotTicketsUseItem.ViewHolder> {


    public JackpotTicketsUseItem(){

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_jackpot_tickets_use_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_jackpot_tickets_use;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        CurrentUser_ currentUser = CurrentUser_.getInstance_(context);

        if (currentUser != null && currentUser.getCurrentUser() != null){

            int availableTicketsCount = currentUser.getCurrentUser().getAvailableJackpotTicketsCount();

            if (availableTicketsCount > 1){

                holder.tvTicketsCount.setText(FuzzieUtils.fromHtml(String.valueOf(availableTicketsCount) + " JACKPOT TICKETS " + "<font color='#FFFFFF'>AVAILABLE</font>"));

            } else {

                holder.tvTicketsCount.setText(FuzzieUtils.fromHtml(String.valueOf(availableTicketsCount) + " JACKPOT TICKET " + "<font color='#FFFFFF'>AVAILABLE</font>"));

            }

            String expirationDate = currentUser.getCurrentUser().getJackpotTicketsExpirationDate();
            if (expirationDate != null && !expirationDate.equals("")){

                holder.tvValid.setText("Valid till " + TimeUtils.dateTimeFormat(expirationDate, "yyyy-MM-dd", "d MMMM yyyy"));

            } else {

                holder.tvValid.setText("");
            }

            if (availableTicketsCount == 0){

                holder.cvUse.setEnabled(false);
                holder.cvUse.setCardBackgroundColor(context.getResources().getColor(R.color.colorJackpotTicketDisable));

            } else {

                holder.cvUse.setEnabled(true);
                holder.cvUse.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }


        holder.cvUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotTicketSetActivity_.intent(context).start();
            }
        });

        holder.cvGetMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotHomeActivity_.intent(context).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTicketsCount;
        TextView tvValid;
        CardView cvUse;
        CardView cvGetMore;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTicketsCount = (TextView) itemView.findViewById(R.id.tvTicketsCount);
            tvValid = (TextView) itemView.findViewById(R.id.tvValid);
            cvUse = (CardView) itemView.findViewById(R.id.cvUse);
            cvGetMore = (CardView) itemView.findViewById(R.id.cvGetMore);
        }
    }
}
