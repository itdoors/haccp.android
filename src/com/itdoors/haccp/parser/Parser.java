package com.itdoors.haccp.parser;

import org.json.JSONException;

import com.itdoors.haccp.exceptions.ServerFailedException;


public interface Parser {
	public Object parse(String json) throws JSONException, ServerFailedException;
}
