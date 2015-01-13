
package com.itdoors.haccp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

import com.itdoors.haccp.Config;

public final class DateUtils {
    private DateUtils() {
    }

    public static Date getDate(String timeStamp) {
        try {
            return new java.util.Date(Long.valueOf(timeStamp) * 1000);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String inUsualFormat(Date date) {
        return date == null ? "-" : new SimpleDateFormat(Config.usualDateFromat).format(date)
                .toString();
    }

    public static Long getCurrentTime() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }
}
