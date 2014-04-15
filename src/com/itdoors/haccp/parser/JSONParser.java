package com.itdoors.haccp.parser;

import org.json.JSONException;

public interface JSONParser {
	public Object parse(String json) throws JSONException;
}
