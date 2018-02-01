package com.ivanhoecambridge.mall.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.models.MultiLike;

import java.util.ArrayList;
import java.util.HashMap;

import factory.HeaderFactory;

/**
 * Created by Kay on 2016-10-21.
 */

public class LikeManager {

    public final static String PREFS_KEY_LIKE_LINK = 	"prefs_key_like_link";

    private static Context mContext;
    private static LikeManager sLikeManager;

    private ArrayList<String> mLocalLikeList;
    private ArrayList<String> mDeltaLikeList;
    private ArrayList<String> mDeltaUnLikeList;
    private long mTimeStamp;

    private static LikeManager getInstance(Context context){
        mContext = context;
        if(sLikeManager == null) sLikeManager = new LikeManager();
        return sLikeManager;
    }

    private LikeManager() {
        mLocalLikeList = KcpUtility.loadGsonArrayListString(mContext, PREFS_KEY_LIKE_LINK);
        mDeltaLikeList = new ArrayList<>();
        mDeltaUnLikeList = new ArrayList<>();
    }

    public ArrayList<String> getLocalLikeListCopy() {
        return new ArrayList<String>(mLocalLikeList);
    }

    /**
     * used for creating delta like list and updating local likelist using the entire likeList passed from @{@link Class<com.ivanhoecambridge.mall.activities.InterestedCategoryActivity>} or @{@link Class<com.ivanhoecambridge.mall.activities.InterestedStoreActivity>}
     * @param likeList
     * @param postLikeListToServer
     * @param handler define if there is additional codes you want to run after
     */
    public synchronized void updateLocalLikeList(ArrayList<String> likeList, ArrayList<String> unlikeList, boolean postLikeListToServer, @Nullable Handler handler){
        mLocalLikeList = likeList;
        mDeltaUnLikeList = unlikeList;
        saveLocalList();
        if(postLikeListToServer) postLikeListToServer(handler);
    }

    private void createDeltaLikeList(String likeLink){
        if(wasLiked(likeLink)) {
            mDeltaUnLikeList.add(likeLink);
        } else {
            mDeltaLikeList.add(likeLink);
        }
    }

    private void updateLocalLikeList(String likeLink){
        if(wasLiked(likeLink)) {
            mLocalLikeList.remove(likeLink);
        } else {
            mLocalLikeList.add(likeLink);
        }
    }

    public synchronized void isLiked(String likeLink){
        createDeltaLikeList(likeLink);
        updateLocalLikeList(likeLink);
        saveLocalList();
    }

    private void saveLocalList(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                KcpUtility.saveGson(mContext, PREFS_KEY_LIKE_LINK, mLocalLikeList);
            }
        });
    }

    public boolean wasLiked(String likeLink){
        return mLocalLikeList.contains(likeLink);
    }

    private HashMap<String, Object> getDeltaMultiLikeBatch(){
        long currentTimeStamp = getCurrentTimeStamp();
        return new MultiLike(mLocalLikeList, mDeltaUnLikeList).getMultiLikeBody();
    }

    private long getCurrentTimeStamp(){
        return System.currentTimeMillis();
    }

    public void clearDeltaLikeList(){
        mDeltaLikeList.clear();
        mDeltaUnLikeList.clear();
    }

    public synchronized void postLikeListToServer(Handler handler){
        KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(mContext, 0, new HeaderFactory().getHeaders(), new LikeListHandler(handler));
        kcpCategoryManager.postMultiLike(getDeltaMultiLikeBatch());
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
                    clearDeltaLikeList();
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
