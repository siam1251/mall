package com.ivanhoecambridge.mall.account;

import android.content.Context;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.constants.Constants;

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

    /**
     * Saves the token used to identify the user to the cache and updates
     * authorization headers in <em>HeaderFactory</em> all subsequent calls to
     * <em>HeaderFactory.getHeaders()</em> will return the updated token.
     * @param context Context object.
     * @param token Authorization token.
     */
    public void saveUserTokenWithRefresh(Context context, String token) {
        if (!token.contains(TOKEN_PREFIX_BEARER)) {
            mUserToken = TOKEN_PREFIX_BEARER + token;
        }
        Constants.updateHeadersAuthorizationToken(mUserToken);
        KcpUtility.cacheToPreferences(context, PREFS_KEY_USER_TOKEN, token);
    }

    public String loadUserToken(Context context){
        return KcpUtility.loadFromCache(context, PREFS_KEY_USER_TOKEN, "");
    }

    public String getUserTokenWithBearer(){
        if(!mUserToken.isEmpty() && !mUserToken.contains(TOKEN_PREFIX_BEARER)){
            return TOKEN_PREFIX_BEARER + mUserToken;
        }
        return mUserToken;
    }

}
