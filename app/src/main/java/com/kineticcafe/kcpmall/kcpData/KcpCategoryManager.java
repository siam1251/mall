package com.kineticcafe.kcpmall.kcpData;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.ProgressBarWhileDownloading;

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
    private ProgressDialog pd;

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

    public KcpService getKcpService(){
        if(mKcpService == null) mKcpService = ServiceFactory.createRetrofitService(mContext, new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        return mKcpService;
    }

    public void downloadFingerPrintingCategories(){
        Call<KcpContentPage> call = getKcpService().getContentPage(Constants.URL_FINGERPRINTING_CATEGORIES, Constants.QUERY_PAGE, Constants.QUERY_PER_PAGE);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpContentPage kcpCorePage = response.body();
                    kcpCategoryRoot.setFingerPrintCategoriesList(kcpCorePage.getKcpEmbedded().getCategoryList());
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public void downloadCategories(){
        Call<KcpContentPage> call = getKcpService().getContentPage(Constants.URL_CATEGORIES, Constants.QUERY_PAGE, Constants.QUERY_PER_PAGE);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpContentPage kcpCorePage = response.body();
                    ArrayList<KcpCategories> categoriesList = kcpCorePage.getKcpEmbedded().getCategoryList();
                    kcpCategoryRoot.setCategoriesList(categoriesList);
                    handleState(DOWNLOAD_COMPLETE);

                    for(KcpCategories kcpCategories : categoriesList) {
                        String externalCode = kcpCategories.getExternalCode();
                        String subCategoriesUrl = kcpCategories.getSubCategoriesLink();
                        downloadSubCategories(externalCode, subCategoriesUrl, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    /**
     *
     * @param externalCode
     * @param url
     * @param callbackExist when preparing the sub categories, callback doesn't exist.
     */
    public void downloadSubCategories(final String externalCode, String url, final boolean callbackExist){
        if(callbackExist) handleState(DOWNLOAD_STARTED);
        Call<KcpContentPage> call = getKcpService().getCorePage(url);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpContentPage kcpCorePage = response.body();
                    kcpCategoryRoot.setSubCategoriesMap(externalCode, kcpCorePage.getKcpEmbedded().getCategoryList());
                    if(callbackExist) handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public void downloadPlacesForThisCategoryExternalId(final String externalCode, String url){
        handleState(DOWNLOAD_STARTED);
        Call<KcpContentPage> call = getKcpService().getCorePage(url);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpContentPage kcpCorePage = response.body();
                    kcpCategoryRoot.setPlacesMap(externalCode, kcpCorePage.getKcpEmbedded().getPlaceList(), true);
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public void downloadPlacesForTheseCategoryIds(ArrayList categoryIds){
        String categories = Utility.convertArrayListToStringWithComma(categoryIds);
        Call<KcpContentPage> call = getKcpService().getPlacesWithCategories(Constants.URL_PLACES, Constants.QUERY_PAGE, Constants.QUERY_PER_PAGE, categories);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    KcpCategoryRoot kcpCategoryRoot = KcpCategoryRoot.getInstance();
                    KcpContentPage kcpCorePage = response.body();
                    kcpCategoryRoot.setPlacesList(kcpCorePage.getKcpPlaces(KcpPlaces.PLACE_TYPE_STORE), true);
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

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
        Call<KcpContentPage> call = getKcpService().postInterestedStores(Constants.URL_POST_INTERESTED_STORES, multiLike.multiLikeBody);

        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    if(response.body().status.equals(RESPONSE_STATUS_COMPLETE)) handleState(DOWNLOAD_COMPLETE);
                } else {
                    handleState(DOWNLOAD_FAILED);
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
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
                ProgressBarWhileDownloading.showProgressDialog(mContext, true);
                break;
            case DOWNLOAD_FAILED:
                ProgressBarWhileDownloading.showProgressDialog(mContext, false);
                break;
            case DOWNLOAD_COMPLETE:
                ProgressBarWhileDownloading.showProgressDialog(mContext, false);
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

}
