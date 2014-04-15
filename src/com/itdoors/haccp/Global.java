package com.itdoors.haccp;

public class Global {

	private static String API_VERSION_CODE  = "v1";
	private static String URL = "http://haccp.itdoors.com.ua/api/";
	
	public static String API_URL = URL + API_VERSION_CODE;
	
	
	public static boolean loggingEnabled = true;
	public static boolean imgLoggingEnabled = false;
	
	public static String dateFormat = "yyyy-MM-dd";
	public static String usualDateFromat = "dd.MM.yyyy";
		
	public static final int TWEET_LENGTH  = 118;
}
