package com.kineticcafe.kcpmall.activities;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.kineticcafe.kcpandroidsdk.constant.KcpConstants;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.constants.Constants;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kay on 2016-04-28.
 */
public class KcpApplication extends MultiDexApplication {

    @Override
//    http://stackoverflow.com/questions/37045787/java-lang-noclassdeffounderror-retrofit2-utils-in-android/37178366
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(Constants.TWITTER_API_KEY, Constants.TWITTER_API_SECRET);
        KcpConstants.setBaseURL(Constants.IS_APP_IN_PRODUCTION);
        if(Constants.IS_APP_IN_PRODUCTION) {
            Fabric.with(this, new TwitterCore(authConfig), new Crashlytics(), new TweetUi());
//          Fabric.with(this, new Crashlytics()); //ENABLE FOR PRODUCTION
        } else {
            Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        }

        KcpUtility.cacheToPreferences(this, Constants.PREF_KEY_WELCOME_MSG_DID_APPEAR, false); //resetting the welcome message flag as false to indicate it has never shown for this app launch


    }
}
