package com.kineticcafe.kcpmall.kcpData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpCorePage;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kay on 2016-05-31.
 */
public class KcpCategoryManager {

    private Context mContext;
    private KcpService mKcpService;
    protected Logger logger = null;
    private Handler mHandler;
    private ArrayList<KcpContentPage> mKcpContentPageList;

    private final String RESPONSE_STATUS_COMPLETE = "complete";

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    public KcpCategoryManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        logger = new Logger(getClass().getName());
    }

    public void downloadCategories(){
        mKcpService = ServiceFactory.createRetrofitService(mContext, new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        Call<KcpCorePage> call = mKcpService.getCategories(Constants.URL_CATEGORIES, Constants.QUERY_PAGE, Constants.QUERY_PER_PAGE);
        call.enqueue(new Callback<KcpCorePage>() {
            @Override
            public void onResponse(Call<KcpCorePage> call, Response<KcpCorePage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpCorePage kcpCorePage = response.body();
                    kcpCategoryRoot.setCategoriesList(kcpCorePage.getKcpEmbedded().getCategoryList());
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpCorePage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }


    public void downloadPlacesWithCategoryIds(ArrayList categoryIds){
        String categories = Utility.convertArrayListToStringWithComma(categoryIds);
        mKcpService = ServiceFactory.createRetrofitService(mContext, new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        Call<KcpCorePage> call = mKcpService.getCategories(Constants.URL_CATEGORIES_WITH_CATEGORY_IDS, Constants.QUERY_PAGE, Constants.QUERY_PER_PAGE, categories);
        call.enqueue(new Callback<KcpCorePage>() {
            @Override
            public void onResponse(Call<KcpCorePage> call, Response<KcpCorePage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpCorePage kcpCorePage = response.body();
                    kcpCategoryRoot.setPlacesList(kcpCorePage.getKcpEmbedded().getPlaceList());
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpCorePage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

   /* {
        "multi_like":
        {
            "like":[
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWh1Yi1jb3JlL0NhdGVnb3J5LzEzMTE3Mzc2Nw"
            },
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWh1Yi1jb3JlL1BsYWNlLzgzMzM3NjEzNg"
            },
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWh1Yi1jb3JlL1BsYWNlLzgzMzM3NjEzNg"
            },
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWh1Yi1jb3JlL0NhdGVnb3J5LzgzNzIyMTE3NQ"
            },
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWh1Yi1jb3JlL0NhdGVnb3J5LzgzNzIyMTE3NQ"
            }
            ],
            "unlike":[
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWh1Yi1jb3L0NhdASDSDnb3J5LzEzMTE3Mzc2Nw"
            },
            {
                "like_link": "http://10.0.20.2:3000/core/user/like/Z2lkOi8vZGF0YWhASDASDEGFWADASDASDASDDaLzgzMzM3NjEzNg"
            }
            ]
        }
    }*/

    public static class MultiLike {
        public HashMap<String, Object> multiLikeBody;
        public MultiLike(ArrayList<String> likedStores, ArrayList<String> unlikedstores){
            multiLikeBody = new HashMap<>();
            HashMap<String, Object> multiLikeMap = new HashMap<>();

            ArrayList<HashMap<String, String>> likeArray = new ArrayList<HashMap<String, String>>();
            ArrayList<HashMap<String, String>> unlikeArray = new ArrayList<HashMap<String, String>>();

            for (String value : likedStores) {
                HashMap<String, String> likeMap = new HashMap<String, String>();
                likeMap.put("like_link", value);
                likeArray.add(likeMap);
            }

            for (String value : unlikedstores) {
                HashMap<String, String> unlikeMap = new HashMap<String, String>();
                unlikeMap.put("like_link", value);
                unlikeArray.add(unlikeMap);
            }

            multiLikeMap.put("like", likeArray);
            multiLikeMap.put("unlike", unlikeArray);
            multiLikeBody.put("multi_like", multiLikeMap);
        }
    }


    public void postInterestedStores(ArrayList<String> likedStores){

        ArrayList<String> cachedFavStores = Utility.loadGsonArrayListString(mContext, Constants.PREFS_KEY_FAV_STORE_LIKE_LINK);
        ArrayList<String> unlikedStores = Utility.getRemovedObjectFromCache(cachedFavStores, likedStores);

        MultiLike multiLike = new MultiLike(likedStores, unlikedStores);
        mKcpService = ServiceFactory.createRetrofitService(mContext, new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        Call<KcpCorePage> call = mKcpService.postInterestedStores(Constants.URL_POST_INTERESTED_STORES, multiLike.multiLikeBody);

        call.enqueue(new Callback<KcpCorePage>() {
            @Override
            public void onResponse(Call<KcpCorePage> call, Response<KcpCorePage> response) {
                if(response.isSuccessful()){
                    if(response.body().status.equals(RESPONSE_STATUS_COMPLETE)) handleState(DOWNLOAD_COMPLETE);
                } else {
                    handleState(DOWNLOAD_FAILED);
                }
            }

            @Override
            public void onFailure(Call<KcpCorePage> call, Throwable t) {
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
            case DOWNLOAD_FAILED:
                break;
            case DOWNLOAD_COMPLETE:
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

}
