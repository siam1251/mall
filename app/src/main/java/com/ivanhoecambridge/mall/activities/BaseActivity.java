package com.ivanhoecambridge.mall.activities;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Kay on 2016-07-15.
 */
public class BaseActivity extends AppCompatActivity implements ComponentCallbacks2 {


    public boolean mIsRunning = false;


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        Log.d("onLowMemory", "onLowMemory");
    }

    @Override
    public void onTrimMemory(final int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d("OnTrimMembyer", "OnTrimMembyer");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsRunning = false;
    }

}
