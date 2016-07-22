package com.kineticcafe.kcpmall.activities;

import android.app.Application;

import com.kineticcafe.kcpmall.analytics.FirebaseTracking;
import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kay on 2016-04-28.
 */
public class KcpApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(Constants.TWITTER_API_KEY, Constants.TWITTER_API_SECRET);
//        Fabric.with(this, new Crashlytics()); //ENABLE FOR PRODUCTION
//        Fabric.with(this, new TwitterCore(authConfig), new Crashlytics(), new TweetUi());
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());

    }
}
