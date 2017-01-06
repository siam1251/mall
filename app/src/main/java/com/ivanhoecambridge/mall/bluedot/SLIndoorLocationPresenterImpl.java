package com.ivanhoecambridge.mall.bluedot;

import android.content.Context;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
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

/**
 * Created by Kay on 2017-01-06.
 */

public class SLIndoorLocationPresenterImpl implements  SLIndoorLocationPresenter, SLConsumer {

    private final PositionAndHeadingMapVisualization positionAndHeadingMapVisualization = new PositionAndHeadingMapVisualization();

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

    private SLBroadcastReceiver receiver = new SLBroadcastReceiver() {

        @Override
        public void didUpdateLocation(SLCoordinate3D location, double uncertaintyRadius, SLLocationStatus status) {
            synchronized (this) {
                logger.debug("didUpdateLocation++" + " slCoordinate3D: " + location + " uncertaintyRadius: " + uncertaintyRadius);

                BlueDotPosition blueDotPosition = new BlueDotPosition(location);
                positionAndHeadingMapVisualization.setPos(blueDotPosition);
                mapViewWithBlueDot.dropBlueDot(blueDotPosition);
            }
        }

        @Override
        public void didUpdateLocationAvailability(LocationAvailability locationAvailability) {
            logger.debug("didUpdateLocationAvailability++" + " locationAvailability: " + locationAvailability);

            // Do something with locationAvailability, for instance hide/show location marker.
        }
        @Override
        public void didUpdateHeading(double heading, SLHeadingStatus status) {
//            logger.debug("didUpdateHeading++" + " heading: " + heading + " SLHeadingStatus: " + status);
            // Do something with the heading here
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
            logger.debug("errorBleNotEnabled++");
            // Prompt the user to enable BLE
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
