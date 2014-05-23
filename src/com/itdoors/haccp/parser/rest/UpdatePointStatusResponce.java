package com.itdoors.haccp.parser.rest;

import com.itdoors.haccp.model.rest.PointRecord;

public class UpdatePointStatusResponce implements Responce{

	private Responce mBaseResponceImpl = new BaseResponce();
	private PointRecord record;
	
	public UpdatePointStatusResponce(PointRecord record) {
		this.record = record;
	}
	
	public PointRecord getPointRecord() {
		return record;
	}
	
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
		StringBuilder sb = new StringBuilder();
		sb.append('[')
		.append("point record:").append( record == null ? "null" : record.toString()).append(',')
		.append("recponce:").append(mBaseResponceImpl.toString())
		.append(']');
		return sb.toString();
	}

}
