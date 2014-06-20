
package com.itdoors.haccp.model.rest.retrofit;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Characteristic implements Serializable{

	
	private static final long serialVersionUID = -8612258782513259696L;
	
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
    	return new ToStringBuilder(this).append("id",id)
    			.append("name", name)
    			.append("unit", unit)
    			.append("criticalValueBottom", criticalValueBottom)
    			.append("criticalValueBottom", criticalValueTop)
    			.toString();
    }

    @Override
    public int hashCode() {
    	return new HashCodeBuilder()
	    	.append(id)
	    	.append(name)
	    	.append(unit)
	    	.append(criticalValueBottom)
	    	.append(criticalValueTop)
	    	.hashCode();
    }

    @Override
    public boolean equals(Object other) {
    
    	if(other == this)
    		return true;
    	if(!(other instanceof Characteristic))
    		return false;
    	
    	Characteristic myOther = (Characteristic)other;
    	
    	return new EqualsBuilder()
    		.append(id, myOther.id)
    		.append(name, myOther.name)
    		.append(unit, myOther.unit)
    		.append(criticalValueBottom, myOther.criticalValueBottom)
    		.append(criticalValueTop, myOther.criticalValueTop)
    		.isEquals();
    }

}
