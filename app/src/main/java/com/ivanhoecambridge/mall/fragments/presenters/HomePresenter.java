package com.ivanhoecambridge.mall.fragments.presenters;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpNavigationRootManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpService;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpSocialFeedManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpUserLikes;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.uiviews.HomeView;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.user.Session;

import java.util.concurrent.atomic.AtomicInteger;

import constants.MallConstants;
import factory.HeaderFactory;

/**
 * Created by petar on 2017-12-07.
 */

public class HomePresenter implements Session.Callbacks{

    private final String TAG = "HomePresenter";
    public final static int NEWS_DEALS = 1;
    public final static int FINGERPRINT = 2;
    public final static int SOCIAL = 3;
    public final static int SOCIAL_FEED_TWITTER = 0;
    public final static int SOCIAL_FEED_IG = 1;

    /**
     * News & Deals, Interests (Categories), Social Feed
     */
    private final int DOWNLOAD_COUNT_DEFAULT = 3;
    /**
     * deals-list, feed, recommended-deals, system-message
     */
    private final int NEWS_MODE_COUNT_DEFAULT = 4;
    /**
     * Deals, Events, Stores, Interests (currently without stores)
     */
    private final int DOWNLOAD_COUNT_USER = 2;

    private HomeView homeView;
    private Session session;

    private KcpNavigationRootManager kcpNavigationRootManager;
    private KcpSocialFeedManager kcpSocialFeedManager;
    private KcpCategoryManager kcpCategoryManager;

    private boolean isUserSignedIn;

    private AtomicInteger downloadCount;
    private AtomicInteger newsModeCount;
    private int newsModeCurrentCount;
    private boolean isCurrentlyDownloading = false;

    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
        session = Session.getInstance(homeView.getContext());
        session.setSessionCallback(this);
    }

    private Handler newsAndDealsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.arg1) {
                case KcpNavigationRootManager.DOWNLOAD_COMPLETE:
                    String mode = (String) inputMessage.obj;
                    homeView.onNewsAndDealsUpdated(false, (String) inputMessage.obj, KcpNavigationRoot.getInstance().getNavigationpage(mode).getKcpContentPageList(true));
                    newsModeCurrentCount = newsModeCount.decrementAndGet();
                    Log.i("NewsCount", "Decrement on complete " + newsModeCurrentCount + " : " + inputMessage.obj);
                    break;
                case KcpNavigationRootManager.DATA_ADDED:
                    homeView.onNewsAndDealsUpdated(true, (String) inputMessage.obj, kcpNavigationRootManager.getKcpContentPageList());
                    Log.i("NewsCount", "Data added");
                    break;
                case KcpNavigationRootManager.DOWNLOAD_USER_CONTENT:
                    String contentType = (String) inputMessage.obj;
                    updateUserContentPages(contentType);
                    Log.i("NewsCount", "User content downloaded");
                    notifyDataDownloaded();
                    break;
                case KcpNavigationRootManager.DOWNLOAD_FAILED:
                    homeView.onDataDownloadFailure(NEWS_DEALS);
                    newsModeCurrentCount = newsModeCount.decrementAndGet();
                    Log.i("NewsCount", "Decrement on failed " + newsModeCurrentCount + " : " + inputMessage.obj);
                    break;
                case KcpNavigationRootManager.DOWNLOAD_MODE_COUNT:
                    homeView.onPreDataDownload();
                    newsModeCount = new AtomicInteger((int) inputMessage.obj);
                    newsModeCurrentCount = newsModeCount.get();
                    Log.i("NewsCount", "Set to " + newsModeCurrentCount);
                    break;
            }
            if (newsModeCurrentCount == 0) {
                Log.i("NewsCount", "N&D DL");
                notifyDataDownloaded();
            }
        }
    };

    private Handler socialFeedHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.arg1) {
                case KcpSocialFeedManager.DOWNLOAD_TWITTER_COMPLETE:
                    homeView.onSocialFeedUpdated(kcpSocialFeedManager, SOCIAL_FEED_TWITTER);
                    break;
                case KcpSocialFeedManager.DOWNLOAD_INSTAGRAM_COMPLETE:
                    homeView.onSocialFeedUpdated(kcpSocialFeedManager, SOCIAL_FEED_IG);
                    break;
                case KcpSocialFeedManager.DOWNLOAD_FAILED:
                    homeView.onDataDownloadFailure(SOCIAL);
                    break;
            }
            Log.i("NewsCount", "Social DL");
            notifyDataDownloaded();
        }
    };

    private Handler fingerPrintHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.arg1) {
                case KcpCategoryManager.DOWNLOAD_COMPLETE:
                    if (isUserSignedIn) {
                        FavouriteManager.getInstance(homeView.getContext()).updateUserFingerprintInterests(KcpUserLikes.getInstance().getUserCategories());
                    }
                    break;
                case KcpCategoryManager.DOWNLOAD_FAILED:
                    homeView.onDataDownloadFailure(FINGERPRINT);
                    break;
            }
            Log.i("NewsCount", "FP DL");
            notifyDataDownloaded();
        }
    };

    private void updateUserContentPages(String contentType) {
        if (contentType.contains(KcpContentTypeFactory.CONTENT_TYPE_DEAL)) {
            FavouriteManager.getInstance(homeView.getContext()).updateUserDeals(KcpUserLikes.getInstance().getUserContentPage(contentType));
        } else if (contentType.contains(KcpContentTypeFactory.CONTENT_TYPE_EVENT)) {
            FavouriteManager.getInstance(homeView.getContext()).updateUserEvents(KcpUserLikes.getInstance().getUserContentPage(contentType));
        }
    }

    public void downloadNewData() {
        if (homeView.getContext() == null) return;

        preDownloadSetup();

        kcpNavigationRootManager.downloadNewsAndDeal();
        kcpCategoryManager.downloadFingerPrintingCategories(isUserSignedIn);
        kcpSocialFeedManager.downloadTwitterTweets(MallConstants.TWITTER_SCREEN_NAME, MallConstants.NUMB_OF_TWEETS, MallConstants.TWITTER_API_KEY, MallConstants.TWITTER_API_SECRET);
        if (isUserSignedIn) {
            kcpNavigationRootManager.downloadContentPagesForUser(String.format(KcpConstants.QUERY_CONTENT_TYPE_DEALS, Constants.ORG));
            kcpNavigationRootManager.downloadContentPagesForUser(String.format(KcpConstants.QUERY_CONTENT_TYPE_EVENT, Constants.ORG));
        }
    }

    public boolean isDownloadInProgress() {
        return isCurrentlyDownloading;
    }

    private void preDownloadSetup() {
        homeView.setProgressIndicator(true);
        if (kcpNavigationRootManager == null) {
            kcpNavigationRootManager = new KcpNavigationRootManager(homeView.getContext(), R.layout.layout_loading_item, HeaderFactory.getHeaders(), newsAndDealsHandler);
        }
        if (kcpCategoryManager == null) {
            kcpCategoryManager = new KcpCategoryManager(homeView.getContext(), R.layout.layout_loading_item, HeaderFactory.getHeaders(), fingerPrintHandler);
        }
        if (kcpSocialFeedManager == null) {
            kcpSocialFeedManager = new KcpSocialFeedManager(homeView.getContext(), socialFeedHandler);
        }

        checkIsUserSignedIn();

        newsModeCount = new AtomicInteger(NEWS_MODE_COUNT_DEFAULT);
        //todo: optimize downloading for user vs core downloads via caching
        downloadCount = new AtomicInteger(isUserSignedIn ? DOWNLOAD_COUNT_DEFAULT + DOWNLOAD_COUNT_USER : DOWNLOAD_COUNT_DEFAULT);
        Log.i("NewsCount - DLC", "Set to " + downloadCount.get());
        isCurrentlyDownloading = true;
    }

    public void refreshAdapterData() {
        String[] adapterCodes = {Constants.EXTERNAL_CODE_RECOMMENDED, Constants.EXTERNAL_CODE_DEAL, Constants.EXTERNAL_CODE_FEED};
        for (String mode : adapterCodes) {
            KcpNavigationPage kcpNavigationPage = KcpNavigationRoot.getInstance().getNavigationpage(mode);
            homeView.onAdapterDataRefresh(mode, kcpNavigationPage.getKcpContentPageList(true));
        }
    }

    private void notifyDataDownloaded() {
        int downloadCountdown = downloadCount.decrementAndGet();
        Log.i("NewsCount - DLC", downloadCountdown + "");
        if (downloadCountdown == 0) {
            homeView.updateProfileData();
            homeView.onAllDataDownloadSuccess();
            homeView.setProgressIndicator(false);
            isCurrentlyDownloading = false;
        }
    }

    private void checkIsUserSignedIn() {
        isUserSignedIn = session.getUserId() != null;
    }

    @Override
    public void onSessionStopped() {
        Log.i(TAG, "Launching POST likes call via Intent-Service");
    }

    @Override
    public void onSessionStarted() {
        Log.i("Session", "Started");
      updateKcpService(KcpManager.recreateKcpService(homeView.getContext(), HeaderFactory.getHeaders()));
    }

    @Override
    public void onSessionEnded() {
        Log.i("Session", "Ended");
        homeView.updateProfileData();
    }

    /**
     * Updates the KCP service end-point with the authorized user bearer-token for each download manager.
     */
    private void updateKcpService(KcpService kcpService) {
        if (kcpNavigationRootManager != null) {
            kcpNavigationRootManager.updateKcpService(kcpService);
        }
        if (kcpCategoryManager != null) {
            kcpCategoryManager.updateKcpService(kcpService);
        }
    }


}
