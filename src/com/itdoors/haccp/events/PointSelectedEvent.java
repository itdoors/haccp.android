
package com.itdoors.haccp.events;

import com.itdoors.haccp.model.Point;

public class PointSelectedEvent {
    private final Point point;

    public PointSelectedEvent(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }
}
