package sg.com.fuzzie.android.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

import static sg.com.fuzzie.android.utils.Constants.COUNTDOWN_TIME;

/**
 * Created by mac on 5/29/17.
 */

public class TimeUtils {

    private String second = " second";

    private String seconds = " seconds";

    private String minute = " minute";

    private String minutes = " minutes";

    private String hour = " hour";

    private String hours = " hours";

    private String day = " day";

    private String days = " days";

    private String week = " week";

    private String weeks = " weeks";

    private String month = " month";

    private String months = " months";

    private String year = " year";

    private String years = " years";

    public String timeUntil(final long millis) {
        return time(millis - System.currentTimeMillis(), true);
    }

    public String timeAgo(final long millis) {
        return time(System.currentTimeMillis() - millis, false);
    }

    public String time(long distanceMillis, final boolean allowFuture) {

        final int seconds = (int)(distanceMillis / 1000);
        final int minutes = seconds / 60;
        final int hours = minutes / 60;
        final int days = hours / 24;
        final int weeks = days / 7;
        final int months = days / 30;
        final int years = days / 365;

        String time;

        if (years >= 1){
            time = String.valueOf(years);
            time += years == 1 ? this.year: this.years;
        } else if (months >= 1){
            time = String.valueOf(months);
            time += months == 1 ? this.month : this.months;
        } else if (weeks >= 1){
            time = String.valueOf(weeks);
            time += weeks == 1 ? this.week : this.weeks;
        } else if (days >= 1){
            time = String.valueOf(days);
            time += days == 1 ? this.day : this.days;
        } else if (hours >= 1){
            time = String.valueOf(hours);
            time += hours == 1 ? this.hour : this.hours;
        } else if (minutes >= 1){
            time = String.valueOf(minutes);
            time += minutes == 1 ? this.minute : this.minutes;
        } else {
            time = String.valueOf(seconds);
            time += seconds == 1 ? this.second : this.seconds;
        }

        return time + " ago";
    }

    public static String dateTimeDDMMYY(String date){

        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "d MMM yy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String dateTimeDDMMYYYY(String date){

        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "d MMM yyyy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String dateTimeDDMMYYYYHMA(String date){
        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "d MMM yyyy, h.ma";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate.replace("AM", "am").replace("PM", "pm");
    }

    public static String dateTimeForOrderHistory(String date){
        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "d MMMM yyyy, h:mma";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate.replace("AM", "am").replace("PM", "pm");
    }

    public static String jackpotDrawHistoryFormat(String date){
        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "dd.MM.yyyy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String jackpotTickets(String date){
        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "EEEE d MMM yyyy, h.mma";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate.replace("AM", "am").replace("PM", "pm");
    }

    public static String redeemStartEndFormat(String date){

        String oldFormat= "yyyy-MM-dd";
        String newFormat= "d MMM yyyy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String jackpotPaySuccessFormat(String date){
        String oldFormat= "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
        String newFormat= "EEEE d MMMM yyyy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String birthdayFormatFromFacebook(String date){

        String oldFormat= "MM/dd/yyyy";
        String newFormat= "yyyy-MM-dd";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String dateTimeFormat(String date, String oldFormat, String newFormat){

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static String jackpotDrawDateTime(long leftTime){
        int seconds = (int)(leftTime / 1000) % 60;
        int mins = (int) (leftTime / 1000 / 60) % 60;
        int hours = (int) (leftTime / 1000 / 60 / 60) % 24;
        int days = (int) (leftTime / 1000 / 60 / 60 / 24);

        String leftTimeString = "";
        if (days < 10){
            leftTimeString += "0" + days;
        } else {
            leftTimeString += days;
        }
        leftTimeString += " : ";

        if (hours < 10){
            leftTimeString += "0" + hours;
        } else {
            leftTimeString += hours;
        }
        leftTimeString += " : ";

        if (mins < 10){
            leftTimeString += "0" + mins;
        } else {
            leftTimeString += mins;
        }
        leftTimeString += " : ";

        if (seconds < 10){
            leftTimeString += "0" + seconds;
        } else {
            leftTimeString += seconds;
        }

        return leftTimeString;
    }

    public static String jackpotDrawDateTimeWithNoSpace(long leftTime){
        int seconds = (int)(leftTime / 1000) % 60;
        int mins = (int) (leftTime / 1000 / 60) % 60;
        int hours = (int) (leftTime / 1000 / 60 / 60) % 24;
        int days = (int) (leftTime / 1000 / 60 / 60 / 24);

        String leftTimeString = "";
        if (days < 10){
            leftTimeString += "0" + days;
        } else {
            leftTimeString += days;
        }
        leftTimeString += ":";

        if (hours < 10){
            leftTimeString += "0" + hours;
        } else {
            leftTimeString += hours;
        }
        leftTimeString += ":";

        if (mins < 10){
            leftTimeString += "0" + mins;
        } else {
            leftTimeString += mins;
        }
        leftTimeString += ":";

        if (seconds < 10){
            leftTimeString += "0" + seconds;
        } else {
            leftTimeString += seconds;
        }

        return leftTimeString;
    }

    public static String netsPayTxnDtmRequestFormat(Date date){

        String newFormat= "yyyyMMdd HH:mm:ss.SSS";

        String formatedDate = "";
        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(date);

        return formatedDate;
    }

    public static boolean isInRedeem(String startDate){

        if (startDate == null || startDate.equals("")) return false;

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        DateTime now = new DateTime();
        DateTime redeemStartTime = parser.parseDateTime(startDate);

        long milliseconds = COUNTDOWN_TIME - (now.getMillis() - redeemStartTime.getMillis());

        return milliseconds > 0;
    }
}
