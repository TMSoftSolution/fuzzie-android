package sg.com.fuzzie.android.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.sjl.foreground.Foreground;

import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;

import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_FRIEND_BIRTHDAY;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_MY_BIRTHDAY;

/**
 * Created by mac on 1/15/18.
 */

public class BirthdayAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        assert bundle != null;
        int id = bundle.getInt("id", -1);

        if (id == NOTIFICATION_ID_MY_BIRTHDAY){

            Intent mainIntent = new Intent(context, MainActivity_.class);
            mainIntent.putExtra("my_birthday", true);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String body = "Your birthday is in 3 days. Update your wishlist";

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

        } else if (id == NOTIFICATION_ID_FRIEND_BIRTHDAY){

            String userId = "";
            if (bundle.getString("user_id") != null){
                userId = bundle.getString("user_id");
            }

            Intent mainIntent = new Intent(context, MainActivity_.class);
            mainIntent.putExtra("friend_birthday", true);
            mainIntent.putExtra("user_id", userId);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String name = "";
            if (bundle.getString("name") != null) {
                name = bundle.getString("name");
            }

            String body = "Today is " + name + "\'s birthday. Send a gift instantly!";

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
