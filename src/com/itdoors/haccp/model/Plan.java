package com.itdoors.haccp.model;

import java.io.Serializable;

public class Plan implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int root = -1;
	
	private int id;
	private String name;
	private int parent_id;
	private boolean haveChilds = false;
	
	public Plan(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public Plan(int id, String name, int parent_id){
		this.id = id;
		this.name = name;
		this.parent_id = parent_id;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getParentId() {
		return parent_id;
	}
	boolean isRoot(){
		return parent_id == root;
	}
	
	public void setHasChilds(boolean has) {
		this.haveChilds = has;
	}
	
	boolean hasChilds(){
		return haveChilds;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Plan)) return false;
		Plan p = (Plan)o;
		return p.id == id &&  
			   p.parent_id == parent_id &&
			   (name == null) ? p.name == null : name.equals(p.name);
	}
	@Override
	public int hashCode() {
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + Integer.valueOf(id).hashCode();
		hash = prime * hash + Integer.valueOf(parent_id).hashCode();
		hash = prime * hash + (name == null ? 0 : name.hashCode());
		
		return hash;
	}
}
