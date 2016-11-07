package com.ivanhoecambridge.mall.mappedin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.service.ServiceFactory;
import com.ivanhoecambridge.mall.constants.Constants;

import factory.HeaderFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Kay on 2016-07-25.
 */
public class AmenitiesManager  {

    private final String RESPONSE_STATUS_COMPLETE = "complete";
    private ProgressDialog pd;

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    protected Handler mHandler;
    protected Logger logger = null;
    protected Context mContext;
    protected AmenityService mAmenityService;
    protected int mLoadingLayout;

    public static Amenities sAmenities = new Amenities();

    public AmenityService getKcpService(){
        ServiceFactory serviceFactory = new ServiceFactory();
        if(mAmenityService == null) mAmenityService = serviceFactory.createRetrofitService(mContext, AmenityService.class, Constants.MALL_INFO_URL_BASE);
        return mAmenityService;
    }

    public AmenitiesManager(Context context, int loadingLayout, Handler handler) {
        mContext = context;
        mHandler = handler;
        mLoadingLayout = loadingLayout;
        logger = new Logger(getClass().getName());
    }

    public void downloadAmenities(){
        Call<Amenities> call = getKcpService().getAmenities(HeaderFactory.AMENITIES_URL);
        call.enqueue(new Callback<Amenities>() {
            @Override
            public void onResponse(Call<Amenities> call, Response<Amenities> response) {
                if(response.isSuccessful()){
                    sAmenities = response.body();
                    handleState(DOWNLOAD_COMPLETE);
                } else handleState(DOWNLOAD_FAILED);
            }

            @Override
            public void onFailure(Call<Amenities> call, Throwable t) {
                logger.error(t);
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
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, true);
                break;
            case DOWNLOAD_FAILED:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, false);
                break;
            case DOWNLOAD_COMPLETE:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, false);
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

    public interface AmenityService {
        @GET
        Call<Amenities> getAmenities(@Url String url);
    }

}
