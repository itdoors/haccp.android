package com.itdoors.haccp.exceptions;

public class UserNeedToReauthorizeException extends Exception {

	
	private static final long serialVersionUID = 8321912563370787052L;
	
	public UserNeedToReauthorizeException() {
		// TODO Auto-generated constructor stub
	}
	public UserNeedToReauthorizeException(String msg){
		  super(msg);
	}

}
