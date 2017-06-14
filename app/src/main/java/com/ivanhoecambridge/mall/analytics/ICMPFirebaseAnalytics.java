package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by simon on 2017-06-06.
 */

public class ICMPFirebaseAnalytics implements AnalyticsInterface {

    private static ICMPFirebaseAnalytics sICMPFirebaseAnalytics = null;
    private FirebaseAnalytics mFirebaseAnalytics = null;

    private ICMPFirebaseAnalytics(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static ICMPFirebaseAnalytics getInstance(Context context) {
        if (sICMPFirebaseAnalytics == null) {
            sICMPFirebaseAnalytics  = new ICMPFirebaseAnalytics(context);
        }
        return sICMPFirebaseAnalytics;
    }

    @Override
    public void logScreenView(Activity activity, String screenName) {
        mFirebaseAnalytics.setCurrentScreen(activity, screenName, null /* class override */);
    }

    @Override
    public void logEvent(String eventName, String category, String action) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("action", action);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }

    @Override
    public void logEvent(String eventName, String category, String action, String label) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("action", action);
        bundle.putString("label", label);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }

    @Override
    public void logEvent(String eventName, String category, String action, long value) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("action", action);
        bundle.putLong("value", value);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }

    @Override
    public void logEvent(String eventName, String category, String action, String label, long value) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("action", action);
        bundle.putString("label", label);
        bundle.putLong("value", value);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }
}
