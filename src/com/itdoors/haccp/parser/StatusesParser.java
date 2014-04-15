package com.itdoors.haccp.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.PointStatus.CODE;

public class StatusesParser implements Parser{
	public static class Content implements Serializable {
		
		private static final long serialVersionUID = 1L;
		public List<PointStatus> records;
		
		@Override
		public String toString(){
			return records == null ? "null" : records.toString();
		}
	}

	@Override
	public Object parse(String json) throws JSONException,
			ServerFailedException {
			
			JSONArray jStatuses = new JSONArray(json);
			List<PointStatus> records = new ArrayList<PointStatus>();
			
			for(int i = 0 ; i < jStatuses.length(); i ++) {
				
				JSONObject jRecord = jStatuses.getJSONObject(i);
				int id = jRecord.getInt("id");
				String codeStr = jRecord.getString("slug");
				String name = jRecord.getString("name");
				
				CODE code = null;
				try {code = CODE.fromString(codeStr);}
				catch(Exception e){	throw new ServerFailedException(e.getMessage());}
				
				PointStatus record = new PointStatus(id, name, code);
				records.add(record);
				
			}
		
		Content content = new Content();
		content.records = records;
		return content;
	}
}
