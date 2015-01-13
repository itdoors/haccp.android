
package com.itdoors.haccp.model;

import java.io.Serializable;
import java.util.Date;

public class Point implements Serializable {

    private static final long serialVersionUID = 2009424751322523255L;

    private final String uid;
    private final String number;
    private final Date installationDate;
    private final Plan plan;
    private final Group group;
    private final Contour contour;
    private final PointStatus status;

    private final Owner owner;

    public static class Builder {

        private final String uid;

        private String number;
        private Date installationDate;
        private Plan plan;
        private Group group;
        private Contour contour;
        private PointStatus status;
        private Owner owner;

        public Builder(String uid) {
            this.uid = uid;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder installationDate(Date installationDate) {
            this.installationDate = installationDate;
            return this;
        }

        public Builder plan(Plan plan) {
            this.plan = plan;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder contour(Contour contour) {
            this.contour = contour;
            return this;
        }

        public Builder status(PointStatus status) {
            this.status = status;
            return this;
        }

        public Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public Point build() {
            return new Point(this);
        }
    }

    private Point(Builder builder) {
        this.uid = builder.uid;
        this.number = builder.number;
        this.installationDate = builder.installationDate;
        this.plan = builder.plan;
        this.group = builder.group;
        this.contour = builder.contour;
        this.status = builder.status;
        this.owner = builder.owner;
    }

    public Contour getContour() {
        return contour;
    }

    public Owner getOwner() {
        return owner;
    }

    public Group getGroup() {
        return group;
    }

    public String getUID() {
        return uid;
    }

    public Date getInstallationDate() {
        return installationDate;
    }

    public String getNumber() {
        return number;
    }

    public Plan getPlan() {
        return plan;
    }

    public PointStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contour == null) ? 0 : contour.hashCode());
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + ((installationDate == null) ? 0 : installationDate.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((plan == null) ? 0 : plan.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
        Point other = (Point) obj;
        if (contour == null) {
            if (other.contour != null)
                return false;
        } else if (!contour.equals(other.contour))
            return false;
        if (group == null) {
            if (other.group != null)
                return false;
        } else if (!group.equals(other.group))
            return false;
        if (installationDate == null) {
            if (other.installationDate != null)
                return false;
        } else if (!installationDate.equals(other.installationDate))
            return false;

        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        if (plan == null) {
            if (other.plan != null)
                return false;
        } else if (!plan.equals(other.plan))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (uid == null) {
            if (other.uid != null)
                return false;
        } else if (!uid.equals(other.uid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point [uid=" + uid + ", number=" + number + ", installationDate="
                + installationDate + ", plan=" + plan + ", group=" + group + ", contour=" + contour
                + ", status=" + status + ", owner=" + owner + "]";
    }

}
