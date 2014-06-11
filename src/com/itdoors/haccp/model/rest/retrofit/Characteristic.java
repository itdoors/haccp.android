
package com.itdoors.haccp.model.rest.retrofit;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Characteristic {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String unit;
    @Expose
    private String criticalValueBottom;
    @Expose
    private String criticalValueTop;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCriticalValueBottom() {
        return criticalValueBottom;
    }

    public void setCriticalValueBottom(String criticalValueBottom) {
        this.criticalValueBottom = criticalValueBottom;
    }

    public String getCriticalValueTop() {
        return criticalValueTop;
    }

    public void setCriticalValueTop(String criticalValueTop) {
        this.criticalValueTop = criticalValueTop;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

}
