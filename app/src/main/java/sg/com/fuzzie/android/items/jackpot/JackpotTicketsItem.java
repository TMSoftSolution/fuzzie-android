package sg.com.fuzzie.android.items.jackpot;

import android.content.Context;

import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.JackpotResult;
import sg.com.fuzzie.android.ui.jackpot.JackpotLiveActivity_;
import sg.com.fuzzie.android.utils.FuzzieData_;
import sg.com.fuzzie.android.utils.TimeUtils;
import sg.com.fuzzie.android.utils.ViewUtils;


/**
 * Created by joma on 10/19/17.
 */

public class JackpotTicketsItem extends AbstractItem<JackpotTicketsItem, JackpotTicketsItem.ViewHolder>{

    private JackpotResult result;
    private FastItemAdapter<JackpotTicketItem> adapter;
    private int ticketsCount = 0;

    private CountDownTimer timer;

    public JackpotTicketsItem(JackpotResult result){
        this.result = result;
    }

    @Override
    public int getType() {
        return R.id.item_jackpot_tickets_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_jackpot_tickets;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        final FuzzieData_ dataManager = FuzzieData_.getInstance_(context);

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        final DateTime now = new DateTime();
        final DateTime drawTime = parser.parseDateTime(result.getDrawDateTime());

        if (result.getDrawDateTime() != null && !result.getDrawDateTime().equals("")){
            holder.tvDrawDate.setText(TimeUtils.jackpotTickets(result.getDrawDateTime()));
        }

        int numberOfTicketsPerWeek = 10;
        if (dataManager.getHome() != null && dataManager.getHome().getJackpot() != null){
            numberOfTicketsPerWeek = dataManager.getHome().getJackpot().getNumberOfTicketsPerWeek();
        }

        if (result.getCombinations().size() > 0){

            ViewUtils.gone(holder.tvBody);
            ViewUtils.visible(holder.rvTickets);

            if (adapter == null){
                adapter = new FastItemAdapter<>();
            } else {
                adapter.clear();
            }

            holder.rvTickets.setLayoutManager(new GridLayoutManager(context, ViewUtils.getTicketCellCount(context)));
            holder.rvTickets.setAdapter(adapter);

            for (List<String> list : result.getCombinations()){
                adapter.add(new JackpotTicketItem(list));
                ticketsCount = ticketsCount + list.size();
            }

            holder.tvTicketCount.setText("JACKPOT TICKETS USED: " + ticketsCount + "/" + numberOfTicketsPerWeek);

        } else {

            ViewUtils.visible(holder.tvBody);
            ViewUtils.gone(holder.rvTickets);

            holder.tvTicketCount.setText("JACKPOT TICKETS USED: " + ticketsCount + "/" + numberOfTicketsPerWeek);

        }

        if (dataManager.getHome().getJackpot().isLive()){

            ViewUtils.gone(holder.tvLeftTime);
            ViewUtils.visible(holder.cvLive);


        } else {

            ViewUtils.visible(holder.tvLeftTime);
            ViewUtils.gone(holder.cvLive);

            long milliseconds = drawTime.getMillis() - now.getMillis();

            timer = new CountDownTimer(milliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    holder.tvLeftTime.setText(TimeUtils.jackpotDrawDateTimeWithNoSpace(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    ViewUtils.gone(holder.tvLeftTime);
                    ViewUtils.visible(holder.cvLive);
                }
            };
            timer.start();

        }

        holder.cvLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotLiveActivity_.intent(context).start();

            }
        });

    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        if (timer != null){
            timer.cancel();
            timer = null;
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDrawDate;
        TextView tvLeftTime;
        TextView tvBody;
        CardView cvLive;
        RecyclerView rvTickets;
        TextView tvTicketCount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDrawDate = (TextView) itemView.findViewById(R.id.tvDrawDate);
            tvLeftTime = (TextView) itemView.findViewById(R.id.tvLeftTime);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            cvLive = (CardView) itemView.findViewById(R.id.cvLive);
            rvTickets = (RecyclerView) itemView.findViewById(R.id.rvTickets);
            tvTicketCount = (TextView) itemView.findViewById(R.id.tvTicketCount);
        }
    }
}
