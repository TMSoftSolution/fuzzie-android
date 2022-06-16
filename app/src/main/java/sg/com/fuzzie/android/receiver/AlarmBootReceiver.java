package sg.com.fuzzie.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import timber.log.Timber;

import sg.com.fuzzie.android.utils.FZAlarmManager;

/**
 * Created by joma on 10/24/17.
 */

public class AlarmBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //only enabling one type of notifications for demo purposes

            Timber.d("Device is rebooted");
            FZAlarmManager.getInstance().scheduleJackpotLiveDrawNotification(context);
        }
    }
}