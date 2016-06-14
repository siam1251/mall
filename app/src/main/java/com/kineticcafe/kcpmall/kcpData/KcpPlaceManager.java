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
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
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
public class KcpPlaceManager {

    private Context mContext;
    private KcpService mKcpService;
    protected Logger logger = null;
    private Handler mHandler;

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;

    public KcpPlaceManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        logger = new Logger(getClass().getName());
    }

    public KcpService getKcpService(){
        if(mKcpService == null) mKcpService = ServiceFactory.createRetrofitService(mContext, new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        return mKcpService;
    }

    public void downloadPlaces(){
        Call<KcpContentPage> call = getKcpService().getContentPage(Constants.URL_PLACES, Constants.QUERY_PAGE, Constants.QUERY_PER_PAGE);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if(response.isSuccessful()){
                    KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
                    KcpContentPage kcpCorePage = response.body();
                    kcpPlacesRoot.setPlacesList(kcpCorePage.getKcpPlaces(KcpPlaces.PLACE_TYPE_STORE), true);
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public void downloadPlace(int placeId){
        Call<KcpPlaces> call = getKcpService().getPlaceWithId(placeId);
        call.enqueue(new Callback<KcpPlaces>() {
            @Override
            public void onResponse(Call<KcpPlaces> call, Response<KcpPlaces> response) {
                if(response.isSuccessful()){
                    KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
                    KcpPlaces kcpPlace = response.body();
                    kcpPlacesRoot.setPlace(kcpPlace);
                    handleState(DOWNLOAD_COMPLETE);
                }
            }

            @Override
            public void onFailure(Call<KcpPlaces> call, Throwable t) {
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
