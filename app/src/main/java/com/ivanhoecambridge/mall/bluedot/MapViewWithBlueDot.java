package com.ivanhoecambridge.mall.bluedot;

import com.senionlab.slutilities.type.SLPixelPoint2D;

/**
 * Created by Kay on 2017-01-06.
 */

public interface MapViewWithBlueDot {
    void dropBlueDot(BlueDotPosition blueDotPosition);
    void translateXBlueDot(float value);
    void translateYBlueDot(float value);
    void translateBlueDot(float x, float y);
}
