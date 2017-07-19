package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;
import android.content.Context;


/**
 * Created by simon on 2017-06-06.
 */
public class Analytics implements AnalyticsInterface{

    private static Analytics sAnalytics = null;
    private ICMPFirebaseAnalytics mFirebaseAnalytics = null;

    private Analytics(Context context) {
        mFirebaseAnalytics = ICMPFirebaseAnalytics.getInstance(context);
    }

    public static Analytics getInstance(Context context) {
        if (sAnalytics == null) {
            sAnalytics  = new Analytics(context);
        }
        return sAnalytics;
    }

    public void logScreenView(Activity activity, String screenName) {
        mFirebaseAnalytics.logScreenView(activity, screenName);
    }

    public void logEvent(String eventName, String category, String action) {
        mFirebaseAnalytics.logEvent(eventName, category, action, null, null);
    }

    public void logEvent(String eventName, String category, String action, String label) {
        mFirebaseAnalytics.logEvent(eventName, category, action, label, null);
    }

    public void logEvent(String eventName, String category, String action, long value) {
        mFirebaseAnalytics.logEvent(eventName, category, action, null, value);
    }

    public void logEvent(String eventName, String category, String action, String label, long value) {
        mFirebaseAnalytics.logEvent(eventName, category, action, label, value);
    }
}
