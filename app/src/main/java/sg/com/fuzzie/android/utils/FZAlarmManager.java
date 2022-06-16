package sg.com.fuzzie.android.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import sg.com.fuzzie.android.api.models.User;
import timber.log.Timber;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.List;

import static sg.com.fuzzie.android.utils.Constants.AlarmReceiverAction.INTENT_BIRTHDAY;
import static sg.com.fuzzie.android.utils.Constants.AlarmReceiverAction.INTENT_JACKPOT;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_FRIEND_BIRTHDAY;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_JACKPOT_LIVE;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_JACKPOT_REMAINDER;
import static sg.com.fuzzie.android.utils.Constants.ScheduledNotificationId.NOTIFICATION_ID_MY_BIRTHDAY;

/**
 * Created by joma on 10/24/17.
 */

public class FZAlarmManager {

    private static FZAlarmManager instance = null;

    private FZAlarmManager(){

    }

    public static FZAlarmManager getInstance(){
        if (instance == null){
            instance = new FZAlarmManager();
        }

        return instance;
    }

    public void scheduleJackpotLiveDrawNotification(Context context){

        CurrentUser currentUser = CurrentUser_.getInstance_(context);

        if (currentUser != null
                && currentUser.getCurrentUser() != null
                && currentUser.getCurrentUser().getSettings().isJackpotDrawNotification() != null
                && currentUser.getCurrentUser().getSettings().isJackpotDrawNotification()
                && currentUser.getJackpotLiveDrawDateTime() != null
                && !currentUser.getJackpotLiveDrawDateTime().equals("")){

            Timber.i("Scheduled Jackpot Live Draw Notification");

            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime now = new DateTime();
            DateTime drawTime = parser.parseDateTime(currentUser.getJackpotLiveDrawDateTime());

            if (now.isBefore(drawTime)){

                Intent intent = getScheduledIntent(INTENT_JACKPOT ,NOTIFICATION_ID_JACKPOT_LIVE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID_JACKPOT_LIVE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                assert alarmManager != null;
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, drawTime.getMillis(), pendingIntent);
            }


        } else {

            Timber.i("Not Scheduled Jackpot Live Draw Notification");
        }

    }

    public void scheduleJackpotRemainderNotification(Context context){

        CurrentUser currentUser = CurrentUser_.getInstance_(context);
        FuzzieData dataManager = FuzzieData_.getInstance_(context);

        if (currentUser != null
                &&currentUser.getCurrentUser() != null
                && currentUser.getJackpotLiveDrawDateTime() != null
                && !currentUser.getJackpotLiveDrawDateTime().equals("")
                && dataManager != null
                && dataManager.getHome() != null){

            int remainder = dataManager.getHome().getJackpot().getNumberOfTicketsPerWeek() - currentUser.getCurrentUser().getCurrentJackpotTicketsCount();

            if (remainder > 0){

                DateTimeFormatter parser = ISODateTimeFormat.dateTime();
                DateTime now = new DateTime();
                DateTime drawTime = parser.parseDateTime(currentUser.getJackpotLiveDrawDateTime());

                // drawTime.getMillis() - 60000 * 395 ---- Trigger Time 12.00 PM (Before 6 hours 35 minutes of live draw time).
                if (now.isBefore(drawTime.getMillis() -  60000 * 395)){

                    Timber.i("Scheduled Jackpot Remainder Notification");

                    Intent intent = getScheduledIntent(INTENT_JACKPOT ,NOTIFICATION_ID_JACKPOT_REMAINDER);
                    intent.putExtra("remainder", remainder);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID_JACKPOT_REMAINDER, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    assert alarmManager != null;
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, drawTime.getMillis() - 60000 * 395, pendingIntent);
                }


            } else {

                cancelScheduledNotification(context, INTENT_JACKPOT, NOTIFICATION_ID_JACKPOT_REMAINDER);
            }


        } else {

            Timber.i("Not Scheduled Jackpot Remainder Notification");
        }

    }

    public void scheduleMyBirthdayNotification(Context context){

        CurrentUser currentUser = CurrentUser_.getInstance_(context);

        if (currentUser != null
                && currentUser.getCurrentUser() != null
                && currentUser.getCurrentUser().getBirthday() != null
                && !currentUser.getCurrentUser().getBirthday().equals("")){



            DateTimeFormatter parser = ISODateTimeFormat.date();
            DateTime birthday = parser.parseDateTime(currentUser.getCurrentUser().getBirthday());

            Calendar birthdayCalendar = Calendar.getInstance();
            birthdayCalendar.setTimeInMillis(System.currentTimeMillis());
            birthdayCalendar.set(Calendar.MONTH, birthday.getMonthOfYear() - 1);
            birthdayCalendar.set(Calendar.DAY_OF_MONTH, birthday.getDayOfMonth() - 3);
            birthdayCalendar.set(Calendar.HOUR, 9);
            birthdayCalendar.set(Calendar.MINUTE, 30);
            birthdayCalendar.set(Calendar.SECOND, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            if (birthdayCalendar.getTimeInMillis() > calendar.getTimeInMillis()){

                Intent intent = getScheduledIntent(INTENT_BIRTHDAY, NOTIFICATION_ID_MY_BIRTHDAY);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID_MY_BIRTHDAY, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                assert alarmManager != null;
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, birthdayCalendar.getTimeInMillis() , pendingIntent);

            }


        }

    }

    public void scheduleFriendsBirthdayNotification(Context context, List<User> friends){

        for (int i = 0 ; i < friends.size() ; i ++){

            User friend = friends.get(i);

            if (friend.getBirthdate() != null && !friend.getBirthdate().equals("")){

                DateTimeFormatter parser = ISODateTimeFormat.date();
                DateTime birthday = parser.parseDateTime(friend.getBirthdate());

                Calendar birthdayCalendar = Calendar.getInstance();
                birthdayCalendar.setTimeInMillis(System.currentTimeMillis());
                birthdayCalendar.set(Calendar.MONTH, birthday.getMonthOfYear() - 1);
                birthdayCalendar.set(Calendar.DAY_OF_MONTH, birthday.getDayOfMonth());
                birthdayCalendar.set(Calendar.HOUR, 8);
                birthdayCalendar.set(Calendar.MINUTE, 30);
                birthdayCalendar.set(Calendar.SECOND, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                if (birthdayCalendar.getTimeInMillis() > calendar.getTimeInMillis()){

                    Intent intent = getScheduledIntent(INTENT_BIRTHDAY, NOTIFICATION_ID_FRIEND_BIRTHDAY);
                    intent.putExtra("user_id", friend.getId());
                    intent.putExtra("name", friend.getFirstName() + " " + friend.getLastName());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID_FRIEND_BIRTHDAY + i + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    assert alarmManager != null;
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, birthdayCalendar.getTimeInMillis() , pendingIntent);
                }


            }

        }

    }

    private Intent getScheduledIntent(String action, int id){

        Intent intent = new Intent(action);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("id", id);

        return intent;
    }

    public void cancelScheduledNotification(Context context, String action,  int id){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, getScheduledIntent(action, id), PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null && pendingIntent != null){

            if (checkScheduledNotification(context, action, id)){

                alarmManager.cancel(pendingIntent);

                Timber.i("Local Notification Cancelled - " + id);
            } else {

                Timber.i("No Intent Scheduled - " + id);

            }

        }


    }

    private boolean checkScheduledNotification(Context context, String action, int id){

        Intent intent = getScheduledIntent(action, id);

        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE) != null;

    }
}
