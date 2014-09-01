package com.huhukun.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by kun on 19/08/2014.
 */
public class NumberUtils {

    public static String longToString(long songsId) {
        return songsId+"";
    }
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private static final DecimalFormat decimalFormatNoDecimal = new DecimalFormat("0");


    public static int getPercentage(BigDecimal realNumber, BigDecimal totalNumber){
        int percentage =  (int)Math.ceil(realNumber.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        percentage = percentage > 100 ? 100: percentage;
        return percentage;
    }

    public static int getPercentage(Date start, Date end, Date current){
        double total = end.getTime() - start.getTime();
        double now = current.getTime() - start.getTime();
        return (int)Math.ceil(now / total * 100.0);
    }

    public static BigDecimal getNumberFromPercentage(int percentage, BigDecimal total){
        return total.multiply(new BigDecimal(percentage).divide(new BigDecimal(100)));
    }

    public static String decimalToString(BigDecimal decimal, boolean useDecimal){
        if(useDecimal){
            return decimalFormat.format(decimal.doubleValue());
        }
        else {
            return decimalFormatNoDecimal.format(decimal.doubleValue());
        }
    }


    public static int getAngle(BigDecimal realNumber, BigDecimal totalNumber) {
        if (totalNumber.compareTo(BigDecimal.ZERO) == 0) return 360;
        int angle =  (int)(realNumber.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP).doubleValue() * 360);
        angle = angle > 360 ? 360 : angle;
        return  angle;
    }

    public static int getAngle(int percentage) {
        return getAngle(new BigDecimal(percentage), new BigDecimal(100));
    }
}
