package com.itdoors.haccp.json;

import com.itdoors.haccp.model.enums.HTTP_METHOD;

public class JSONRequest {

	public interface Callback{
		void onComplete(JSONResponce responce);
	}
	
	protected String url;
	protected Callback callback;
	protected boolean isCanceled;
	protected HTTP_METHOD httpMethod;

	public String getUrl() {
		return url;
	}
	
	public Callback getCallback() {
		return callback;
	}
	public void cancel(){
		this.isCanceled = true;
	}
	public void enable(){
		this.isCanceled = false;
	}
	public boolean isCanceled(){
		return this.isCanceled;
	}
	
	public HTTP_METHOD getHttpMethod() {
		return httpMethod;
	}
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(!(o instanceof JSONRequest)) return false;
		JSONRequest r = (JSONRequest)o;
		return r.url.equals(this.url) && r.isCanceled == this.isCanceled;
	}
	
	@Override
	public int hashCode() {
		return url.hashCode() + Boolean.valueOf(isCanceled).hashCode();
	}
	
}
