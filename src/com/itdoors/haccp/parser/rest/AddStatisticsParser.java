package com.itdoors.haccp.parser.rest;

import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.model.rest.StatisticsRecord;

public class AddStatisticsParser implements Parser {
	@Override
	public Responce parse(String json) throws JSONException{
		return  new AddStatisticsResponce(
					StatisticsRecord.valueOf( new JSONObject(json))
				);
	}

}
