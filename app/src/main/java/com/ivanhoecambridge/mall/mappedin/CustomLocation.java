package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.jpct.Logger;
import com.mappedin.sdk.ImageCollection;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.Polygon;
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
    private static HashMap<String, CustomLocation> locationHashmapByExternalId = new HashMap<>(); //used to find polygons for stores - that use external iD
//    private static HashMap<String, CustomLocation> locationHashmapById = new HashMap<>(); //used to find polygons for parking lots - that use ID
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
//                locationHashmapById.put(id, this);
            }



            //2016. 11. 10 - https://api.mappedin.com/1/location?venue=vaughan-mills doesn't give 'parking' and i don't know where the 'parking' data comes from for vaughan mills
            //wherever 'parking' data comes for VM, it receives this data from type: "amenity", amenity: "parking"
            //whwereas for Tsawwassen, the parking data comes from type: "amenity", amenities: "parking"

            amenityType = rawData.string(TYPE_AMENITY);
            if(rawData.string(TYPE) != null && rawData.string(TYPE).equals(TYPE_AMENITIES)) {
                ArrayList<CustomLocation> amenityList;
                if(amenityHashmap.containsKey(amenityType)) amenityList = amenityHashmap.get(amenityType);
                else amenityList = new ArrayList<>();
                amenityList.add(this);
                amenityHashmap.put(amenityType, amenityList);
                if(amenityType != null && amenityType.equals(TYPE_AMENITY_PARKING)){
                    if(id != null) {
                        parkingHashMap.put(id, this);
                    }
                }
            }


            if(amenityType != null && amenityType.equals(TYPE_AMENITY_PARKING)){
                if(id != null) {
                    parkingHashMap.put(id, this);
                }
            }

        //2016. 11. 10 - noticed that parking polygons for Tsawwassen's under type: "amenity", amenity: "parking" so rawData.string(TYPE).equals(TYPE_AMENITIES) gives "amenity" NOT "amenities"



        } catch (Exception var3) {
            Logger.log("create location failed");
        }
    }

    public static HashMap<String, ArrayList<CustomLocation>> getAmenityHashMap() {
        return amenityHashmap;
    }

    public static CustomLocation getLocationWithLocationId(String locationId){
        try {
            for (ArrayList<CustomLocation> customLocations : amenityHashmap.values()) {
                for(CustomLocation customLocation : customLocations) {
                    if(customLocation.getId().equals(locationId)){
                        return customLocation;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static Location getLocationWithExternalCode(String externalCode){
        if(locationHashmapByExternalId.containsKey(externalCode)) return locationHashmapByExternalId.get(externalCode);
        return null;
    }

    public static ArrayList<Polygon> getPolygonsFromLocationWithExternalCode(String externalCode){
        if(locationHashmapByExternalId.containsKey(externalCode)) return locationHashmapByExternalId.get(externalCode).getPolygons();
        return null;
    }

    public static Location getParkingLocationWithId(String id){
        if(parkingHashMap.containsKey(id)) return parkingHashMap.get(id);
        return null;
    }

    public static ArrayList<Polygon> getParkingPolygonsFromLocationWithId(String id){
        if(parkingHashMap.containsKey(id)) return parkingHashMap.get(id).getPolygons();
        return null;
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

    public String getId() {
        if(id == null) return "";
        return this.id;
    }
}
