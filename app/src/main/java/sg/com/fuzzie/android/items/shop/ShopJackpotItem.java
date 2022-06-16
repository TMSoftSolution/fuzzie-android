package sg.com.fuzzie.android.items.shop;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;

import sg.com.fuzzie.android.ui.jackpot.JackpotLiveActivity_;
import timber.log.Timber;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Jackpot;
import sg.com.fuzzie.android.ui.jackpot.JackpotHomeActivity_;
import sg.com.fuzzie.android.ui.jackpot.JackpotDrawHistoryActivity_;
import sg.com.fuzzie.android.utils.CustomTypefaceSpan;
import sg.com.fuzzie.android.utils.FZTimer;
import sg.com.fuzzie.android.utils.TimeUtils;

import static sg.com.fuzzie.android.utils.Constants.JACKPOT_DRAW_TIME_INTERVAL;

/**
 * Created by joma on 10/12/17.
 */

public class ShopJackpotItem extends AbstractItem<ShopJackpotItem, ShopJackpotItem.ViewHolder> {

    private Context context;
    private Home home;
    private FZTimer timer;

    public ShopJackpotItem(Home home){
        this.home = home;
    }

    @Override
    public int getType() {
        return R.id.item_shop_jackpot_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_shop_jackpot;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        context = holder.itemView.getContext();

        String first = "Win S$150,000 cash prizes every Wed & Sat, 6.35pm.";
        String second = " Enter now to get your free Jackpot tickets.";
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Bold.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);
        SpannableString spannableString  = new SpannableString(first + second);
        spannableString.setSpan(typefaceSpan, 0, first.length() , 0);
        holder.tvBody.setText(spannableString);

        final Jackpot jackpot = home.getJackpot();

        if (jackpot != null && jackpot.isLive()){
            holder.llLive.setVisibility(View.VISIBLE);
            holder.tvBody.setVisibility(View.GONE);
            holder.llDrawTime.setVisibility(View.GONE);
        } else {
            holder.llLive.setVisibility(View.GONE);
            holder.tvBody.setVisibility(View.VISIBLE);
            holder.llDrawTime.setVisibility(View.VISIBLE);
        }


        if (jackpot != null && !jackpot.isLive() && jackpot.getDrawTime() != null && !jackpot.getDrawTime().equals("")){

            if (timer == null){
                timer = new FZTimer();
            }

            timer.setInterval(JACKPOT_DRAW_TIME_INTERVAL);
            timer.setOnTaskRunListener(new FZTimer.OnTaskRunListener() {
                @Override
                public void onTaskRun(long past_time, String rendered_time) {

                    DateTimeFormatter parser = ISODateTimeFormat.dateTime();
                    DateTime now = new DateTime();
                    DateTime drawTime = parser.parseDateTime(jackpot.getDrawTime());

                    long leftTime = drawTime.getMillis() - now.getMillis();

                    if (leftTime > 0){
//                        Timber.d(String.valueOf(leftTime));
                        holder.tvDrawTime.setText(TimeUtils.jackpotDrawDateTime(leftTime));

                    } else {
                        holder.tvDrawTime.setText("00 : 00 : 00 : 00");
                        timer.stopTimer();
                    }
                }
            });
            timer.startTimer();
            Timber.e("Timer is running...");
        }

        holder.cvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotHomeActivity_.intent(context).start();
            }
        });

        holder.cvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotDrawHistoryActivity_.intent(context).start();
            }
        });

        holder.llLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JackpotLiveActivity_.intent(context).start();
            }
        });

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);

        if (timer != null){
            timer.stopTimer();
            timer = null;
            Timber.e("Timer is stopped...");
        }
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBody;
        CardView cvEnter;
        CardView cvViewAll;
        LinearLayout llDrawTime;
        TextView tvDrawTime;
        LinearLayout llLive;

        public ViewHolder(View view) {
            super(view);

            tvBody = (TextView) view.findViewById(R.id.tvBody);
            cvEnter = (CardView) view.findViewById(R.id.cvEnter);
            cvViewAll = (CardView) view.findViewById(R.id.cvViewAll);
            llDrawTime = (LinearLayout) view.findViewById(R.id.llDrawTime);
            tvDrawTime = (TextView) view.findViewById(R.id.tvDrawTime);
            llLive = (LinearLayout) view.findViewById(R.id.llLive);
        }
    }
}
