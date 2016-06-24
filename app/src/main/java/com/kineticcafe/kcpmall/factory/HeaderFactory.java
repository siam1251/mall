package com.kineticcafe.kcpmall.factory;

import com.kineticcafe.kcpmall.activities.Constants;

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

    private static String HEADER_VALUE_DATAHUB_CATALOG = Constants.HEADER_VALUE_DATAHUB_CATALOG_VM;
    public final static String MALL_INFO_OFFLINE_TEXT = "mallinfo.json";
    public final static String MALL_INFO_URL_BASE = "https://api.myjson.com/";
    public final static String MALL_INFO_URL_VM = "bins/3ng0z"; //vaughan mills
    public final static String MALL_INFO_URL_MP = "bins/2zv9f"; //metropolis
    public static String MALL_INFO_URL = MALL_INFO_URL_VM; //metropolis


    public static String MALL_NAME = "Vaughan Mills";

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
        } else if(catalog.equals(Constants.HEADER_VALUE_DATAHUB_CATALOG_MP)) {
            MALL_NAME = "Metropolis Metrotown";
            MALL_INFO_URL = MALL_INFO_URL_MP;
        }

        constructHeader();
    }

    public static void constructHeader() {
        mHeaders = new HashMap<String, String>();

//        mHeaders.put(HEADER_KEY_DATAHUB_CATALOG,    Constants.HEADER_VALUE_DATAHUB_CATALOG);
        mHeaders.put(HEADER_KEY_DATAHUB_CATALOG,    HEADER_VALUE_DATAHUB_CATALOG);
        mHeaders.put(HEADER_KEY_DATAHUB_LOCALE,     Constants.HEADER_VALUE_DATAHUB_LOCALE);
        mHeaders.put(HEADER_KEY_CLIENT_TOKEN,       Constants.HEADER_VALUE_CLIENT_TOKEN);
        mHeaders.put(HEADER_KEY_AUTHORIZATION,      getAuthorizationToken());

        //below two headers are specially needed for view_all_content
        mHeaders.put(HEADER_KEY_CONTENT_TYPE,       Constants.HEADER_VALUE_CONTENT_TYPE);
        mHeaders.put(HEADER_KEY_ACCEPT,             Constants.HEADER_VALUE_ACCEPT);

    }

    //TODO: implement save and get auth token here
    private static String getAuthorizationToken() {
        return Constants.HEADER_VALUE_AUTHROZATION;
    }

}
