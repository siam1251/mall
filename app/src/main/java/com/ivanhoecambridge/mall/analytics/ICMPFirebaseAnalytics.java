package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by simon on 2017-06-06.
 */

public class ICMPFirebaseAnalytics implements ICMPFirebaseAnalyticsInterface {

    private static ICMPFirebaseAnalytics sICMPFirebaseAnalytics = null;
    private FirebaseAnalytics mFirebaseAnalytics = null;
    private final int mParamNameCharacterLimit = 40;
    private final int mParamValueCharacterLimit = 100;

    private ICMPFirebaseAnalytics(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static ICMPFirebaseAnalytics getInstance(Context context) {
        if (sICMPFirebaseAnalytics == null) {
            sICMPFirebaseAnalytics  = new ICMPFirebaseAnalytics(context);
        }
        return sICMPFirebaseAnalytics;
    }

    private Bundle makeBundle(String category, String action, String label, Long value) {
        Bundle bundle = new Bundle();

        if (category.length() > mParamValueCharacterLimit) {
            category = category.substring(0, mParamValueCharacterLimit);
        }
        bundle.putString("category", category);

        if (action.length() > mParamValueCharacterLimit) {
            action = action.substring(0, mParamValueCharacterLimit);
        }
        bundle.putString("action", action);

        if (label != null) {
            if (label.length() > mParamValueCharacterLimit) {
                label = label.substring(0, mParamValueCharacterLimit);
            }
            bundle.putString("label", label);
        }

        if (value != null) {
            bundle.putLong("value", value);
        }

        return bundle;
    }

    @Override
    public void logScreenView(Activity activity, String screenName) {
        if (screenName.length() > mParamValueCharacterLimit) {
            screenName = screenName.substring(0, mParamValueCharacterLimit);
        }
        mFirebaseAnalytics.setCurrentScreen(activity, screenName, null /* class override */);
    }

    @Override
    public void logEvent(String eventName, String category, String action, String label, Long value) {
        if (eventName.length() > mParamNameCharacterLimit) {
            eventName = eventName.substring(0, mParamNameCharacterLimit);
        }
        Bundle bundle = makeBundle(category, action, label, value);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }
}
