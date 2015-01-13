
package com.itdoors.haccp.ui.interfaces;

import com.itdoors.haccp.model.Point;

public interface OnPointPressedListener {
    public void onPointPressed(Point point);

    public void onPointPressed(String pointId);
}
