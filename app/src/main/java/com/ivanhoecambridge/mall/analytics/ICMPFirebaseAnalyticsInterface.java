package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;

/**
 * Created by simon on 2017-07-14.
 */

public interface ICMPFirebaseAnalyticsInterface {

    public void logScreenView(Activity activity, String screenName);

    public void logEvent(String eventName, String category, String action, String label, Long value);
}
