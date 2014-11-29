
package com.itdoors.haccp.model.rest.retrofit;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

public class Statistic implements Serializable {

    private static final long serialVersionUID = -2273511950533524818L;

    @Expose
    private String id;
    @Expose
    private String value;
    @Expose
    private String entryDate;
    @Expose
    private Characteristic characteristic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public Characteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                .append("value", value)
                .append("entryDate", entryDate)
                .append("characteristic", characteristic)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(value)
                .append(entryDate)
                .append(characteristic)
                .hashCode();
    }

    @Override
    public boolean equals(Object other) {

        if (other == this)
            return true;
        if (!(other instanceof Statistic))
            return false;

        Statistic myOther = (Statistic) other;

        return new EqualsBuilder()
                .append(id, myOther.id)
                .append(value, myOther.value)
                .append(entryDate, myOther.entryDate)
                .append(characteristic, myOther.characteristic)
                .isEquals();
    }
}
