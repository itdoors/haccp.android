package com.itdoors.haccp.exceptions.rest;

public class WebServiceFailedException extends Exception {

	private static final long serialVersionUID = 4633112160053338910L;
	
	
	public WebServiceFailedException() {
		super();
	}
	public WebServiceFailedException(String msg){
		super(msg);
	}
	public WebServiceFailedException(String msg, Exception e) {
		super(msg, e);
	}
	
}
