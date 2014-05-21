package com.itdoors.haccp.exceptions;

public class ServerFailedException extends Exception{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 3266636237457769511L;
	
	public ServerFailedException() {
	}
	  public ServerFailedException(String msg){
		  super(msg);
	  }
}
