package com.ivanhoecambridge.mall.managers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.interfaces.CompletionListener;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.models.MultiLike;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import factory.HeaderFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Kay on 2016-07-05.
 */
public class FavouriteManager {

    interface FavouriteListener {
        void onFavouriteReset();
    }

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
    private final static String KEY_GSON_FAV_CAT                = "CAT";
    private final static String KEY_GSON_FAV_GENERAL            = "FAV_GENERAL";
    private final static String KEY_GSON_UNFAV_GENERAL            = "UNFAV_GENERAL";

    public final static String PREFS_KEY_CATEGORY = 			"prefs_key_category";
    public final static String PREFS_KEY_FAV_STORE_LIKE_LINK = 	"prefs_key_store_like_link";

    private static HashMap<String, KcpContentPage> mDealFavs;
    private static HashMap<String, KcpContentPage> mEventFavs;
    private static HashMap<String, KcpContentPage> mStoreFavs;
    private static HashMap<String, KcpCategories> mCatFavs;


    private static ArrayList<String> mFavsSynced;
    private static ArrayList<String> mUnfavsSynced;

    private FavouriteInterface mFavouriteInterface;

    //MULTI LIKES SYNCING
    private static ScheduledExecutorService sScheduler;
    private final int DELAY_MULTI_LIKE_SYNCING = 5;


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
        mCatFavs = loadGsonHashMapCategories(KEY_GSON_FAV_CAT);

        mFavsSynced = KcpUtility.loadGsonArrayListString(mContext, KEY_GSON_FAV_GENERAL);
        mUnfavsSynced = KcpUtility.loadGsonArrayListString(mContext, KEY_GSON_UNFAV_GENERAL);
    }

    public void resetFavourites(Context context, CompletionListener completionListener) {
        mDealFavs.clear();
        mEventFavs.clear();
        mStoreFavs.clear();
        mCatFavs.clear();
        clearFavouritesFromCache(context, completionListener);
    }

    private void clearFavouritesFromCache(final Context context, final CompletionListener completionListener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                KcpUtility.saveGson(context, KEY_GSON_FAV_STORE, mStoreFavs);
                KcpUtility.saveGson(context, KEY_GSON_FAV_CAT, mCatFavs);
                completionListener.onComplete(true);
            }
        });
    }

    public SidePanelManagers.FavouriteListener mFavouriteListener;
    public void setFavouriteListener(SidePanelManagers.FavouriteListener favouriteListener){
        mFavouriteListener = favouriteListener;
    }

    public boolean addOrRemoveFavContent(final String likeLink, final KcpContentPage kcpContentPage, FavouriteInterface favouriteInterface){
        mFavouriteInterface = favouriteInterface;
        return addOrRemoveFavContent(likeLink, kcpContentPage);
    }

    public boolean addOrRemoveFavContent(final String likeLink, final KcpContentPage kcpContentPage){
        if(kcpContentPage == null) return false;

        mGsonFavKey = getGsonKey(kcpContentPage);

        if(mGsonFavKey.equals("")) return false;

        //AsyncTask doesn't work here - . You can't have multiple Asynctasks running in parallel up until SDK 11. Check
        //http://stackoverflow.com/questions/4080808/asynctask-doinbackground-does-not-run
        Thread favThread = new Thread(new Runnable() {
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

                //TODO: see if there's update to likes/unlikes and decide to sync with server or not
                //TODO: start / reset the 5 seconds timer which runs updateFavs()

                //why use ExecutorService instead of timer http://stackoverflow.com/questions/409932/java-timer-vs-executorservice
                //every DELAY_MULTI_LIKE_SYNCING seconds, it tries to sync with the server the likes / unlikes
                if(sScheduler != null) sScheduler.shutdownNow();
                sScheduler = Executors.newScheduledThreadPool(1);
                ScheduledFuture<?> handl = sScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        updateFavs();
                    }
                }, DELAY_MULTI_LIKE_SYNCING, SECONDS);
            }
        });
        favThread.start();

        return true;
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

    public static HashMap<String, KcpCategories> loadGsonHashMapCategories(String gsonFavKey){
        Gson gson = new Gson();
        String json = mContext.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(gsonFavKey, "");
        Type listType = new TypeToken<HashMap<String, KcpCategories>>() {}.getType();
        HashMap<String, KcpCategories> obj = gson.fromJson(json, listType);
        if(obj == null) return new HashMap<String, KcpCategories>();
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

    public HashMap<String, KcpCategories> getFavCatMap(){
        return new HashMap<>(mCatFavs);
    }

    public HashMap<String, KcpContentPage> getFavStoreMap(){
        return new HashMap<String, KcpContentPage>(mStoreFavs);
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
        return mCatFavs.size();
    }

    /**
     * Updates the user favourite categories/interests from the returned KCP categories list.
     * @param categories KCP Categories list.
     */
    public void updateUserFingerprintInterests(ArrayList<KcpCategories> categories) {
        HashMap<String, KcpCategories> likeMap = new HashMap<>();
        for (KcpCategories category : categories) {
            likeMap.put(category.getLikeLink(), category);
        }
        updateFavCat(likeMap, null, false, null);
    }

    public void updateUserDeals(ArrayList<KcpContentPage> contentPages) {
        listToHashMap(mDealFavs, contentPages);
    }

    public void updateUserEvents(ArrayList<KcpContentPage> contentPages) {
        listToHashMap(mEventFavs, contentPages);
    }

    private void listToHashMap(HashMap<String, KcpContentPage> userContentPageMap, ArrayList<KcpContentPage> userContentPages) {
        for (KcpContentPage content : userContentPages) {
            userContentPageMap.put(content.getLikeLink(), content);
        }

    }

    /**
     * Updates the user favourite categories locally on the device with the option to post changes to the server.
     * @param likeList User likes list
     * @param unlikeList User non-likes list
     * @param postLikeListToServer Set as true to post changes to server, false to keep it local.
     * @param handler Handler object to handle ui changes
     */
    public synchronized void updateFavCat(final HashMap<String, KcpCategories> likeList, HashMap<String, KcpCategories> unlikeList, boolean postLikeListToServer, @Nullable Handler handler){
        mCatFavs = likeList;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                KcpUtility.saveGson(mContext, KEY_GSON_FAV_CAT, likeList);
            }
        });
        if(postLikeListToServer) {
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(mContext, 0, HeaderFactory.getHeaders(), new LikeListHandler(handler));
            kcpCategoryManager.postInterestedCategories(getDeltaMultiLikeBatch( getLikeListFromMap(likeList) , getLikeListFromMap(unlikeList)));
        }
    }

    public synchronized void updateFavStore(final HashMap<String, KcpContentPage> likeList, HashMap<String, KcpContentPage> unlikeList, boolean postLikeListToServer, @Nullable Handler handler){
        mStoreFavs = likeList;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                KcpUtility.saveGson(mContext, KEY_GSON_FAV_STORE, likeList);
            }
        });
        if(postLikeListToServer) {
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(mContext, 0, new HeaderFactory().getHeaders(), new LikeListHandler(handler));
            kcpCategoryManager.postInterestedCategories(getDeltaMultiLikeBatch( getLikeListFromContentPage(likeList) , getLikeListFromContentPage(unlikeList)));
        }
    }

    /**
     * 1. collect all stored favs.
     * 2. create unLiked by subtracting favs from lastly synced favs.
     * 3. temporarily save the stored favs in favsTemp
     * 4. create likes to sync by subtracting lastly synced favs from store favs.
     * 5. when multi-like post succeeds, save lastly synced favs as the favsTemp (shouldn't save as favs because you need to keep track of all synced favs, not just the favs just added)
     */
    public synchronized void updateFavs() {
        final ArrayList<String> favs = new ArrayList<>(); //current likes
        favs.addAll(mDealFavs.keySet());
        favs.addAll(mEventFavs.keySet());
        favs.addAll(mStoreFavs.keySet());

        ArrayList<String> unFavs = new ArrayList<>(mFavsSynced);
        unFavs.removeAll(favs); //get unlikes by comparing lastly synced likes and current likes
        final ArrayList<String> favsTemp = new ArrayList<>(favs); //save temp entire favs so when multi like post succeeds, save it
        favs.removeAll(mFavsSynced); //get only likes that weren't synced

        if(favs.size() > 0 || unFavs.size() > 0) { //update likes only if you have one
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(mContext, 0, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    switch (inputMessage.arg1) {
                        case KcpCategoryManager.DOWNLOAD_FAILED:
                            break;
                        case KcpCategoryManager.DOWNLOAD_COMPLETE:
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mFavsSynced = favsTemp;
                                    KcpUtility.saveGson(mContext, KEY_GSON_FAV_GENERAL, favsTemp);
                                }
                            });
                            break;
                        default:
                            super.handleMessage(inputMessage);
                    }
                }
            });
            kcpCategoryManager.postInterestedCategories(getDeltaMultiLikeBatch(favs, unFavs));
        }
    }

    public static ArrayList<String> getLikeListFromMap(HashMap<String, KcpCategories> map){
        ArrayList<String> likeList = new ArrayList<String>();
        for (String key : map.keySet()) {
            likeList.add(key);
        }
        return likeList;
    }

    public static ArrayList<String> getLikeListFromContentPage(HashMap<String, KcpContentPage> map){
        ArrayList<String> likeList = new ArrayList<String>();
        for (String key : map.keySet()) {
            likeList.add(key);
        }
        return likeList;
    }


    private HashMap<String, Object> getDeltaMultiLikeBatch(ArrayList<String> likeList, ArrayList<String> unlikeList){
        return new MultiLike(likeList, unlikeList).getMultiLikeBody();
    }

    public class LikeListHandler extends Handler {
        private Handler handler;
        public LikeListHandler (Handler handler){
            this.handler = handler;
        }

        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.arg1) {
                case KcpCategoryManager.DOWNLOAD_FAILED:
                    break;
                case KcpCategoryManager.DOWNLOAD_COMPLETE:
                    if(mFavouriteListener != null) mFavouriteListener.onFavouriteChanged();
                    break;
                default:
                    super.handleMessage(inputMessage);
            }

            Message message = new Message();
            message.arg1 = inputMessage.arg1;
            if(handler != null) handler.sendMessage(message);

        }
    }


}
