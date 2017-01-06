package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Overlay2DImage;

/**
 * Created by Kay on 2016-12-08.
 */

public class Pin {

    private Overlay2DImage overlay2DImage;
    private Coordinate coordinate;
    private double latitude;
    private double longitude;

    public Pin(Coordinate coordinate, Overlay2DImage overlay2DImage){
        this.coordinate = coordinate;
        this.overlay2DImage = overlay2DImage;
    }

    public Pin(Coordinate coordinate, Overlay2DImage overlay2DImage, double latitude, double longitude){
        this.coordinate = coordinate;
        this.overlay2DImage = overlay2DImage;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate(){
        return coordinate;
    }

    public void setOverlay2DImage(Overlay2DImage overlay2DImage){
        this.overlay2DImage = overlay2DImage;
    }

    public Overlay2DImage getOverlay2DImage(){
        return overlay2DImage;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}
