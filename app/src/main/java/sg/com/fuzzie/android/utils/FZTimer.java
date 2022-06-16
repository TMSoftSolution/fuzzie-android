package sg.com.fuzzie.android.utils;

import android.os.Handler;
import timber.log.Timber;

import org.androidannotations.annotations.EBean;

import java.util.Timer;

/**
 * Created by mac on 8/14/17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class FZTimer {

    private Handler handler = new Handler();
    private int past_time;
    private long interval;
    private OnTaskRunListener onTaskRunListener;

    public FZTimer(){
        this.past_time = 0;
        this.interval = 1000;
    }

    public void setInterval(long interval){
        this.interval = interval;
    }

    public interface OnTaskRunListener{
        void onTaskRun(long past_time, String rendered_time);
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            past_time += interval;
            handler.postDelayed(this,interval);
            onTaskRunListener.onTaskRun(past_time, time_render(past_time / 1000));
//            Timber.e("Timer is running...");
        }
    };

    public void startTimer(){
        handler.removeCallbacks(task);
        handler.postDelayed(task, interval);
    }

    public void stopTimer(){
        handler.removeCallbacks(task);
//        Timber.e("Timer is stopped...");
    }

    private String time_render(int seconds){
        int mins = seconds / 60;
        int hours = mins / 60;
        int sec = seconds > 60 ? seconds % 60 : seconds;
        String hours_str;
        String seconds_str;
        String mins_str;
        if (hours < 0){
            hours_str = "00";
        } else {
            if (hours < 10){
                hours_str = "0" + hours;
            } else {
                hours_str = String.valueOf(hours);
            }
        }

        if (mins < 0){
            mins_str = "00";
        } else {
            if (mins < 10){
                mins_str = "0" + mins;
            } else {
                mins_str = String.valueOf(mins);
            }
        }

        if (sec < 10){
            seconds_str = "0" + sec;
        } else {
            seconds_str = String.valueOf(sec);
        }

        return hours_str + ":" + mins_str + ":" + seconds_str;
    }

    public void setOnTaskRunListener(OnTaskRunListener onTaskRunListener) {
        this.onTaskRunListener = onTaskRunListener;
    }
}
