package com.ivanhoecambridge.mall.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.data.Attribute;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.managers.ETManager;

import org.w3c.dom.Attr;

import java.util.ArrayList;

/**
 * Created by Kay on 2017-03-01.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener{

    private static final String TAG = "SettingsFragment";
    private Preference thePreference;
    public static final String KEY_ATTRIBUTE_WEEKLY_NOTIFICATION = "notification_deals_weekly_digest_enabled";
    public static final String KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION = "notification_deals_daily_reminders_enabled";
    public static final String KEY_ATTRIBUTE_DAILY_EVENT_NOTIFICATION = "notification_events_daily_reminders_enabled";

    private SwitchPreferenceCompat spWeeklyDigest;
    private SwitchPreferenceCompat spDailyDigest;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thePreference = findPreference("chosen_theme");

        if (thePreference != null) {
            thePreference.setOnPreferenceChangeListener(this);
        } else {
            Log.d(TAG, "Preference is empty");
        }

        spWeeklyDigest = (SwitchPreferenceCompat) findPreference(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION);
        spDailyDigest = (SwitchPreferenceCompat) findPreference(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.getInstance(getContext()).logScreenView(this.getActivity(), "Settings Screen - Notifications Section");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        setSwitch();
    }

    /**
     * get ETPush attributes and set the preference in the beginning.
     * Even if ET update fails through updateEt() (e.g. internet connection lost), attributes are cached
     * and it gets eventually updated automatically when there is internet connection.
     * so this may not be necessary. but who knows ET might change the cached attribute after it fails several times to update.
     */
    private void setSwitch(){
        try {
            ArrayList<Attribute> attributes = ETManager.getETAttributes();
            for(Attribute attribute : attributes) {
                if(attribute.getKey().equals(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION)){
                    if(spWeeklyDigest != null) {
                        spWeeklyDigest.setChecked(Boolean.valueOf(attribute.getValue()));
                    }
                }  else if(attribute.getKey().equals(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION)) {
                    if(spDailyDigest != null) {
                        spDailyDigest.setChecked(Boolean.valueOf(attribute.getValue()));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error setting switch from ETManager.getETAttributes()");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean weeklyAttributeAdded = false;
        boolean dailyDealAttributeAdded = false;
        boolean dailyEventAttributeAdded = false;
        if(key.equals(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION)) {
            weeklyAttributeAdded = ETManager.addETAttribute(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION, String.valueOf(sharedPreferences.getBoolean(KEY_ATTRIBUTE_WEEKLY_NOTIFICATION, false)));
        } else if(key.equals(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION)){
            dailyDealAttributeAdded = ETManager.addETAttribute(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION, String.valueOf(sharedPreferences.getBoolean(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION, false)));
            dailyEventAttributeAdded = ETManager.addETAttribute(KEY_ATTRIBUTE_DAILY_EVENT_NOTIFICATION, String.valueOf(sharedPreferences.getBoolean(KEY_ATTRIBUTE_DAILY_DEAL_NOTIFICATION, false)));
        }

        if(weeklyAttributeAdded || dailyDealAttributeAdded || dailyEventAttributeAdded) {
            ETManager.updateET();
        }
    }
}
