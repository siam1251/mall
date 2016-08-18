package com.kineticcafe.kcpmall.mappedin;

import android.util.Log;

import com.mappedin.jpct.Logger;
import com.mappedin.sdk.ImageCollection;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.RawData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by paul on 2016-06-14.
 */
public class CustomLocation extends Location {
    public String description;
    public ImageCollection logo;
    private String externalID;
    private String id;
    private String amenityType;

    private final String TYPE_AMENITIES = "amenities";
    private final String TYPE_AMENITY = "amenity";
    private final String TYPE = "type";
    private final String TYPE_EXTERNAL_ID = "externalId";
    private final String TYPE_ID = "id";
    public static final String TYPE_AMENITY_PARKING = "parking";


    private static HashMap<String, ArrayList<CustomLocation>> amenityHashmap = new HashMap<>();
    private static HashMap<String, CustomLocation> locationHashmapByExternalId = new HashMap<>();
    private static HashMap<String, CustomLocation> locationHashmapById = new HashMap<>();
    private static HashMap<String, CustomLocation> parkingHashMap = new HashMap<>();

    public CustomLocation(RawData rawData) throws Exception {
        super(rawData);
        try {
            description = rawData.string("description");
            logo = rawData.imageCollection("logo");

            //todo: disabled for testing
            if(rawData.string(TYPE_EXTERNAL_ID) != null) {
                externalID = rawData.stringForce(TYPE_EXTERNAL_ID);
                locationHashmapByExternalId.put(externalID, this);
            }

            if(rawData.string(TYPE_ID) != null) {
                id = rawData.stringForce(TYPE_ID);
            }

            if(rawData.string(TYPE) != null && rawData.string(TYPE).equals(TYPE_AMENITIES)) {
                amenityType = rawData.string(TYPE_AMENITY);
                if(amenityType.equals("atm")){
                    String a = "ef";
                    Log.d("ATM", "FOUND!");
                }

                ArrayList<CustomLocation> amenityList;
                if(amenityHashmap.containsKey(amenityType)) amenityList = amenityHashmap.get(amenityType);
                else amenityList = new ArrayList<>();
                amenityList.add(this);
                amenityHashmap.put(amenityType, amenityList);

                if(amenityType.equals("parking")){
                    String a = "ef";
                    Log.d("parking", "FOUND!");

                    if(id != null) {
                        parkingHashMap.put(id, this);
                    }
                }
            }

        } catch (Exception var3) {
            Logger.log("create location failed");
        }
    }

    public static HashMap<String, ArrayList<CustomLocation>> getAmenityHashMap() {
        return amenityHashmap;
    }

    public static HashMap<String, CustomLocation> getLocationHashMap() {
        return locationHashmapByExternalId;
    }

    public static HashMap<String, CustomLocation> getParkingHashMap() {
        return parkingHashMap;
    }

    public String getExternalID() {
        if(externalID == null) return "-1";
        return externalID;
    }

    public String getAmenityType(){
        if(amenityType == null) return "";
        return amenityType;

    }
}
