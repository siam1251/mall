package com.ivanhoecambridge.mall.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.activities.KcpApplication;
import com.salesforce.marketingcloud.MarketingCloudSdk;
import com.salesforce.marketingcloud.registration.Attribute;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kay on 2017-03-02.
 */

public class MarketingCloudManager {

    public static final String TAG = "MCM";

    private final static String GSON_KEY_REQUESTED_NOTIF = "notifications_requested";
    public static final String KEY_ATTRIBUTE_WEEKLY_NOTIFICATION = "notification_deals_weekly_digest_enabled";
    public static final String KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION = "notification_deals_daily_reminders_enabled";
    public static final String KEY_ATTRIBUTE_DAILY_EVENT_NOTIFICATION = "notification_events_daily_reminders_enabled";


    public interface NotificationRequestListener {
        void onNotificationRequestRequired();
    }

    public static List<Attribute> getRegistrationAttributes(){
        try {
            MarketingCloudSdk marketingCloud = MarketingCloudSdk.getInstance();
            if (marketingCloud == null) return new ArrayList<>();

            return marketingCloud.getRegistrationManager().getAttributes();
        } catch (IllegalStateException e) {
            return new ArrayList<>();
        }
    }



    public static boolean addRegistrationAttribute(String key, String value){
        try {
            return MarketingCloudSdk.getInstance() != null &&
                    MarketingCloudSdk.getInstance().getRegistrationManager()
                            .edit()
                            .setAttribute(key, value)
                            .commit();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void updateContactKey(String value) {
        try {
            MarketingCloudSdk marketingCloud = MarketingCloudSdk.getInstance();
            if (marketingCloud == null) return;

            marketingCloud.getRegistrationManager()
                    .edit()
                    .setContactKey(value)
                    .commit();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Marketing Cloud not initialized");
        }
    }

    public static void registerMarketingCloudListener(@NonNull KcpApplication.MarketingCloudListener readyListener) {
        KcpApplication.registerMarketingCloudListener(readyListener);
    }


    public static void toggleAllNotifications(boolean toggleOn) {
        addRegistrationAttribute(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION, String.valueOf(toggleOn));
        addRegistrationAttribute(KEY_ATTRIBUTE_DAILY_EVENT_NOTIFICATION, String.valueOf(toggleOn));
        addRegistrationAttribute(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION, String.valueOf(toggleOn));
    }

    public static void requestNotificationPermission(Context context, NotificationRequestListener notificationListener) {
        if (!KcpUtility.loadFromSharedPreferences(context, GSON_KEY_REQUESTED_NOTIF, false)) {
            KcpUtility.saveToSharedPreferences(context, GSON_KEY_REQUESTED_NOTIF, true);
            notificationListener.onNotificationRequestRequired();
        }
    }

    public static String getDeviceId() {
        try {
            if (MarketingCloudSdk.getInstance() != null) {
                return MarketingCloudSdk.getInstance()
                        .getRegistrationManager()
                        .getDeviceId();
            } else {
                return "";
            }
        } catch (IllegalStateException e) {
            return "";
        }
    }

}
