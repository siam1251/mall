package com.ivanhoecambridge.mall.bluedot;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.crashReports.CustomizedExceptionHandler;
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

import java.util.HashMap;
import java.util.Map;

import slutilities.SLSettings;

import static com.ivanhoecambridge.mall.activities.MainActivity.VIEWPAGER_PAGE_MAP;
import static com.ivanhoecambridge.mall.bluedot.BluetoothManager.mDidAskToTurnOnBluetooth;
import static com.ivanhoecambridge.mall.bluedot.PositionAndHeadingMapVisualization.sLocationFindingMode;
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
    private Context mContext;
    public static LocationAvailability sLocationAvailability = LocationAvailability.NOT_AVAILABLE;
    private MapViewWithBlueDot mapViewWithBlueDot;
    private static CoordinateListener sCoordinateListener;
    private static LocationManager sLocationManager;



    public static class GeofenceLocation {
        String name;
        double latitude;
        double longitude;
        double radius;
        int floorIndex;
        private boolean didEnterGeofence = false;

        /**
         * @isForActiveMallDetection when set true, this geofence's only used for active mall mode and NOT for detecting location.
         */
        private boolean isForActiveMallDetection = false;

        public GeofenceLocation(String name, double latitude, double longitude, double radius, int floor) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
            this.floorIndex = floor;
        }

        /**
         *
         * @param name Name(Key) of geofence
         * @param latitude latitude of geofence
         * @param longitude longitude of geofence
         * @param radius radius of geofence
         * @param floor senions floor that needs to be mapped to real floor
         * @param isForActiveMallDetection when set true, this geofence's only used for active mall mode and NOT for detecting location.
         */
        public GeofenceLocation(String name, double latitude, double longitude, double radius, int floor, boolean isForActiveMallDetection) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
            this.floorIndex = floor;
            this.isForActiveMallDetection = isForActiveMallDetection;
        }

        private SLCoordinate3D getSLCoordinate3D(){
            return new SLCoordinate3D(latitude, longitude, new FloorNr(floorIndex));
        }

        public SLCircle getSLCircle(){
            return new SLCircle(new SLGeometryId(name), getSLCoordinate3D(), radius);
        }

        public double getRadius() {
            return radius;
        }

        public boolean getDidEnterGeofence() {
            return didEnterGeofence;
        }

        public boolean isForActiveMallDetection() {
            return isForActiveMallDetection;
        }

        public void setDidEnterGeofence(boolean didEnterGeometry) {
            this.didEnterGeofence = didEnterGeometry;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public SLIndoorLocationPresenterImpl(Context context, MapViewWithBlueDot mapViewWithBlueDot) {
        this.mContext = context;
        this.mapViewWithBlueDot = mapViewWithBlueDot;
        positionAndHeadingMapVisualization.init(mapViewWithBlueDot);
        sLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        sCoordinateListener = new CoordinateListener(positionAndHeadingMapVisualization); //method 1
    }

    @Override
    public void onResume() {
        mapViewWithBlueDot.dropGreyBlueDot();
        initService();
    }

    public void initService(){
        try {
            serviceManager = SLServiceManager.getInstance(mContext);
            serviceManager.registerReceiver(receiver);
            serviceManager.start(SLSettings.MAP_KEY, SLSettings.CUSTOMER_ID);
            serviceManager.bindService(this);
        } catch (SLJsonProcessingException e) {
            e.printStackTrace();
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
                if(sLocationFindingMode == PositionAndHeadingMapVisualization.LocationFindingMode.BEACON) {
                    positionAndHeadingMapVisualization.setPos(blueDotPosition);
                }
            }
        }

        @Override
        public void didUpdateLocationAvailability(LocationAvailability locationAvailability) {
            if(sLocationAvailability != locationAvailability) {
                sLocationAvailability = locationAvailability;
                updateFromGPS();
            }
        }
        @Override
        public void didUpdateHeading(double heading, SLHeadingStatus status) {
            positionAndHeadingMapVisualization.setHeading((float) heading, status);
        }

        @Override
        public void didFinishLoadingManager() {
        }
        @Override
        public void errorWhileLoadingManager(String errorMsg) {
            logger.debug("errorWhileLoadingManager++" + " errorMsg: " + errorMsg);
        }

        @Override
        public void didUpdateMotionType(SLMotionType motionType) {
        }

        @Override
        public void errorBleNotEnabled() {
            BluetoothManager bluetoothManager = new BluetoothManager(mContext);
            if (Build.VERSION.SDK_INT >= 18 && !mDidAskToTurnOnBluetooth) {
                if(((MainActivity) mContext).getViewerPosition() != VIEWPAGER_PAGE_MAP) {
                    mAskForBluetooth = true;
                } else {
                    bluetoothManager.turnOnBluetooth();
                }
            }
        }

        @Override
        public void errorWifiNotEnabled() {
            logger.debug("errorWifiNotEnabled++");
        }
        @Override
        public void didEnterGeometry(SLGeometry geometry) {
        }
        @Override
        public void didLeaveGeometry(SLGeometry geometry) {
        }
    };

    public void updateFromGPS(){
        synchronized (this) {
            if(sLocationAvailability.isAvailable()) {
                sLocationFindingMode = PositionAndHeadingMapVisualization.LocationFindingMode.BEACON;
                sCoordinateListener.setUpdating(false);
                sLocationManager.removeUpdates(sCoordinateListener);
            } else {
                if(isWithinGeofence(GEOFENCE_LOCATIONS)) {
                    if ((Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) || Build.VERSION.SDK_INT < 23) {
                        if(!sCoordinateListener.isUpdating()){
                            sLocationFindingMode = PositionAndHeadingMapVisualization.LocationFindingMode.GPS;
                            sCoordinateListener.setUpdating(true);
                            sLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, CoordinateListener.UPDATE_INTERVAL_TIME, CoordinateListener.UPDATE_DISTANCE_IN_BETWEEN, sCoordinateListener);
                            sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, CoordinateListener.UPDATE_INTERVAL_TIME, CoordinateListener.UPDATE_DISTANCE_IN_BETWEEN, sCoordinateListener);
                        }
                    }
                } else {
                    sLocationFindingMode = PositionAndHeadingMapVisualization.LocationFindingMode.NONE;
                    sCoordinateListener.setUpdating(false);
                    sLocationManager.removeUpdates(sCoordinateListener);
                    mapViewWithBlueDot.dropGreyBlueDot();
                }
            }
        }
    }

    private boolean isWithinGeofence(HashMap<String, GeofenceLocation> geofenceLocations){
        for (Map.Entry<String, GeofenceLocation> entry : geofenceLocations.entrySet()) {
            if(entry.getValue().getDidEnterGeofence()) return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        if (serviceManager != null) {
            try {
                serviceManager.unbindService(this);
            } catch (SLIndoorLocationException e) {
                e.printStackTrace();
            }
        }
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
        return mContext;
    }

    @Override
    public void didBindToService() {
        if (firstTimeBound) {
            didBindAndLoad();
        }
        firstTimeBound = false;
    }
}
