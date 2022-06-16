package sg.com.fuzzie.android.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import sg.com.fuzzie.android.FuzzieApplication_;
import timber.log.Timber;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sjl.foreground.Foreground;

import java.util.Map;

import sg.com.fuzzie.android.MainActivity_;
import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.ui.me.InviteFriendsActivity_;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_FRIENDS;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_RATE_APP;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_WISHLIST;
import static sg.com.fuzzie.android.utils.Constants.BROADCAST_NOTIFICATION_WISHLIST_WITH_ID;

/**
 * Created by nurimanizam on 25/1/17.
 */

public class MyFcmListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        SharedPreferences mPrefs;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (message != null && message.getFrom() != null) {
            if (message.getNotification() != null && message.getNotification().getBody() != null){
                Timber.e("From: " + message.getFrom());
                Timber.e("Notification Message Body: " + message.getNotification().getBody());
            }

            if (message.getData() != null && message.getData().size() > 0){
                Timber.e("Message data payload: " + message.getData());
                if (mPrefs.getBoolean("loggedin", false)){
                    handleDataPayload(message);
                }
            }

        }
    }

    private void handleDataPayload(RemoteMessage message){

        Map data = message.getData();
        String title = "";
        String body = "";
        String redirect_to;
        String userId;

        if (data.get("redirect_to") != null){

            redirect_to = (String) data.get("redirect_to");
            if (data.get("title") != null){
                title = (String) data.get("title");
            }
            if (data.get("body") != null){
                body = (String )data.get("body");
            }

            if (redirect_to.equals("referrals")){
                if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()){

                    Intent intent = new Intent(this, MainActivity_.class);
                    intent.putExtra("to", "referrals");
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent viewPendingIntent =
                            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_bear_bowtie)
                                    .setColor(Color.parseColor("#fa3e3f"))
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setContentIntent(viewPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                    // Build the notification and issues it with notification manager.
                    int notificationId = (int)(System.currentTimeMillis() / 1000);
                    notificationManager.notify(notificationId, notificationBuilder.build());

                } else {

                    Intent intent = new Intent(this, InviteFriendsActivity_.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            } else if (redirect_to.equals("wishlist")){
                if (data.get("user_id") != null){

                    userId = (String) data.get("user_id");

                    if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()){

                        Intent intent = new Intent(this, MainActivity_.class);
                        intent.putExtra("to", "wishlist");
                        intent.putExtra("user_id", userId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent viewPendingIntent =
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.ic_bear_bowtie)
                                        .setColor(Color.parseColor("#fa3e3f"))
                                        .setContentTitle(title)
                                        .setContentText(body)
                                        .setAutoCancel(true)
                                        .setContentIntent(viewPendingIntent);

                        // Get an instance of the NotificationManager service
                        NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                        // Build the notification and issues it with notification manager.
                        int notificationId = (int)(System.currentTimeMillis() / 1000);
                        notificationManager.notify(notificationId, notificationBuilder.build());
                    } else {
                        Intent intent = new Intent(BROADCAST_NOTIFICATION_WISHLIST_WITH_ID);
                        intent.putExtra("user_id", userId);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }

                } else {

                    if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()){

                        Intent intent = new Intent(this, MainActivity_.class);
                        intent.putExtra("to", "wishlist");
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent viewPendingIntent =
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.ic_bear_bowtie)
                                        .setColor(Color.parseColor("#fa3e3f"))
                                        .setContentTitle(title)
                                        .setContentText(body)
                                        .setAutoCancel(true)
                                        .setContentIntent(viewPendingIntent);

                        // Get an instance of the NotificationManager service
                        NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                        // Build the notification and issues it with notification manager.
                        int notificationId = (int)(System.currentTimeMillis() / 1000);
                        notificationManager.notify(notificationId, notificationBuilder.build());
                    } else {
                        Intent intent = new Intent(BROADCAST_NOTIFICATION_WISHLIST);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }

                }


            } else if (redirect_to.equals("friends")){

                if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()){

                    Intent intent = new Intent(this, MainActivity_.class);
                    intent.putExtra("to", "friends");
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent viewPendingIntent =
                            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_bear_bowtie)
                                    .setColor(Color.parseColor("#fa3e3f"))
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setContentIntent(viewPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                    // Build the notification and issues it with notification manager.
                    int notificationId = (int)(System.currentTimeMillis() / 1000);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                } else {

                    Intent intent = new Intent(BROADCAST_NOTIFICATION_FRIENDS);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            } else if (redirect_to.equals("giftbox_for_friends")){
                if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()){
                    Intent intent = new Intent(this, MainActivity_.class);
                    intent.putExtra("to", "giftbox_for_friends");
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent viewPendingIntent =
                            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_bear_bowtie)
                                    .setColor(Color.parseColor("#fa3e3f"))
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setContentIntent(viewPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                    // Build the notification and issues it with notification manager.
                    int notificationId = (int)(System.currentTimeMillis() / 1000);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                } else {
                    Intent intent = new Intent(BROADCAST_NOTIFICATION_GIFTBOX_FOR_FRIENDS);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            } else if (redirect_to.equals("rate_app")){
                if (Foreground.get(FuzzieApplication_.getInstance()).isBackground()){
                    Intent intent = new Intent(this, MainActivity_.class);
                    intent.putExtra("to", "rate_app");
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent viewPendingIntent =
                            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_bear_bowtie)
                                    .setColor(Color.parseColor("#fa3e3f"))
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setContentIntent(viewPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                    // Build the notification and issues it with notification manager.
                    int notificationId = (int)(System.currentTimeMillis() / 1000);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                } else {
                    Intent intent = new Intent(BROADCAST_NOTIFICATION_RATE_APP);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            } else if (redirect_to.equals("lucky_packets")){

                Intent intent = new Intent(this, MainActivity_.class);
                intent.putExtra("to", "lucky_packets");

                if (data.get("red_packet_bundle_id") != null){
                    String bundleId = (String) data.get("red_packet_bundle_id");
                    intent.putExtra("bundle_id", bundleId);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent viewPendingIntent =
                        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_bear_bowtie)
                                .setColor(Color.parseColor("#fa3e3f"))
                                .setContentTitle(title)
                                .setContentText(body)
                                .setAutoCancel(true)
                                .setContentIntent(viewPendingIntent);

                // Get an instance of the NotificationManager service
                NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(this);

                // Build the notification and issues it with notification manager.
                int notificationId = (int)(System.currentTimeMillis() / 1000);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        }
    }

}
