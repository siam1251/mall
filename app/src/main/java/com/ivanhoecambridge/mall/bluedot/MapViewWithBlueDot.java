package com.ivanhoecambridge.mall.bluedot;

import com.senionlab.slutilities.type.SLPixelPoint2D;

/**
 * Created by Kay on 2017-01-06.
 */

public interface MapViewWithBlueDot {
    void dropBlueDot(double x, double y, int floor);
    int getCurrentFloor();
    void removeBlueDot();
    void drawHeading(double x, double y, float heading);
}
