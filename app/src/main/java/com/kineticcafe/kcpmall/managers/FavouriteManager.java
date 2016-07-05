package com.kineticcafe.kcpmall.managers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-07-05.
 */
public class FavouriteManager {

    private final static String CONTENT_TYPE_DEAL         = "deal";
    private final static String CONTENT_TYPE_ANNOUNCEMENT = "announcement";
    private final static String CONTENT_TYPE_EVENT        = "event";
    private final static String CONTENT_TYPE_STORE        = "store";

    private String mGsonFavKey = "";
    private final static String KEY_GSON_FAV_DEAL               = "DEAL";
    private final static String KEY_GSON_FAV_EVENT_ANOUNCEMENT  = "EVENT_ANNOUNCEMENT";
    private final static String KEY_GSON_FAV_STORE              = "STORE";
    private final static String KEY_GSON_FAV_INTERST            = "INTERESTS";

//    private static HashMap<String, Integer> mFavTracker;

    private static HashMap<String, KcpContentPage> mDealFavs;
    private static HashMap<String, KcpContentPage> mEventFavs;
    private static HashMap<String, KcpContentPage> mStoreFavs;
    private static HashMap<String, KcpContentPage> mInterestFavs;


    private static FavouriteManager sFavouriteManager;
    public static FavouriteManager getInstance(Context context) {
        if(sFavouriteManager == null) sFavouriteManager = new FavouriteManager(context);
        return sFavouriteManager;
    }

    private FavouriteManager(Context context){
        mDealFavs = loadGsonHashMapContentPage(context, KEY_GSON_FAV_DEAL);
        mEventFavs = loadGsonHashMapContentPage(context, KEY_GSON_FAV_EVENT_ANOUNCEMENT);
        mStoreFavs = loadGsonHashMapContentPage(context, KEY_GSON_FAV_STORE);
        mInterestFavs = loadGsonHashMapContentPage(context, KEY_GSON_FAV_INTERST);

//        mFavTracker = new HashMap<>();
//        mFavTracker.put(KEY_GSON_FAV_DEAL, getFavDealContentPages(context).size());
//        mFavTracker.put(KEY_GSON_FAV_EVENT_ANOUNCEMENT, getFavEventContentPages(context).size());
//        mFavTracker.put(KEY_GSON_FAV_STORE, getFavStoreContentPages(context).size());
//        mFavTracker.put(KEY_GSON_FAV_INTERST, 0);
    }

    public SidePanelManagers.FavouriteListener mFavouriteListener;
    public void setFavouriteListener(SidePanelManagers.FavouriteListener favouriteListener){
        mFavouriteListener = favouriteListener;
    }


    public boolean addOrRemoveFavContent(final Context context, final String likeLink, final KcpContentPage kcpContentPage){
        if(kcpContentPage == null) return false;

        mGsonFavKey = getGsonKey(kcpContentPage);

        if(mGsonFavKey.equals("")) return false;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                HashMap<String, KcpContentPage> favHashMap = null;
                if(kcpContentPage.content_type.contains(CONTENT_TYPE_DEAL)){
                    favHashMap = mDealFavs;
                } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EVENT)){
                    favHashMap = mEventFavs;
                } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_ANNOUNCEMENT)){
                    favHashMap = mEventFavs;
                } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_STORE)){
                    favHashMap = mStoreFavs;
                } else return;

                if(favHashMap.containsKey(likeLink)) {
                    favHashMap.remove(likeLink);
                    showToast(context, context.getResources().getString(R.string.warning_fav_removed));
                } else {
                    favHashMap.put(likeLink, kcpContentPage);
                    showToast(context, context.getResources().getString(R.string.warning_fav_added));
                }
                KcpUtility.saveGson(context, mGsonFavKey, favHashMap);
                if(mFavouriteListener != null) mFavouriteListener.onFavouriteChanged();
                mGsonFavKey = "";
            }
        });

        return true;
    }

    public void showToast(final Context context, final String text){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }



    public String getGsonKey(KcpContentPage kcpContentPage){
        if(kcpContentPage.content_type.contains(CONTENT_TYPE_DEAL)){
            return KEY_GSON_FAV_DEAL;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EVENT)){
            return KEY_GSON_FAV_EVENT_ANOUNCEMENT;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_ANNOUNCEMENT)){
            return KEY_GSON_FAV_EVENT_ANOUNCEMENT;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_STORE)){
            return KEY_GSON_FAV_STORE;
        } else return "";
    }

    public boolean isLiked(Context context, String likeLink, KcpContentPage kcpContentPage){
        HashMap<String, KcpContentPage> favHashMap = null;
        if(kcpContentPage.content_type.contains(CONTENT_TYPE_DEAL)){
            favHashMap = mDealFavs;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EVENT)){
            favHashMap = mEventFavs;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_ANNOUNCEMENT)){
            favHashMap = mEventFavs;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_STORE)){
            favHashMap = mStoreFavs;
        } else return false;

        return favHashMap.containsKey(likeLink);
    }

    public static HashMap<String, KcpContentPage> loadGsonHashMapContentPage(Context context, String gsonFavKey){
        Gson gson = new Gson();
        String json = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(gsonFavKey, "");
        Type listType = new TypeToken<HashMap<String, KcpContentPage>>() {}.getType();
        HashMap<String, KcpContentPage> obj = gson.fromJson(json, listType);
        if(obj == null) return new HashMap<String, KcpContentPage>();
        else return obj;
    }

    public static ArrayList<KcpContentPage> getFavDealContentPages(Context context){
        return new ArrayList<KcpContentPage>(mDealFavs.values());
    }

    public static ArrayList<KcpContentPage> getFavEventContentPages(Context context){
        return new ArrayList<KcpContentPage>(mEventFavs.values());
    }

    public static ArrayList<KcpContentPage> getFavStoreContentPages(Context context){
        return new ArrayList<KcpContentPage>(mStoreFavs.values());
    }

    public int getDealFavSize(Context context){
        return mDealFavs.size();
//        return mFavTracker.get(KEY_GSON_FAV_DEAL);
    }

    public int getEventAnnouncementFavSize(Context context){
        return mEventFavs.size();
//        return mFavTracker.get(KEY_GSON_FAV_EVENT_ANOUNCEMENT);
    }

    public int getStoreFavSize(Context context){
        return mStoreFavs.size();
//        return mFavTracker.get(KEY_GSON_FAV_STORE);
    }


    public int getInterestFavSize(Context context){
        return mInterestFavs.size();
//        return mFavTracker.get(KEY_GSON_FAV_INTERST);
    }
}
