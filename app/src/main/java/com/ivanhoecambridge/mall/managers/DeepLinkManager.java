package com.ivanhoecambridge.mall.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Kay on 2017-02-27.
 */

public class DeepLinkManager {

    protected static final String TAG = "DeepLinkManager";
    private static Context mContext;
    private final String KEY_OPEN_DIRECT_URL = "_od";
    private final String URI_TAB = "tab/";
    public static final String URI_EXTERNAL_CODE_PROFILE = "profile";
    public static final String URI_EXTERNAL_CODE_MAP = "map";
    public static final String URI_EXTERNAL_CODE_HOME_DEAL = "home_dealExternalCode";
    public static final String URI_EXTERNAL_CODE_HOME_EVENT = "home_eventExternalCode";
    public static final String URI_EXTERNAL_CODE_MALL_DIRECTORY_PLACE = "malldirectory_placesExternalCode";

    public DeepLinkManager(Context context) {
        context = mContext;
    }

    public DeepLinkManager handleDeepLink(Intent intent, DeepLinkParseListener deepLinkParseListener){
        try {
            if(intent == null) return this;
            this.deepLinkParseListener = deepLinkParseListener;
            //https://developer.android.com/training/app-indexing/deep-linking.html - Android Deep Link Documentation
            //https://halogenmobile.atlassian.net/wiki/display/IC/Deep+Linking - Confluence Deep Link Documentation

            //intent delivered from Exact Target Push
            if(intent.getExtras() == null && intent.getExtras().getString(KEY_OPEN_DIRECT_URL) != null) {
                String deepLinkURL = intent.getExtras().getString(KEY_OPEN_DIRECT_URL);
                if(deepLinkURL.contains(URI_TAB)) {
                    if(deepLinkParseListener != null) deepLinkParseListener.openTab(TextUtils.split(deepLinkURL, URI_TAB));
//                    handleTab(TextUtils.split(deepLinkURL, URI_TAB)[1]);
                }
                // intent delivered from URI
            } else {
                Uri data = intent.getData();
                if(data.toString().contains(URI_TAB)){
                    if(deepLinkParseListener != null) deepLinkParseListener.openTab(TextUtils.split(data.toString(), URI_TAB));
//                    handleTab(TextUtils.split(data.toString(), URI_TAB)[1]);
                }


            }


            return this;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return this;
    }

    /*private void handleTab(String deepLinkDestination){
        if(deepLinkParseListener != null) deepLinkParseListener.openTab(deepLinkDestination);
    }

    private void handleTab(String[] externalData){
        if(externalData[0].equals(URI_EXTERNAL_CODE_PROFILE)) {

        }

    }*/

    private void handleParking(){

    }

    private DeepLinkParseListener deepLinkParseListener;
    public interface DeepLinkParseListener {
        public void openTab(String[] externalCode);
        public void openTab(String externalCode);
        public void openDetailPage(String externalCode);
    }

}
