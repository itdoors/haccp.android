package com.itdoors.haccp.model;

import java.io.Serializable;

public class Owner implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	
	public Owner(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return 31 * (31 + Integer.valueOf(id).hashCode()) + name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Owner)) return false;
		Owner ow = (Owner)o;
		return ow.id == id && 
			   ow.name == name; 
	}
}
