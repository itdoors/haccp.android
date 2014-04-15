package com.itdoors.haccp.parser;

public interface SimpleListParserFactory<T> extends ParserFactory{
	public SimpleListParser<T> createParser();
}
