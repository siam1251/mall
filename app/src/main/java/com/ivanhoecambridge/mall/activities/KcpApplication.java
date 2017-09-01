package com.ivanhoecambridge.mall.activities;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.exacttarget.etpushsdk.ETAnalytics;
import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETLogListener;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETPushConfig;
import com.exacttarget.etpushsdk.ETPushConfigureSdkListener;
import com.exacttarget.etpushsdk.ETRequestStatus;
import com.exacttarget.etpushsdk.util.EventBus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.account.KcpAccount;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.constants.JanRain;
import com.janrain.android.Jump;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.LinkedHashSet;

import constants.MallConstants;
import factory.HeaderFactory;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Kay on 2016-04-28.
 */
public class KcpApplication extends MultiDexApplication implements ETLogListener, ETPushConfigureSdkListener {

    private static final String TAG = "KcpApplication";
    public static final boolean ANALYTICS_ENABLED = false;
    public static final boolean CLOUD_PAGES_ENABLED = false;
    public static final boolean WAMA_ENABLED = false;
    public static final boolean PROXIMITY_ENABLED = false;
    public static final boolean LOCATION_ENABLED = true;
    private static final LinkedHashSet<EtPushListener> listeners = new LinkedHashSet<>();
    private static ETPush etPush;

    @Override
    protected void attachBaseContext(Context newBase) { //http://stackoverflow.com/questions/37045787/java-lang-noclassdeffounderror-retrofit2-utils-in-android/37178366
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    public static ETPush getEtPush(@NonNull final EtPushListener etPushListener) {
        if (etPush == null) {
            listeners.add(etPushListener);
        }
        return etPush;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(MallConstants.TWITTER_API_KEY, MallConstants.TWITTER_API_SECRET);
        KcpConstants.setBaseURL(Constants.IS_APP_IN_PRODUCTION);
        KcpAccount.getInstance().initialize(getApplicationContext());
        HeaderFactory.changeCatalog(HeaderFactory.HEADER_VALUE_DATAHUB_CATALOG);

        if(BuildConfig.REPORT_CRASH) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
            Fabric.with(this, new TwitterCore(authConfig), new Crashlytics(), new TweetUi());
        } else {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
            Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        }

        KcpUtility.cacheToPreferences(this, Constants.PREF_KEY_WELCOME_MSG_DID_APPEAR, false); //resetting the welcome message flag as false to indicate it has never shown for this app launch
        if(BuildConfig.PUSH){
            //EXACT TARGET
            EventBus.getInstance().register(this);
            try {

                 String appId = MallConstants.ET_APP_ID;
                 String accessToken = MallConstants.ET_ACCESS_TOKEN;
                 String gcmSenderId = MallConstants.GCM_SENDER_ID;


                ETPush.configureSdk(new ETPushConfig.Builder(this)
                                .setEtAppId(appId)
                                .setAccessToken(accessToken)
                                .setGcmSenderId(gcmSenderId)
                                .setOpenDirectRecipientClass(MainActivity.class) //prevent the SDK from using the default ETLandingPage activity to open the OpenDirect URL sent with the message payload
                                .setAnalyticsEnabled(ANALYTICS_ENABLED)
                                .setLocationEnabled(LOCATION_ENABLED)
                                .setPiAnalyticsEnabled(WAMA_ENABLED)
                                .setCloudPagesEnabled(CLOUD_PAGES_ENABLED)
                                .setProximityEnabled(PROXIMITY_ENABLED)
                                .setNotificationResourceId(R.drawable.icn_noti)
                                .build()
                        , this // Our ETPushConfigureSdkListener
                );
            } catch (ETException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        //disable Fabric crashlytics for staging
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build(), new Crashlytics());
        //disable Firebase crashlytics for staging but this stops error log from showing in debug mode
        /*Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException (Thread thread, Throwable e) {
                if(BuildConfig.REPORT_CRASH) FirebaseCrash.report( e);
            }
        });*/

        if (BuildConfig.JANRAIN_DEV) {
            Jump.init(getApplicationContext(), JanRain.configure(JanRain.AppType.DEV));
        } else {
            Jump.init(getApplicationContext(), JanRain.configure(JanRain.AppType.PROD));
        }

    }

    @Override
    public void out(int i, String s, String s1, @Nullable Throwable throwable) {

    }

    @Override
    public void onETPushConfigurationSuccess(ETPush etPush, ETRequestStatus etRequestStatus) {
        KcpApplication.etPush = etPush;
        ETAnalytics.trackPageView("data://ReadyAimFireCompleted", "Marketing Cloud SDK Initialization Complete");

        // If there was an user recoverable issue with Google Play Services then show a notification to the user
        int googlePlayServicesStatus = etRequestStatus.getGooglePlayServiceStatusCode();
        if (googlePlayServicesStatus != ConnectionResult.SUCCESS && GoogleApiAvailability.getInstance().isUserResolvableError(googlePlayServicesStatus)) {
            GoogleApiAvailability.getInstance().showErrorNotification(this, googlePlayServicesStatus);
        }

        String sdkState;
        try {
            sdkState = ETPush.getInstance().getSDKState();
        } catch (ETException e) {
            sdkState = e.getMessage();
        }
        Log.v(TAG, sdkState); // Write the current SDK State to the Logs.

        if (!listeners.isEmpty()) { // Tell our listeners that the SDK is ready for use
            for (EtPushListener listener : listeners) {
                if (listener != null) {
                    listener.onReadyForPush(etPush);
                }
            }
            listeners.clear();
        }
    }

    @Override
    public void onETPushConfigurationFailed(ETException etException) {
        Log.e(TAG, etException.getMessage(), etException);
    }

    public interface EtPushListener {
        void onReadyForPush(ETPush etPush);
    }
}
