package com.itdoors.haccp.model.rest.retrofit;

import com.octo.android.robospice.request.SpiceRequest;

public class SpiceRequestInfo<T> {
	
	private SpiceRequest<T> spiceRequest;
	private String cachekey;
	private long durration;
	
	public SpiceRequestInfo( SpiceRequest<T> spiceRequest, String cachekey, long durration){
		this.spiceRequest = spiceRequest;
		this.cachekey = cachekey;
		this.durration = durration;
	}
	
	public SpiceRequest<T> getSpiceRequest() {
		return spiceRequest;
	}
	public String getCachekey() {
		return cachekey;
	}
	public long getDurration() {
		return durration;
	}
	
}
