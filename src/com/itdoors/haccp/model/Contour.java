
package com.itdoors.haccp.model;

import java.io.Serializable;

public class Contour implements Serializable {

    private static final long serialVersionUID = -52010213231432500L;

    private final int id;
    private final String name;
    private final int colour;
    private final Service service;

    public Contour(int id, String name) {
        this(id, name, 0);
    }

    public Contour(int id, String name, int colour) {
        this(id, name, colour, null);
    }

    public Contour(int id, String name, int colour, Service service) {

        this.id = id;
        this.name = name;
        this.colour = colour;
        this.service = service;

    }

    public String getName() {
        return name;
    }

    public int getColour() {
        return colour;
    }

    public int getId() {
        return id;
    }

    public Service getService() {
        return service;
    }

    @Override
    public String toString() {
        return "Contour [id=" + id + ", name=" + name + ", colour=" + colour + ", service="
                + service + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + colour;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((service == null) ? 0 : service.hashCode());
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
        Contour other = (Contour) obj;
        if (colour != other.colour)
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (service == null) {
            if (other.service != null)
                return false;
        } else if (!service.equals(other.service))
            return false;
        return true;
    }

}
