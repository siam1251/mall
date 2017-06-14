package com.ivanhoecambridge.mall.analytics;

import android.app.Activity;

/**
 * Created by simon on 2017-06-06.
 */

public interface AnalyticsInterface {

    public void logScreenView(Activity activity, String screenName);

    public void logEvent(String eventName, String category, String action);

    public void logEvent(String eventName, String category, String action, String label);

    public void logEvent(String eventName, String category, String action, long value);

    public void logEvent(String eventName, String category, String action, String label, long value);
}
