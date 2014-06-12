package com.itdoors.haccp.rest.robospice_retrofit;

import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.Converter;

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
	
	@Override
	protected Builder createRestAdapterBuilder() {

		String serverUrl = getServerUrl();
		Converter converter = getConverter();
		
		RestAdapter.Builder builder = new RestAdapter.Builder()
			.setEndpoint(serverUrl)
			.setConverter(converter)
			.setLogLevel(LogLevel.FULL);
		
		return builder;
	}

}
