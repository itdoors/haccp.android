package com.itdoors.haccp.parser;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.StatisticsRecord;

public class AddStatisticResponceParser implements Parser{
	public class Content implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public StatisticsRecord record;
		
	}

	@Override
	public Object parse(String json) throws JSONException,
			ServerFailedException {

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
		
		JSONObject jCharacteristic = jRecord.getJSONObject("characteristic");
		int charId = jCharacteristic.getInt("id");
		String charName = jCharacteristic.getString("name");
		String unit = jCharacteristic.getString("unit");
		
		int criticalValueBottom = jCharacteristic.getInt("criticalValueBottom");
		int criticalValueTop = jCharacteristic.getInt("criticalValueTop");
		
		GroupCharacteristic groupCharacteristics = new GroupCharacteristic(charId, charName, unit, criticalValueBottom, criticalValueTop);
		StatisticsRecord record = new StatisticsRecord(recordId, null, groupCharacteristics, recordEntrydate, recordValue);
		
		Content content = new Content();
		content.record = record;
		
		return content;
	}
}
