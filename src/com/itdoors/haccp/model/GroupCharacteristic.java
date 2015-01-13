
package com.itdoors.haccp.model;

import java.io.Serializable;

public class GroupCharacteristic implements Serializable {

    private static final long serialVersionUID = -8034385170203936420L;

    private final int id;

    private final String name;
    private final String description;
    private final String unit;

    private final int max_value;
    private final int min_value;

    private final int critical_bottom_value;
    private final int critical_top_value;

    public GroupCharacteristic(int id, String name, String uint, int bottom, int top) {

        this(id, name, uint, null, 100, 0, bottom, top);
    }

    public GroupCharacteristic(int id, String name, String uint, String description, int max,
            int min, int bottom, int top) {

        this.id = id;
        this.name = name;
        this.unit = uint;

        this.description = description;
        this.max_value = max;
        this.min_value = min;
        this.critical_bottom_value = bottom;
        this.critical_top_value = top;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxValue() {
        return max_value;
    }

    public int getMinValue() {
        return min_value;
    }

    public int getCriticalBottomValue() {
        return critical_bottom_value;
    }

    public int getCriticalTopValue() {
        return critical_top_value;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {

        int prime = 31;
        int hash = 1;

        hash = prime * hash + id;
        hash = prime * hash + (unit == null ? 0 : unit.hashCode());

        hash = prime * hash + max_value;
        hash = prime * hash + min_value;
        hash = prime * hash + critical_bottom_value;
        hash = prime * hash + critical_top_value;

        hash = prime * hash + (name == null ? 0 : name.hashCode());
        hash = prime * hash + (description == null ? 0 : description.hashCode());

        return hash;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Group))
            return false;
        GroupCharacteristic obj = (GroupCharacteristic) o;
        return (obj.id == id)
                &&
                (unit == null ? obj.unit == null : unit.equals(obj.unit))
                &&
                (obj.min_value == min_value)
                &&
                (obj.max_value == max_value)
                &&
                (obj.critical_top_value == critical_top_value)
                &&
                (obj.critical_bottom_value == critical_bottom_value)
                &&
                (name == null ? obj.name == null : name.equals(obj.name))
                &&
                (description == null ? obj.description == null : description
                        .equals(obj.description));

    }

}
