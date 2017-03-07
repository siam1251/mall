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

    public DeepLinkManager(Context context) {
        context = mContext;
    }

    public DeepLinkManager handleDeepLink(Intent intent, DeepLinkParseListener deepLinkParseListener){
        try {
            if(intent == null) return this;
            this.deepLinkParseListener = deepLinkParseListener;
            //https://developer.android.com/training/app-indexing/deep-linking.html
            String action = intent.getAction();
            Uri data = intent.getData();

            if(intent.getExtras() == null) return this;
            String deepLinkURL = intent.getExtras().getString(KEY_OPEN_DIRECT_URL);
            if(deepLinkURL != null) {
                if(deepLinkURL.contains(URI_TAB)) {
                    handleTab(TextUtils.split(deepLinkURL, URI_TAB)[1]);
                }
            }
            return this;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return this;
    }

    private void handleTab(String deepLinkDestination){
        if(deepLinkParseListener != null) deepLinkParseListener.onDeepLinkParsed(deepLinkDestination);
    }

    private void handleParking(){

    }

    private DeepLinkParseListener deepLinkParseListener;
    public interface DeepLinkParseListener {
        public void onDeepLinkParsed(String externalCode);
    }

}
