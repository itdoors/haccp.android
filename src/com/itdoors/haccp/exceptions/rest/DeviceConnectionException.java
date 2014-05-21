package com.itdoors.haccp.exceptions.rest;


public class DeviceConnectionException extends Exception {

	private static final long serialVersionUID = -6125451761022992738L;

	private boolean retry = false;
	
	public DeviceConnectionException() {
		super();
	}
	public DeviceConnectionException(String msg){
		super(msg);
	}
	public DeviceConnectionException(String msg, Exception e) {
		super(msg, e);
	}
	public void setRetry(boolean retry) {
		this.retry = retry;
	}
	public boolean isRetry(){
		return retry;
	}
}
