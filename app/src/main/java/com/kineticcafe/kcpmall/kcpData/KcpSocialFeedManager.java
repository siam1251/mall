package com.kineticcafe.kcpmall.kcpData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpandroidsdk.utils.Utility;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.instagram.InstagramService;
import com.kineticcafe.kcpmall.instagram.model.InstagramFeed;
import com.kineticcafe.kcpmall.instagram.model.Media;
import com.kineticcafe.kcpmall.instagram.model.Recent;
import com.kineticcafe.kcpmall.twitter.TwitterAsyncTask;
import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kay on 2016-05-31.
 */
public class KcpSocialFeedManager {

    private Context mContext;
    protected Logger logger = null;
    private Handler mHandler;
    private ArrayList<TwitterTweet> mTwitterTweets;
    private Recent mRecent;

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_TWITTER_COMPLETE = 2;
    public static final int DOWNLOAD_INSTAGRAM_COMPLETE = 3;

    public KcpSocialFeedManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        logger = new Logger(getClass().getName());

    }

    public void downloadTwitterTweets() {
        try {
            if (mContext != null && Utility.isNetworkAvailable(mContext)) {
                new TwitterAsyncTask(
                        Constants.NUMB_OF_TWEETS,
                        Constants.TWITTER_API_KEY,
                        Constants.TWITTER_API_SECRET,
                        new TwitterAsyncTask.OnTwitterFeedDownloadCompleteListener() {
                            @Override
                            public void onTwitterFeedDownloadComplete(ArrayList<TwitterTweet> twitterTweets) {
                                if(twitterTweets == null) {
                                    handleState(DOWNLOAD_FAILED);
                                } else {
                                    mTwitterTweets = twitterTweets;
                                    handleState(DOWNLOAD_TWITTER_COMPLETE);
                                }
                            }
                        }).execute(Constants.TWITTER_SCREEN_NAME, 5, this);
            }
        } catch (Exception e) {
            logger.error(e);
            handleState(DOWNLOAD_FAILED);
        }
    }

    public void downloadInstagram() {
        try {
            if (mContext != null && Utility.isNetworkAvailable(mContext)) {
                InstagramService instagramService = ServiceFactory.createRetrofitService(mContext, InstagramService.class, Constants.INSTAGRAM_BASE_URL);;
                Call<Recent> recent = instagramService.getRecentWithAccessToken(Constants.INSTAGRAM_USER_ID, Constants.INSTAGRAM_ACCESS_TOKEN, Constants.NUMB_OF_INSTA, null, null, null, null);
//                Call<Recent> recent = instagramService.getRecentWithClientId(Constants.INSTAGRAM_USER_ID, Constants.INSTAGRAM_CLIENT_ID, Constants.NUMB_OF_INSTA, null, null, null, null);
                recent.enqueue(new Callback<Recent>() {
                    @Override
                    public void onResponse(Call<Recent> call, Response<Recent> response) {
                        if (response.isSuccessful()) {
                            mRecent = response.body();
                            handleState(DOWNLOAD_INSTAGRAM_COMPLETE);
                        } else {
                            logger.debug("instagram download unsuccessful");
                        }
                    }

                    @Override
                    public void onFailure(Call<Recent> call, Throwable t) {
                        logger.error(t);
                        handleState(DOWNLOAD_FAILED);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(e);
            handleState(DOWNLOAD_FAILED);
        }
    }


    public ArrayList<TwitterTweet> getTwitterTweets(){
        if(mTwitterTweets == null) return new ArrayList<>();
        return mTwitterTweets;
    }

    public Recent getRecent(){
        return mRecent;
    }

    private void handleState(int state){
        handleState(state, null);
    }
    private void handleState(int state, @Nullable String mode){
        if(mHandler == null) return;
        Message message = new Message();
        message.arg1 = state;
        message.obj = mode;
        switch (state){
            case DOWNLOAD_FAILED:
                break;
            case DOWNLOAD_TWITTER_COMPLETE:
                break;
            case DOWNLOAD_INSTAGRAM_COMPLETE:
                break;
        }
        mHandler.sendMessage(message);
    }



}
