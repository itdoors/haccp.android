package com.itdoors.haccp.json;

public class JSONResponce {
	
	private JSONRequest request;
	private Exception exception;
	private Object content;
	
	public JSONResponce(JSONRequest request, Exception exception, Object content) {
		this.request = request;
		this.exception = exception;
		this.content = content;
	}
	public Exception getException() {
		return exception;
	}
	public Object getContent() {
		return content;
	}
	public JSONRequest getRequest() {
		return request;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
