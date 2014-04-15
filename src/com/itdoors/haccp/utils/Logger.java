package com.itdoors.haccp.utils;

import com.itdoors.haccp.BuildConfig;
import com.itdoors.haccp.Global;

import android.util.Log;

public class Logger {
	public static void Logi(Class<?> mClass, String msg){
		if(BuildConfig.DEBUG && Global.loggingEnabled){
			Log.i(mClass.getSimpleName(), msg);
		}
	}
	public static void Loge(Class<?> mClass, String msg){
		if(BuildConfig.DEBUG && Global.loggingEnabled){
			Log.e(mClass.getSimpleName(), msg);
		}
	}
}
