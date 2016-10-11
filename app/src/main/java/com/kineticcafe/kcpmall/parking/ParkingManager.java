package com.kineticcafe.kcpmall.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.mappedin.Amenities;
import com.kineticcafe.kcpmall.utility.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Kay on 2016-07-25.
 */
public class ParkingManager {

    private final String RESPONSE_STATUS_COMPLETE = "complete";
    private ProgressDialog pd;

    public static String KEY_PARKING_LOT_POSITION= "key_parking_lot_position";
    public static String KEY_ENTRANCE_POSITION = "key_entrance_position";
    public static String KEY_PARKING_NOTES = "key_parking_notes";


    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    protected Handler mHandler;
    protected Logger logger = null;
    protected Context mContext;
    protected ParkingService mParkingService;
    protected int mLoadingLayout;

    public static Parkings sParkings = new Parkings();

    public ParkingService getKcpService(){
        ServiceFactory serviceFactory = new ServiceFactory();
        if(mParkingService == null) mParkingService = serviceFactory.createRetrofitService(mContext, ParkingService.class, HeaderFactory.MALL_INFO_URL_BASE);
        return mParkingService;
    }

    public ParkingManager(Context context, int loadingLayout, Handler handler) {
        mContext = context;
        mHandler = handler;
        mLoadingLayout = loadingLayout;
        logger = new Logger(getClass().getName());
    }

    public void downloadParkings(){
        Call<Parkings> call = getKcpService().getParkings(HeaderFactory.PARKING_URL);
        call.enqueue(new Callback<Parkings>() {
            @Override
            public void onResponse(Call<Parkings> call, Response<Parkings> response) {
                if(response.isSuccessful()){
                    sParkings = response.body();
                    handleState(DOWNLOAD_COMPLETE);
                } else handleState(DOWNLOAD_FAILED);
            }

            @Override
            public void onFailure(Call<Parkings> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public interface ParkingService {
        @GET
        Call<Parkings> getParkings(@Url String url);
    }


    protected static void saveParkingNotes(final Context context, String note){
        KcpUtility.cacheToPreferences(context, KEY_PARKING_NOTES, note);
    }

    public static String getParkingNotes(final Context context){
        return KcpUtility.loadFromCache(context, KEY_PARKING_NOTES, "");
    }

    public static void saveParkingSpotAndEntrance(final Context context, String note, int parkingLotPosition, int entrancePosition){
        KcpUtility.cacheToPreferences(context, KEY_PARKING_LOT_POSITION, parkingLotPosition);
        KcpUtility.cacheToPreferences(context, KEY_ENTRANCE_POSITION, entrancePosition);
        saveParkingNotes(context, note);
        Amenities.saveToggle(context, Amenities.GSON_KEY_PARKING, true);
    }

    public static int getSavedParkingLotPosition(final Context context){
        return KcpUtility.loadIntFromCache(context, KEY_PARKING_LOT_POSITION, -1);
    }

    public static int getSavedEntrancePosition(final Context context){
        return KcpUtility.loadIntFromCache(context, KEY_ENTRANCE_POSITION, -1);
    }

    public static boolean isParkingLotSaved(final Context context){
        if(KcpUtility.loadIntFromCache(context, KEY_PARKING_LOT_POSITION, -1) != -1) return true;
        else return false;
    }

    public static Parking getMyParkingLot(final Context context){
        int parkingLotPosition = ParkingManager.getSavedParkingLotPosition(context);
        return ParkingManager.sParkings.getParkings().get(parkingLotPosition);
    }

    public static ChildParking getMyEntrance(final Context context){
        int entrancePosition = ParkingManager.getSavedEntrancePosition(context);
        return getMyParkingLot(context).getChildParkings().get(entrancePosition);
    }

    public static void removeParkingLot(final Context context){
        KcpUtility.cacheToPreferences(context, KEY_PARKING_LOT_POSITION, -1);
        KcpUtility.cacheToPreferences(context, KEY_ENTRANCE_POSITION, -1);
        saveParkingNotes(context, "");
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

}
