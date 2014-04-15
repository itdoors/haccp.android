package com.itdoors.haccp.model;

import java.io.Serializable;

public class Plan implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	
	public Plan(int id, String name) {
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
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Plan)) return false;
		Plan p = (Plan)o;
		return p.id == id && p.name.equals(name);
	}
	@Override
	public int hashCode() {
		return 31 * (31 + Integer.valueOf(id).hashCode()) + name.hashCode();
	}
}
