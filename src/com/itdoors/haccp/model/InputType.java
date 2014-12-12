
package com.itdoors.haccp.model;

import java.util.Arrays;

public class InputType {

    private final Range range;
    private final Property property;

    public InputType(Range range, Property property) {

        this.range = range;
        this.property = property;

    }

    public Property getProperty() {
        return property;
    }

    public Range getRange() {
        return range;
    }

    public static enum Range {

        STEP, INT, ARRAY;

        public static Range fromString(String type) {

            if (type.equals("INT"))
                return INT;
            if (type.equals("STEP"))
                return STEP;
            if (type.equals("ARRAY"))
                return ARRAY;
            throw new IllegalArgumentException(
                    "Only \"INT\", \"STEP\" or \"ARRAY\",  input type avaliable");
        }

    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("inputType = {\n")
                .append("range = ").append(range.toString()).append("\n")
                .append("property = ").append(property.toString()).append("\n")
                .append("}")
                .toString();
    }

    public static abstract class Property {

    }

    public static class StepProperty extends Property {
        private final int step;

        public StepProperty(int step) {
            this.step = step;
        }

        public int getStep() {
            return step;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("step_property = {\n")
                    .append("step = ").append(step).append("\n")
                    .append("}")
                    .toString();
        }
    }

    public static class IntProperty extends Property {
        @Override
        public String toString() {
            return new StringBuilder()
                    .append("int_property = {\n")
                    .append("}")
                    .toString();
        }
    }

    public static class ArrayProperty extends Property {

        private final int[] array;

        public ArrayProperty(int[] array) {
            this.array = Arrays.copyOf(array, array.length);
        }

        public int[] getArray() {
            return array;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("array_property = {\n")
                    .append("array = ").append(Arrays.toString(array)).append("\n")
                    .append("}")
                    .toString();
        }
    }

}
