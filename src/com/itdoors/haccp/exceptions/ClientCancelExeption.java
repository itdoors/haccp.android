package com.itdoors.haccp.exceptions;

public class ClientCancelExeption extends Exception{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ClientCancelExeption() {
	}
	public ClientCancelExeption(String msg){
	  super(msg);
	}
}
