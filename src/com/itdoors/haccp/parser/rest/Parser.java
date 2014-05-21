package com.itdoors.haccp.parser.rest;

import org.json.JSONException;

import com.itdoors.haccp.exceptions.rest.WebServiceFailedException;

public interface Parser {
	public Responce parse (String json) throws JSONException, WebServiceFailedException;
}

