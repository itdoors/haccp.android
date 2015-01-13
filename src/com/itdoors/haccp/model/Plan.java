
package com.itdoors.haccp.model;

import java.io.Serializable;

public class Plan implements Serializable {

    private static final long serialVersionUID = -6914841960864258201L;

    public static final int root = -1;

    private final int id;
    private final String name;
    private final int parentId;
    private final boolean hasChildren;

    public Plan(int id, String name) {
        this(id, name, root);
    }

    public Plan(int id, String name, int parent_id) {
        this(id, name, parent_id, false);
    }

    public Plan(int id, String name, int parentId, boolean hasChildren) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.hasChildren = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getParentId() {
        return parentId;
    }

    boolean isRoot() {
        return parentId == root;
    }

    boolean hasChilds() {
        return hasChildren;
    }

    @Override
    public String toString() {
        return "Plan [id=" + id + ", name=" + name + ", parentId=" + parentId + ", hasChildren="
                + hasChildren + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (hasChildren ? 1231 : 1237);
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + parentId;
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
        Plan other = (Plan) obj;
        if (hasChildren != other.hasChildren)
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parentId != other.parentId)
            return false;
        return true;
    }

}
