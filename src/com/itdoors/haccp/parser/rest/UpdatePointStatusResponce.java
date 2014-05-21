package com.itdoors.haccp.parser.rest;

public class UpdatePointStatusResponce implements Responce{

	private Responce mBaseResponceImpl = new BaseResponce();
	
	@Override
	public long getRequestId() {
		return mBaseResponceImpl.getRequestId();
	}
	@Override
	public void setRequestId(long requestId) {
		mBaseResponceImpl.setRequestId(requestId);
	}
	@Override
	public int getHttpStatusCode() {
		return mBaseResponceImpl.getHttpStatusCode();
	}
	@Override
	public void setHttpStatusCode(int httpStatusCode) {
		mBaseResponceImpl.setHttpStatusCode(httpStatusCode);
	}
	
	@Override
	public String toString() {
		return mBaseResponceImpl.toString();
	}

}
