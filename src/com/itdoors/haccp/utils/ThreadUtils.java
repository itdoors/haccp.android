package com.itdoors.haccp.utils;

public class ThreadUtils {
	public static void addDelay(long delay){
		try {
			Thread.sleep(delay);
		} 	catch (InterruptedException e) {
			e.printStackTrace();
		}	
    }
}
