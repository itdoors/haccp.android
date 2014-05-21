package com.itdoors.haccp.parser.rest;

public interface Responce {
	
	public long getRequestId();
	public void setRequestId(long requestId);
	
	public int getHttpStatusCode();
	public void setHttpStatusCode(int httpStatusCode);
	public String toString();

}
