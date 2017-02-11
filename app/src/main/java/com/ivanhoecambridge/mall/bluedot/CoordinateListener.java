package com.ivanhoecambridge.mall.bluedot;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ivanhoecambridge.mall.crashReports.CustomizedExceptionHandler;

import java.lang.ref.WeakReference;
import java.util.Map;

import static com.ivanhoecambridge.mall.bluedot.PositionAndHeadingMapVisualization.sGeofenceEntered;
import static com.ivanhoecambridge.mall.bluedot.PositionAndHeadingMapVisualization.sLocationFindingMode;
import static slutilities.SLSettings.GEOFENCE_LOCATIONS;

/**
 * Created by Kay on 2017-01-20.
 */

public class CoordinateListener implements LocationListener {

    private WeakReference<PositionAndHeadingMapVisualization> mPositionAndHeadingMapVisualization;
    private boolean isUpdating = false;
    static long UPDATE_INTERVAL_TIME = 100;
    static float UPDATE_DISTANCE_IN_BETWEEN = 0f;


    public CoordinateListener(PositionAndHeadingMapVisualization positionAndHeadingMapVisualization) {
        mPositionAndHeadingMapVisualization = new WeakReference<PositionAndHeadingMapVisualization>(positionAndHeadingMapVisualization);
    }

    @Override
    public void onLocationChanged(Location loc) {
//        Log.d("CoordinateListener", "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude());

        int floorIndex = 0;
        double radius = 100000;
        String geofence = "";
        String geofences = "";
        for (Map.Entry<String, SLIndoorLocationPresenterImpl.GeofenceLocation> entry : GEOFENCE_LOCATIONS.entrySet()) {
            if(entry.getValue().getDidEnterGeofence()) {
                geofences = geofences + ", " + entry.getValue().name;
                if (radius > entry.getValue().radius) {
                    floorIndex = entry.getValue().floorIndex;
                    radius = entry.getValue().radius;
                    geofence = entry.getValue().name;
                }
            }
        }

        if(floorIndex != 1) {
            Log.e("FLOORINDEX", geofence + " " +  floorIndex);
        }
        BlueDotPosition blueDotPosition = new BlueDotPosition(loc, floorIndex);
        PositionAndHeadingMapVisualization positionAndHeadingMapVisualization = mPositionAndHeadingMapVisualization.get();
        if(sLocationFindingMode == PositionAndHeadingMapVisualization.LocationFindingMode.GPS) {
//            sGeofenceEntered = geofence + " : " + geofences;
            sGeofenceEntered = geofence;
            positionAndHeadingMapVisualization.setPos(blueDotPosition);
        }
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

    public boolean isUpdating() {
        return isUpdating;
    }

    public void setUpdating(boolean updating) {
        isUpdating = updating;
    }

}
