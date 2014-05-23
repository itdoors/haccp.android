package com.itdoors.haccp.parser.rest;

import org.json.JSONException;
import org.json.JSONObject;

import com.itdoors.haccp.model.rest.PointRecord;

public class UpdatePointStatusParser implements Parser{
	@Override
	public Responce parse(String json) throws JSONException {
		return new UpdatePointStatusResponce( 
					PointRecord.valueOf( new JSONObject(json) ));
	}

}
