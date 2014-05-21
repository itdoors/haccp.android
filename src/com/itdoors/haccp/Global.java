package com.itdoors.haccp;

public class Global {

	private static String API_VERSION_CODE  = "v1";
	private static String URL = "http://haccp.itdoors.com.ua/api/";
	
	public static String API_URL = URL + API_VERSION_CODE;
	
	public static boolean loggingEnabled = true;
	public static boolean imgLoggingEnabled = false;
	
	public static String dateFormat = "yyyy-MM-dd";
	public static String usualDateFromat = "dd.MM.yyyy";
	
	public static String PREFERENCES_DOWNLOAD_DATE = "pref_down_date";
	public static String REST_PREFS = "rest_pref";

}
