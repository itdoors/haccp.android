package com.itdoors.haccp.exceptions.rest;

public class AuthenticationFailureException extends Exception {

	private static final long serialVersionUID = 7812663754873849353L;
	
	public AuthenticationFailureException() {
		super();
	}
	public AuthenticationFailureException(String msg){
		super(msg);
	}
}
