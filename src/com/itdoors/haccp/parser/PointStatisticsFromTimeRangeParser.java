package com.itdoors.haccp.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.StatisticsRecord;
public class PointStatisticsFromTimeRangeParser implements Parser{
	
	public static class Content implements Serializable{
		private static final long serialVersionUID = 1L;
		
		public Boolean hasMoreStatiscticItems;
		public List<StatisticsRecord> records;
		
		@Override
		public String toString() {
			
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("records:").append(records == null ? "null" : records.toString()).append(";");
			sb.append("hasMore:").append(hasMoreStatiscticItems == null ? false : hasMoreStatiscticItems.toString());
			sb.append("}");
			
			return sb.toString();
		}
	}
	
	@Override
	public Object parse(String json) throws JSONException,
			ServerFailedException {
		
		JSONObject jObj = new JSONObject(json);
		
		boolean hasMore = jObj.getBoolean("more");
		JSONArray jStatistics = jObj.getJSONArray("statistics");
		List<StatisticsRecord> records = new ArrayList<StatisticsRecord>();
		
		for(int i = 0 ; i < jStatistics.length(); i ++) {
			
			JSONObject jRecord = jStatistics.getJSONObject(i);
			int recordId = jRecord.getInt("id");
			double recordValue = jRecord.getDouble("value");
			
			String recordEntrydateStr = jRecord.getString("entryDate");
			java.util.Date recordEntrydate = null;
			try{
				recordEntrydate = new java.util.Date(Long.valueOf(recordEntrydateStr)*1000);
			}	catch(Exception e){ e.printStackTrace();}
			
			JSONObject jCharacteristic = jRecord.getJSONObject("characteristic");
			int charId = jCharacteristic.getInt("id");
			String charName = jCharacteristic.getString("name");
			String unit = jCharacteristic.getString("unit");
			
			int criticalValueBottom = jCharacteristic.getInt("criticalValueBottom");
			int criticalValueTop = jCharacteristic.getInt("criticalValueTop");
			
			GroupCharacteristic groupCharacteristics = new GroupCharacteristic(charId, charName, unit, criticalValueBottom, criticalValueTop);
			
			StatisticsRecord record = new StatisticsRecord(recordId, groupCharacteristics, recordEntrydate, recordValue);
			records.add(record);
			
		}
		
		Content content = new Content();
		content.records = records;
		content.hasMoreStatiscticItems = hasMore;
		
		return content;
	}
}
