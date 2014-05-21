package com.itdoors.haccp.parser.rest;

import org.json.JSONException;

import com.itdoors.haccp.exceptions.rest.WebServiceFailedException;

public class UpdatePointStatusParser implements Parser{

	@Override
	public Responce parse(String json) throws JSONException,
			WebServiceFailedException {
		return new UpdatePointStatusResponce();
	}

}
