package com.kineticcafe.kcpmall.activities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.kineticcafe.kcpmall.analytics.FirebaseTracking;
import com.crashlytics.android.Crashlytics;
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
//        Fabric.with(this, new Crashlytics()); //ENABLE FOR PRODUCTION
//        Fabric.with(this, new TwitterCore(authConfig), new Crashlytics(), new TweetUi());
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());

    }
}
