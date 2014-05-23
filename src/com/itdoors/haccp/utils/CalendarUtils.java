package com.itdoors.haccp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import com.itdoors.haccp.Global;


public class CalendarUtils {
	public static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}

	public static Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public static Date currentDate(){
		Calendar calendar  = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		return calendar.getTime();
	}
	
	public static Date addToCurrent(int field, int value){
		Calendar calendar = Calendar.getInstance();
		calendar.set(field, value);
		return calendar.getTime();
	}
	
	public static String toTimeStamp(Date date){
		return Long.toString(date.getTime() / 1000);
	}
	
	public static Date fromTimeStamp(String timeStamp){
		return new Date(Long.valueOf(timeStamp)*1000);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String inUsualDateFromat(String timeStamp){
		return new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(timeStamp)*1000)).toString();
	}
}
