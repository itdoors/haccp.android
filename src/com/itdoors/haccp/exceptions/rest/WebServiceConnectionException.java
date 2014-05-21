package com.itdoors.haccp.exceptions.rest;

public class WebServiceConnectionException extends Exception {

	
	private static final long serialVersionUID = -8489984478742938773L;
	
	private boolean retry = false;
	
	public WebServiceConnectionException() {
		super();
	}
	public WebServiceConnectionException(String msg){
		super(msg);
	}
	public WebServiceConnectionException(String conMsg, boolean retry) {
		super(conMsg);
		this.retry = retry;
	}

	public boolean isRetry(){
		return this.retry;
	}
}
