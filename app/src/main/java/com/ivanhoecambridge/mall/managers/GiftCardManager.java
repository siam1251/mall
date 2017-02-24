package com.ivanhoecambridge.mall.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.service.ServiceFactory;
import com.ivanhoecambridge.kcpandroidsdk.twitter.TwitterAPI;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.GiftCardActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.giftcard.GiftCard;
import com.ivanhoecambridge.mall.giftcard.GiftCardResponse;
import com.ivanhoecambridge.mall.parking.ParkingManager;
import com.ivanhoecambridge.mall.parking.Parkings;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;

import factory.HeaderFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Kay on 2017-02-16.
 */

public class GiftCardManager {

    private static Context mContext;
    private final static String KEY_GSON_GIFT_CARD = "GIFT_CARD";
    private final static String PARAM_PROGRAM = "Ivanhoe138";
    private final static String PARAM_FIELDS = "available_balance,status";

    private final static String HEADER_KEY_ACCEPT = "Accept";
    private final static String HEADER_KEY_AUTH = "Authorization";
    private final static String HEADER_KEY_ICMP_AUTO = "ICMPStoreFinancialAuthName";
    private final static String HEADER_KEY_ICMP_PASS = "ICMPStoreFinancialAuthPassword";

    private final static String HEADER_VALUE_ACCEPT = "application/json";
    private final static String HEADER_VALUE_ICMP_AUTO = "Ivanhoestats";
    private final static String HEADER_VALUE_ICMP_PASS = "1Iv@nh0e!";

    private static HashMap<String, GiftCard> giftCards;
    private static GiftCardManager sGiftCardManager;
    protected GiftCardService mGiftCardService;
    protected Handler mHandler;

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    public static GiftCardManager getInstance(Context context) {
        mContext = context;
        if(sGiftCardManager == null) sGiftCardManager = new GiftCardManager();
        return sGiftCardManager;
    }

    public GiftCardManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public GiftCardManager() {
        giftCards = loadGiftCard();
    }

    public interface GiftCardService {
        @GET
        Call<GiftCardResponse> getGCBalance(
                @Url String url,
                @Query("program") String program,
                @Query("fields") String fields);
    }

    public GiftCardUpdateListener mGiftCardUpdateListener;
    public void setGiftCardUpdateListener(GiftCardUpdateListener giftCardUpdateListener){
        mGiftCardUpdateListener = giftCardUpdateListener;
    }

    public HashMap<String, GiftCard> getGiftCards() {
        return giftCards;
    }

    public static HashMap<String, GiftCard> loadGiftCard(){
        Gson gson = new Gson();
        String json = mContext.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(KEY_GSON_GIFT_CARD, "");
        Type listType = new TypeToken<HashMap<String, GiftCard>>() {}.getType();
        HashMap<String, GiftCard> obj = gson.fromJson(json, listType);
        if(obj == null) return new HashMap<String, GiftCard>();
        else return obj;
    }

    private void saveGiftCard(){
        KcpUtility.saveGson(mContext, KEY_GSON_GIFT_CARD, giftCards);
    }

    public boolean addCard(String cardNumber, float cardBalance){
        boolean cardExist = false;
        if(!giftCards.containsKey(cardNumber)) {
        } else {
            cardExist = true;
        }
        giftCards.put(cardNumber, new GiftCard(cardNumber, cardBalance));
        saveGiftCard();
        return cardExist;
    }

    public boolean removeCard(String cardNumber){
        if(giftCards.containsKey(cardNumber)) {
            giftCards.remove(cardNumber);
            saveGiftCard();
            return true;
        } return false;
    }

    /**
     * update balance of all cards saved
     */
    public void updateBalance(){
        for (String cardNumber : giftCards.keySet()) {
            updateBalance(cardNumber);
        }
    }

    /**
     * update balance of a single card
     * @param cardNumber
     */
    private void updateBalance(final String cardNumber){
        if(giftCards.containsKey(cardNumber)) {

            GiftCardManager giftCardManager = new GiftCardManager(mContext, new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    switch (inputMessage.arg1) {
                        case KcpCategoryManager.DOWNLOAD_FAILED:
                            break;
                        case KcpCategoryManager.DOWNLOAD_COMPLETE:
                            GiftCardResponse giftCardResponse = (GiftCardResponse) inputMessage.obj;
                            addCard(cardNumber, giftCardResponse.getAvailableBalance());
                            Toast.makeText(mContext, mContext.getString(R.string.gc_refresh), Toast.LENGTH_SHORT).show(); //toast each gc balance
                            if(mGiftCardUpdateListener != null) mGiftCardUpdateListener.onGiftCardUpdated();
                            break;
                        default:
                            super.handleMessage(inputMessage);
                    }
                }
            });
            giftCardManager.checkCardBalance(cardNumber);
        } else {
        }
    }

    public interface GiftCardUpdateListener {
        void onGiftCardUpdated();
    }

    public GiftCardService getKcpService(){
        ServiceFactory serviceFactory = new ServiceFactory();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_KEY_ACCEPT,     HEADER_VALUE_ACCEPT);
        headers.put(HEADER_KEY_AUTH,       getAuth());
        headers.put(HEADER_KEY_ICMP_AUTO,  HEADER_VALUE_ICMP_AUTO);
        headers.put(HEADER_KEY_ICMP_PASS,  HEADER_VALUE_ICMP_PASS);

        if(mGiftCardService == null) mGiftCardService = serviceFactory.createRetrofitService(mContext, headers, GiftCardService.class, "https://webservices.storefinancial.net/api/v1/en/cards/");
        return mGiftCardService;
    }

    private String getAuth(){
        String credentials = HEADER_VALUE_ICMP_AUTO + ":" + HEADER_VALUE_ICMP_PASS;
        final String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP); //not working
        return basicAuth;
    }

    public void checkCardBalance(String cardNumber){
        cardNumber = cardNumber.replace(" ", "");
        Call<GiftCardResponse> call = getKcpService().getGCBalance(cardNumber, PARAM_PROGRAM, PARAM_FIELDS);
        call.enqueue(new Callback<GiftCardResponse>() {
            @Override
            public void onResponse(Call<GiftCardResponse> call, Response<GiftCardResponse> response) {
                if(response.isSuccessful()){
                    GiftCardResponse giftCardResponse = response.body();
                    handleState(DOWNLOAD_COMPLETE, giftCardResponse);
                } else {
                    int code = response.code();
                    String errorMsg;
                    switch(code) {
                        case 400: //malformed syntax
                            errorMsg = response.message();
                            break;
                        case 401: //auth failed
                            errorMsg = response.message();
                            break;
                        case 404: //URL doesn't match any of defined routes
                            errorMsg = mContext.getString(R.string.error_code_404);
                            break;
                        case 500: //server error
                            errorMsg = mContext.getString(R.string.error_code_500);
                            break;
                        case 504: //server error
                            errorMsg = mContext.getString(R.string.error_code_500);
                            break;
                        default:
                            errorMsg = response.message();
                            break;
                    }

                    handleState(DOWNLOAD_FAILED, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<GiftCardResponse> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    private void handleState(int state){
        handleState(state, mContext.getString(R.string.error_code_internet));
    }

    private void handleState(int state, @Nullable Object result){
        if(mHandler == null) return;
        Message message = new Message();
        message.arg1 = state;
        message.obj = result;
        mHandler.sendMessage(message);
    }

}
