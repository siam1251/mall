package com.kineticcafe.kcpmall.analytics;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Kay on 2016-07-15.
 */
public class FirebaseTracking {

    private static FirebaseTracking sFirebaseTracking;
    private static Bundle sUserProfile;
    public static class Param {
        public static final String DEVICE_ID = "custom_device_id";
        public static final String DEVICE_MANUFACTURE_MODEL = "custom_manufacture_model"; //Samsung GT-S5830L
        public static final String OS_VERSION = "custom_os_version"; //6.0.1
        public static final String TIME_STAMP_MILLISECOND = "time_stamp_in_milliseconds"; // 1468601912402
        public static final String TIME_STAMP_SECOND = "time_stamp_in_seconds"; // 1468601912
        public static final String TIME_STAMP_FORMATTED = "time_stamp_formatted"; //yyyy-MM-dd HH:mm:ss
        public static final String TESTING_DEVICE_ID = "DeviceID"; //yyyy-MM-dd HH:mm:ss

        protected Param() {
        }
    }

    public static class Event {
        public static final String APP_LAUNCH = "custom_app_launch"; //when app's put background
        public static final String APP_PUT_BACKGROUND = "custom_put_background"; //when app's put background
        public static final String APP_PUT_FOREGROUND = "custom_put_foreground"; //when app's put foreground

        protected Event() {
        }
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    private Context mContext;

    private FirebaseTracking(Context context) {
        mContext = context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static FirebaseTracking getInstance(Context context){
        if(sFirebaseTracking == null) sFirebaseTracking = new FirebaseTracking(context);
        return sFirebaseTracking;
    }

    /**
     * @return userProfile bundle that has unique device Id, device model and OS version
     */
    public Bundle getUserProfile() {
        if(sUserProfile == null) {
            sUserProfile = new Bundle();
            String deviceId = FirebaseUtility.getDeviceId(mContext);
            sUserProfile.putString(Param.DEVICE_ID, deviceId);
            sUserProfile.putString(Param.TESTING_DEVICE_ID, deviceId);
            sUserProfile.putString(Param.DEVICE_MANUFACTURE_MODEL, FirebaseUtility.getDeviceName());
            sUserProfile.putString(Param.OS_VERSION, Build.VERSION.RELEASE);
        }
        return sUserProfile;
    }

    public Bundle getUserProfileWithFormattedTimeStamp(){
        Bundle bundle = new Bundle(getUserProfile());
        bundle.putString(Param.TIME_STAMP_FORMATTED, FirebaseUtility.getCurrentTimeStampFormatted());
        return bundle;
    }

    public void logAppLaunch() {
        mFirebaseAnalytics.logEvent(Event.APP_LAUNCH, getUserProfileWithFormattedTimeStamp());
    }
    public void logAppPutBackground() {
        mFirebaseAnalytics.logEvent(Event.APP_PUT_BACKGROUND, getUserProfileWithFormattedTimeStamp());
    }

    public void logAppPutForeground() {
        mFirebaseAnalytics.logEvent(Event.APP_PUT_FOREGROUND, getUserProfileWithFormattedTimeStamp());
    }

    public void logViewItemList(String itemCategory) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    public void logEvent(Bundle bundle){
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}

