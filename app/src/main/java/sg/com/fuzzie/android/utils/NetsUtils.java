package sg.com.fuzzie.android.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static sg.com.fuzzie.android.utils.Constants.NETSPAY_INCOMING_REQUEST_IP;
import static sg.com.fuzzie.android.utils.Constants.NETSPAY_MID;
import static sg.com.fuzzie.android.utils.Constants.NETSPAY_S2S;

public class NetsUtils {

    public static String netsError(String responseCode){

        String errorMessage = "";
        String errorCode = "";

        String[] splits = responseCode.split("-");
        if (splits.length == 2){
            errorCode = splits[1];
        }

        switch (errorCode){

            case "":
                errorMessage = "Unknown Error";
                break;
            case "01000":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01001":
                errorMessage = "Payment declined. Please try again.";
                break;
            case "01003":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01004":
                errorMessage = "Payment declined. Invalid amount.";
                break;
            case "01005":
                errorMessage = "Payment declined. Invalid account.";
                break;
            case "01006":
                errorMessage = "Payment declined. Invalid account.";
                break;
            case "01007":
                errorMessage = "Payment declined. Invalid account.";
                break;
            case "01010":
                errorMessage = "Payment declined. Exceeded activity/pin-retry Limit.";
                break;
            case "01011":
                errorMessage = "Payment declined. Exceeded Account Limit.";
                break;
            case "01098":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01099":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01100":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01101":
                errorMessage = "Payment declined. Invalid Credit Card Number.";
                break;
            case "01102":
                errorMessage = "Payment declined. Invalid Expiry Date.";
                break;
            case "01103":
                errorMessage = "Payment declined. Expired Card.";
                break;
            case "01104":
                errorMessage = "Payment declined. Invalid CVV.";
                break;
            case "01105":
                errorMessage = "Payment declined. Invalid Data.";
                break;
            case "01200":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01201":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01202":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01203":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01204":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01301":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "01302":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "02000":
                errorMessage = "Payment declined. Please contact Merchant.";
                break;
            case "02001":
                errorMessage = "Payment declined. Time Out.";
                break;
            case "02002":
                errorMessage = "Payment declined. User Session Expired.";
                break;
            case "02003":
                errorMessage = "Payment declined. User Cancelled Txn";
                break;
            case "02010":
                errorMessage = "Payment declined. User Cancelled Txn.";
                break;
            case "02200":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "02201":
                errorMessage = "Payment declined. Please contact card issuer.";
                break;
            case "02800":
                errorMessage = "Service unavailable. Please try again later.";
                break;
            case "02801":
                errorMessage = "Service unavailable. Please try again later.";
                break;
            case "02850":
                errorMessage = "Service unavailable. Please try again later.";
                break;
            case "02852":
                errorMessage = "Service unavailable. Please try again later.";
                break;
            case "03117":
                errorMessage = "System Error. Invalid Amount.";
                break;
            case "03118":
                errorMessage = "System Error. Invalid Amount. Below transaction threshold amount.";
                break;
            case "03119":
                errorMessage = "System Error. Invalid Amount. Exceed transaction threshold amount.";
                break;
            case "50104":
                errorMessage = "Payment declined. Transaction cancelled by user.";
                break;
            case "50151":
                errorMessage = "Payment declined. Time out, please try again.";
                break;
            case "50152":
                errorMessage = "Payment declined. Time out, please try again.";
                break;
            case "69002":
                errorMessage = "Payment declined. Time out, please try again.";
                break;
            case "69071":
                errorMessage = "Payment declined. Please install NETSPay Application.";
                break;
            case "69072":
                errorMessage = "Payment declined. Please try again.";
                break;
            case "69073":
                errorMessage = "Payment declined. Please try again.";
                break;
            case "69074":
                errorMessage = "Payment declined. Please try again.";
                break;
            default:
                errorMessage = "System Error. Please contact merchant.";
                break;
        }

        errorMessage = errorMessage + " - " + responseCode;

        return errorMessage;
    }

    public static String getTransactionRequest(String txnRef, int payAmount){

        Date date = Calendar.getInstance().getTime();

        return "{\"ss\":\"1\"," +
                "\"msg\":{" +
                "\"netsMid\":\"" + NETSPAY_MID + "\"," +
                "\"ss\":\"1\"," +
                "\"submissionMode\":\"B\"," +
                "\"txnAmount\":\"" + String.valueOf(payAmount) + "\"," +
                "\"merchantTxnRef\":\"" + txnRef + "\"," +
                "\"merchantTxnDtm\":\"" + TimeUtils.netsPayTxnDtmRequestFormat(date) + "\"," +
                "\"paymentType\":\"SALE\"," +
                "\"currencyCode\":\"SGD\"," +
                "\"merchantTimeZone\":\"+8\"," +
                "\"b2sTxnEndURL\":\"\"," +
                "\"s2sTxnEndURL\":\"" + NETSPAY_S2S + "\"," +
                "\"clientType\":\"S\"," +
                "\"netsMidIndicator\":\"U\"," +
                "\"ipAddress\":\"" + NETSPAY_INCOMING_REQUEST_IP + "\"," +
                "\"language\":\"en\"," +
                "\"mobileOS\":\"ANDROID\"}}";
    }

    public static String getRandomString(int length){

        final String DATA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0 ; i < length ; i++){
            sb.append(DATA.charAt(random.nextInt(DATA.length())));
        }

        return sb.toString();
    }
}
