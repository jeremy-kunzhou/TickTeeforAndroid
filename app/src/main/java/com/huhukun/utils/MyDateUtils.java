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

    public static long getNumberOfDayOfWeekInDays(long days, int firstDayOfWeek, int... dayOfWeek)
    {
        if (dayOfWeek == null) return  0;
        if (dayOfWeek.length == 7) return days;
        long count = 0;
        long weeks = days / 7;
        long offset = days % 7;
        if (offset > 0) {
            int[] weekOfDays = new int[(int)offset];
            for (int i = 0; i < offset; i++) {
                weekOfDays[i] = (firstDayOfWeek - 1 + i) % 7 + 1;
            }
            next: for (int j = 0; j < dayOfWeek.length; j++) {
                for (int i = 0; i < weekOfDays.length; i++) {
                    if (dayOfWeek[j] == weekOfDays[i]) {
                        count++;
                        continue next;
                    }
                }
            }
        }
        count += weeks * dayOfWeek.length;
        return count;
    }
}
