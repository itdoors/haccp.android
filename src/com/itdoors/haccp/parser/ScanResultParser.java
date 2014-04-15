package com.itdoors.haccp.parser;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanResultParser implements JSONParser {

	public static final String ID = "id";
	@Override
	public Object parse(String json) throws JSONException{
	
	  	JSONObject jObj = new JSONObject(json);
	  	int id = jObj.getInt(ID);
	  	
		return id;
	}

}
