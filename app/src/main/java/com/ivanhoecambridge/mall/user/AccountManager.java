package com.ivanhoecambridge.mall.user;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.google.gson.annotations.SerializedName;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.service.ServiceFactory;
import com.ivanhoecambridge.mall.account.KcpAccount;
import com.ivanhoecambridge.mall.interfaces.CompletionListener;
import com.ivanhoecambridge.mall.managers.ETManager;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.managers.GiftCardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import factory.HeaderFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    private static final String KEY_SECRET = "secret";
    private static final String KEY_PASSWORD = "password";
    private static final String VALUE_DEVICE_CREDENTIAL = "DeviceCredential";
    private static final String VALUE_JANRAIN_CREDENTIAL = "JanrainCredential";

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

    public interface KcpAccountMergeListener {
        void onAccountMergeSuccess();
        void onAccountMergeFailed(int errorCode, String error);
    }

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

    public AccountManager(Context context) {
        mContext = context;
        mHeadersMap = HeaderFactory.getHeaders();
        logger = new Logger(getClass().getName());
    }

    public void setUiHandler(Handler handler) {
        if (mHandler == null) {
            this.mHandler = handler;
        }
    }

    public UserService getKcpService(){
        if(mUserService == null) mUserService = ServiceFactory.createRetrofitService(mContext, mHeadersMap, UserService.class, KcpConstants.getBaseURL());
        return mUserService;
    }

    public void downloadUserToken() {
        postToCreateToken();

    }

    protected void postToCreateToken(){
        final String identifier = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Log.d(TAG, "identifier: " + identifier + " password: " + password);

        try {
            ETPush.getInstance().setSubscriberKey(identifier);
        } catch (ETException e) {
            e.printStackTrace();
        }

        final KcpUser kcpUser = new KcpUser(identifier, password);
        Call<Token> createUser = getKcpService().postToUserService(KcpConstants.URL_POST_CREATE_USER, kcpUser.kcpUser);
        createUser.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){

                    Call<Token> tokenCall = getKcpService().postToUserService(KcpConstants.URL_POST_CREATE_TOKEN, kcpUser.kcpToken.kcpTokenMap);
                    tokenCall.enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(Call<Token> call, Response<Token> response) {
                            if(response.isSuccessful()){
                                String token = response.body().getToken();
                                if(!token.equals("")){
                                    Log.i(TAG, "LOCAL TOKEN: " + token);
                                    updateResponseBearerToken(token);
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

    /**
     * Posts a request to KCP with a Janrain credential payload, if successful the response will update the user bearer token
     * and the Janrain user will be merged with a valid KCP user. If the user is syncing for the first time, this call will fail with
     * a 422 and will cause a sync call {@link #syncJanrainUserToKcp(HashMap, KcpAccountMergeListener)} to run first and then re-execute this call.
     * @param identifier Janrain UUID
     * @param mergeListener Merge callback listener
     */
    public void updateUserTokenWithJanrainId(final String identifier, final KcpAccountMergeListener mergeListener) {
        final HashMap<String, String> janrainPayload = createJanrainPayload(identifier);
        Call<Token> requestNewBearerToken = getKcpService().postToUserService(KcpConstants.URL_POST_CREATE_TOKEN, janrainPayload);
        requestNewBearerToken.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Sign In Token:" + response.body().getToken());
                    updateResponseBearerToken(response.body().getToken());
                    updateGiftCards(identifier);
                    updateETSubscriberKey(identifier);
                    mergeListener.onAccountMergeSuccess();
                } else {
                    if (response.code() == 422) {
                        syncJanrainUserToKcp(janrainPayload, mergeListener);
                    } else {
                        mergeListener.onAccountMergeFailed(-1, response.errorBody().toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                logger.error(t);
                mergeListener.onAccountMergeFailed(-1, t.getMessage());
            }
        });
    }

    /**
     * Posts a request to KCP to create a new user credential based on the given Janrain credentials. This is only called when attempting to update
     * the bearer token with a new user that has not been merged with KCP.
     * @param janrainCredentials Janrain credentials containing the uuid
     * @param mergeListener Merge callback listener.
     */
    private void syncJanrainUserToKcp(HashMap<String, String> janrainCredentials, final KcpAccountMergeListener mergeListener) {
        Call<Credential> requestCreateCredential = getKcpService().createUserCredential(KcpConstants.URL_POST_ADD_CREDENTIAL, janrainCredentials);
        requestCreateCredential.enqueue(new Callback<Credential>() {
            @Override
            public void onResponse(Call<Credential> call, Response<Credential> response) {
                if (response.isSuccessful()) {
                    updateUserTokenWithJanrainId(response.body().getJanrainId(), mergeListener);
                } else {
                    mergeListener.onAccountMergeFailed(response.code(), response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Credential> call, Throwable t) {
                logger.error(t);
                mergeListener.onAccountMergeFailed(-1, t.getMessage());
            }
        });
    }

    /**
     * Updates the user bearer token when a call to /a/token is successful.
     * @param token Token returned by a successful response.
     */
    private void updateResponseBearerToken(String token) {
        if (!token.isEmpty()) {
            KcpAccount.getInstance().saveGsonUserToken(mContext, token);
            HeaderFactory.constructHeader();
            mHeadersMap = HeaderFactory.getHeaders();
            Log.i(TAG, "Latest token: " + mHeadersMap.get("Authorization"));
            mUserService = ServiceFactory.createRetrofitService(mContext, mHeadersMap , UserService.class, KcpConstants.getBaseURL());
        }
    }

    /**
     * Deletes the user bearer token when the user signs out of the app.
     * On success, all profile data is reset and a new user is created.
     */
    private void deleteUserToken() {
        Call<ResponseBody> deleteToken = getKcpService().deleteUserToken();
        deleteToken.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "DELETE with token: " + response.raw().request().header("Authorization"));
                if (response.isSuccessful()) {
                    resetUserProfile();
                } else {
                    Log.e(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                logger.error(t);
            }
        });
    }



    /**
     * Updates any existing gift cards to be merged into the new account.
     * @param userId Janrain userId
     */
    private void updateGiftCards(String userId) {
        GiftCardManager.migrateLegacyGiftCards(mContext, userId);
    }

    /**
     * Updates the ExactTarget subscriber key with the newly merged Janrain Id
     * @param userId Janrain userId
     */
    private void updateETSubscriberKey(String userId) {
        ETManager.updateSubscriberKey(userId);
    }

    private HashMap<String, String> createJanrainPayload(String janrainId) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put(KEY_TYPE, VALUE_JANRAIN_CREDENTIAL);
        payload.put(KEY_ID, janrainId);
        payload.put(KEY_SECRET, janrainId);
        return payload;
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

    /**
     * Sends a DELETE request to KCP for the user bearer token.
     */
    public void signOutAndReset() {
        deleteUserToken();
        downloadUserToken();
        //create new device user -> invalidate token -> downloadUserToken()
    }

    private void resetUserProfile() {
        GiftCardManager.getInstance(mContext).reset();
        FavouriteManager.getInstance(mContext).resetFavourites(mContext, new CompletionListener() {
            @Override
            public void onComplete(boolean success) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Session.getInstance(mContext).endSession(mContext);
                    }
                });
            }
        });

    }


    public interface UserService {
        @POST
        Call<Token> postToUserService(
                @Url String url,
                @Body HashMap userAccount);
        @POST
        Call<Credential> createUserCredential(
                @Url String url,
                @Body HashMap userPayload
        );
        @DELETE(KcpConstants.URL_POST_CREATE_TOKEN)
        Call<ResponseBody> deleteUserToken();
    }

    public class Token {
        @SerializedName("token")
        private String token;

        public String getToken(){
            if(token == null) return "";
            return token;
        }
    }

    private class Credential {
        private int id;
        private String type;
        private String identifier;

        private int getId() {
            return id;
        }

        private String getJanrainId() {
            return (identifier == null) ? "" : identifier;
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
