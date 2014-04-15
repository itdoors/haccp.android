package com.itdoors.haccp.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.model.DataType;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.GroupCharacteristicField;
import com.itdoors.haccp.model.InputType;

public class StaticticsAddInputParser implements Parser{

	public static class Content implements Serializable{
		private static final long serialVersionUID = 1L;
		public List<GroupCharacteristicField> characteristicFields;
		
		@Override
		public String toString() {
			if(characteristicFields == null) return "null";
			return characteristicFields.toString();
		}
	}
	
	@Override
	public Object parse(String json) throws JSONException,
			ServerFailedException {
		
		JSONArray jCharacteristicFields = new JSONArray(json);
		List<GroupCharacteristicField> records = new ArrayList<GroupCharacteristicField>();
		
		for(int i = 0 ; i < jCharacteristicFields.length(); i ++) {
			
			JSONObject jField = jCharacteristicFields.getJSONObject(i);
			int id = jField.getInt("id");
			String name = jField.getString("name");
			String unit = jField.getString("unit");
			
			int min = jField.getInt("allowValueMin");
			int max = jField.getInt("allowValueMax");
			
			int criticalValueBottom = jField.getInt("criticalValueBottom");
			int criticalValueTop = jField.getInt("criticalValueTop");
			
			
			String dataTypeStr = jField.getString("dataType");
			String inputTypeStr = jField.getString("inputType");
			
			try{
				
				DataType dataType = DataType.fromString(dataTypeStr);
				InputType inputType = InputType.fromString(inputTypeStr);
				
				GroupCharacteristic groupCharacteristics = new GroupCharacteristic(id, name, unit, null, max, min, criticalValueBottom, criticalValueTop);
				GroupCharacteristicField field = new GroupCharacteristicField(groupCharacteristics, dataType, inputType);
				records.add(field);
				
			}
			catch(IllegalArgumentException e){
				throw new ServerFailedException(e.getMessage());
			}
			
		}
		
		Content content = new Content();
		content.characteristicFields = records;
		
		return content;
	}

}
