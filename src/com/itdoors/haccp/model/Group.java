
package com.itdoors.haccp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.itdoors.haccp.utils.Logger;

public class Group implements Serializable {

    private static final long serialVersionUID = -4431776932806031271L;

    private final int id;
    private final String name;
    private final List<GroupCharacteristic> characteristics = new ArrayList<GroupCharacteristic>();

    public static class Builder {

        private final int id;
        private String name;
        private List<GroupCharacteristic> characteristics;

        public Builder(int id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder characteristics(List<GroupCharacteristic> characteristics) {
            this.characteristics = characteristics;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }

    private Group(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        if (builder.characteristics != null)
            this.characteristics.addAll(builder.characteristics);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasCharacteristics() {
        return characteristics == null || characteristics.isEmpty();
    }

    public List<GroupCharacteristic> getCharacteristics() {
        return new ArrayList<GroupCharacteristic>(characteristics);
    }

    public GroupCharacteristic getFirstCharacteristic() {
        return (characteristics == null || characteristics.isEmpty() ? null : characteristics
                .get(0));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((characteristics == null) ? 0 : characteristics.hashCode());
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
        Group other = (Group) obj;
        if (characteristics == null) {
            if (other.characteristics != null)
                return false;
        } else if (!characteristics.equals(other.characteristics))
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
        return "Group [id=" + id + ", name=" + name + ", characteristics="
                + characteristics.toString() + "]";
    }

}
