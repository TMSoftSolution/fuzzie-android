package sg.com.fuzzie.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sg.com.fuzzie.android.R;
import sg.com.fuzzie.android.api.models.Category;
import sg.com.fuzzie.android.api.models.PaymentMethod;

import static sg.com.fuzzie.android.utils.Constants.NETSPAY_APP_PACKAGE_NAME;

/**
 * Created by nurimanizam on 17/12/16.
 */

public class FuzzieUtils {

    public static String[] SORT_BY = {"Trending", "Likes", "Cashback Percentage", "Name"};
    public static String[] SORT_BY_JACKPOT = {"Trending", "Price (high)", "Price (low)", "Lowest cost per ticket", "Jackpot tickets given", "Cashback", "Name (A to Z)"};

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static Category getSubCategory(List<Category> subCategories, int subCategoryId) {
        Category subCategory = null;

        for (Category category : subCategories) {
            if (category.getId() == subCategoryId) {
                subCategory = category;
                break;
            }
        }
        return subCategory;
    }

    public static int getSubCategoryWhiteIcon(int subCategoryId) {
        switch (subCategoryId) {
            case 1:
                return  R.drawable.ic_category_1_white;
            case 2:
                return  R.drawable.ic_category_2_white;
            case 3:
                return  R.drawable.ic_category_3_white;
            case 4:
                return  R.drawable.ic_category_4_white;
            case 5:
                return  R.drawable.ic_category_5_white;
            case 6:
                return  R.drawable.ic_category_6_white;
            case 7:
                return  R.drawable.ic_category_7_white;
            case 8:
                return  R.drawable.ic_category_8_white;
            case 9:
                return  R.drawable.ic_category_9_white;
            case 10:
                return  R.drawable.ic_category_10_white;
            case 11:
                return  R.drawable.ic_category_11_white;
            case 12:
                return  R.drawable.ic_category_12_white;
            case 13:
                return  R.drawable.ic_category_13_white;
            case 14:
                return  R.drawable.ic_category_14_white;
            case 15:
                return  R.drawable.ic_category_15_white;
            case 16:
                return  R.drawable.ic_category_16_white;
            case 17:
                return  R.drawable.ic_category_17_white;
            case 18:
                return  R.drawable.ic_category_18_white;
            case 19:
                return  R.drawable.ic_category_19_white;
            case 20:
                return  R.drawable.ic_category_20_white;
            case 21:
                return  R.drawable.ic_category_21_white;
            case 22:
                return  R.drawable.ic_category_22_white;
            case 23:
                return  R.drawable.ic_category_23_white;
            case 24:
                return  R.drawable.ic_category_24_white;
            case 25:
                return  R.drawable.ic_category_25_white;
            case 26:
                return  R.drawable.ic_category_26_white;
            case 27:
                return  R.drawable.ic_category_27_white;
            case 28:
                return  R.drawable.ic_category_28_white;
            case 29:
                return  R.drawable.ic_category_29_white;
            case 30:
                return R.drawable.ic_category_30_white;
            case 31:
                return R.drawable.ic_category_31_white;
            case 32:
                return R.drawable.ic_category_32_white;

            default:
                return R.drawable.ic_category_30_white;
        }
    }

    public static int getSubCategoryRedIcon(int subCategoryId) {
        switch (subCategoryId) {
            case 1:
                return  R.drawable.ic_category_1_red;
            case 2:
                return  R.drawable.ic_category_2_red;
            case 3:
                return  R.drawable.ic_category_3_red;
            case 4:
                return  R.drawable.ic_category_4_red;
            case 5:
                return  R.drawable.ic_category_5_red;
            case 6:
                return  R.drawable.ic_category_6_red;
            case 7:
                return  R.drawable.ic_category_7_red;
            case 8:
                return  R.drawable.ic_category_8_red;
            case 9:
                return  R.drawable.ic_category_9_red;
            case 10:
                return  R.drawable.ic_category_10_red;
            case 11:
                return  R.drawable.ic_category_11_red;
            case 12:
                return  R.drawable.ic_category_12_red;
            case 13:
                return  R.drawable.ic_category_13_red;
            case 14:
                return  R.drawable.ic_category_14_red;
            case 15:
                return  R.drawable.ic_category_15_red;
            case 16:
                return  R.drawable.ic_category_16_red;
            case 17:
                return  R.drawable.ic_category_17_red;
            case 18:
                return  R.drawable.ic_category_18_red;
            case 19:
                return  R.drawable.ic_category_19_red;
            case 20:
                return  R.drawable.ic_category_20_red;
            case 21:
                return  R.drawable.ic_category_21_red;
            case 22:
                return  R.drawable.ic_category_22_red;
            case 23:
                return  R.drawable.ic_category_23_red;
            case 24:
                return  R.drawable.ic_category_24_red;
            case 25:
                return  R.drawable.ic_category_25_red;
            case 26:
                return  R.drawable.ic_category_26_red;
            case 27:
                return  R.drawable.ic_category_27_red;
            case 28:
                return  R.drawable.ic_category_28_red;
            case 29:
                return  R.drawable.ic_category_29_red;
            case 30:
                return R.drawable.ic_category_30_red;
            case 31:
                return R.drawable.ic_category_31_red;
            case 32:
                return R.drawable.ic_category_32_red;

            default:
                return R.drawable.ic_category_30_red;
        }
    }

    public static int getSubCategoryBlackIcon(int subCategoryId) {
        switch (subCategoryId) {
            case 1:
                return  R.drawable.ic_category_1_black;
            case 2:
                return  R.drawable.ic_category_2_black;
            case 3:
                return  R.drawable.ic_category_3_black;
            case 4:
                return  R.drawable.ic_category_4_black;
            case 5:
                return  R.drawable.ic_category_5_black;
            case 6:
                return  R.drawable.ic_category_6_black;
            case 7:
                return  R.drawable.ic_category_7_black;
            case 8:
                return  R.drawable.ic_category_8_black;
            case 9:
                return  R.drawable.ic_category_9_black;
            case 10:
                return  R.drawable.ic_category_10_black;
            case 11:
                return  R.drawable.ic_category_11_black;
            case 12:
                return  R.drawable.ic_category_12_black;
            case 13:
                return  R.drawable.ic_category_13_black;
            case 14:
                return  R.drawable.ic_category_14_black;
            case 15:
                return  R.drawable.ic_category_15_black;
            case 16:
                return  R.drawable.ic_category_16_black;
            case 17:
                return  R.drawable.ic_category_17_black;
            case 18:
                return  R.drawable.ic_category_18_black;
            case 19:
                return  R.drawable.ic_category_19_black;
            case 20:
                return  R.drawable.ic_category_20_black;
            case 21:
                return  R.drawable.ic_category_21_black;
            case 22:
                return  R.drawable.ic_category_22_black;
            case 23:
                return  R.drawable.ic_category_23_black;
            case 24:
                return  R.drawable.ic_category_24_black;
            case 25:
                return  R.drawable.ic_category_25_black;
            case 26:
                return  R.drawable.ic_category_26_black;
            case 27:
                return  R.drawable.ic_category_27_black;
            case 28:
                return  R.drawable.ic_category_28_black;
            case 29:
                return  R.drawable.ic_category_29_black;
            case 30:
                return R.drawable.ic_category_30_black;
            case 31:
                return R.drawable.ic_category_31_black;
            case 32:
                return R.drawable.ic_category_32_black;

            default:
                return R.drawable.ic_category_30_black;
        }
    }

    public static int getMarkerRedIcon(int subCategoryId){
        switch (subCategoryId) {
            case 1:
                return  R.drawable.marker_category_select_1;
            case 2:
                return  R.drawable.marker_category_select_2;
            case 3:
                return  R.drawable.marker_category_select_3;
            case 4:
                return  R.drawable.marker_category_select_4;
            case 5:
                return  R.drawable.marker_category_select_5;
            case 6:
                return  R.drawable.marker_category_select_6;
            case 7:
                return  R.drawable.marker_category_select_7;
            case 8:
                return  R.drawable.marker_category_select_8;
            case 9:
                return  R.drawable.marker_category_select_9;
            case 10:
                return  R.drawable.marker_category_select_10;
            case 11:
                return  R.drawable.marker_category_select_11;
            case 12:
                return  R.drawable.marker_category_select_12;
            case 13:
                return  R.drawable.marker_category_select_13;
            case 14:
                return  R.drawable.marker_category_select_14;
            case 15:
                return  R.drawable.marker_category_select_15;
            case 16:
                return  R.drawable.marker_category_select_16;
            case 17:
                return  R.drawable.marker_category_select_17;
            case 18:
                return  R.drawable.marker_category_select_18;
            case 19:
                return  R.drawable.marker_category_select_19;
            case 20:
                return  R.drawable.marker_category_select_20;
            case 21:
                return  R.drawable.marker_category_select_21;
            case 22:
                return  R.drawable.marker_category_select_22;
            case 23:
                return  R.drawable.marker_category_select_23;
            case 24:
                return  R.drawable.marker_category_select_24;
            case 25:
                return  R.drawable.marker_category_select_25;
            case 26:
                return  R.drawable.marker_category_select_26;
            case 27:
                return  R.drawable.marker_category_select_27;
            case 28:
                return  R.drawable.marker_category_select_28;
            case 29:
                return  R.drawable.marker_category_select_29;
            case 30:
                return R.drawable.marker_category_select_30;
            case 31:
                return R.drawable.marker_category_select_40;

            default:
                return R.drawable.marker_category_select_30;
        }
    }

    public static int getMarkerWhiteIcon(int subCategoryId){
        switch (subCategoryId) {
            case 1:
                return  R.drawable.marker_category_unselect_1;
            case 2:
                return  R.drawable.marker_category_unselect_2;
            case 3:
                return  R.drawable.marker_category_unselect_3;
            case 4:
                return  R.drawable.marker_category_unselect_4;
            case 5:
                return  R.drawable.marker_category_unselect_5;
            case 6:
                return  R.drawable.marker_category_unselect_6;
            case 7:
                return  R.drawable.marker_category_unselect_7;
            case 8:
                return  R.drawable.marker_category_unselect_8;
            case 9:
                return  R.drawable.marker_category_unselect_9;
            case 10:
                return  R.drawable.marker_category_unselect_10;
            case 11:
                return  R.drawable.marker_category_unselect_11;
            case 12:
                return  R.drawable.marker_category_unselect_12;
            case 13:
                return  R.drawable.marker_category_unselect_13;
            case 14:
                return  R.drawable.marker_category_unselect_14;
            case 15:
                return  R.drawable.marker_category_unselect_15;
            case 16:
                return  R.drawable.marker_category_unselect_16;
            case 17:
                return  R.drawable.marker_category_unselect_17;
            case 18:
                return  R.drawable.marker_category_unselect_18;
            case 19:
                return  R.drawable.marker_category_unselect_19;
            case 20:
                return  R.drawable.marker_category_unselect_20;
            case 21:
                return  R.drawable.marker_category_unselect_21;
            case 22:
                return  R.drawable.marker_category_unselect_22;
            case 23:
                return  R.drawable.marker_category_unselect_23;
            case 24:
                return  R.drawable.marker_category_unselect_24;
            case 25:
                return  R.drawable.marker_category_unselect_25;
            case 26:
                return  R.drawable.marker_category_unselect_26;
            case 27:
                return  R.drawable.marker_category_unselect_27;
            case 28:
                return  R.drawable.marker_category_unselect_28;
            case 29:
                return  R.drawable.marker_category_unselect_29;
            case 30:
                return R.drawable.marker_category_unselect_30;
            case 31:
                return R.drawable.marker_category_unselect_40;

            default:
                return R.drawable.marker_category_unselect_30;
        }
    }

    public static int getMarkerWhiteStoreCounts(int storeCounts){
        switch (storeCounts) {
            case 2:
                return R.drawable.marker_category_unselect_31;
            case 3:
                return R.drawable.marker_category_unselect_32;
            case 4:
                return R.drawable.marker_category_unselect_33;
            case 5:
                return R.drawable.marker_category_unselect_34;
            case 6:
                return R.drawable.marker_category_unselect_35;
            case 7:
                return R.drawable.marker_category_unselect_36;
            case 8:
                return R.drawable.marker_category_unselect_37;
            case 9:
                return R.drawable.marker_category_unselect_38;
            default:
                return R.drawable.marker_category_unselect_39;
        }
    }

    public static int getMarkerRedStoreCounts(int storeCounts){
        switch (storeCounts) {
            case 2:
                return R.drawable.marker_category_select_31;
            case 3:
                return R.drawable.marker_category_select_32;
            case 4:
                return R.drawable.marker_category_select_33;
            case 5:
                return R.drawable.marker_category_select_34;
            case 6:
                return R.drawable.marker_category_select_35;
            case 7:
                return R.drawable.marker_category_select_36;
            case 8:
                return R.drawable.marker_category_select_37;
            case 9:
                return R.drawable.marker_category_select_38;
            default:
                return R.drawable.marker_category_select_9;
        }
    }

    public  static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public  static boolean isValidPhone(String target) {
        return !TextUtils.isEmpty(target) && target.matches("[0-9]{8}") && ((target.charAt(0) == '3') || (target.charAt(0) == '8') || (target.charAt(0) == '9' || target.charAt(0) == '2'));
    }

    public static int generateRandomAvatarId(){
        Random r = new Random();
        return r.nextInt(7);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        final double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static boolean isCuttingOffLiveDraw(Context context){

        CurrentUser currentUser = CurrentUser_.getInstance_(context);
        if (currentUser.getCurrentUser() != null && currentUser.getJackpotLiveDrawDateTime() != null){
            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
            DateTime now = new DateTime();
            DateTime drawTime = parser.parseDateTime(currentUser.getJackpotLiveDrawDateTime());

            if (now.getMillis() >= drawTime.getMillis() - 15 * 60000){
                return true;
            }
        }

        return false;
    }

    public static SpannableString getFormattedValue(Context context, double balance, float scale){

        String value = "";
        String currency = "S$";
        String intValue = "";
        String decimalValue = "";
        String [] splits = String.valueOf(balance).split("\\.");
        intValue = splits[0];
        if (splits.length > 1 && balance % 1 != 0){
            decimalValue = "." + splits[1];
            if (decimalValue.length() == 2){
                decimalValue = decimalValue + "0";
            } else if (decimalValue.length() >= 3){
                decimalValue = decimalValue.substring(0, 3);
            }
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Light.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);

        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Black.ttf");
        TypefaceSpan typefaceSpan1 = new CustomTypefaceSpan("", typeface1);

        SpannableString spannableString;
        if (decimalValue.length() == 0){
            value = currency + intValue;
            spannableString = new SpannableString(value);
            spannableString.setSpan(typefaceSpan, 0, currency.length(), 0);

        } else {
            value = currency + intValue + decimalValue;
            spannableString = new SpannableString(value);
            spannableString.setSpan(typefaceSpan, 0, currency.length(), 0);
            spannableString.setSpan(new RelativeSizeSpan(scale), currency.length() + intValue.length(), value.length(), 0);
        }

        spannableString.setSpan(typefaceSpan1, currency.length(), currency.length() + intValue.length(), 0);

        return spannableString;

    }

    public static SpannableString getFormattedValue(double balance, float scale){

        String value = "";
        String currency = "S$";
        String intValue = "";
        String decimalValue = "";
        String [] splits = String.valueOf(balance).split("\\.");
        intValue = splits[0];
        if (splits.length > 1 && balance % 1 != 0){
            decimalValue = "." + splits[1];
            if (decimalValue.length() == 2){
                decimalValue = decimalValue + "0";
            } else if (decimalValue.length() >= 3){
                decimalValue = decimalValue.substring(0, 3);
            }
        }

        SpannableString spannableString;
        if (decimalValue.length() == 0){
            value = currency + intValue;
            spannableString = new SpannableString(value);

        } else {
            value = currency + intValue + decimalValue;
            spannableString = new SpannableString(value);
            spannableString.setSpan(new RelativeSizeSpan(scale), currency.length() + intValue.length(), value.length(), 0);
        }

        return spannableString;

    }

    public static SpannableString getFormattedPaymentSuccessCashbackValue(Context context, double balance, float scale){

        String value = "";
        String currency = "+S$";
        String intValue = "";
        String decimalValue = "";
        String [] splits = String.valueOf(balance).split("\\.");
        intValue = splits[0];
        if (splits.length > 1 && balance % 1 != 0){
            decimalValue = "." + splits[1];
            if (decimalValue.length() == 2){
                decimalValue = decimalValue + "0";
            } else if (decimalValue.length() >= 3){
                decimalValue = decimalValue.substring(0, 3);
            }
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato_Light.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);

        SpannableString spannableString;
        if (decimalValue.length() == 0){
            value = currency + intValue;
            spannableString = new SpannableString(value);
            spannableString.setSpan(typefaceSpan, 0, currency.length(), 0);

        } else {
            value = currency + intValue + decimalValue;
            spannableString = new SpannableString(value);
            spannableString.setSpan(typefaceSpan, 0, currency.length(), 0);
            spannableString.setSpan(new RelativeSizeSpan(scale), currency.length() + intValue.length(), value.length(), 0);
        }

        return spannableString;

    }

    public static SpannableString getFormattedCashbackPercentage(double percentage, float scale){

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(1);
        format.setRoundingMode(RoundingMode.HALF_UP);

        String suffix = "%";
        String value = "";
        String intValue = "";
        String decimalValue = "";
        String [] splits = format.format(percentage).split("\\.");
        intValue = splits[0];
        if (splits.length > 1 && percentage % 1 != 0){
            decimalValue = "." + splits[1];
        }

        SpannableString spannableString;
        if (decimalValue.length() == 0){
            value = intValue + suffix;
            spannableString = new SpannableString(value);

        } else {
            value = intValue + decimalValue + suffix;
            spannableString = new SpannableString(value);
        }

        return spannableString;

    }

    public static SpannableString getFormattedPowerUpPercentage(double percentage, float scale){

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(1);
        format.setRoundingMode(RoundingMode.HALF_UP);

        String value = "";
        String prefix = "+";
        String suffix = "%";
        String intValue = "";
        String decimalValue = "";
        String [] splits = format.format(percentage).split("\\.");
        intValue = splits[0];
        if (splits.length > 1 && percentage % 1 != 0){
            decimalValue = "." + splits[1];
        }

        SpannableString spannableString;
        if (decimalValue.length() == 0){
            value = prefix + intValue + suffix;
            spannableString = new SpannableString(value);

        } else {
            value = prefix + intValue + decimalValue + suffix;
            spannableString = new SpannableString(value);
            spannableString.setSpan(new RelativeSizeSpan(scale), prefix.length() + intValue.length(), value.length(), 0);
        }

        return spannableString;

    }

    public static String getFormattedPercentage(double percentage){

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(1);
        format.setRoundingMode(RoundingMode.HALF_UP);

        return format.format(percentage);
    }

    public static PaymentCardType getPaymentCardType(String cardNumber){

        String visaRegex = "^4[0-9]{6,}$";
        String masterRegex = "^5[1-5][0-9]{5,}|222[1-9][0-9]{3,}|22[3-9][0-9]{4,}|2[3-6][0-9]{5,}|27[01][0-9]{4,}|2720[0-9]{3,}$";
        String amexRegex = "^3[47][0-9]{5,}$";

        if (cardNumber.matches(visaRegex)){

            return PaymentCardType.PAYMENT_CARD_TYPE_VISA;

        } else if (cardNumber.matches(masterRegex)){

            return PaymentCardType.PAYMENT_CARD_TYPE_MASTER;

        } else if (cardNumber.matches(amexRegex)){

            return PaymentCardType.PAYMENT_CARD_TYPE_AMEX;

        }

        return PaymentCardType.PAYMENT_CARD_TYPE_NONE;
    }

    public static int getPaymentCardIcon(PaymentCardType cardType){

        switch (cardType){

            case PAYMENT_CARD_TYPE_VISA:
                return R.drawable.card_visa;

            case PAYMENT_CARD_TYPE_MASTER:
                return R.drawable.card_mastercard;

            case PAYMENT_CARD_TYPE_AMEX:
                return R.drawable.card_amex;

            case PAYMENT_CARD_TYPE_NONE:
                return R.drawable.card_default;

        }

        return R.drawable.card_default;
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints  = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        hints.put(EncodeHintType.MARGIN, 0);
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static String generateSignature(String txnReq,String secretKey) throws
            Exception{
        String concatPayloadAndSecretKey = txnReq + secretKey;
        String hmac =
                encodeBase64(hashSHA256ToBytes(concatPayloadAndSecretKey.getBytes()));
        System.out.println("hmac" + hmac);
        return hmac;
    }

    private static byte[] hashSHA256ToBytes(byte[] input) throws Exception
    {
        byte[] byteData = null;
        MessageDigest md = MessageDigest.getInstance("SHA256");
        md.update(input);
        byteData = md.digest();
        return byteData;
    }

    private static String encodeBase64(byte[] data)
    {
        return new String(Base64.encode(data, Base64.NO_WRAP));
    }

    public static PaymentMethod eNetsPaymentMethod(){

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setCardType("ENETS");

        return paymentMethod;
    }

    public static boolean isNetsInstalled(Context context){

        PackageManager pm = context.getPackageManager();

        try {

            PackageInfo info=pm.getPackageInfo(NETSPAY_APP_PACKAGE_NAME, PackageManager.GET_META_DATA);
            return true;

        } catch (PackageManager.NameNotFoundException e){

            return false;
        }
    }
}
