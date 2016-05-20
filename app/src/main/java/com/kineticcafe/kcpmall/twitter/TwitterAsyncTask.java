package com.kineticcafe.kcpmall.twitter;

import android.os.AsyncTask;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;

import java.util.ArrayList;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterTweet>> {
    protected final Logger logger = new Logger(getClass().getName());
    private int mNumbOfTweets;
    private String mTwitterAPIKey;
    private String mTwitterAPISecret;

    private OnTwitterFeedDownloadCompleteListener mTwitterFeedDownloadCompleteListener;
    public interface OnTwitterFeedDownloadCompleteListener{
        void onTwitterFeedDownloadComplete(ArrayList<TwitterTweet> twitterTweets);
    }

    public TwitterAsyncTask(int numbOfTweets, String twitterAPIKey, String twitterAPISecret, OnTwitterFeedDownloadCompleteListener twitterFeedDownloadCompleteListener) {
        mTwitterFeedDownloadCompleteListener = twitterFeedDownloadCompleteListener;
        mNumbOfTweets = numbOfTweets;
        mTwitterAPIKey = twitterAPIKey;
        mTwitterAPISecret = twitterAPISecret;
    }

    @Override
    protected ArrayList<TwitterTweet> doInBackground(Object... params) {
        logger.debug("TWITTER DOINBG");
        ArrayList<TwitterTweet> twitterTweets = null;
        try {
            if (params.length > 0) {
                TwitterAPI twitterAPI = new TwitterAPI(mTwitterAPIKey, mTwitterAPISecret, mNumbOfTweets);
                twitterTweets = twitterAPI.getTwitterTweets(params[0].toString());
            }
            return twitterTweets;
        } catch (Exception e) {
            logger.debug("TIWTTER ERROR");
            logger.error(e);
        }
        return twitterTweets;

    }

    @Override
    protected void onPostExecute(ArrayList<TwitterTweet> twitterTweets) {
        logger.debug("TIWTTER ONPOSTEXECUTE");
        if(mTwitterFeedDownloadCompleteListener != null) {
            logger.debug("TIWTTER mTwitterFeedDownloadCompleteListener FOUND");
            mTwitterFeedDownloadCompleteListener.onTwitterFeedDownloadComplete(twitterTweets);
        } else logger.debug("TIWTTER mTwitterFeedDownloadCompleteListener NOT FOUND!!!!!!!!");

    }
}
