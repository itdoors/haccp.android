package com.itdoors.haccp.rest.robospice_retrofit;

import com.itdoors.haccp.Global;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class MySpiceService extends RetrofitGsonSpiceService{
	
	@Override
	public void onCreate() {
		addRetrofitInterface(HaccpApi.class);
		super.onCreate();
	}
	
	@Override
	protected String getServerUrl() {
		return Global.BASE_URL;
	}
}
