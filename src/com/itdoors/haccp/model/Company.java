package com.itdoors.haccp.model;

import java.io.Serializable;

public class Company implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private final String name;
	
	public Company(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + Integer.valueOf(id).hashCode();
		hash = prime * hash + (name == null ? 0 : name.hashCode());
		
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(!(o instanceof Company)) return false;
		Company c = (Company)o;
		return c.id == id && 
			   (name == null ? c.name == null : name.equals(c.name));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id:" + id +",");
		sb.append("name:" + (name == null ? "null" : name ));
		sb.append("}");
		return sb.toString();
	}
}
