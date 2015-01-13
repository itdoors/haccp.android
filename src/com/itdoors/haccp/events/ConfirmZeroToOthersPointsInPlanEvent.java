
package com.itdoors.haccp.events;

import java.util.ArrayList;
import java.util.List;

import com.itdoors.haccp.model.Point;

public class ConfirmZeroToOthersPointsInPlanEvent {

    private final List<Point> points;

    public ConfirmZeroToOthersPointsInPlanEvent(List<Point> points) {
        this.points = new ArrayList<Point>();
        this.points.addAll(points);
    }

    public List<Point> getPoints() {
        return points;
    }
}
