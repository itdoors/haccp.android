package com.itdoors.haccp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import com.itdoors.haccp.Global;

public class DateUtils {
	public static Date getDate(String timeStamp){
		try{
			return new java.util.Date( Long.valueOf(timeStamp)*1000 );
		}
		catch(Exception e){return null;}
	}
	@SuppressLint("SimpleDateFormat")
	public static String inUsualFormat(Date date){
		 return date == null ? "-" : new SimpleDateFormat(Global.usualDateFromat).format(date).toString();
	}
}
