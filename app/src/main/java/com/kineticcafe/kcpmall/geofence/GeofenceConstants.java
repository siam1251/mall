package com.kineticcafe.kcpmall.geofence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Kay on 2016-08-11.
 */
public final class GeofenceConstants {

    private GeofenceConstants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

//    public static final float GEOFENCE_RADIUS_IN_METERS = 500; // 500 m
    public static final float GEOFENCE_RADIUS_IN_METERS = 100; // 500 m

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> GEOFENCE_AREA_LAT_LONG = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        GEOFENCE_AREA_LAT_LONG.put("SFO", new LatLng(37.621313, -122.378955));

        // Googleplex.
        GEOFENCE_AREA_LAT_LONG.put("GOOGLE", new LatLng(37.422611,-122.0840577));
//        GEOFENCE_AREA_LAT_LONG.put("Kinetic Cafe", new LatLng(43.6424077, -79.3745555));//kinetic cafe building
        GEOFENCE_AREA_LAT_LONG.put("Kinetic Cafe", new LatLng(43.642848, -79.375370));//real location
    }
}