package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;
import android.content.Context;


/**
 * Created by simon on 2017-06-06.
 */
public class Analytics implements AnalyticsInterface{

    private static Analytics sAnalytics = null;
    private ICMPFirebaseAnalytics firebaseAnalytics = null;

    private Analytics(Context context) {
        firebaseAnalytics = ICMPFirebaseAnalytics.getInstance(context);
    }

    public static Analytics getInstance(Context context) {
        if (sAnalytics == null) {
            sAnalytics  = new Analytics(context);
        }
        return sAnalytics;
    }

    public void logScreenView(Activity activity, String screenName) {
        firebaseAnalytics.logScreenView(activity, screenName);
    }

    public void logEvent(String eventName, String category, String action) {
        firebaseAnalytics.logEvent(eventName, category, action);
    }

    public void logEvent(String eventName, String category, String action, String label) {
        firebaseAnalytics.logEvent(eventName, category, action, label);
    }

    public void logEvent(String eventName, String category, String action, long value) {
        firebaseAnalytics.logEvent(eventName, category, action, value);
    }

    public void logEvent(String eventName, String category, String action, String label, long value) {
        firebaseAnalytics.logEvent(eventName, category, action, label, value);
    }
}
