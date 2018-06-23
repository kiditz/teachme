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

    public static String getTimeAgo(long duration) {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = (currentTime - duration) / 1000;
        long minutes = Math.round(timeElapsed / 60);
        long hours = Math.round(timeElapsed / 3600);
        long days = Math.round(timeElapsed / 86400);
        long weeks = Math.round(timeElapsed / 604800);
        long months = Math.round(timeElapsed / 2600640);
        long years = Math.round(timeElapsed / 31207680);

        // Seconds
        if (timeElapsed <= 60) {
            return "just now";
        }
        //Minutes
        else if (minutes <= 60) {
            if (minutes == 1) {
                return "one minute ago";
            } else {
                return minutes + " minutes ago";
            }
        }
        //Hours
        else if (hours <= 24) {
            if (hours == 1) {
                return "an hour ago";
            } else {
                return hours + " hrs ago";
            }
        }
        //Days
        else if (days <= 7) {
            if (days == 1) {
                return "yesterday";
            } else {
                return days + " days ago";
            }
        }
        //Weeks
        else if (weeks <= 4.3) {
            if (weeks == 1) {
                return "a week ago";
            } else {
                return weeks + " weeks ago";
            }
        }
        //Months
        else if (months <= 12) {
            if (months == 1) {
                return "a month ago";
            } else {
                return months + " months ago";
            }
        }
        //Years
        else {
            if (years == 1) {
                return "one year ago";
            } else {
                return years + " years ago";
            }
        }
    }
}
