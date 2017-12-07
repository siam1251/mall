package com.ivanhoecambridge.mall.managers;

import android.content.Context;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.data.Attribute;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;

import java.util.ArrayList;

/**
 * Created by Kay on 2017-03-02.
 */

public class ETManager {

    private final static String GSON_KEY_REQUESTED_NOTIF = "notifications_requested";
    public static final String KEY_ATTRIBUTE_WEEKLY_NOTIFICATION = "notification_deals_weekly_digest_enabled";
    public static final String KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION = "notification_deals_daily_reminders_enabled";
    public static final String KEY_ATTRIBUTE_DAILY_EVENT_NOTIFICATION = "notification_events_daily_reminders_enabled";

    public interface NotificationRequestListener {
        void onNotificationRequestRequired();
    }

    public static ArrayList<Attribute> getETAttributes(){
        try {
            return ETPush.getInstance().getAttributes();
        } catch (ETException e) {
            e.printStackTrace();
        }
        return new ArrayList<Attribute>();
    }

    public static boolean addETAttribute(String key, String value){
        try {
            return ETPush.getInstance().addAttribute(key, value);
        } catch (ETException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateET(){
        try {
            ETPush.getInstance().updateEt();
        } catch (ETException e) {
            e.printStackTrace();
        }
    }

    public static void updateSubscriberKey(String subscriberKey) {
        try {
            ETPush.getInstance().setSubscriberKey(subscriberKey);
            ETPush.getInstance().updateEt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toggleAllNotifications(boolean toggleOn) {
        addETAttribute(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION, String.valueOf(toggleOn));
        addETAttribute(KEY_ATTRIBUTE_DAILY_EVENT_NOTIFICATION, String.valueOf(toggleOn));
        addETAttribute(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION, String.valueOf(toggleOn));
        updateET();
    }

    public static void requestNotificationPermission(Context context, NotificationRequestListener notificationListener) {
        if (!KcpUtility.loadFromSharedPreferences(context, GSON_KEY_REQUESTED_NOTIF, false)) {
            KcpUtility.saveToSharedPreferences(context, GSON_KEY_REQUESTED_NOTIF, true);
            notificationListener.onNotificationRequestRequired();
        }
    }

    public static String getDeviceId() {
        String deviceId = "";
        try {
            deviceId = ETPush.getInstance().getDeviceId();
        } catch (ETException e) {
            e.printStackTrace();
        }
        return deviceId;
    }

}
