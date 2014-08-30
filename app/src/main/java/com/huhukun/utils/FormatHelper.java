package com.huhukun.utils;

import java.text.DateFormat;
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

    public static final DateFormat shortLocalDateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    public static final DateFormat shortLocalDateTimeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

    public static final DateFormat serverDateTimeFormatter = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
    public static final DateFormat serverDateFormatter = new SimpleDateFormat(SERVER_DATE_FORMAT);

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

    public static String fromTimeZonetoUTCDate(Date date)
    {
        DateFormat converter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        converter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return converter.format(date);
    }


}
