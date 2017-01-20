package com.ivanhoecambridge.mall.bluedot;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Kay on 2017-01-20.
 */

public class CoordinateListener implements LocationListener {
    private WeakReference<PositionAndHeadingMapVisualization> mPositionAndHeadingMapVisualization;

    public CoordinateListener(PositionAndHeadingMapVisualization positionAndHeadingMapVisualization) {
        mPositionAndHeadingMapVisualization = new WeakReference<PositionAndHeadingMapVisualization>(positionAndHeadingMapVisualization);
    }

    @Override
    public void onLocationChanged(Location loc) {

        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();
        Log.d("CoordinateListener", "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
