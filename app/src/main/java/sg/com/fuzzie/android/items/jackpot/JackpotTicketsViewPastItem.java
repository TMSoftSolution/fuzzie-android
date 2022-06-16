package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.ui.jackpot.JackpotDrawHistoryActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;

public class JackpotTicketsViewPastItem extends AbstractItem<JackpotTicketsViewPastItem, JackpotTicketsViewPastItem.ViewHolder> {

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.id_jackpot_tickets_view_past_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_jackpot_tickets_view_past;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        final FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        holder.cvViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotDrawHistoryActivity_.intent(context).start();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cvViewResult;

        public ViewHolder(View itemView) {
            super(itemView);

            cvViewResult = (CardView) itemView.findViewById(R.id.cvViewResult);

        }
    }
}
