package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;
import android.content.Context;


/**
 * Created by simon on 2017-06-06.
 */
public class Analytics implements AnalyticsInterface{

    private static Analytics sAnalytics = null;
    private ICMPGoogleAnalytics googleAnalytics = null;
    private ICMPFirebaseAnalytics firebaseAnalytics = null;

    private Analytics(Context context) {
        sAnalytics = Analytics.getInstance(context);
        googleAnalytics = ICMPGoogleAnalytics.getInstance(context);
        firebaseAnalytics = ICMPFirebaseAnalytics.getInstance(context);
    }

    public static Analytics getInstance(Context context) {
        if (sAnalytics == null) {
            sAnalytics  = new Analytics(context);
        }
        return sAnalytics;
    }

    public void logScreenView (Activity activity, String screenName) {
        googleAnalytics.logScreenView(activity, screenName);
        firebaseAnalytics.logScreenView(activity, screenName);
    }

    public void logEvent (String eventName, String category, String action) {
        googleAnalytics.logEvent(eventName, category, action);
        firebaseAnalytics.logEvent(eventName, category, action);
    }

    public void logEvent (String eventName, String category, String action, String label) {
        googleAnalytics.logEvent(eventName, category, action, label);
        firebaseAnalytics.logEvent(eventName, category, action, label);
    }

    public void logEvent (String eventName, String category, String action, long value) {
        googleAnalytics.logEvent(eventName, category, action, value);
        firebaseAnalytics.logEvent(eventName, category, action, value);
    }

    public void logEvent (String eventName, String category, String action, String label, long value) {
        googleAnalytics.logEvent(eventName, category, action, label, value);
        firebaseAnalytics.logEvent(eventName, category, action, label, value);
    }
}
