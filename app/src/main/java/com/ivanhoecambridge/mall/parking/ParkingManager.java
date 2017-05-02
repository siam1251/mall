package com.ivanhoecambridge.mall.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.service.ServiceFactory;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import factory.HeaderFactory;

import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.managers.KcpNotificationManager;
import com.ivanhoecambridge.mall.mappedin.Amenities;
import com.ivanhoecambridge.mall.mappedin.MapUtility;
import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Map;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;

import static com.ivanhoecambridge.mall.managers.KcpNotificationManager.NOTIFICATION_ID_WELCOME;

/**
 * Created by Kay on 2016-07-25.
 */
public class ParkingManager {

    public enum ParkingMode { NONE, LOCATION, COORDINATE };
    private final String RESPONSE_STATUS_COMPLETE = "complete";
    private ProgressDialog pd;

    public static String KEY_PARKING_LOT_POSITION= "key_parking_lot_position";
    public static String KEY_ENTRANCE_POSITION = "key_entrance_position";
    public static String KEY_PARKING_LOT_COORDINATE = "key_parking_lot_coordinate";
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


    public static class ParkingSpot{

        double x;
        double y;
        double elevation;

        public ParkingSpot(double x, double y, double elevation) {
            this.x = x;
            this.y = y;
            this.elevation = elevation;
        }

        public Coordinate getCoordinate(Map[] maps){
            if(maps == null) return null;
            android.location.Location targetLocation = MapUtility.getLocation(x, y);
            int index = MapUtility.getIndexWithMapElevation(maps, elevation);
            Coordinate coordinate  = new Coordinate(targetLocation, maps[index]);
            return coordinate;
        }
    }

    public ParkingManager(Context context, int loadingLayout, Handler handler) {
        mContext = context;
        mHandler = handler;
        mLoadingLayout = loadingLayout;
        logger = new Logger(getClass().getName());
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
        clearParkingSpotCoordinate(context);
        KcpNotificationManager.cancelNotification(context, NOTIFICATION_ID_WELCOME);
    }

    public static void saveParkingSpotAndEntrance(final Context context, String parkingId){
        int[] parkingPostion = new int[2];
        ParkingManager.sParkings.getParkingPositionById(parkingId, parkingPostion);
        KcpUtility.cacheToPreferences(context, KEY_PARKING_LOT_POSITION, parkingPostion == null ? 0 : parkingPostion[0]);
        KcpUtility.cacheToPreferences(context, KEY_ENTRANCE_POSITION, parkingPostion == null ? 0 : parkingPostion[1]);
        Amenities.saveToggle(context, Amenities.GSON_KEY_PARKING, true);
        clearParkingSpotCoordinate(context);
        KcpNotificationManager.cancelNotification(context, NOTIFICATION_ID_WELCOME);
    }

    public static void saveParkingSpotAndEntrance(final Context context, double latitude, double longitude, double elevation){
        KcpUtility.cacheToPreferences(context, KEY_PARKING_LOT_POSITION, -1);
        KcpUtility.cacheToPreferences(context, KEY_ENTRANCE_POSITION, -1);
        ParkingSpot parkingSpot = new ParkingSpot(latitude, longitude, elevation);
        KcpUtility.saveGson(context, KEY_PARKING_LOT_COORDINATE, parkingSpot);
        Amenities.saveToggle(context, Amenities.GSON_KEY_PARKING, true);
        KcpNotificationManager.cancelNotification(context, NOTIFICATION_ID_WELCOME);
    }

    public static void clearParkingSpotCoordinate(final Context context){
        KcpUtility.saveGson(context, KEY_PARKING_LOT_COORDINATE, null);
    }


    public static ParkingSpot getSavedParkingSpot(final Context context){
        if(context == null) return null;
        Gson gson = new Gson();
        String json = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(KEY_PARKING_LOT_COORDINATE, "");
        if(json.equals("")) return null;
        Type listType = new TypeToken<ParkingSpot>() {}.getType();
        ParkingSpot obj = gson.fromJson(json, listType);
        return obj;
    }


    public static int getSavedParkingLotPosition(final Context context){
        return KcpUtility.loadIntFromCache(context, KEY_PARKING_LOT_POSITION, -1);
    }

    public static int getSavedEntrancePosition(final Context context){
        return KcpUtility.loadIntFromCache(context, KEY_ENTRANCE_POSITION, -1);
    }

    public static boolean isParkingLotSaved(final Context context){
        if(getSavedParkingSpot(context) != null || KcpUtility.loadIntFromCache(context, KEY_PARKING_LOT_POSITION, -1) != -1) return true;
        else return false;
    }

    public static Parking getMyParkingLot(final Context context){
        int parkingLotPosition = ParkingManager.getSavedParkingLotPosition(context);
        if(parkingLotPosition == -1) return null;
        return ParkingManager.sParkings.getParkings().get(parkingLotPosition);
    }

    public static ChildParking getMyEntrance(final Context context){
        int entrancePosition = ParkingManager.getSavedEntrancePosition(context);
        if(entrancePosition == -1) return null;
        return getMyParkingLot(context).getChildParkings().get(entrancePosition);
    }

    public static void removeParkingLot(final Context context){
        clearParkingSpotCoordinate(context);
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
                break;
            case DOWNLOAD_FAILED:
                break;
            case DOWNLOAD_COMPLETE:
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

    public static ParkingMode getParkingMode(Context context){
        if(!isParkingLotSaved(context)) return ParkingMode.NONE;
        else {
            if(KcpUtility.loadIntFromCache(context, KEY_PARKING_LOT_POSITION, -1) != -1) return ParkingMode.LOCATION;
            else return ParkingMode.COORDINATE;
        }
    }
}
