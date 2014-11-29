
package com.itdoors.haccp.model.rest;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsRecord {

    private int id;
    private double value;
    private Date entryDate;
    private Date createdAt;

    private int groupCharacteristicsId;
    private String pointId;

    public StatisticsRecord(int id, int groupCharacteristicsId, Date entryDate, double value,
            String pointId) {
        this(id, groupCharacteristicsId, null, entryDate, value, pointId);
    }

    public StatisticsRecord(int id, int groupCharacteristicsId, Date createdAt, Date entryDate,
            double value, String pointId) {
        this.id = id;
        this.groupCharacteristicsId = groupCharacteristicsId;
        this.createdAt = createdAt;
        this.entryDate = entryDate;
        this.value = value;
        this.pointId = pointId;
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

    public int getGroupCharacteristicsId() {
        return groupCharacteristicsId;
    }

    public int getId() {
        return id;
    }

    public String getPointId() {
        return pointId;
    }

    public static StatisticsRecord valueOf(JSONObject jObj) throws JSONException {

        int recordId = jObj.getInt("id");
        double recordValue = jObj.getDouble("value");
        String recordEntrydateStr = jObj.getString("entryDate");
        java.util.Date recordEntrydate = null;
        try {
            recordEntrydate = new java.util.Date(Long.valueOf(recordEntrydateStr) * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int characteristicsId = jObj.getInt("characteristicId");
        String pointId = jObj.getString("pointId");

        return new StatisticsRecord(recordId, characteristicsId, recordEntrydate, recordValue,
                pointId);
    }

    @Override
    public int hashCode() {

        int prime = 31;
        int hash = 1;

        hash = prime * hash + id;
        hash = prime * hash + (createdAt == null ? 0 : pointId.hashCode());
        hash = prime * hash + groupCharacteristicsId;
        hash = prime * hash + Double.valueOf(value).hashCode();
        hash = prime * hash + (createdAt == null ? 0 : createdAt.hashCode());
        hash = prime * hash + (entryDate == null ? 0 : entryDate.hashCode());

        return hash;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof StatisticsRecord))
            return false;
        StatisticsRecord statistics = (StatisticsRecord) o;

        return (statistics.id == id)
                &&
                (statistics.value == value)
                &&
                (statistics.pointId == pointId)
                &&
                (statistics.groupCharacteristicsId == groupCharacteristicsId)
                &&
                (createdAt == null ? statistics.createdAt == null : createdAt
                        .equals(statistics.createdAt))
                &&
                (entryDate == null ? statistics.entryDate == null : entryDate
                        .equals(statistics.entryDate));
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id:" + id + ";");
        sb.append("point_id:" + pointId + ";");
        sb.append("groupCharId: " + groupCharacteristicsId + ";");
        sb.append("value:" + value + ";");
        String entryDateStr = (entryDate == null) ? "null" : entryDate.toString();
        sb.append("entryDate:" + entryDateStr + ";");
        sb.append("};");

        return sb.toString();
    }

}
