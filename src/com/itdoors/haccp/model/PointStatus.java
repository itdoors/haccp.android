
package com.itdoors.haccp.model;

import java.io.Serializable;

public class PointStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum CODE {
        WORKING, NO_ACCESS, MISSING, BROKEN;
        public static CODE fromString(String code) {
            if (code.equals(WORKING.toString()))
                return WORKING;
            else if (code.equals(NO_ACCESS.toString()))
                return NO_ACCESS;
            else if (code.equals(MISSING.toString()))
                return MISSING;
            else if (code.equals(BROKEN.toString()))
                return BROKEN;

            else
                throw new IllegalArgumentException(
                        "CODE: only  working, noaccess, missing or  broken acceptable!");
        }

        @Override
        public String toString() {
            switch (this) {
                case WORKING:
                    return "working";
                case NO_ACCESS:
                    return "noaccess";
                case MISSING:
                    return "missing";
                case BROKEN:
                    return "broken";

                default:
                    return super.toString();
            }
        }
    }

    private final int id;
    private final String name;
    private final CODE code;

    public PointStatus(int id, String name, CODE code) {

        this.id = id;
        this.name = name;
        this.code = code;

    }

    public PointStatus(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = CODE.fromString(code);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CODE getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PointStatus))
            return false;
        PointStatus st = (PointStatus) o;
        return st.id == id &&
                (name == null ? st.name == null : name.equals(st.name)) &&
                (code == null ? st.code == null : code.equals(st.code));
    }

    @Override
    public int hashCode() {

        int prime = 31;
        int hash = 1;

        hash = prime * hash + Integer.valueOf(id).hashCode();
        hash = prime * hash + (name == null ? 0 : name.hashCode());
        hash = prime * hash + (code == null ? 0 : code.hashCode());

        return hash;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("id = " + id + ", ");
        sb.append("code = " + (code == null ? "null" : code.toString()) + ", ");
        sb.append("name = " + (name == null ? "null" : name));
        sb.append("]");

        return sb.toString();
    }
}
