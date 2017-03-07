package com.ivanhoecambridge.mall.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpContentManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpPlaceManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.activities.MoviesActivity;
import com.ivanhoecambridge.mall.activities.ParkingActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.HomeFragment;

import factory.HeaderFactory;

/**
 * Created by Kay on 2017-02-27.
 */

public class DeepLinkManager {

    protected static final String TAG = "DeepLinkManager";
    private static MainActivity mMainActivity;
    private static Context mContext;
    private final String KEY_OPEN_DIRECT_URL = "_od";
    private final String URI_TAB = "tab";
    private final String URI_PARKING = "parking";
    public static final String URI_EXTERNAL_CODE_PROFILE = "profile";
    public static final String URI_EXTERNAL_CODE_MAP = "map";
    public static final String URI_EXTERNAL_CODE_HOME_DEAL = "home_dealExternalCode";
    public static final String URI_EXTERNAL_CODE_HOME_EVENT = "home_eventExternalCode";
    public static final String URI_EXTERNAL_CODE_MALL_DIRECTORY_PLACE = "malldirectory_placesExternalCode";
    public static final String URI_EXTERNAL_CODE_MALL_INFO_CINEMA = "mallinfo_cinema";

    public DeepLinkManager(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mContext = mainActivity.getApplicationContext();
    }

    public DeepLinkManager handleDeepLink(Intent intent){
        try {
            if(intent == null) return this;
            //https://developer.android.com/training/app-indexing/deep-linking.html - Android Deep Link Documentation
            //https://halogenmobile.atlassian.net/wiki/display/IC/Deep+Linking - Confluence Deep Link Documentation
            String scheme = mMainActivity.getResources().getString(R.string.applicationId) + "://";
            //intent delivered from Exact Target Push
            String deepLinkURL = "";
            if(intent.getExtras() == null && intent.getExtras().getString(KEY_OPEN_DIRECT_URL) != null) {
                deepLinkURL = intent.getExtras().getString(KEY_OPEN_DIRECT_URL);
            } else { // intent delivered from URI
                Uri data = intent.getData();
                deepLinkURL = data.toString();
            }
            parseIntentData(TextUtils.split(deepLinkURL, scheme)[1]);
            return this;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return this;
    }

    /**
     *
     * @param externalData is the string comes after the scheme ("com.ivanhoecambridge.metropolis://")
     */
    public void parseIntentData(String externalData) {

        String[] dataSplittedBySlash = TextUtils.split(externalData, "/");
        String tab = dataSplittedBySlash[0];
        String destinationPage = dataSplittedBySlash.length > 1 ? dataSplittedBySlash[1] : "";

        //PARKING
        if(tab.startsWith(URI_PARKING)) {
            final Intent intent = new Intent (mMainActivity, ParkingActivity.class);
            mMainActivity.startActivityForResult(intent, Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
            return;
        }

        //OTHER TABS
        if(tab.equals(URI_TAB)) {
            if(destinationPage.equals(URI_EXTERNAL_CODE_PROFILE)) {
                mMainActivity.openLeftDrawerLayout();
            } else if(destinationPage.equals(URI_EXTERNAL_CODE_MAP) || destinationPage.startsWith(URI_EXTERNAL_CODE_MAP)) {
                mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_MAP);
            } else if(destinationPage.equals(URI_EXTERNAL_CODE_HOME_DEAL) || destinationPage.equals(URI_EXTERNAL_CODE_HOME_EVENT)){
                mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_HOME);

                if(destinationPage.equals(URI_EXTERNAL_CODE_HOME_EVENT)) HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_NEWS);
                else if(destinationPage.equals(URI_EXTERNAL_CODE_HOME_DEAL)) HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_DEALS);

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
            } else if (destinationPage.equals(URI_EXTERNAL_CODE_MALL_DIRECTORY_PLACE)){

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

            } else if (destinationPage.equals(URI_EXTERNAL_CODE_MALL_INFO_CINEMA)){
                Intent intent = new Intent(mMainActivity, MoviesActivity.class);
                intent.putExtra(Constants.ARG_TRANSITION_ENABLED, false);
                mMainActivity.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
            }
        }
    }

    private DeepLinkParseListener deepLinkParseListener;
    public interface DeepLinkParseListener {
        public void openTab(String[] externalCode);
        public void openTab(String externalCode);
        public void openDetailPage(String externalCode);
    }

}
