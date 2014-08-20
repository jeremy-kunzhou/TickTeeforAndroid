package com.huhukun.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kun on 19/08/2014.
 */
public class MyDateUtils {
    public static Date stringToDateForWS(String string) {
        return null;
    }

    public static long addToCurrent(int type, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(type, i);
        return calendar.getTimeInMillis();
    }

    public static long currentDateMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String dateToStringForWS(long dlDate) {
        return dlDate+"";
    }
}
