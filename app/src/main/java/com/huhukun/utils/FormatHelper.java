package com.huhukun.utils;

import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by kun on 18/08/2014.
 */
public class FormatHelper {
    private static final String TAG = App_Constants.APP_TAG + "FormatHelper";
    public static final String SERVER_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";

    public static final DateFormat serverDateTimeFormatter = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
    public static final DateFormat shortLocalDateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    public static String UseDateFormatter(DateFormat formatter, Date date)
    {
        if(date == null) return null;
        return formatter.format(date);
    }

    public static String fromUTCtoTimeZoneDate(Date date, TimeZone timeZone)
    {
        DateFormat converter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        converter.setTimeZone(timeZone);
        return converter.format(date);
    }

    public static String fromUTCToLocal(Date date)
    {
        if (date == null) return null;
        return fromUTCtoTimeZoneDate(date, TimeZone.getDefault());
    }

    public static String fromUTCStringToLocalString(String dateString) throws ParseException {
        DateFormat converter = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
        converter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = converter.parse(dateString);
        return fromUTCToLocal(date);


    }

    public static Date fromLocalDateStringToUTC(String dateString) throws ParseException {
        DateFormat converter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        converter.setTimeZone(TimeZone.getDefault());
        return converter.parse(dateString);
    }

    public static String fromLocalDateTimeStringToUTCString(String dateString) throws ParseException {
        return toUTCString(fromLocalDateTimeStringToUTC(dateString));
    }

    public static Date fromLocalDateTimeStringToUTC(String dateString) throws ParseException {
        DateFormat converter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        converter.setTimeZone(TimeZone.getDefault());
        return converter.parse(dateString);
    }

    public static String toUTCString(Date date){
        if (date == null) return null;
        DateFormat serverDateTimeFormatter = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
        serverDateTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return serverDateTimeFormatter.format(date);
    }

    public static String toLocalDateString(Date date){
        if (date == null) return null;
        DateFormat serverDateTimeFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        serverDateTimeFormatter.setTimeZone(TimeZone.getDefault());
        return serverDateTimeFormatter.format(date);
    }

    public static String toLocalDateTimeString(Date date){
        if (date == null) return null;
        DateFormat serverDateTimeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        serverDateTimeFormatter.setTimeZone(TimeZone.getDefault());
        return serverDateTimeFormatter.format(date);
    }

    public static Date toLocalDateFromUTCString(String string) throws ParseException {
        DateFormat serverDateTimeFormatter = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
        serverDateTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return serverDateTimeFormatter.parse(string);
    }

    public static String fromTimeZonetoUTCDate(Date date)
    {
        if (date == null) return null;
        DateFormat converter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        converter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return converter.format(date);
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }


}
