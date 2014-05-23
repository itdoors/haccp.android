package com.itdoors.haccp.model.rest;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class PointRecord {

	private int id;
	private String name;
	private Date installationDate;
	private String mapLatitude;
	private String mapLongitude;
	private String imageLatitude;
	private String imageLongitude;
	private int contourId;
	private int planId;
	private int groupId;
	private int statusId;

	public PointRecord(int id, String name, int contourId, int groupId,
			int planId, int statusId, Date installationdate,
			String mapLatitude, String mapLongitude, String imageLatitude,
			String imageLongitude) {

		this.id = id;
		this.name = name;
		this.contourId = contourId;
		this.groupId = groupId;
		this.planId = planId;
		this.statusId = statusId;
		this.installationDate = installationdate;
		this.mapLatitude = mapLatitude;
		this.mapLongitude = mapLongitude;
		this.imageLatitude = imageLatitude;
		this.imageLongitude = imageLongitude;
	}

	public static PointRecord valueOf(JSONObject jObj) throws JSONException {

		int id = jObj.getInt("id");
		String name = jObj.getString("name");
		String instDateStr = jObj.getString("installationDate");
		Date installationDate = null;

		try {
			installationDate = new java.util.Date(
					Long.valueOf(instDateStr) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String mapLatitude = jObj.getString("mapLatitude");
		String mapLongitude = jObj.getString("mapLongitude");
		String imageLatitude = jObj.getString("imageLatitude");
		String imageLongitude = jObj.getString("imageLongitude");
		int contourId = jObj.getInt("contourId");
		int planId = jObj.getInt("planId");
		int groupId = jObj.getInt("groupId");
		int statusId = jObj.getInt("statusId");

		return new PointRecord(id, name, contourId, groupId, planId, statusId,
				installationDate, mapLatitude, mapLongitude, imageLatitude,
				imageLongitude);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getContourId() {
		return contourId;
	}

	public int getGroupId() {
		return groupId;
	}

	public int getPlanId() {
		return planId;
	}

	public int getStatusId() {
		return statusId;
	}

	public Date getInstallationDate() {
		return installationDate;
	}

	public String getMapLatitude() {
		return mapLatitude;
	}

	public String getMapLongitude() {
		return mapLongitude;
	}

	public String getImageLatitude() {
		return imageLatitude;
	}

	public String getImageLongitude() {
		return imageLongitude;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append('[')
				.append("id:")
				.append(id)
				.append(';')
				.append("name:")
				.append(name == null ? "null" : name)
				.append(';')
				.append("contourId:")
				.append(contourId)
				.append(';')
				.append("groupId:")
				.append(groupId)
				.append(';')
				.append("planId:")
				.append(planId)
				.append(';')
				.append("statusId:")
				.append(statusId)
				.append(';')
				.append("instalationDate:")
				.append(installationDate == null ? "null" : installationDate
						.toString()).append(';').append("mapLatitude:")
				.append(mapLatitude == null ? "null" : mapLatitude).append(';')
				.append("mapLongitude:")
				.append(mapLongitude == null ? "null" : mapLongitude)
				.append(';').append("imageLatitude:")
				.append(imageLatitude == null ? "null" : imageLatitude)
				.append(';').append("imageLongitude:")
				.append(imageLongitude == null ? "null" : imageLongitude)
				.append(']');

		return sb.toString();
	}

	@Override
	public int hashCode() {

		int prime = 31;
		int hash = 1;

		hash = prime * hash + id;
		hash = prime * hash + (name == null ? 0 : name.hashCode());
		hash = prime * hash + contourId;
		hash = prime * hash + groupId;
		hash = prime * hash + planId;
		hash = prime * hash + statusId;

		hash = prime * hash
				+ (installationDate == null ? 0 : installationDate.hashCode());
		hash = prime * hash
				+ (mapLatitude == null ? 0 : mapLatitude.hashCode());
		hash = prime * hash
				+ (mapLongitude == null ? 0 : mapLongitude.hashCode());
		hash = prime * hash
				+ (imageLatitude == null ? 0 : imageLatitude.hashCode());
		hash = prime * hash
				+ (imageLongitude == null ? 0 : imageLongitude.hashCode());

		return hash;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (!(o instanceof PointRecord))
			return false;
		PointRecord pointRecord = (PointRecord) o;

		return (pointRecord.id == id)
				&& (name == null ? pointRecord.name == null : name
						.equals(pointRecord.name))
				&& (pointRecord.contourId == contourId)
				&& (pointRecord.groupId == groupId)
				&& (pointRecord.planId == planId)
				&& (pointRecord.statusId == statusId)
				&& (installationDate == null ? pointRecord.installationDate == null
						: installationDate.equals(pointRecord.installationDate))
				&& (mapLatitude == null ? pointRecord.mapLatitude == null
						: mapLatitude.equals(pointRecord.mapLatitude))
				&& (mapLongitude == null ? pointRecord.mapLongitude == null
						: mapLongitude.equals(pointRecord.mapLongitude))
				&& (imageLatitude == null ? pointRecord.imageLatitude == null
						: imageLatitude.equals(pointRecord.imageLatitude))
				&& (imageLongitude == null ? pointRecord.imageLongitude == null
						: imageLongitude.equals(pointRecord.imageLongitude));

	}

}
