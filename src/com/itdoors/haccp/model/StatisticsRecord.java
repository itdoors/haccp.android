package com.itdoors.haccp.model;

import java.io.Serializable;
import java.util.Date;

public class StatisticsRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Point point;
	
	private int id;
	private double value;
	private Date entryDate;
	private GroupCharacteristic groupCharacteristics;
	
	private Date createdAt;
	
	public StatisticsRecord(int id, Point point, GroupCharacteristic groupCharacteristics, Date entryDate, double value) {
		this(id, point, groupCharacteristics, null, entryDate, value);
	}
	
	
	public StatisticsRecord(int id, Point point, GroupCharacteristic groupCharacteristics, Date createdAt, Date entryDate, double value) {
		this.id = id;
		this.point = point;
		this.groupCharacteristics = groupCharacteristics;
		this.createdAt = createdAt;
		this.entryDate = entryDate;
		this.value = value;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public Point getPoint() {
		return point;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public double getValue() {
		return value;
	}
	public GroupCharacteristic getGroupCharacteristics() {
		return groupCharacteristics;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
	
		int prime = 31;
		int hash = 1;
		
		hash = prime * hash + id;
		hash = prime * hash + point.hashCode();
		hash = prime * hash + (groupCharacteristics == null ? 0 : groupCharacteristics.hashCode());
		hash = prime * hash + Double.valueOf(value).hashCode();
		hash = prime * hash + (createdAt == null ? 0 : createdAt.hashCode());
		hash = prime * hash + (entryDate == null ? 0 : entryDate.hashCode());
		
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(!(o instanceof Point)) return false;
		StatisticsRecord statistics = (StatisticsRecord)o;
		
		return  (statistics.id == id) && 
				(statistics.value == value) &&
				(statistics.point.equals(point)) &&
				(groupCharacteristics == null ? statistics.groupCharacteristics == null : groupCharacteristics.equals(statistics.groupCharacteristics)) &&
				(createdAt == null ? statistics.createdAt == null : createdAt.equals(statistics.createdAt)) &&
				(entryDate == null ? statistics.entryDate == null : entryDate.equals(statistics.entryDate));
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id:" + id + ";");
		sb.append("value:" + value + ";");
		
		String entryDateStr = ( entryDate == null ) ? "null" : entryDate.toString();
		sb.append("entryDate:" + entryDateStr + ";");
		
		String pointStr = ( point == null ) ? "null" : point.toString();
		sb.append("point:" + pointStr + ";");
		sb.append("};");
		
		return sb.toString();
	}
	
	
}
