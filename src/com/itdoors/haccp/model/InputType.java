package com.itdoors.haccp.model;

public enum InputType {
	RANGE;
	public String toString() {return "RANGE";}
	public static InputType fromString(String type){
		if(type.equals(RANGE.toString())) return RANGE;
		else throw new IllegalArgumentException("Only \"RANGE\" input type avaliable");
	}
}
