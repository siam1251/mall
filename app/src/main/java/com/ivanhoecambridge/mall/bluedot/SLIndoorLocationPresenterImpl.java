package com.ivanhoecambridge.mall.bluedot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.senionlab.slutilities.geofencing.geometries.SLCircle;
import com.senionlab.slutilities.geofencing.geometries.SLGeometryId;
import com.senionlab.slutilities.geofencing.interfaces.SLGeometry;
import com.senionlab.slutilities.service.SLBroadcastReceiver;
import com.senionlab.slutilities.service.SLConsumer;
import com.senionlab.slutilities.service.SLServiceManager;
import com.senionlab.slutilities.type.FloorNr;
import com.senionlab.slutilities.type.LocationAvailability;
import com.senionlab.slutilities.type.SLCoordinate3D;
import com.senionlab.slutilities.type.SLHeadingStatus;
import com.senionlab.slutilities.type.SLIndoorLocationException;
import com.senionlab.slutilities.type.SLJsonProcessingException;
import com.senionlab.slutilities.type.SLLocationStatus;
import com.senionlab.slutilities.type.SLMotionType;
import com.senionlab.slutilities.type.SLPixelPoint2D;

import java.util.HashMap;
import java.util.Map;

import geofence.GeofenceConstants;
import slutilities.SLSettings;

import static com.ivanhoecambridge.mall.activities.MainActivity.VIEWPAGER_PAGE_MAP;
import static com.ivanhoecambridge.mall.bluedot.BluetoothManager.mDidAskToTurnOnBluetooth;
import static slutilities.SLSettings.GEOFENCE_LOCATIONS;

/**
 * Created by Kay on 2017-01-06.
 */

public class SLIndoorLocationPresenterImpl implements  SLIndoorLocationPresenter, SLConsumer {

    private final PositionAndHeadingMapVisualization positionAndHeadingMapVisualization = new PositionAndHeadingMapVisualization();
    public static boolean mAskForBluetooth = false;
    private boolean firstTimeBound = true;

    protected final Logger logger = new Logger(getClass().getName());
    private SLServiceManager serviceManager;
    private Context context;
    public static LocationAvailability sLocationAvailability = LocationAvailability.NOT_AVAILABLE;
    private MapViewWithBlueDot mapViewWithBlueDot;
    private static CoordinateListener sCoordinateListener;


    public static class GeofenceLocation {
        String name;
        double latitude;
        double longitude;
        double radius;
        int floor;
        private boolean didEnterGeofence = false;

        public GeofenceLocation(String name, double latitude, double longitude, double radius, int floor) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
            this.floor = floor;
        }

        private SLCoordinate3D getSLCoordinate3D(){
            return new SLCoordinate3D(latitude, longitude, new FloorNr(floor));
        }

        public SLCircle getSLCircle(){
            return new SLCircle(new SLGeometryId(name), getSLCoordinate3D(), radius);
        }

        public boolean getDidEnterGeofence() {
            return didEnterGeofence;
        }

        public void setDidEnterGeofence(boolean didEnterGeometry) {
            this.didEnterGeofence = didEnterGeometry;
        }
    }


    public SLIndoorLocationPresenterImpl(Context context, MapViewWithBlueDot mapViewWithBlueDot) {
        this.context = context;
        this.mapViewWithBlueDot = mapViewWithBlueDot;
        positionAndHeadingMapVisualization.init(mapViewWithBlueDot);
    }


    @Override
    public void onResume() {
        try {
            serviceManager = SLServiceManager.getInstance(context);
            serviceManager.registerReceiver(receiver);
            serviceManager.start(SLSettings.MAP_KEY, SLSettings.CUSTOMER_ID);
            serviceManager.bindService(this);
        } catch (SLJsonProcessingException e) {
            e.printStackTrace();
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        }

        if (!SLServiceManager.getInstance(getContext()).isBound(this)) {
            try {
                SLServiceManager.getInstance(getContext()).bindService(this);
            } catch (SLIndoorLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public PositionAndHeadingMapVisualization getPositionAndHeadingMapVisualization(){
        return positionAndHeadingMapVisualization;
    }

    private SLBroadcastReceiver receiver = new SLBroadcastReceiver() {

        @Override
        public void didUpdateLocation(SLCoordinate3D location, double uncertaintyRadius, SLLocationStatus status) {
            synchronized (this) {
                BlueDotPosition blueDotPosition = new BlueDotPosition(location);
                positionAndHeadingMapVisualization.setPos(blueDotPosition);
            }
        }

        @Override
        public void didUpdateLocationAvailability(LocationAvailability locationAvailability) {
//            logger.debug("didUpdateLocationAvailability++" + " locationAvailability: " + locationAvailability);
//            if(!locationAvailability.isAvailable()) mapViewWithBlueDot.removeBlueDot();
            sLocationAvailability = locationAvailability;


            if(!locationAvailability.isAvailable() && sCoordinateListener == null) {
                sCoordinateListener = new CoordinateListener(positionAndHeadingMapVisualization);
            } else {
                sCoordinateListener = null;
            }

        }
        @Override
        public void didUpdateHeading(double heading, SLHeadingStatus status) {
            Log.d("bluedot", "BlueDotPosition: "  + " headingRaw: " + heading);
            positionAndHeadingMapVisualization.setHeading((float) heading, status);
        }

        @Override
        public void didFinishLoadingManager() {
        }
        @Override
        public void errorWhileLoadingManager(String errorMsg) {
            logger.debug("errorWhileLoadingManager++" + " errorMsg: " + errorMsg);

            // Handle error here
        }

        @Override
        public void didUpdateMotionType(SLMotionType motionType) {
            logger.debug("didUpdateMotionType++" + " SLMotionType: " + motionType);
            // Do something with the motion type here
        }

        @Override
        public void errorBleNotEnabled() {
            BluetoothManager bluetoothManager = new BluetoothManager(context);
            if (Build.VERSION.SDK_INT >= 18 && !mDidAskToTurnOnBluetooth) {
                if(((MainActivity) context).getViewerPosition() != VIEWPAGER_PAGE_MAP) {
                    mAskForBluetooth = true;
                } else {
                    bluetoothManager.turnOnBluetooth();
                }
            }
        }

        @Override
        public void errorWifiNotEnabled() {
            logger.debug("errorWifiNotEnabled++");
            // Prompt the user to enable WiFi
        }
        @Override
        public void didEnterGeometry(SLGeometry geometry) {
            logger.debug("didEnterGeometry++");
            GEOFENCE_LOCATIONS.get(geometry.getGeometryId()).setDidEnterGeofence(true);


        }
        @Override
        public void didLeaveGeometry(SLGeometry geometry) {
            logger.debug("didLeaveGeometry++");
            GEOFENCE_LOCATIONS.get(geometry.getGeometryId()).setDidEnterGeofence(false);
//            mapViewWithBlueDot.removeBlueDot();



        }
    };

    private boolean isWithinGeofence(HashMap<String, GeofenceLocation> geofenceLocations){
        for (Map.Entry<String, GeofenceLocation> entry : geofenceLocations.entrySet()) {
            if(entry.getValue().getDidEnterGeofence()) return true;
        }
        return false;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

        if (serviceManager != null) {
            serviceManager.unregisterReceiver(receiver);
            try {
                serviceManager.stop();
            } catch (SLIndoorLocationException e) {
                e.printStackTrace();
            }
        }

    }

    private void didBindAndLoad() {
        try {
            for (Map.Entry<String, GeofenceLocation> entry : GEOFENCE_LOCATIONS.entrySet()) {
                serviceManager.addGeometry(entry.getValue().getSLCircle());
            }
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        }

    }



    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void didBindToService() {
        if (firstTimeBound) {
            didBindAndLoad();
        }
        firstTimeBound = false;
    }
}
