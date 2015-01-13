
package com.itdoors.haccp.events;

import java.util.ArrayList;
import java.util.List;

import com.itdoors.haccp.model.Point;

public class ApplyAddZeroExceptEvent {

    private final List<Point> points;

    public ApplyAddZeroExceptEvent(List<Point> points) {
        this.points = new ArrayList<Point>();
        this.points.addAll(points);
    }

    public List<Point> getPoints() {
        return points;
    }
}
