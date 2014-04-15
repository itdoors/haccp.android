package com.itdoors.haccp.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Group;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.Plan;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.StatisticsRecord;

public class ControlPointParser implements Parser{
	
	public static class Content implements Serializable{
		private static final long serialVersionUID = 1L;
		
		public Point point;
		public List<StatisticsRecord> records;
		public Boolean hasMoreStatiscticItems;
		
		@Override
		public String toString() {
			
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("point:").append(point == null ? "null" : point.toString()).append(",");
			sb.append("records:").append(records == null ? "null" : records.toString()).append(";");
			sb.append("hasMore:").append(hasMoreStatiscticItems == null ? false : hasMoreStatiscticItems.toString());
			sb.append("}");
			
			return sb.toString();
		}
	}

	@Override
	public Object parse(String json) throws JSONException,
			ServerFailedException {
		
		JSONObject resultJObj = (new JSONArray(json)).getJSONObject(0);
		
		int pointId = resultJObj.getInt("id");
		int pointNumber = resultJObj.getInt("number");
		String pointInDateStr = resultJObj.getString("installationDate");
		java.util.Date pointInDate = null;
		
		try{
			pointInDate = new java.util.Date(Long.valueOf(pointInDateStr)*1000);
		}	catch(Exception e){ e.printStackTrace();}
		
		JSONObject planJObj = resultJObj.getJSONObject("plan");
		int planId = planJObj.getInt("id");
		String planName = planJObj.getString("name");
		
		JSONObject groupJObj = resultJObj.getJSONObject("group");
		int groupId = groupJObj.getInt("id");
		String groupName = groupJObj.getString("name");
		
		JSONObject contourJObj = resultJObj.getJSONObject("contour");
		int contourId = contourJObj.getInt("id");
		String contourName = contourJObj.getString("name");
		
		Plan plan = new Plan(planId, planName);
		Group group = new Group(groupId, groupName);
		Contour contour = new Contour(contourId, contourName);
		
		JSONObject statusJObj = resultJObj.getJSONObject("status");
		int statusId = statusJObj.getInt("id");
		String statusName = statusJObj.getString("name");
		String statusCode = statusJObj.getString("slug");
		
		PointStatus status = null;
		try {
			status = new PointStatus(statusId, statusName, statusCode);
		}	catch(Exception e){ e.printStackTrace();}
		
		Point point = new Point(pointId, pointNumber, pointInDate, plan, group, contour, status);
		
		
		List<StatisticsRecord> records = new ArrayList<StatisticsRecord>();
		
		JSONObject jsonStatisticsObj = resultJObj.getJSONObject("statistics");
		boolean hasMoreItems = jsonStatisticsObj.getBoolean("more");
				
			
			
			JSONArray jStatistics = jsonStatisticsObj.getJSONArray("statistics");
			
			for(int i = 0 ; i < jStatistics.length(); i ++) {
				
				JSONObject jRecord = jStatistics.getJSONObject(i);
				int recordId = jRecord.getInt("id");
				double recordValue = jRecord.getDouble("value");
				
				String recordEntrydateStr = jRecord.getString("entryDate");
				java.util.Date recordEntrydate  = null;
				try{
					recordEntrydate = new java.util.Date(Long.valueOf(recordEntrydateStr)*1000);
				}catch(Exception e){ e.printStackTrace();}
				
				JSONObject jCharacteristic = jRecord.getJSONObject("characteristic");
				int charId = jCharacteristic.getInt("id");
				String charName = jCharacteristic.getString("name");
				String unit = jCharacteristic.getString("unit");
				
				int criticalValueBottom = jCharacteristic.getInt("criticalValueBottom");
				int criticalValueTop = jCharacteristic.getInt("criticalValueTop");
				
				GroupCharacteristic groupCharacteristics = new GroupCharacteristic(charId, charName, unit, criticalValueBottom, criticalValueTop);
				StatisticsRecord record = new StatisticsRecord(recordId, point, groupCharacteristics, recordEntrydate, recordValue);
				
				records.add(record);
				
			}
		
		Content content = new Content();
		content.point = point;
		content.records = records;
		content.hasMoreStatiscticItems = Boolean.valueOf(hasMoreItems);
		
		return content;
	}

}
