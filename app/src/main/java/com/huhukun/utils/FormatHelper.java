package com.huhukun.utils;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by kun on 18/08/2014.
 */
public class FormatHelper {

    public static final DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    public static final DateFormat shortDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
}
