
package com.itdoors.haccp.model;

import java.io.Serializable;
import java.util.Date;

public class Point implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private int number;
    private Date installationDate;
    private Plan plan;
    private Group group;
    private Contour contour;
    private PointStatus status;

    private Owner owner;

    public Point(String id, int number, Date inDate, Plan plan, Group group, Contour contour) {
        this(id, number, inDate, plan, group, contour, null, null);
    }

    public Point(String id, int number, Date inDate, Plan plan, Group group, Contour contour,
            PointStatus status) {
        this(id, number, inDate, plan, group, contour, status, null);
    }

    public Point(String id, int number, Date inDate, Plan plan, Group group, Contour contour,
            PointStatus status, Owner owner) {
        this.uid = id;
        this.number = number;
        this.installationDate = inDate;
        this.plan = plan;
        this.group = group;
        this.contour = contour;
        this.owner = owner;
        this.status = status;
    }

    public Contour getContour() {
        return contour;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public Group getGroup() {
        return group;
    }

    public String getId() {
        return uid;
    }

    public Date getInstallationDate() {
        return installationDate;
    }

    public int getNumber() {
        return number;
    }

    public Plan getPlan() {
        return plan;
    }

    public PointStatus getStatus() {
        return status;
    }

    public void setStatus(PointStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {

        int prime = 31;
        int hash = 1;

        hash = prime * hash + Integer.valueOf(uid).hashCode();
        hash = prime * hash + Integer.valueOf(number).hashCode();
        hash = prime * hash + (installationDate == null ? 0 : installationDate.hashCode());
        hash = prime * hash + (group == null ? 0 : group.hashCode());
        hash = prime * hash + (plan == null ? 0 : plan.hashCode());
        hash = prime * hash + (contour == null ? 0 : contour.hashCode());
        hash = prime * hash + (status == null ? 0 : status.hashCode());
        hash = prime * hash + (owner == null ? 0 : owner.hashCode());

        return hash;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Group))
            return false;
        Point cp = (Point) o;
        return cp.uid == uid
                &&
                cp.number == number
                &&
                (plan == null ? cp.plan == null : plan.equals(cp.plan))
                &&
                (group == null ? cp.group == null : group.equals(cp.group))
                &&
                (installationDate == null ? cp.installationDate == null : installationDate
                        .equals(cp.installationDate)) &&
                (contour == null ? cp.contour == null : contour.equals(cp.contour)) &&
                (status == null ? cp.status == null : status.equals(cp.status)) &&
                (owner == null ? cp.owner == null : owner.equals(cp.owner));
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("id = " + uid + ", ");
        sb.append("number = " + number + ", ");
        sb.append("installationDate = "
                + (installationDate == null ? "null" : installationDate.toString()) + ", ");
        sb.append("plan = " + (plan == null ? "null" : plan.toString()) + ", ");
        sb.append("group = " + (group == null ? "null" : group.toString()) + ", ");
        sb.append("contour = " + (contour == null ? "null" : contour.toString()) + ", ");
        sb.append("status = " + (status == null ? "null" : status.toString()) + ", ");
        sb.append("owner = " + (owner == null ? "null" : owner.toString()) + ", ");
        sb.append("]");
        return sb.toString();

    }
}
