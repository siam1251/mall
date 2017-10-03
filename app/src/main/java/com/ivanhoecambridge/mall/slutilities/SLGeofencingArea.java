package com.ivanhoecambridge.mall.slutilities;

import com.senionlab.slutilities.geofencing.interfaces.SLGeometry;

/**
 * Created by petar on 2017-09-25.
 */

public class SLGeofencingArea {
    private String     name;
    private SLGeometry geometry;

    public SLGeofencingArea(String name, SLGeometry geometry) {
        this.setName(name);
        this.setGeometry(geometry);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SLGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(SLGeometry geometry) {
        this.geometry = geometry;
    }
}
