package com.kineticcafe.kcpmall.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.MainActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Kay on 2016-08-23.
 */
public class GeofenceManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static final int INITIAL_REQUEST = 1337;
    public static final int LOCATION_REQUEST = INITIAL_REQUEST+3;
    public static final String GEOFENCE_IS_CONNECTED = "geofence_is_connected";

    private Context mContext;
    protected static final String TAG = "MainActivity";
    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
    private MyWebRequestReceiver mMyWebRequestReceiver;

    public GeofenceManager(Context context) {

        mContext = context;
        //geofence
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        mSharedPreferences = mContext.getSharedPreferences(GeofenceConstants.SHARED_PREFERENCES_NAME,
                mContext.MODE_PRIVATE);

        mGeofencesAdded = mSharedPreferences.getBoolean(GeofenceConstants.GEOFENCES_ADDED_KEY, false);

        initialize();
    }

    private void initialize() {
        populateGeofenceList();
        buildGoogleApiClient();
    }

    public boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mContext, perm));
    }


    public void setGeofence(boolean enableGeofence){
        if(enableGeofence) {
            addGeofencesButtonHandler();
        } else {
            removeGeofencesButtonHandler();
        }
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    //GEOFENCE
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void addGeofencesButtonHandler() {

        if (!canAccessLocation()) {
            ActivityCompat.requestPermissions((Activity) mContext, INITIAL_PERMS, INITIAL_REQUEST);
        }

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(mContext, mContext.getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            registerBoardcastReceiver();
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    public void removeGeofencesButtonHandler() {

        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(mContext, mContext.getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            unRegisterBroadcastReceiver();
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().

        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(GeofenceConstants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            /*Toast.makeText(
                    mContext,
                    mContext.getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();*/
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(mContext,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    public void unRegisterBroadcastReceiver(){
        if(mMyWebRequestReceiver != null) mContext.unregisterReceiver(mMyWebRequestReceiver);
    }

    private void registerBoardcastReceiver(){
        if(mMyWebRequestReceiver == null) {
            mMyWebRequestReceiver = new MyWebRequestReceiver();
        }
        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(mMyWebRequestReceiver, filter);
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : GeofenceConstants.GEOFENCE_AREA_LAT_LONG.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            GeofenceConstants.GEOFENCE_RADIUS_IN_METERS
                    )

                    .setExpirationDuration(GeofenceConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    .build());
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
//        Toast.makeText(mContext, "Connected to GoogleApiClient", Toast.LENGTH_LONG).show();
        setGeofence(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
//        Toast.makeText(mContext, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
//        Toast.makeText(mContext, "Connection suspended", Toast.LENGTH_LONG).show();
    }

    public class MyWebRequestReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean response = intent.getBooleanExtra(GeofenceManager.GEOFENCE_IS_CONNECTED, false);
            if(mContext != null) {
                if(response) {
                    ((MainActivity) mContext).setActiveMall(true);
                } else {
                    ((MainActivity) mContext).setActiveMall(false);
                }
            }

//            String responseString = intent.getStringExtra(MyWebRequestService.RESPONSE_STRING);
//            String reponseMessage = intent.getStringExtra(MyWebRequestService.RESPONSE_MESSAGE);



        }
    }




}
