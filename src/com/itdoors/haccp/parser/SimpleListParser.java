package com.itdoors.haccp.parser;

import java.util.List;

import org.json.JSONException;

public interface SimpleListParser<T> extends Parser {
	public List<T> parse(String json) throws JSONException;
	public int getTotalCount();
	public int getCurrentCount();
}
