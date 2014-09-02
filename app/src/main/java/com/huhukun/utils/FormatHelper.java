package com.huhukun.utils;

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

    public static String toUTCString(Date date){
        DateFormat serverDateTimeFormatter = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
        serverDateTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return serverDateTimeFormatter.format(date);
    }

    public static String toLocalDateString(Date date){
        DateFormat serverDateTimeFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        serverDateTimeFormatter.setTimeZone(TimeZone.getDefault());
        return serverDateTimeFormatter.format(date);
    }

    public static String toLocalDateTimeString(Date date){
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
        DateFormat converter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        converter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return converter.format(date);
    }


}
