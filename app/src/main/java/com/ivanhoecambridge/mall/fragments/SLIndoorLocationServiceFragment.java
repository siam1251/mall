package com.ivanhoecambridge.mall.fragments;

import android.content.Context;
import android.os.Bundle;

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
import com.senionlab.slutilities.type.SLMotionType;

/**
 * Created by Kay on 2017-01-05.
 */

public class SLIndoorLocationServiceFragment extends BaseFragment implements SLConsumer {

    private SLServiceManager serviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mapKey = ""; // Map key should be obtained from SenionLab
        String custId = ""; // Customer id should be obtained from SenionLab
        SLServiceManager serviceManager = SLServiceManager.getInstance(getActivity());
        serviceManager.registerReceiver(receiver);
        serviceManager.start(mapKey, custId);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            serviceManager.bindService(this);
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            serviceManager.unbindService(this);
        } catch (SLIndoorLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        SLServiceManager serviceManager = SLServiceManager.getInstance(this);
        serviceManager.unregisterReceiver(receiver);
        serviceManager.stop();
        super.onDestroy();
    }

    // --------------------------------
    // Broadcast receiver
    // --------------------------------
    private SLBroadcastReceiver receiver = new SLBroadcastReceiver() {

        @Override
        public void didUpdateLocation(SLCoordinate3D location, double uncertaintyRadius) {
            // Do something with the location here
        }
        @Override
        public void didUpdateLocationAvailability((LocationAvailability locationAvailability) {
            // Do something with locationAvailability, for instance hide/show location marker.
        }
        @Override
        public void didUpdateHeading(double heading, SLHeadingStatus status) {
            // Do something with the heading here
        }

        @Override
        public void didFinishLoadingManager() {}
        @Override
        public void errorWhileLoadingManager(String errorMsg) {
            // Handle error here
        }

        @Override
        public void didUpdateMotionType(SLMotionType motionType) {
            // Do something with the motion type here
        }
        @Override
        public void errorBleNotEnabled() {
            // Prompt the user to enable BLE
        }
        @Override
        public void errorWifiNotEnabled() {
            // Prompt the user to enable WiFi
        }
        @Override
        public void didEnterGeometry(SLGeometry geometry) {
            // The user entered the area defined by geometry
        }
        @Override
        public void didLeaveGeometry(SLGeometry geometry) {
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
        return this;
    }
}
