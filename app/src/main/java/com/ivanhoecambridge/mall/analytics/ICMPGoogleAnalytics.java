package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.R;

/**
 * Created by simon on 2017-06-06.
 */

public class ICMPGoogleAnalytics implements AnalyticsInterface {

    private static ICMPGoogleAnalytics instance = null;
    private static Tracker sTracker = null;
    private GoogleAnalytics googleAnalytics = null;


    private ICMPGoogleAnalytics(Context context) {
        googleAnalytics = GoogleAnalytics.getInstance(context);
        if(BuildConfig.DEBUG) {
            googleAnalytics.setDryRun(true);
        }
    }

    public static ICMPGoogleAnalytics getInstance(Context context) {
        if (instance == null) {
            instance  = new ICMPGoogleAnalytics(context);
        }
        return instance;
    }

    synchronized private Tracker getDefaultTracker() {
        if (sTracker == null) {
            sTracker = googleAnalytics.newTracker(R.xml.global_tracker);
        }
        return sTracker;
    }

    @Override
    public void logScreenView(Activity activity, String screenName) {
        Tracker t = getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void logEvent(String eventName, String category, String action) {
        Tracker t = getDefaultTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    @Override
    public void logEvent(String eventName, String category, String action, String label) {
        Tracker t = getDefaultTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    @Override
    public void logEvent(String eventName, String category, String action, long value) {
        Tracker t = getDefaultTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setValue(value)
                .build());
    }

    @Override
    public void logEvent(String eventName, String category, String action, String label, long value) {
        Tracker t = getDefaultTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }
}
