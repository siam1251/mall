package com.ivanhoecambridge.mall.user;

import android.content.Context;
import android.os.Handler;
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
        void onSessionStarted();
        void onSessionEnded();
    }


    private static Session instance;
    private String userId;
    private Callbacks sessionCallback;
    private AccountManager accountManager;
    private boolean isSessionSynced = false;
    private boolean isInSession = false;


    public static Session getInstance(Context context) {
        if (instance == null) {
            instance = new Session();
            instance.loadInUser(context);
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    private Session() {}

    private void loadInUser(Context context) {
        this.userId = KcpUtility.loadFromCache(context, JanrainRecordManager.KEY_USER_ID, null);
        if (userId.isEmpty()) {
            userId = null;
        }
    }

    public void requestSignOut(Context context, Handler handler) {
       if (accountManager == null) {
           accountManager = new AccountManager(context);
           accountManager.setUiHandler(handler);
       }
        this.isSessionSynced = false;
        accountManager.signOutAndReset();
    }

   public void setSessionCallback(Callbacks sessionCallback) {
        this.sessionCallback = sessionCallback;
   }


    public void refreshUserInSession(Context context) {
        if (isInSession) return;
        loadInUser(context);
        if (userId != null) {
            setSessionSynced(false);
        }
        isInSession = true;
        sessionCallback.onSessionStarted();
    }

    public void endSession(Context context) {
        KcpUtility.removeFromCache(context, JanrainRecordManager.KEY_USER_ID);
        this.userId = null;
        isInSession = false;
        sessionCallback.onSessionEnded();
    }

    public @Nullable String getUserId() {
        return userId;
    }

    public boolean isSessionSynced() {
        return isSessionSynced;
    }

    public void setSessionSynced(boolean isSynced) {
        this.isSessionSynced = isSynced;
    }


    @Override
    public void onActivityStopped() {
        Log.i(TAG, "Launch Intent-Service");
        if (sessionCallback != null) {
            sessionCallback.onSessionStopped();
        }
    }

}
