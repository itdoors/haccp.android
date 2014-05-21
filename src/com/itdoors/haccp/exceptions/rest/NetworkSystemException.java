package com.itdoors.haccp.exceptions.rest;

public class NetworkSystemException extends Exception {

	private static final long serialVersionUID = 3175885975373289431L;
	
	public NetworkSystemException() {
		super();
	}
	public NetworkSystemException(String msg){
		super(msg);
	}
}
