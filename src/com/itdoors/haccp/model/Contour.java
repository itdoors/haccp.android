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
	
	public Contour(int id, String name) {
		
		this.id = id;
		this.name = name;
		this.colour = 0;
	
	}

	public Contour(int id, String name, int colour) {
		
		this.id = id;
		this.name = name;
		this.colour = colour;
	
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
	

	@Override
	public int hashCode() {
		
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + Integer.valueOf(id).hashCode();
		hash = prime * hash + Integer.valueOf(colour).hashCode();
		hash = prime * hash + (name == null ? 0 : name.hashCode());
		
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(!(o instanceof Contour)) return false;
		Contour c = (Contour)o;
		return c.id == id && 
			   c.colour == colour && 
			   (name == null ? c.name == null : name.equals(c.name));
	}
	
}
