package com.itdoors.haccp.json;

import java.util.List;

import org.apache.http.NameValuePair;

import com.itdoors.haccp.model.enums.HTTP_METHOD;

public class JSONRequestNameValuePair extends JSONRequest{
	
	private List<NameValuePair> params;
	
	public JSONRequestNameValuePair(String url, List<NameValuePair> params, JSONRequest.Callback callback) {
		this(url,HTTP_METHOD.POST, params,callback);
	}
	
	public JSONRequestNameValuePair(String url, HTTP_METHOD httpMethod, JSONRequest.Callback callback) {
		this(url,HTTP_METHOD.POST, null, callback);
	}
	
	public JSONRequestNameValuePair(String url, HTTP_METHOD httpMethod, List<NameValuePair> params, JSONRequest.Callback callback) {
		this.url = url;
		this.httpMethod = httpMethod;
		this.params = params;
		this.callback = callback;
	}
	
	public List<NameValuePair> getParams() {
		return params;
	}
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o)) return false;
		if(!(o instanceof JSONRequestNameValuePair)) return false;
		JSONRequestNameValuePair r = (JSONRequestNameValuePair)o;
		return r.params.equals(this.params);
	}
	@Override
	public int hashCode() {
		return super.hashCode() + params.hashCode();
	}
}
