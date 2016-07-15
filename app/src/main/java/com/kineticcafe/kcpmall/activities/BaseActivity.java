package com.kineticcafe.kcpmall.activities;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kineticcafe.kcpmall.analytics.FirebaseTracking;

/**
 * Created by Kay on 2016-07-15.
 */
public class BaseActivity extends AppCompatActivity implements ComponentCallbacks2 {
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
        Log.d("onLowMemory", "onLowMemory");
    }

    @Override
    public void onTrimMemory(final int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d("OnTrimMembyer", "OnTrimMembyer");
            FirebaseTracking.getInstance(this).logAppPutBackground();
        }
        // you might as well implement some memory cleanup here and be a nice Android dev.
    }
}
