package com.ivanhoecambridge.mall.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpContentManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpPlaceManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContent;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.HomeFragment;

import java.util.ArrayList;

import factory.HeaderFactory;

import static com.ivanhoecambridge.mall.activities.MainActivity.VIEWPAGER_PAGE_DIRECTORY;

/**
 * Created by Kay on 2017-02-27.
 */

public class DeepLinkManager {

    protected static final String TAG = "DeepLinkManager";
    private static MainActivity mMainActivity;
    private static Context mContext;
    private final String KEY_OPEN_DIRECT_URL = "_od";
    private final String URI_TAB = "tab";
    public static final String URI_EXTERNAL_CODE_PROFILE = "profile";
    public static final String URI_EXTERNAL_CODE_MAP = "map";
    public static final String URI_EXTERNAL_CODE_HOME_DEAL = "home_dealExternalCode";
    public static final String URI_EXTERNAL_CODE_HOME_EVENT = "home_eventExternalCode";
    public static final String URI_EXTERNAL_CODE_MALL_DIRECTORY_PLACE = "malldirectory_placesExternalCode";

    public DeepLinkManager(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mContext = mainActivity.getApplicationContext();
    }

    public DeepLinkManager handleDeepLink(Intent intent, DeepLinkParseListener deepLinkParseListener){
        try {
            if(intent == null) return this;
            this.deepLinkParseListener = deepLinkParseListener;
            //https://developer.android.com/training/app-indexing/deep-linking.html - Android Deep Link Documentation
            //https://halogenmobile.atlassian.net/wiki/display/IC/Deep+Linking - Confluence Deep Link Documentation


            String scheme = mMainActivity.getResources().getString(R.string.applicationId) + "://";
            //intent delivered from Exact Target Push
            if(intent.getExtras() == null && intent.getExtras().getString(KEY_OPEN_DIRECT_URL) != null) {
                String deepLinkURL = intent.getExtras().getString(KEY_OPEN_DIRECT_URL);
                if(deepLinkURL.contains(URI_TAB)) {
                    parseIntentData(TextUtils.split(deepLinkURL, scheme)[1]);
                }
            // intent delivered from URI
            } else {
                Uri data = intent.getData();
                if(data.toString().contains(URI_TAB)){
//                    if(deepLinkParseListener != null) deepLinkParseListener.openTab(TextUtils.split(data.toString(), URI_TAB));
                    parseIntentData(TextUtils.split(data.toString(), scheme)[1]);
                }

            }


            return this;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return this;
    }


    public void parseIntentData(String externalData) {

        String[] dataSplittedBySlash = TextUtils.split(externalData, "/");

        if(dataSplittedBySlash[0].equals(URI_TAB)) {
            if(dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_PROFILE)) {
                mMainActivity.openLeftDrawerLayout();
            } else if(dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_MAP)) {
                mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_MAP);
            } else if(dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_HOME_DEAL) || dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_HOME_EVENT)){
                mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_HOME);

                if(dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_HOME_EVENT)) HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_NEWS);
                else if(dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_HOME_DEAL)) HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_DEALS);

                //TODO: move tab to deal/event first
                String externalCode = dataSplittedBySlash[2];
                int id = Integer.valueOf(externalCode);

                ProgressBarWhileDownloading.showProgressDialog(mMainActivity, R.layout.layout_loading_item, true);
                KcpContentManager kcpContentManager = new KcpContentManager(mContext, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        ProgressBarWhileDownloading.showProgressDialog(mMainActivity, R.layout.layout_loading_item, false);
                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                if(NetworkManager.isConnected(mMainActivity)) return;
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                KcpContentPage kcpContentPage = (KcpContentPage) inputMessage.obj;

                                Intent intent = new Intent(mContext, DetailActivity.class);
                                intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                                mMainActivity.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);

                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });

                kcpContentManager.downloadContents(id);
            } else if (dataSplittedBySlash[1].equals(URI_EXTERNAL_CODE_MALL_DIRECTORY_PLACE)){

                mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_DIRECTORY);
                String externalCode = dataSplittedBySlash[2];
                final int id = Integer.valueOf(externalCode);

                ProgressBarWhileDownloading.showProgressDialog(mMainActivity, R.layout.layout_loading_item, true);
                KcpPlaceManager kcpPlaceManager = new KcpPlaceManager(mMainActivity, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        ProgressBarWhileDownloading.showProgressDialog(mMainActivity, R.layout.layout_loading_item, false);
                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                if(NetworkManager.isConnected(mMainActivity)) return;
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
                                KcpPlaces kcpPlace = kcpPlacesRoot.getPlaceById(id);

                                KcpContentPage kcpContentPage = new KcpContentPage();
                                kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);

                                Intent intent = new Intent(mContext, DetailActivity.class);
                                intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);
                                mMainActivity.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);

                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                kcpPlaceManager.downloadPlace(id);

            }
        }
    }


    private void handleParking(){

    }

    private DeepLinkParseListener deepLinkParseListener;
    public interface DeepLinkParseListener {
        public void openTab(String[] externalCode);
        public void openTab(String externalCode);
        public void openDetailPage(String externalCode);
    }

}
