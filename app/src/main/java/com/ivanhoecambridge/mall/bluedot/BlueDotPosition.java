package com.ivanhoecambridge.mall.bluedot;

import com.senionlab.slutilities.type.FloorNr;
import com.senionlab.slutilities.type.SLCoordinate2D;
import com.senionlab.slutilities.type.SLCoordinate3D;

import slutilities.SLMapPositions;

/**
 * Created by Kay on 2017-01-06.
 */

public class BlueDotPosition {

    private double latitude;
    private double longitude;
    private FloorNr floorNr;

    private int mappedInFloor;


    public BlueDotPosition(SLCoordinate3D location){
        SLCoordinate2D slCoordinate2D = location.b();
        latitude = slCoordinate2D.getLatitude();
        longitude = slCoordinate2D.getLongitude();
        floorNr = location.getFloorNr();
        mappedInFloor = SLMapPositions.getMapLevel(floorNr.intValue());
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public FloorNr getFloorNr(){
        return floorNr;
    }

    public int getMappedInFloor() {
        return mappedInFloor;
    }



}
