package com.kineticcafe.kcpmall.managers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.interfaces.FavouriteInterface;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-07-05.
 */
public class FavouriteManager {

    /**
     * KEEP IN MIND TO REDUCE THE CALLS (SAVE/LOAD) TO SHAREDPREF TO MINIMUM THROUGH THIS MANAGER
     */

    private static Context mContext;
    private final static String CONTENT_TYPE_DEAL         = "deal";
    private final static String CONTENT_TYPE_ANNOUNCEMENT = "announcement";
    private final static String CONTENT_TYPE_EVENT        = "event";
    private final static String CONTENT_TYPE_STORE        = "store";
    private final static String CONTENT_TYPE_INTEREST        = "category";

    private String mGsonFavKey = "";
    private final static String KEY_GSON_FAV_DEAL               = "DEAL";
    private final static String KEY_GSON_FAV_EVENT_ANOUNCEMENT  = "EVENT_ANNOUNCEMENT";
    private final static String KEY_GSON_FAV_STORE              = "STORE";

    public final static String PREFS_KEY_CATEGORY = 			"prefs_key_category";
    public final static String PREFS_KEY_FAV_STORE_LIKE_LINK = 	"prefs_key_store_like_link";

    private static HashMap<String, KcpContentPage> mDealFavs;
    private static HashMap<String, KcpContentPage> mEventFavs;
    private static HashMap<String, KcpContentPage> mStoreFavs;
    private static ArrayList<Integer> mInterestedCategoryList;
    private static ArrayList<String> mInterestedStoreList;
    private FavouriteInterface mFavouriteInterface;


    private static FavouriteManager sFavouriteManager;
    public static FavouriteManager getInstance(Context context) {
        mContext = context;
        if(sFavouriteManager == null) sFavouriteManager = new FavouriteManager();
        return sFavouriteManager;
    }

    private FavouriteManager(){
        mDealFavs = loadGsonHashMapContentPage(KEY_GSON_FAV_DEAL);
        mEventFavs = loadGsonHashMapContentPage(KEY_GSON_FAV_EVENT_ANOUNCEMENT);
        mStoreFavs = loadGsonHashMapContentPage(KEY_GSON_FAV_STORE);
        mInterestedCategoryList = KcpUtility.loadGsonArrayList(mContext, PREFS_KEY_CATEGORY);
        mInterestedStoreList = KcpUtility.loadGsonArrayListString(mContext, PREFS_KEY_FAV_STORE_LIKE_LINK);
    }

    public SidePanelManagers.FavouriteListener mFavouriteListener;
    public void setFavouriteListener(SidePanelManagers.FavouriteListener favouriteListener){
        mFavouriteListener = favouriteListener;
    }

    public ArrayList<Integer> getInterestedCategoryList(){
        return new ArrayList<Integer>(mInterestedCategoryList);
    }

    public ArrayList<String> getInterestedStoreList(){
        return new ArrayList<String>(mInterestedStoreList);
    }


    public boolean addOrRemoveFavContent(final String likeLink, final KcpContentPage kcpContentPage, FavouriteInterface favouriteInterface){
        mFavouriteInterface = favouriteInterface;
        return addOrRemoveFavContent(likeLink, kcpContentPage);
    }

    public boolean addOrRemoveFavContent(final String likeLink, final KcpContentPage kcpContentPage){
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
                    showToast(mContext.getResources().getString(R.string.warning_fav_removed));
                } else {
                    favHashMap.put(likeLink, kcpContentPage);
                    showToast(mContext.getResources().getString(R.string.warning_fav_added));
                }
                KcpUtility.saveGson(mContext, mGsonFavKey, favHashMap);
                if(mFavouriteListener != null) mFavouriteListener.onFavouriteChanged();
                mGsonFavKey = "";
                if(mFavouriteInterface != null) mFavouriteInterface.didChangeFavourite();
            }
        });

        return true;
    }

    public void cacheInterestedCategoryList(ArrayList<Integer> favCatTempList){
        mInterestedCategoryList = new ArrayList<>(favCatTempList);
        KcpUtility.saveGson(mContext, PREFS_KEY_CATEGORY, favCatTempList);
        if(mFavouriteListener != null) mFavouriteListener.onFavouriteChanged();
    }

    public void cacheInterestedStoreList(ArrayList<String> interestedStoreList){
        mInterestedStoreList = new ArrayList<String>(interestedStoreList);
        KcpUtility.saveGson(mContext, PREFS_KEY_FAV_STORE_LIKE_LINK, interestedStoreList);
        //No Need to notify through the listener
    }

    public void showToast(final String text){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
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

    public boolean isLiked(String likeLink, KcpContentPage kcpContentPage){
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

    public static HashMap<String, KcpContentPage> loadGsonHashMapContentPage(String gsonFavKey){
        Gson gson = new Gson();
        String json = mContext.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(gsonFavKey, "");
        Type listType = new TypeToken<HashMap<String, KcpContentPage>>() {}.getType();
        HashMap<String, KcpContentPage> obj = gson.fromJson(json, listType);
        if(obj == null) return new HashMap<String, KcpContentPage>();
        else return obj;
    }

    public static ArrayList<KcpContentPage> getFavDealContentPages(){
        return new ArrayList<KcpContentPage>(mDealFavs.values());
    }

    public static ArrayList<KcpContentPage> getFavEventContentPages(){
        return new ArrayList<KcpContentPage>(mEventFavs.values());
    }

    public static ArrayList<KcpContentPage> getFavStoreContentPages(){
        return new ArrayList<KcpContentPage>(mStoreFavs.values());
    }

    public int getDealFavSize(){
        return mDealFavs.size();
    }

    public int getEventAnnouncementFavSize(){
        return mEventFavs.size();
    }

    public int getStoreFavSize(){
        return mStoreFavs.size();
    }

    public int getInterestFavSize(){
        return mInterestedCategoryList.size();
    }
}
