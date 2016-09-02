package com.kineticcafe.kcpmall.user;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.kineticcafe.kcpandroidsdk.constant.KcpConstants;
import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.factory.HeaderFactory;

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

    private static final String KEY_TOKEN = "token";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "identifier";
    private static final String KEY_PASSWORD = "password";
    private static final String VALUE_DEVICE_CREDENTIAL = "DeviceCredential";

    public final static String PREFS_KEY_USER_TOKEN = 	"prefs_key_user_token";


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
    public static String mUserToken = "";


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
        if(mUserService == null) mUserService = serviceFactory.createRetrofitService(mContext, mHeadersMap, UserService.class, KcpConstants.URL_BASE);
        return mUserService;
    }


    public void getUserToken() {
        mUserToken = loadUserToken();
        if(mUserToken.equals("")) {
            postToCreateToken();
        } else {
            handleState(DOWNLOAD_COMPLETE);
        }
    }

    public void saveGsonUserToken(String token){
        KcpUtility.cacheToPreferences(mContext, PREFS_KEY_USER_TOKEN, token);
    }

    public String loadUserToken(){
        return KcpUtility.loadFromCache(mContext, PREFS_KEY_USER_TOKEN, "");
    }

    protected void postToCreateToken(){
        UserAccount userAccount = new UserAccount(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        //TAO GAVE ME THIS AND IT WORKS
        /*{
            "type": "DeviceCredential",
                "identifier": "007A27A5-1351-4D2C-910F-33C8009DB127",
                "password": "2804462D-407F-4DF2-B1B2-1D2F6EAFBE69"
        }*/


        Call<Token> call = getKcpService().postInterestedStores(KcpConstants.URL_POST_CREATE_TOKEN, userAccount.tempMap);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    String token = response.body().getToken();
                    if(!token.equals("")){
                        //token received - use this
                        saveGsonUserToken(token);
                        mUserToken = token;
                        HeaderFactory.constructHeader(); //update the header
                        handleState(DOWNLOAD_COMPLETE);
                    }
                } else {
                    handleState(DOWNLOAD_FAILED);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
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

    public class UserAccount {
//        public HashMap<String, Object> userAccount = new HashMap<>();
        public HashMap<String, String> tempMap = new HashMap<>();

        public UserAccount(String identifier, String password){
            tempMap.put(KEY_TYPE, VALUE_DEVICE_CREDENTIAL);
            tempMap.put(KEY_ID, identifier);
            tempMap.put(KEY_PASSWORD, password);

//            userAccount.put(KEY_TOKEN, tempMap);
        }

        public UserAccount(String type, String identifier, String password){
            tempMap.put(KEY_TYPE, type);
            tempMap.put(KEY_ID, identifier);
            tempMap.put(KEY_PASSWORD, password);

//            userAccount.put(KEY_TOKEN, tempMap);
        }
    }




}
