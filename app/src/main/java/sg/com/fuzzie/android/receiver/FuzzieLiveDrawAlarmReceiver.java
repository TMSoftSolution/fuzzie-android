package sg.com.fuzzie.android.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.sjl.foreground.Foreground;

import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.utils.FuzzieData;
import sg.com.fuzzie.android.utils.FuzzieData_;
import timber.log.Timber;


import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_JACKPOT_LIVE_NOTIFICATION;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_JACKPOT_LIVE;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_JACKPOT_REMAINDER;

/**
 * Created by joma on 10/24/17.
 */

public class FuzzieLiveDrawAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        assert bundle != null;
        int id = bundle.getInt("id", -1);

        if (id == NOTIFICATION_ID_JACKPOT_LIVE) {

            Timber.d("Jackpot Live Draw Notification");

            if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()) {
                Intent mainIntent = new Intent(context, MainActivity_.class);
                mainIntent.putExtra("jackpot_live", true);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent viewPendingIntent =
                        PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String title = "Jackpot live draw is starting";
                String name = "";

                if (bundle.getString("name") != null) {
                    name = bundle.getString("name");
                }

                String body = "Join the crowd and see if you win. Good luck!";

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_bear_bowtie)
                                .setColor(Color.parseColor("#fa3e3f"))
                                .setContentTitle(title)
                                .setContentText(body)
                                .setAutoCancel(true)
                                .setContentIntent(viewPendingIntent);

                // Get an instance of the NotificationManager service
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // Build the notification and issues it with notification manager.
                int notificationId = (int) (System.currentTimeMillis() / 1000);
                notificationManager.notify(notificationId, notificationBuilder.build());
            } else {
                FuzzieData dataManager = FuzzieData_.getInstance_(context);
                if (dataManager.getHome() != null && dataManager.getHome().getJackpot() != null && !dataManager.getHome().getJackpot().isLive()) {
                    Intent liveIntent = new Intent(BROADCAST_JACKPOT_LIVE_NOTIFICATION);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(liveIntent);
                }

            }
        } else if (id == NOTIFICATION_ID_JACKPOT_REMAINDER){

            Timber.d("Jackpot Remainder Notification");

            Intent mainIntent = new Intent(context, MainActivity_.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            int remainder = bundle.getInt("remainder", 0);

            String body = "Jackpot live draw at 6.35pm. Get your 4D tickets ready. Good luck!";
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_bear_bowtie)
                            .setColor(Color.parseColor("#fa3e3f"))
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setContentIntent(viewPendingIntent);

            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Build the notification and issues it with notification manager.
            int notificationId = (int) (System.currentTimeMillis() / 1000);
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

    }
}
