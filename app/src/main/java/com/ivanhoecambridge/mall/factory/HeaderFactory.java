package com.ivanhoecambridge.mall.factory;

import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.user.AccountManager;

import java.util.HashMap;

/**
 * Created by Kay on 2016-05-13.
 */
public class HeaderFactory {
    private static final String HEADER_KEY_DATAHUB_CATALOG  = "Datahub-Catalog";
    private static final String HEADER_KEY_DATAHUB_LOCALE   = "Datahub-Locale";
    private static final String HEADER_KEY_CLIENT_TOKEN     = "Client-Token";
    private static final String HEADER_KEY_CONTENT_TYPE     = "Content-Type";
    private static final String HEADER_KEY_ACCEPT           = "Accept";
    private static final String HEADER_KEY_AUTHORIZATION    = "Authorization";

    private static final String TOKEN_PREFIX_BEARER    = "Bearer ";



    //------------------------------ END POINT ------------------------------
    private static String HEADER_VALUE_DATAHUB_CATALOG = Constants.HEADER_VALUE_DATAHUB_CATALOG_VM; //CHANGE THE END POINT to VM
//    private static String HEADER_VALUE_DATAHUB_CATALOG = Constants.HEADER_VALUE_DATAHUB_CATALOG_MP; //CHANGE THE END POINT to MP
    //------------------------------ END POINT ------------------------------


    public final static String MALL_INFO_OFFLINE_TEXT = "mallinfo.json";
    public final static String MALL_INFO_URL_BASE = "https://api.myjson.com/";
    private final static String MALL_INFO_URL_VM = "bins/1ouit"; //vaughan mills
    private final static String MALL_INFO_URL_MP = "bins/2zv9f"; //metropolis

    public final static String AMENITIES_OFFLINE_TEXT = "amenities.json";
    private final static String AMENITIES_URL_VM = "bins/3ihvo";
    private final static String AMENITIES_URL_MP = "bins/3ihvo";

    public final static String PARKING_OFFLINE_TEXT = "parking.json";
    private final static String PARKING_URL_VM = "bins/1c8ul";
    private final static String PARKING_URL_MP = "bins/1c8ul";


    public static String SEARCH_INDEX_URL_BASE = "https://kcp-pkg.s3-us-west-2.amazonaws.com/";
    private final static String SEARCH_INDEX_MP_STAGING = "indexes/staging/metropolis-at-metrotown-index.msgpack";
    private final static String SEARCH_INDEX_MP_PRODUCTION = "indexes/production/metropolis-at-metrotown-index.msgpack";
    private final static String SEARCH_INDEX_VM_STAGING = "indexes/staging/vaughan-mills-index.msgpack";
    private final static String SEARCH_INDEX_VM_PRODUCTION = "indexes/production/vaughan-mills-index.msgpack";


    public static String MALL_INFO_URL = MALL_INFO_URL_VM;
    public static String AMENITIES_URL = AMENITIES_URL_VM;
    public static String PARKING_URL = PARKING_URL_VM;
    private static String SEARCH_INDEX_URL = SEARCH_INDEX_VM_STAGING;


    //MOVIES
    public final static String MOVIE_URL_BASE = "http://ivanhoe.webservice.cinema-source.com/";


    public static String MALL_NAME = "Vaughan Mills";
    public static String MAP_VENUE_NAME = "Vaughan Mills";
//    public static String MALL_NAME = "Metropolis Metrotown"; //should go with HEADER_VALUE_DATAHUB_CATALOG initially
//    public static String MAP_VENUE_NAME = "Metropolis";

    private static HashMap<String, String> mHeaders;
    public static HashMap<String, String> getHeaders(){
        if(mHeaders == null) {
            constructHeader();
        }
        return mHeaders;
    }

    public static void changeCatalog(String catalog){
        HEADER_VALUE_DATAHUB_CATALOG = catalog;
        //TODO: used for google map place search but it's gotta be removed eventually once build variants are used
        if(catalog.equals(Constants.HEADER_VALUE_DATAHUB_CATALOG_VM)) {
            MALL_NAME = "Vaughan Mills";
            MALL_INFO_URL = MALL_INFO_URL_VM;
            AMENITIES_URL = AMENITIES_URL_VM;
            PARKING_URL = PARKING_URL_VM;
            MAP_VENUE_NAME = "Vaughan Mills";
            SEARCH_INDEX_URL = getSearchIndexUrl();
        } else if(catalog.equals(Constants.HEADER_VALUE_DATAHUB_CATALOG_MP)) {
            MALL_NAME = "Metropolis Metrotown";
            MALL_INFO_URL = MALL_INFO_URL_MP;
            AMENITIES_URL = AMENITIES_URL_MP;
            PARKING_URL = PARKING_URL_MP;
            MAP_VENUE_NAME = "Metropolis";
            SEARCH_INDEX_URL = getSearchIndexUrl();
        }

        constructHeader();
    }

    public static void constructHeader() {
        mHeaders = new HashMap<String, String>();

        mHeaders.put(HEADER_KEY_DATAHUB_CATALOG,    HEADER_VALUE_DATAHUB_CATALOG);
        mHeaders.put(HEADER_KEY_DATAHUB_LOCALE,     Constants.HEADER_VALUE_DATAHUB_LOCALE);
        mHeaders.put(HEADER_KEY_CLIENT_TOKEN,       getClientToken());
        mHeaders.put(HEADER_KEY_AUTHORIZATION,      getAuthorizationToken());

        //below two headers are specially needed for view_all_content
        mHeaders.put(HEADER_KEY_CONTENT_TYPE,       Constants.HEADER_VALUE_CONTENT_TYPE);
        mHeaders.put(HEADER_KEY_ACCEPT,             Constants.HEADER_VALUE_ACCEPT);
    }

    //TODO: implement save and get auth token here
    private static String getAuthorizationToken() {
        if(!AccountManager.mUserToken.equals("")) {
            return TOKEN_PREFIX_BEARER + AccountManager.mUserToken;
        }
        return AccountManager.mUserToken;
    }

    public static HashMap<String, String> getTokenHeader(){

        HashMap<String, String> headers = new HashMap<String, String>();

        headers.put(HEADER_KEY_DATAHUB_CATALOG,    HEADER_VALUE_DATAHUB_CATALOG);
        headers.put(HEADER_KEY_DATAHUB_LOCALE,     Constants.HEADER_VALUE_DATAHUB_LOCALE);
        headers.put(HEADER_KEY_CLIENT_TOKEN,       getClientToken());
        headers.put(HEADER_KEY_CONTENT_TYPE,       Constants.HEADER_VALUE_CONTENT_TYPE);
        headers.put(HEADER_KEY_ACCEPT,             Constants.HEADER_VALUE_ACCEPT);

        return headers;
    }

    public static String getClientToken(){
        if(Constants.IS_APP_IN_PRODUCTION) return Constants.HEADER_VALUE_CLIENT_TOKEN_PRODUCTION;
        else return Constants.HEADER_VALUE_CLIENT_TOKEN_STAGING;
    }

    public static String getSearchIndexUrl(){
        if(HEADER_VALUE_DATAHUB_CATALOG.equals(Constants.HEADER_VALUE_DATAHUB_CATALOG_VM)) {
            if(Constants.IS_APP_IN_PRODUCTION) return SEARCH_INDEX_VM_PRODUCTION;
            else return SEARCH_INDEX_VM_STAGING;
        } else if (HEADER_VALUE_DATAHUB_CATALOG.equals(Constants.HEADER_VALUE_DATAHUB_CATALOG_MP)){
            if(Constants.IS_APP_IN_PRODUCTION) return SEARCH_INDEX_MP_PRODUCTION;
            else return SEARCH_INDEX_MP_STAGING;
        } else return "";
    }
}
