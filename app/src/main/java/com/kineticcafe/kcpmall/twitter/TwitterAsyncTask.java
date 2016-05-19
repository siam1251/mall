package com.kineticcafe.kcpmall.twitter;

import android.os.AsyncTask;

import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;

import java.util.ArrayList;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterTweet>> {

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
        ArrayList<TwitterTweet> twitterTweets = null;
        if (params.length > 0) {
            TwitterAPI twitterAPI = new TwitterAPI(mTwitterAPIKey, mTwitterAPISecret, mNumbOfTweets);
            twitterTweets = twitterAPI.getTwitterTweets(params[0].toString());
        }
        return twitterTweets;
    }

    @Override
    protected void onPostExecute(ArrayList<TwitterTweet> twitterTweets) {
        if(mTwitterFeedDownloadCompleteListener != null) mTwitterFeedDownloadCompleteListener.onTwitterFeedDownloadComplete(twitterTweets);
    }
}
