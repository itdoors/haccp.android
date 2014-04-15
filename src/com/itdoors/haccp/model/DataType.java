package com.itdoors.haccp.model;

public enum DataType {
	INTEGER;
	public String toString() {return "integer";}
	public static DataType fromString(String type) throws IllegalArgumentException{
		if(type.equals(INTEGER.toString()))		return INTEGER;
		else throw new IllegalArgumentException("Only \"integer\" datatype avaliable");
	}
}
