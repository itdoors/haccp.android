package com.itdoors.haccp.rest;

import java.util.concurrent.Callable;
import android.content.Context;

public abstract class RESTTask implements Callable<Boolean>{
	
	protected final Context context;
	
	public RESTTask(Context context) {
		this.context = context;
	}
	
	
	
}
