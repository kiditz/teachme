package com.slerpio.teachme.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault());

    public static String printDate(long dateLong){
        return printDate(new Date(dateLong));
    }
    public static String printDate(Date date){
        return DATE_FORMAT.format(date);
    }
}
