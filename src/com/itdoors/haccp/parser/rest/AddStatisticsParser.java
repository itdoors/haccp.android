package com.itdoors.haccp.parser.rest;

import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.model.rest.StatisticsRecord;

public class AddStatisticsParser implements Parser {

	@Override
	public Responce parse(String json) throws JSONException{
		
		JSONObject jRecord = new JSONObject(json);
		
		int recordId = jRecord.getInt("id");
		double recordValue = jRecord.getDouble("value");
		String recordEntrydateStr = jRecord.getString("entryDate");
		java.util.Date recordEntrydate = null;
		try{
			recordEntrydate = new java.util.Date(Long.valueOf(recordEntrydateStr)*1000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		int characteristicsId = jRecord.getInt("characteristicId");
		int pointId = jRecord.getInt("pointId");
		Responce responce = new AddStatisticsResponce(
								new StatisticsRecord(recordId, characteristicsId, recordEntrydate, recordValue, pointId)
							);
		return responce;
	}

}
