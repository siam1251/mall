package com.ivanhoecambridge.mall.activities;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.crashlytics.android.core.CrashlyticsListener;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.account.KcpAccount;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.constants.Janrain;
import com.ivanhoecambridge.mall.rating.AppRatingManager;
import com.janrain.android.Jump;
import com.salesforce.marketingcloud.InitializationStatus;
import com.salesforce.marketingcloud.MarketingCloudConfig;
import com.salesforce.marketingcloud.MarketingCloudSdk;
import com.salesforce.marketingcloud.notifications.NotificationMessage;
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
public class KcpApplication extends MultiDexApplication implements MarketingCloudSdk.InitializationListener, MarketingCloudSdk.WhenReadyListener {

    private static final String                                    TAG                 = "KcpApplication";
    public static final boolean                                    ANALYTICS_ENABLED   = true;
    public static final boolean                                    CLOUD_PAGES_ENABLED = false;
    public static final boolean                                    WAMA_ENABLED        = true;
    public static final boolean                                    PROXIMITY_ENABLED   = false;
    public static final boolean                                    LOCATION_ENABLED    = true;
    private static final LinkedHashSet<MarketingCloudListener> listeners = new LinkedHashSet<>();

    @Override
    protected void attachBaseContext(Context newBase) { //http://stackoverflow.com/questions/37045787/java-lang-noclassdeffounderror-retrofit2-utils-in-android/37178366
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        AppRatingManager.init(getApplicationContext());
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(MallConstants.TWITTER_API_KEY, MallConstants.TWITTER_API_SECRET);
        KcpConstants.setBaseURL(Constants.IS_APP_IN_PRODUCTION);
        KcpAccount.getInstance().initialize(getApplicationContext());
        Constants.setLocale(getApplicationContext(), R.string.locale);
        HeaderFactory.changeCatalog(HeaderFactory.HEADER_VALUE_DATAHUB_CATALOG);

        if(BuildConfig.REPORT_CRASH) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
            final CrashlyticsCore core = new CrashlyticsCore.Builder()
                    .listener(new CrashlyticsListener() {
                        @Override
                        public void crashlyticsDidDetectCrashDuringPreviousExecution() {
                            AppRatingManager.trackCrashReport(getApplicationContext());
                        }
                    }).build();
            Fabric.with(this, new TwitterCore(authConfig), new Crashlytics.Builder().core(core).build(), new TweetUi());
        } else {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
            Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        }

        KcpUtility.cacheToPreferences(this, Constants.PREF_KEY_WELCOME_MSG_DID_APPEAR, false); //resetting the welcome message flag as false to indicate it has never shown for this app launch
        if(BuildConfig.PUSH){

            if (KcpUtility.isVersionAtLeast(26)) {
                createNotificationChannel();
            }

            try {
                MarketingCloudSdk.init(this, MarketingCloudConfig.builder()
                        .setApplicationId(MallConstants.ET_APP_ID)
                        .setAccessToken(MallConstants.ET_ACCESS_TOKEN)
                        .setGcmSenderId(MallConstants.GCM_SENDER_ID)
                        .setOpenDirectRecipient(MainActivity.class)
                        .setAnalyticsEnabled(ANALYTICS_ENABLED)
                        .setGeofencingEnabled(LOCATION_ENABLED)
                        .setPiAnalyticsEnabled(WAMA_ENABLED)
                        .setCloudPagesEnabled(CLOUD_PAGES_ENABLED)
                        .setProximityEnabled(PROXIMITY_ENABLED)
                        .setNotificationSmallIconResId(R.drawable.icn_noti)
                        .setNotificationChannelIdProvider(new com.salesforce.marketingcloud.notifications.NotificationManager.NotificationChannelIdProvider() {
                            @Override
                            public String getNotificationChannelId(@NonNull Context context, @NonNull NotificationMessage notificationMessage) {
                                return Constants.CHANNEL_NAME;
                            }
                        })
                        .build(), this);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        //disable Fabric crashlytics for staging
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build(), new Crashlytics());
        Jump.init(getApplicationContext(), Janrain.configure(Janrain.AppType.PROD));

    }

    @TargetApi(26)
    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(BuildConfig.MARKETING_CLOUD_ID, Constants.NOTIFICATION_GROUP_NAME));

        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(getString(R.string.notification_description));
        channel.setGroup(BuildConfig.MARKETING_CLOUD_ID);
    }

    @Override
    public void complete(InitializationStatus status) {
        if (status.isUsable()) {
            if (status.status() == InitializationStatus.Status.COMPLETED_WITH_DEGRADED_FUNCTIONALITY) {
                if (status.locationsError()) {
                    GoogleApiAvailability.getInstance()
                            .showErrorNotification(this, status.locationPlayServicesStatus());
                }
            }
        } else {
            Log.e(TAG, "Failed to initialize MarketingCloudSDK: " + status.status().toString());
            Log.e(TAG, status.unrecoverableException().getMessage());
        }

    }

    @Override
    public void ready(MarketingCloudSdk marketingCloud) {
        if (marketingCloud != null) {
            marketingCloud.getAnalyticsManager().trackPageView("data://ReadyAimFireCompleted", "Marketing Cloud SDK Initialization Complete");
            notifyListeners();
        }
    }


    public static void registerMarketingCloudListener(MarketingCloudListener listener) {
       listeners.add(listener);
       try {
           if (MarketingCloudSdk.getInstance() != null) {
               notifyListeners();
           }
       } catch (IllegalStateException e) {
           Log.e(TAG, "Marketing not initialized");
       }
    }

    private static void notifyListeners() {
        if (!listeners.isEmpty()) {
            for (MarketingCloudListener marketingCloudListener : listeners) {
                marketingCloudListener.onReadyForPush(MarketingCloudSdk.getInstance());
            }
        }
    }


    public interface MarketingCloudListener {
        void onReadyForPush(MarketingCloudSdk marketingCloudSdk);
    }

}
