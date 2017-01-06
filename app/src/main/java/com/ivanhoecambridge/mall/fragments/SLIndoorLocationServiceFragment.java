package com.ivanhoecambridge.mall.fragments;

import android.content.Context;
import android.os.Bundle;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.BuildConfig;
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
import com.senionlab.slutilities.type.SLLocationState;
import com.senionlab.slutilities.type.SLLocationStatus;
import com.senionlab.slutilities.type.SLMotionType;

import java.util.ArrayList;

import slutilities.SLSettings;

/**
 * Created by Kay on 2017-01-05.
 */

public class SLIndoorLocationServiceFragment extends BaseFragment implements SLConsumer {

    protected final Logger logger = new Logger(getClass().getName());
    private SLServiceManager serviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.BLUEDOT) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BuildConfig.BLUEDOT) {
            try {

                serviceManager = SLServiceManager.getInstance(getActivity());
                serviceManager.registerReceiver(receiver);
                serviceManager.start(SLSettings.MAP_KEY, SLSettings.CUSTOMER_ID);

                //                https://developer.senionlab.com/documentation/sdk/64/classcom_1_1senionlab_1_1slutilities_1_1service_1_1_s_l_service_manager.html
            /*SLCoordinate3D location = new SLCoordinate3D(58.39354824, 15.56134828, new FloorNr(3));
            SLLocationState locationState = new SLLocationState(location, 5.0, SLLocationStatus.CONFIRMED);
            SLCoordinate3D location2 = new SLCoordinate3D(58.39348912, 15.56118789, new FloorNr(3));
            SLLocationState locationState2 = new SLLocationState(location2, 5.0, SLLocationStatus.CONFIRMED);
            ArrayList<SLLocationState> locationStateList = new ArrayList<SLLocationState>();
            locationStateList.add(locationState);
            locationStateList.add(locationState2);
            serviceManager.startMockupLocation(SLSettings.MAP_KEY, SLSettings.CUSTOMER_ID, locationStateList);*/

//            SLServiceManager.getInstance(getActivity().getApplicationContext()).bindService(this);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if(BuildConfig.BLUEDOT) {
            try {
                serviceManager.unbindService(this);
            } catch (SLIndoorLocationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if(BuildConfig.BLUEDOT) {
            SLServiceManager serviceManager = SLServiceManager.getInstance(getActivity());
            serviceManager.unregisterReceiver(receiver);
            try {
                serviceManager.stop();
            } catch (SLIndoorLocationException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    // --------------------------------
    // Broadcast receiver
    // --------------------------------
    private SLBroadcastReceiver receiver = new SLBroadcastReceiver() {

        @Override
        public void didUpdateLocation(SLCoordinate3D slCoordinate3D, double v, SLLocationStatus slLocationStatus) {
            logger.debug("didUpdateLocation++" + " slCoordinate3D: " + slCoordinate3D + " v: " + v);




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

    // --------------------------------
    // SLConsumer methods
    // --------------------------------
    @Override
    public void didBindToService() {
        // Do things that require you to be bound to the positioning service.
        // For instance, register regions to be monitored for geofencing.

        // Define a circle and add to the list of monitored regions
        SLCoordinate3D origin = new SLCoordinate3D(58.39341485, 15.56097329, new FloorNr(3));
        SLCircle circle = new SLCircle(new SLGeometryId("circle"), origin, 5.0);
        try {
            serviceManager.addGeometry(circle);
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Context getContext() {
        return getActivity();
    }
}
