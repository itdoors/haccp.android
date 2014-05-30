package com.itdoors.haccp.utils;

import com.itdoors.haccp.BuildConfig;
import com.itdoors.haccp.Global;

import android.util.Log;

public class Logger {
	public static void Logi(Class<?> mClass, String msg){
		if(BuildConfig.DEBUG && Global.loggingEnabled){
			Log.i(mClass.getSimpleName(), msg == null ? "null" : msg);
		}
	}
	public static void Loge(Class<?> mClass, String msg){
		if(BuildConfig.DEBUG && Global.loggingEnabled){
			Log.e(mClass.getSimpleName(), msg);
		}
	}
	
	public static void Loge(Class<?> mClass, String msg, Throwable cause){
		if(BuildConfig.DEBUG && Global.loggingEnabled){
			Log.e(mClass.getSimpleName(), msg, cause);
		}
	}
	
	public static void Logd(Class<?> mClass, String msg){
		if(BuildConfig.DEBUG && Global.loggingEnabled){
			Log.d(mClass.getSimpleName(), msg);
		}
	}
	public static void Logd(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Global.loggingEnabled) {
            Log.d(tag, message);
        }
    }

    public static void Logd(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Global.loggingEnabled) {
            Log.d(tag, message, cause);
        }
    }

    public static void Logv(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
    	 if (BuildConfig.DEBUG && Global.loggingEnabled) {
            Log.v(tag, message);
        }
    }

    public static void Logv(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
    	 if (BuildConfig.DEBUG && Global.loggingEnabled) {
            Log.v(tag, message, cause);
        }
    }

    public static void Logi(final String tag, String message) {
    	if(BuildConfig.DEBUG && Global.loggingEnabled){
    		Log.i(tag, message);
    	}
    }

    public static void Logi(final String tag, String message, Throwable cause) {
    	if(BuildConfig.DEBUG && Global.loggingEnabled){
    		Log.i(tag, message, cause);
    	}
    }

    public static void Logw(final String tag, String message) {
    	if(BuildConfig.DEBUG && Global.loggingEnabled){
    		Log.w(tag, message);
    	}
    }

    public static void Logw(final String tag, String message, Throwable cause) {
    	if(BuildConfig.DEBUG && Global.loggingEnabled){
    		Log.w(tag, message, cause);
    	}
    }

    public static void Loge(final String tag, String message) {
    	if(BuildConfig.DEBUG && Global.loggingEnabled){
    		Log.e(tag, message);
    	}
    }

    public static void Loge(final String tag, String message, Throwable cause) {
    	if(BuildConfig.DEBUG && Global.loggingEnabled){
    		Log.e(tag, message, cause);
    	}
    }
}
