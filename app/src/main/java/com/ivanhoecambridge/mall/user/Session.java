package com.ivanhoecambridge.mall.user;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.signup.JanrainRecordManager;

/**
 * Created by petar on 2017-12-14.
 */

public class Session implements ActivityListener{

    private final String TAG = getClass().getSimpleName();

    public interface Callbacks {
        void onSessionStopped();
    }


    private static Session instance;
    private String userId;
    private Callbacks sessionCallback;
    private AccountManager accountManager;


    public static Session getInstance(Context context) {
        if (instance == null) {
            instance = new Session();
            instance.loadInUser(context);

        }
        return instance;
    }

    private Session() {}

    private void loadInUser(Context context) {
        this.userId = KcpUtility.loadFromCache(context, JanrainRecordManager.KEY_USER_ID, null);
    }

    public void requestSignOut(Context context) {
       if (accountManager == null) {
           accountManager = new AccountManager(context);
       }
        accountManager.signOutAndReset();
    }

   public void setSessionCallback(Callbacks sessionCallback) {
        this.sessionCallback = sessionCallback;
   }


    public void refreshUserInSession(Context context) {
        loadInUser(context);
    }

    public void endSession(Context context) {
        KcpUtility.removeFromCache(context, JanrainRecordManager.KEY_USER_ID);
        this.userId = null;
    }

    public @Nullable String getUserId() {
        return userId;
    }

    @Override
    public void onActivityStopped() {
        Log.i(TAG, "Launch Intent-Service");
        if (sessionCallback != null) {
            sessionCallback.onSessionStopped();
        }
    }

}
