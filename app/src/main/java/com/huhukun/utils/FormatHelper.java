package com.huhukun.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by kun on 18/08/2014.
 */
public class FormatHelper {

    public static final DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    public static final DateFormat shortDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

    public static final DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


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
