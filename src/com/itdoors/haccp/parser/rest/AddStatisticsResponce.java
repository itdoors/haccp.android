package com.itdoors.haccp.parser.rest;

import com.itdoors.haccp.model.rest.StatisticsRecord;

public class AddStatisticsResponce implements Responce {
	
	private Responce mBaseResponceImpl = new BaseResponce();
	private StatisticsRecord record;
	
	public AddStatisticsResponce(StatisticsRecord record) {
		this.record = record;
	}
	public StatisticsRecord getStatisticRecord(){
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
		sb.append("[")
			.append("request_id : ")
			.append(mBaseResponceImpl.getRequestId())
			.append(",")
			.append("responce_status : ")
			.append(mBaseResponceImpl.getHttpStatusCode())
			.append(",")
			.append("statisticrecord : ")
			.append(( record == null ? "null" : record.toString() ))
		.append("]");
		return sb.toString();
	}
}
