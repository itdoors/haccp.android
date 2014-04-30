package com.itdoors.haccp.model;

import java.io.Serializable;

public class CompanyObject implements Serializable{

	private static final long serialVersionUID = 1L;
	private final int id;
	private final String name;
	private final Company company;
	
	public CompanyObject(int id, String name, Company company) {
		
		this.id = id;
		this.name = name;
		this.company = company;
	
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public Company getCompany() {
		return company;
	}

	@Override
	public int hashCode() {
		
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + Integer.valueOf(id).hashCode();
		hash = prime * hash + (name == null ? 0 : name.hashCode());
		hash = prime * hash + (company == null ? 0 : company.hashCode());
		
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(!(o instanceof Company)) return false;
		CompanyObject cobj = (CompanyObject)o;
		return cobj.id == id && 
			   (name == null ? cobj.name == null : name.equals(cobj.name)) && 
			   (company == null ? cobj.company == null : company.equals(cobj.company));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id:" + id +",");
		sb.append("name:" + (name == null ? "null" : name ));
		sb.append("company: " + (company == null ? "null" : company.toString()));
		sb.append("}");
		return sb.toString();
	}

	
}
