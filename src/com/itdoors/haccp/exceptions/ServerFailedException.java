package com.itdoors.haccp.exceptions;

public class ServerFailedException extends Exception{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ServerFailedException() {
	}
	  public ServerFailedException(String msg){
		  super(msg);
	  }
}
