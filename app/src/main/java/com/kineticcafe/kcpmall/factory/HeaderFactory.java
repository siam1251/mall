package com.kineticcafe.kcpmall.factory;

import com.kineticcafe.kcpmall.activities.Constants;

import java.util.HashMap;

/**
 * Created by Kay on 2016-05-13.
 */
public class HeaderFactory {
    private static final String HEADER_KEY_DATAHUB_CATALOG = "Datahub-Catalog";
    private static final String HEADER_KEY_DATAHUB_LOCALE = "Datahub-Locale";
    private static final String HEADER_KEY_CLIENT_TOKEN = "Client-Token";
    private static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_KEY_ACCEPT = "Accept";

    private static HashMap<String, String> mHeaders;
    public static HashMap<String, String> getHeaders(){
        if(mHeaders == null) {
            constructHeader();
        }
        return mHeaders;
    }

    public static void constructHeader(){
        mHeaders = new HashMap<String, String>();

        mHeaders.put(HEADER_KEY_DATAHUB_CATALOG, Constants.HEADER_VALUE_DATAHUB_CATALOG);
        mHeaders.put(HEADER_KEY_DATAHUB_LOCALE, Constants.HEADER_VALUE_DATAHUB_LOCALE);
        mHeaders.put(HEADER_KEY_CLIENT_TOKEN, Constants.HEADER_VALUE_CLIENT_TOKEN);
        //below two headers are specially needed for view_all_content
        mHeaders.put(HEADER_KEY_CONTENT_TYPE, Constants.HEADER_VALUE_CONTENT_TYPE);
        mHeaders.put(HEADER_KEY_ACCEPT, Constants.HEADER_VALUE_ACCEPT);
    }
}
