package com.ivanhoecambridge.mall.bluedot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.senionlab.slutilities.geofencing.interfaces.SLGeometry;
import com.senionlab.slutilities.service.SLBroadcastReceiver;
import com.senionlab.slutilities.service.SLConsumer;
import com.senionlab.slutilities.service.SLServiceManager;
import com.senionlab.slutilities.type.LocationAvailability;
import com.senionlab.slutilities.type.SLCoordinate3D;
import com.senionlab.slutilities.type.SLHeadingStatus;
import com.senionlab.slutilities.type.SLIndoorLocationException;
import com.senionlab.slutilities.type.SLJsonProcessingException;
import com.senionlab.slutilities.type.SLLocationStatus;
import com.senionlab.slutilities.type.SLMotionType;
import com.senionlab.slutilities.type.SLPixelPoint2D;

import slutilities.SLSettings;

import static com.ivanhoecambridge.mall.activities.MainActivity.VIEWPAGER_PAGE_MAP;
import static com.ivanhoecambridge.mall.bluedot.BluetoothManager.mDidAskToTurnOnBluetooth;

/**
 * Created by Kay on 2017-01-06.
 */

public class SLIndoorLocationPresenterImpl implements  SLIndoorLocationPresenter, SLConsumer {

    private final PositionAndHeadingMapVisualization positionAndHeadingMapVisualization = new PositionAndHeadingMapVisualization();
    public static boolean mAskForBluetooth = false;

    protected final Logger logger = new Logger(getClass().getName());
    private SLServiceManager serviceManager;
    private Context context;


    private MapViewWithBlueDot mapViewWithBlueDot;

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
                /*if(blueDotPosition.getMappedInFloor() != mapViewWithBlueDot.getCurrentFloor()) {
                    mapViewWithBlueDot.removeBlueDot();
                    return;
                } else {*/
//                    long dtMili = System.currentTimeMillis();
//                    Log.d("bluedot", "BlueDotPosition RAW: "  + blueDotPosition.getLatitude() + " " + blueDotPosition.getLongitude());
//                    mapViewWithBlueDot.dropBlueDot(blueDotPosition);
                positionAndHeadingMapVisualization.setPos(blueDotPosition);
//                }
            }
        }

        @Override
        public void didUpdateLocationAvailability(LocationAvailability locationAvailability) {
            logger.debug("didUpdateLocationAvailability++" + " locationAvailability: " + locationAvailability);
            if(!locationAvailability.isAvailable()) mapViewWithBlueDot.removeBlueDot();

            // Do something with locationAvailability, for instance hide/show location marker.
        }
        @Override
        public void didUpdateHeading(double heading, SLHeadingStatus status) {
            Log.d("bluedot", "BlueDotPosition: "  + " headingRaw: " + heading);
            // Do something with the heading here
            positionAndHeadingMapVisualization.setHeading((float) heading, status);
        }

        @Override
        public void didFinishLoadingManager() {}
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
            // The user entered the area defined by geometry
        }
        @Override
        public void didLeaveGeometry(SLGeometry geometry) {
            logger.debug("didLeaveGeometry++");
            mapViewWithBlueDot.removeBlueDot();
            // The user left the area defined by geometry
        }
    };


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

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void didBindToService() {
        // Do things that require you to be bound to the positioning service.
        // For instance, register regions to be monitored for geofencing.

        // Define a circle and add to the list of monitored regions
        /*SLCoordinate3D origin = new SLCoordinate3D(58.39341485, 15.56097329, new FloorNr(3));
        SLCircle circle = new SLCircle(new SLGeometryId("circle"), origin, 5.0);
        try {
            serviceManager.addGeometry(circle);
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        }*/
    }
}
