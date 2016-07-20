package com.kineticcafe.kcpmall.activities;

import android.app.Application;

import com.kineticcafe.kcpmall.analytics.FirebaseTracking;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Kay on 2016-04-28.
 */
public class KcpApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics()); //ENABLE FOR PRODUCTION
    }
}
