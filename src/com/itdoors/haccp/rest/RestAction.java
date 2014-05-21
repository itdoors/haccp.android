package com.itdoors.haccp.rest;

import com.itdoors.haccp.utils.Logger;

public enum RestAction {
	
	INSERT_STATISTICS, UPDATE_POINT_STATUS;
	
	private static final String TAG = "RestAction";
	
	public static final int INSERT_STATISTICS_CODE = 0;
	public static final int UPDATE_POINT_STATUS_CODE = 1;
	
	public static RestAction valueOf(int code){
		switch (code) {
			case INSERT_STATISTICS_CODE:
				return INSERT_STATISTICS;
			case UPDATE_POINT_STATUS_CODE:
				return UPDATE_POINT_STATUS;
			default:
				String msg = "Unknown rest action code : " + code;
				Logger.Loge(TAG, msg);
				throw new IllegalArgumentException(msg);
				
		}
		
	}
	
	@Override
	public String toString() {
		switch (this) {
			case INSERT_STATISTICS:
				return "INSERT_STATISTICS";
			case UPDATE_POINT_STATUS:
				return "UPDATE_POINT_STATUS";
		}
		return super.toString();
	}
	
}
