package sg.com.fuzzie.android.ui.jackpot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.FuzzieAPI;
import sg.com.fuzzie.android.api.models.Home;
import sg.com.fuzzie.android.api.models.Jackpot;
import sg.com.fuzzie.android.core.BaseActivity;
import sg.com.fuzzie.android.items.jackpot.TicketItem;
import sg.com.fuzzie.android.utils.Constants;
import sg.com.fuzzie.android.utils.CurrentUser;
import sg.com.fuzzie.android.utils.FZAlarmManager;
import sg.com.fuzzie.android.utils.FZTimer;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieUtils;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static sg.com.fuzzie.android.utils.Constants.AlarmReceiverAction.INTENT_JACKPOT;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_CHANGE_TAB;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_RESULT_REFRESH;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_REFRESH_USER;
import static sg.com.fuzzie.android.utils.Constants.JACKPOT_DRAW_TIME_INTERVAL;

/**
 * Created by mac on 1/24/18.
 */

@EActivity(R.layout.activity_jackpot_ticket_set_success)
public class JackpotTicketSetSuccessActivity extends BaseActivity implements FZTimer.OnTaskRunListener{

    boolean notify = false;
    Map<String, List<String>> digitsMap;
    FastItemAdapter<TicketItem> adapter;

    Home home;
    FZTimer timer;

    @Bean
    FuzzieData dataManager;

    @Bean
    CurrentUser currentUser;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvDigit)
    RecyclerView rvDigit;

    @ViewById(R.id.ivSwitch)
    ImageView ivSwitch;

    @ViewById(R.id.tvDays)
    TextView tvDays;

    @ViewById(R.id.tvHours)
    TextView tvHours;

    @ViewById(R.id.tvMins)
    TextView tvMins;

    @ViewById(R.id.tvSec)
    TextView tvSec;

    @AfterViews
    void callAfterViewInjection(){

        tvTitle.setText("GREAT JOB, " + currentUser.getCurrentUser().getFirstName() + "!");

        showLockedTickets();

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_REFRESH_USER));
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_JACKPOT_RESULT_REFRESH));

        home = dataManager.getHome();

        notify = currentUser.getCurrentUser().getSettings().isJackpotDrawNotification();
        if (notify){
            ivSwitch.setImageResource(R.drawable.switch_on);
            FZAlarmManager.getInstance().scheduleJackpotLiveDrawNotification(context);
        } else {
            ivSwitch.setImageResource(R.drawable.switch_off);
            FZAlarmManager.getInstance().cancelScheduledNotification(context, INTENT_JACKPOT, Constants.ScheduledNotificationId.NOTIFICATION_ID_JACKPOT_LIVE);
        }

        FZAlarmManager.getInstance().scheduleJackpotRemainderNotification(this);
    }

    private void showLockedTickets(){
        adapter = new FastItemAdapter<TicketItem>();
        rvDigit.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvDigit.setAdapter(adapter);

        digitsMap = dataManager.getTicketsMap(dataManager.digits);
        for (Map.Entry<String, List<String>> entry : digitsMap.entrySet()){
            List<String> digit = entry.getValue();
            adapter.add(new TicketItem(digit));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (home != null && home.getJackpot() != null){
            Jackpot jackpot = home.getJackpot();
            boolean isCuttingOffTime = FuzzieUtils.isCuttingOffLiveDraw(context);
            if ((!isCuttingOffTime && jackpot.getDrawTime() != null && !jackpot.getDrawTime().equals(""))
                    || (isCuttingOffTime && jackpot.getNextDrawTime() != null && !jackpot.getNextDrawTime().equals("")) ){
                if (timer == null){
                    timer = new FZTimer();
                }
                timer.startTimer();
                timer.setInterval(JACKPOT_DRAW_TIME_INTERVAL);
                timer.setOnTaskRunListener(this);
                Timber.e("Timer is running...");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null){
            timer.stopTimer();
            timer = null;
            Timber.e("Timer is stopped...");
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Click(R.id.ivSwitch)
    void switchButtonClicked(){
        notify = !notify;
        if (notify){
            ivSwitch.setImageResource(R.drawable.switch_on);
            FZAlarmManager.getInstance().scheduleJackpotLiveDrawNotification(context);
        } else {
            ivSwitch.setImageResource(R.drawable.switch_off);
            FZAlarmManager.getInstance().cancelScheduledNotification(context, INTENT_JACKPOT, Constants.ScheduledNotificationId.NOTIFICATION_ID_JACKPOT_LIVE);
        }

        Call<Void> call = FuzzieAPI.APIService().setJackpotDrawNotification(currentUser.getAccesstoken(), notify);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.BROADCAST_REFRESH_USER));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Click(R.id.cvDone)
    void walletButtonClicked(){

        Intent intent = new Intent(BROADCAST_CHANGE_TAB);
        intent.putExtra("tabId",R.id.tab_wallet);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        Intent mainIntent = new Intent(context, MainActivity_.class);
        mainIntent.putExtra("go_jackpot_tickets", true);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mainIntent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTaskRun(long past_time, String rendered_time) {
        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        DateTime now = new DateTime();
        DateTime drawTime;
        if (FuzzieUtils.isCuttingOffLiveDraw(context)){
            drawTime  = parser.parseDateTime(home.getJackpot().getNextDrawTime());
        } else {
            drawTime =  parser.parseDateTime(home.getJackpot().getDrawTime());
        }

        long leftTime = drawTime.getMillis() - now.getMillis();
        if (leftTime > 0){
            int seconds = (int)(leftTime / 1000) % 60;
            int mins = (int) (leftTime / 1000 / 60) % 60;
            int hours = (int) (leftTime / 1000 / 60 / 60) % 24;
            int days = (int) (leftTime / 1000 / 60 / 60 / 24);

            if (seconds < 10){
                tvSec.setText("0" + seconds);
            } else {
                tvSec.setText(String.valueOf(seconds));
            }

            if (mins < 10){
                tvMins.setText("0" + mins);
            } else {
                tvMins.setText(String.valueOf(mins));
            }

            if (hours < 10){
                tvHours.setText("0" + hours);
            } else {
                tvHours.setText(String.valueOf(hours));
            }

            if (days < 10){
                tvDays.setText("0" + days);
            } else {
                tvDays.setText(String.valueOf(days));
            }
        }
    }
}
