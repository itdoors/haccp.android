package com.itdoors.haccp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int id;
	String name;
	
	List<GroupCharacteristic> characteristics;
	
	
	public Group(int id, String name) {
		
		this.id = id;
		this.name = name;
	}

	public Group(int id, String name, GroupCharacteristic characteristic) {
		
		this.id = id;
		this.name = name;
		this.characteristics = new ArrayList<GroupCharacteristic>();
		this.characteristics.add(characteristic);
		
	}
	
	public Group(int id, String name, List<GroupCharacteristic> characteristics) {
		
		this.id = id;
		this.name = name;
		this.characteristics = characteristics;
		
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public boolean hasCharacteristics(){
		return characteristics == null || characteristics.isEmpty();
	}
	
	public List<GroupCharacteristic> getCharacteristics() {
		return new ArrayList<GroupCharacteristic>(characteristics);
	}
	
	public GroupCharacteristic getFirstCharacteristic(){
		return (characteristics == null || characteristics.isEmpty() ? null : characteristics.get(0));
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(!(o instanceof Group)) return false;
		Group g = (Group)o;
		return  g.id == id && 
				g.name.equals(name) && 
				(characteristics == null ? g.characteristics == null : characteristics.equals(g.characteristics));
	}
	@Override
	public int hashCode() {
		
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + Integer.valueOf(id).hashCode();
		hash = prime * hash + name.hashCode();
		hash = prime * hash + (characteristics == null ? 0 : characteristics.hashCode());
		
		return hash;
	
	}
}
