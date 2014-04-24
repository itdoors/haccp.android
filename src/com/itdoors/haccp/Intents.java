package com.itdoors.haccp;

public final class Intents {
	
	public static final class CalendarTimeRange{
		
		public static final String FROM_TIME_STAMP = "com.itdoors.haccp.Intents.CalendarTimeRange.FROM_TIME_STAMP";
		public static final String TO_TIME_STAMP = "com.itdoors.haccp.Intents.CalendarTimeRange.TO_TIME_STAMP";
	}
	public static final class Statistic{
		public static final String STATISTIC_RECORD = "com.itdoors.haccp.Intents.Statistic.STATISTIC_RECORD";
		
	}
	public static final class Status{
		public static final String CHANGED_STATUS = "com.itdoors.haccp.Intents.Statistic.STATUS_RECORD";
	}
	public static final class SyncComplete{
		
		public static final String ACTION_FINISHED_SYNC = "com.itdoors.haccp.action.ACTION_FINISHED_SYNC";
		public static final String LOCAL_SYNC_COMPELTED_SUCCESFULLY = "com.itdoors.haccp.Intents.LOCAL_SYNC_COMPELTED_SUCCESFULLY";
		
	}
}
