package com.ivanhoecambridge.mall.account;

import android.content.Context;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.user.AccountManager;

/**
 * Created by Kay on 2016-11-02.
 */

public class KcpAccount {

    private String mUserToken = "";
    private static KcpAccount sKcpAccount;
    public final static String PREFS_KEY_USER_TOKEN = 	"prefs_key_user_token";
    private static final String TOKEN_PREFIX_BEARER    = "Bearer ";

    public KcpAccount() {}

    public static KcpAccount getInstance(){
        if(sKcpAccount == null) sKcpAccount = new KcpAccount();
        return sKcpAccount;
    }

    public boolean isTokenAvailable(){
        if(mUserToken.equals("")) return false;
        else return true;
    }

    public String getUserToken(){
        return mUserToken;
    }

    public void initialize(Context context){
        mUserToken = loadUserToken(context);
    }

    public void saveGsonUserToken(Context context, String token){
        KcpUtility.cacheToPreferences(context, PREFS_KEY_USER_TOKEN, token);
        mUserToken = token;
    }

    public String loadUserToken(Context context){
        return KcpUtility.loadFromCache(context, PREFS_KEY_USER_TOKEN, "");
    }

    public String getUserTokenWithBearer(){
        if(!mUserToken.equals("")){
            return TOKEN_PREFIX_BEARER + mUserToken;
        }
        return mUserToken;
    }

}
