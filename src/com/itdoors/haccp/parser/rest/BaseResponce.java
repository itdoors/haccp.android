package com.itdoors.haccp.parser.rest;

public class BaseResponce implements Responce {
	
	private long requestId;
	private int statusCode;
	
	@Override
	public long getRequestId() {
		return requestId;
	}

	@Override
	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	@Override
	public int getHttpStatusCode() {
		return statusCode;
	}

	@Override
	public void setHttpStatusCode(int httpStatusCode) {
		this.statusCode = httpStatusCode;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[")
			.append("request_id : " + requestId)
			.append(",")
			.append("responce_status : " + statusCode)
		.append("]");
		return sb.toString();
	}

}
