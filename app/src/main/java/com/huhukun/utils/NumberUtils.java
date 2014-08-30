package com.huhukun.utils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by kun on 19/08/2014.
 */
public class NumberUtils {
    public static String longToString(long songsId) {
        return songsId+"";
    }


    public static int getPercentage(BigDecimal realNumber, BigDecimal totalNumber){
        int percentage =  (int)Math.ceil(realNumber.divide(totalNumber, 0, BigDecimal.ROUND_HALF_UP).doubleValue());
        percentage = percentage > 100 ? 100: percentage;
        return percentage;
    }

    public static int getPercentage(Date start, Date end, Date current){
        double total = end.getTime() - start.getTime();
        double now = current.getTime() - start.getTime();
        return (int)Math.ceil(now / total * 100.0);
    }
}
