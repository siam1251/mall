package com.ivanhoecambridge.mall.user;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.service.ServiceFactory;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.account.KcpAccount;
import factory.HeaderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Kay on 2016-09-02.
 */
public class AccountManager {

    private static final String TAG = "AccountManager";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";
    private static final String KEY_CREDENTIALS = "credentials";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "identifier";
    private static final String KEY_PASSWORD = "password";
    private static final String VALUE_DEVICE_CREDENTIAL = "DeviceCredential";

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    private final String RESPONSE_STATUS_COMPLETE = "complete";
    protected UserService mUserService;
    protected Handler mHandler;
    protected Logger logger = null;
    protected Context mContext;
    protected HashMap<String, String> mHeadersMap;

    /**
     *
     * @param context
     * @param headersMap DO NOT include Authorization
     * @param handler
     */

    public AccountManager(Context context, HashMap<String, String> headersMap, Handler handler) {
        mContext = context;
        mHandler = handler;
        mHeadersMap = headersMap;
        logger = new Logger(getClass().getName());
    }

    public UserService getKcpService(){
        ServiceFactory serviceFactory = new ServiceFactory();
        if(mUserService == null) mUserService = serviceFactory.createRetrofitService(mContext, mHeadersMap, UserService.class, KcpConstants.getBaseURL());
        return mUserService;
    }

    public void downloadUserToken() {
        postToCreateToken();
    }

    protected void postToCreateToken(){
        String identifier = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Log.d(TAG, "identifier: " + identifier + " password: " + password);
        final KcpUser kcpUser = new KcpUser(identifier, password);
        Call<Token> createUser = getKcpService().postInterestedStores(KcpConstants.URL_POST_CREATE_USER, kcpUser.kcpUser);
        createUser.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    Call<Token> tokenCall = getKcpService().postInterestedStores(KcpConstants.URL_POST_CREATE_TOKEN, kcpUser.kcpToken.kcpTokenMap);
                    tokenCall.enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(Call<Token> call, Response<Token> response) {
                            if(response.isSuccessful()){
                                String token = response.body().getToken();
                                if(!token.equals("")){
                                    //token received - use this
                                    KcpAccount.getInstance().saveGsonUserToken(mContext, token);
                                    HeaderFactory.constructHeader(); //update the header
                                    handleState(DOWNLOAD_COMPLETE);
                                }
                            } else handleState(DOWNLOAD_FAILED);
                        }

                        @Override
                        public void onFailure(Call<Token> call, Throwable t) {
                            logger.error(t);
                            handleState(DOWNLOAD_FAILED);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                logger.error(t);
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    private void handleState(int state){
        handleState(state, null);
    }

    private void handleState(int state, @Nullable String mode){
        if(mHandler == null) return;
        Message message = new Message();
        message.arg1 = state;
        message.obj = mode;
        switch (state){
            case DOWNLOAD_STARTED:
                break;
            case DOWNLOAD_FAILED:
                break;
            case DOWNLOAD_COMPLETE:
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

    public interface UserService {
        @POST
        Call<Token> postInterestedStores(
                @Url String url,
                @Body HashMap userAccount);
    }

    public class Token {
        @SerializedName("token")
        private String token;

        public String getToken(){
            if(token == null) return "";
            return token;
        }
    }

    public class KcpUser {
        public HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> kcpUser = new HashMap<>();
        public KcpToken kcpToken;
        public KcpUser(String identifier, String password){
            HashMap<String, ArrayList<HashMap<String, String>>> credentialsMap = new HashMap<>();
            ArrayList<HashMap<String, String>> kcpTokens = new ArrayList<>();
            kcpToken = new KcpToken(identifier, password);
            kcpTokens.add(kcpToken.kcpTokenMap);
            credentialsMap.put(KEY_CREDENTIALS, kcpTokens);
            kcpUser.put(KEY_USER, credentialsMap);
        }
    }

    public class KcpToken {
        public HashMap<String, String> kcpTokenMap = new HashMap<>();
        public KcpToken(String identifier, String password){
            kcpTokenMap.put(KEY_TYPE, VALUE_DEVICE_CREDENTIAL);
            kcpTokenMap.put(KEY_ID, identifier);
            kcpTokenMap.put(KEY_PASSWORD, password);
        }
    }
}