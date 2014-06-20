package com.itdoors.haccp.model.rest.retrofit;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


public class MoreStatistics {

    @Expose
    private Boolean more;
    @Expose
    private List<Statistic> statistics = new ArrayList<Statistic>();

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
    	return new ToStringBuilder(this).append("more",more)
    			.append("statistics", statistics)
    			.toString();
    }

    @Override
    public int hashCode() {
    	return new HashCodeBuilder()
	    	.append(more)
	    	.append(statistics)
	    	.hashCode();
    }

    @Override
    public boolean equals(Object other) {
    
    	if(other == this)
    		return true;
    	if(!(other instanceof MoreStatistics))
    		return false;
    	
    	MoreStatistics myOther = (MoreStatistics)other;
    	
    	return new EqualsBuilder()
    		.append(more, myOther.more)
    		.append(statistics, myOther.statistics)
    		.isEquals();
    }

}  