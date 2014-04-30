package com.itdoors.haccp.model;

import java.io.Serializable;

public class Contour implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private final String name;
	private final int colour;
	private final Service service;
	
	public Contour(int id, String name) {
		this(id,name,0);
	}

	public Contour(int id, String name, int colour) {
		this(id,name,colour,null);
	}
	
	public Contour(int id, String name, int colour, Service service) {
		
		this.id = id;
		this.name = name;
		this.colour = colour;
		this.service = service;
	
	}
	
	public String getName() {
		return name;
	}
	
	public int getColour() {
		return colour;
	}
	
	public int getId() {
		return id;
	}
	
	public Service getService() {
		return service;
	}

	@Override
	public int hashCode() {
		
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + Integer.valueOf(id).hashCode();
		hash = prime * hash + Integer.valueOf(colour).hashCode();
		hash = prime * hash + (name == null ? 0 : name.hashCode());
		hash = prime * hash + (service == null ? 0 : service.hashCode());
		
		return hash;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
			.append("id: " + id +",")
			.append("name: " + (name==null ? "null" : name) +",")
			.append("color: " + colour +",")
			.append("service: " + (service == null ? "null" : service.toString())) 
		.append("}");
		return sb.toString();
	}
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(!(o instanceof Contour)) return false;
		Contour c = (Contour)o;
		return c.id == id && 
			   c.colour == colour && 
			   (name == null ? c.name == null : name.equals(c.name)) &&
			   (service == c.service);
	}
	
}
