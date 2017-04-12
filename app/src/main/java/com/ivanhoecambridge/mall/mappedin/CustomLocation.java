package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.sdk.ImageSet;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.Polygon;
import com.mappedin.sdk.Utils;
import com.mappedin.sdk.Venue;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by paul on 2016-06-14.
 */
public class CustomLocation extends Location {

    public String description;
    public ImageSet logo;
    private String externalID;
    private String id;
    private String amenityType;

    private final String TYPE_AMENITIES = "amenities";
    private final String TYPE_AMENITY = "amenity";
    private final String TYPE = "type";
    private final String TYPE_EXTERNAL_ID = "externalId";
    private final String TYPE_ID = "id";
    public static final String TYPE_AMENITY_PARKING = "parking";


    private final String TYPE_ELEVATOR = "elevator";
    private final String TYPE_ESCALATOR_STAIRS = "escalator/stairs";


    private static HashMap<String, ArrayList<CustomLocation>> amenityHashmap = new HashMap<>();
    private static HashMap<String, CustomLocation> locationHashmapByExternalId = new HashMap<>(); //used to find polygons for stores - that use external iD
    private static HashMap<String, CustomLocation> parkingHashMap = new HashMap<>();


    public CustomLocation(ByteBuffer data, int _index, Venue venue) {
        super(data, _index, venue);

        id = Utils.encodingString(data);
        externalID = Utils.encodingString(data);
        amenityType = Utils.encodingString(data);
        description = Utils.encodingString(data);
        logo = Utils.encodingImageSet(data);

        if(externalID != null) {
            locationHashmapByExternalId.put(externalID, this);
        }

        if(amenityType != null) {
            ArrayList<CustomLocation> amenityList;

            if(amenityHashmap.containsKey(amenityType)) {

                amenityList = amenityHashmap.get(amenityType);
            }
            else {

                amenityList = new ArrayList<>();
                amenityList.add(this);
                amenityHashmap.put(amenityType, amenityList);
                if (amenityType != null && amenityType.equals(TYPE_AMENITY_PARKING)) {
                    if (id != null) {
                        parkingHashMap.put(id, this);
                    }
                }
            }
        }

        //2016. 12. 8 - metropolis has pins like 'escalator/stairs' and 'elevator' and they dont' have 'amenity' as their type
        if(amenityType != null && (amenityType.equals(TYPE_ELEVATOR) || amenityType.equals(TYPE_ESCALATOR_STAIRS))){

            ArrayList<CustomLocation> amenityList;

            if(amenityHashmap.containsKey(amenityType)) {

                amenityList = amenityHashmap.get(amenityType);
            }
            else {
                amenityList = new ArrayList<>();
            }

            amenityList.add(this);
            amenityHashmap.put(amenityType, amenityList);
        }

        if(amenityType != null && amenityType.equals(TYPE_AMENITY_PARKING)){
            if(id != null) {
                parkingHashMap.put(id, this);
            }
        }
    }

//    public CustomLocation(LocationData rawData) throws Exception {
//        super(rawData);
//        try {
//            description = rawData.string("description");
//            logo = rawData.imageCollection("logo");
//
//            //todo: disabled for testing
//            if(rawData.string(TYPE_EXTERNAL_ID) != null) {
//                externalID = rawData.stringForce(TYPE_EXTERNAL_ID);
//                locationHashmapByExternalId.put(externalID, this);
//            }
//
//            if(rawData.string(TYPE_ID) != null) {
//                id = rawData.stringForce(TYPE_ID);
//            }
//
//            //2016. 11. 10 - https://api.mappedin.com/1/location?venue=vaughan-mills doesn't give 'parking' and i don't know where the 'parking' data comes from for vaughan mills
//            //wherever 'parking' data comes for VM, it receives this data from type: "amenity", amenity: "parking"
//            //whwereas for Tsawwassen, the parking data comes from type: "amenity", amenities: "parking"
//
//            amenityType = rawData.string(TYPE_AMENITY);
//            if(rawData.string(TYPE) != null && rawData.string(TYPE).equals(TYPE_AMENITIES)) {
//                ArrayList<CustomLocation> amenityList;
//                if(amenityHashmap.containsKey(amenityType)) amenityList = amenityHashmap.get(amenityType);
//                else amenityList = new ArrayList<>();
//                amenityList.add(this);
//                amenityHashmap.put(amenityType, amenityList);
//                if(amenityType != null && amenityType.equals(TYPE_AMENITY_PARKING)){
//                    if(id != null) {
//                        parkingHashMap.put(id, this);
//                    }
//                }
//            }
//
//            //2016. 12. 8 - metropolis has pins like 'escalator/stairs' and 'elevator' and they dont' have 'amenity' as their type
//            if(rawData.string(TYPE) != null && (rawData.string(TYPE).equals(TYPE_ELEVATOR) || rawData.string(TYPE).equals(TYPE_ESCALATOR_STAIRS))){
//
//                amenityType = rawData.string(TYPE);
//                ArrayList<CustomLocation> amenityList;
//                if(amenityHashmap.containsKey(amenityType)) amenityList = amenityHashmap.get(amenityType);
//                else amenityList = new ArrayList<>();
//                amenityList.add(this);
//                amenityHashmap.put(amenityType, amenityList);
//            }
//
//
//            if(amenityType != null && amenityType.equals(TYPE_AMENITY_PARKING)){
//                if(id != null) {
//                    parkingHashMap.put(id, this);
//                }
//            }
//
//        //2016. 11. 10 - noticed that parking polygons for Tsawwassen's under type: "amenity", amenity: "parking" so rawData.string(TYPE).equals(TYPE_AMENITIES) gives "amenity" NOT "amenities"
//
//        } catch (Exception var3) {
//            Logger.log("create location failed");
//        }
//    }

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

        if(locationHashmapByExternalId.containsKey(externalCode)) {
            return locationHashmapByExternalId.get(externalCode);
        }

        return null;
    }

    public static ArrayList<Polygon> getPolygonsFromLocationWithExternalCode(String externalCode){

        if(locationHashmapByExternalId.containsKey(externalCode)){
            ArrayList<Polygon> polygons = new ArrayList<>();

            if (locationHashmapByExternalId.get(externalCode).getPolygons() != null) {
                polygons = new ArrayList<Polygon>(Arrays.asList(locationHashmapByExternalId.get(externalCode).getPolygons()));
            }

            return polygons;
       }

       return null;
    }

    public static Location getParkingLocationWithId(String id){
        if(parkingHashMap.containsKey(id)) return parkingHashMap.get(id);
        return null;
    }

    public static ArrayList<Polygon> getParkingPolygonsFromLocationWithId(String id){

        if(parkingHashMap.containsKey(id)) {

            ArrayList<Polygon> polygons = new ArrayList<>();

            if (parkingHashMap.get(id).getPolygons() != null) {
                polygons = new ArrayList<Polygon>(Arrays.asList(parkingHashMap.get(id).getPolygons()));
            }

            return polygons;
        }

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
