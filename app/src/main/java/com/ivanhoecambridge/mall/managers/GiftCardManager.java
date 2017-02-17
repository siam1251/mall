package com.ivanhoecambridge.mall.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ivanhoecambridge.mall.giftcard.GiftCard;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Kay on 2017-02-16.
 */

public class GiftCardManager {

    private static Context mContext;
    private final static String KEY_GSON_GIFT_CARD = "GIFT_CARD";

    private HashMap<String, GiftCard> giftCards;
    private static GiftCardManager sGiftCardManager;

    public static GiftCardManager getInstance(Context context) {
        mContext = context;
        if(sGiftCardManager == null) sGiftCardManager = new GiftCardManager();
        return sGiftCardManager;
    }

    public GiftCardManager() {
        giftCards = loadGiftCard();
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

    public boolean addCard(String cardNumber, float cardBalance){
        if(!giftCards.containsKey(cardNumber)) {
            giftCards.put(cardNumber, new GiftCard(cardNumber, cardBalance));
            return true;
        } return false;
    }

    public boolean removeCard(String cardNumber){
        if(giftCards.containsKey(cardNumber)) {
            giftCards.remove(cardNumber);
            return true;
        } return false;
    }

    public void updateBalance(){
        for (String cardNumber : giftCards.keySet()) {
            updateBalance(cardNumber);
        }
    }

    public void updateBalance(String cardNumber){
        if(giftCards.containsKey(cardNumber)) {


        }
        if(mGiftCardUpdateListener != null) mGiftCardUpdateListener.onGiftCardUpdated();
    }

    public interface GiftCardUpdateListener {
        void onGiftCardUpdated();
    }



}
