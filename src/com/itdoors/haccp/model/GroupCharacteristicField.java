
package com.itdoors.haccp.model;

import java.io.Serializable;

public class GroupCharacteristicField implements Serializable {

    private static final long serialVersionUID = -2852555914675266780L;

    private final GroupCharacteristic characteristic;
    private final DataType dataType;
    private final InputType inputType;

    public GroupCharacteristicField(GroupCharacteristic characteristic, DataType dataType,
            InputType inputType) {
        this.characteristic = characteristic;
        this.dataType = dataType;
        this.inputType = inputType;
    }

    public GroupCharacteristic getCharacteristic() {
        return characteristic;
    }

    public DataType getDataType() {
        return dataType;
    }

    public InputType getInputType() {
        return inputType;
    }

    @Override
    public int hashCode() {

        int prime = 31;
        int hash = 1;

        hash = prime * hash + (characteristic == null ? 0 : characteristic.hashCode());
        hash = prime * hash + dataType.hashCode();
        hash = prime * hash + inputType.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof GroupCharacteristicField))
            return false;
        GroupCharacteristicField g = (GroupCharacteristicField) o;
        return (characteristic == null ? g.characteristic == null : characteristic
                .equals(g.characteristic)) &&
                dataType.equals(g.dataType) &&
                inputType.equals(g.inputType);
    }
}
