
package com.itdoors.haccp.model;

import java.io.Serializable;

public class CompanyObject implements Serializable {

    private static final long serialVersionUID = -736931963653388616L;

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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((company == null) ? 0 : company.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompanyObject other = (CompanyObject) obj;
        if (company == null) {
            if (other.company != null)
                return false;
        } else if (!company.equals(other.company))
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompanyObject [id=" + id + ", name=" + name + ", company=" + company + "]";
    }

}
