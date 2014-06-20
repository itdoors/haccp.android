package com.itdoors.haccp;

public class Global {

	public static String BASE_URL = "http://haccp.itdoors.com.ua";
	
	private static String API_VERSION_CODE  = "v1";
	public static String API_URL = BASE_URL + "/api/" + API_VERSION_CODE;
	
	public static boolean loggingEnabled = true;
	
	public static String dateFormat = "yyyy-MM-dd";
	public static String usualDateFromat = "dd.MM.yyyy";
	
	public static String PREFERENCES_DOWNLOAD_DATE = "pref_down_date";
	public static String REST_PREFS = "rest_pref";

}
