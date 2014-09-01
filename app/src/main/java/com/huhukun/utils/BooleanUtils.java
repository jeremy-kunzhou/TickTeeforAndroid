package com.huhukun.utils;

/**
 * Created by kun on 1/09/2014.
 */
public class BooleanUtils {
    public static boolean parse(String string){
        if (string.equals("0")){
            return false;
        } else if ( string.equals("1")){
            return true;
        } else {
            return Boolean.getBoolean(string);
        }
    }
}
