package com.itdoors.haccp.exceptions;

public class ClientCancelExeption extends Exception{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 8615316317244141541L;

	public ClientCancelExeption() {
	}
	public ClientCancelExeption(String msg){
	  super(msg);
	}
}
