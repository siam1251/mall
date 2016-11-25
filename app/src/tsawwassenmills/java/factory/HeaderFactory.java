package factory;

import com.ivanhoecambridge.mall.account.KcpAccount;
import com.ivanhoecambridge.mall.constants.Constants;

import java.util.HashMap;

import static com.ivanhoecambridge.mall.constants.Constants.HEADER_KEY_ACCEPT;
import static com.ivanhoecambridge.mall.constants.Constants.HEADER_KEY_AUTHORIZATION;
import static com.ivanhoecambridge.mall.constants.Constants.HEADER_KEY_CLIENT_TOKEN;
import static com.ivanhoecambridge.mall.constants.Constants.HEADER_KEY_CONTENT_TYPE;
import static com.ivanhoecambridge.mall.constants.Constants.HEADER_KEY_DATAHUB_CATALOG;
import static com.ivanhoecambridge.mall.constants.Constants.HEADER_KEY_DATAHUB_LOCALE;

/**
 * Created by Kay on 2016-05-13.
 */
public class HeaderFactory {

    //------------------------------ END POINT ------------------------------
    public static String HEADER_VALUE_DATAHUB_CATALOG = "tsawwassen-mills";
    //------------------------------ END POINT ------------------------------

    public static String MALL_NAME = "Tsawwassen Mills";
    public static String MAP_VENUE_NAME = "Tsawwassen Mills";
    private final static String SEARCH_INDEX_VM_STAGING = "indexes/staging/tsawwassen-mills-index.msgpack";
    private final static String SEARCH_INDEX_VM_PRODUCTION = "indexes/production/tsawwassen-mills-index.msgpack";

    public static String MALL_INFO_URL = "bins/2iu4o";
    public static String AMENITIES_URL = "bins/5bea9";
    public static String PARKING_URL = "bins/141i4";

    private static HashMap<String, String> mHeaders;
    public static HashMap<String, String> getHeaders(){
        if(mHeaders == null) {
            constructHeader();
        }
        return mHeaders;
    }

    public static void changeCatalog(String catalog){
        HEADER_VALUE_DATAHUB_CATALOG = catalog;
        constructHeader();
    }

    public static void constructHeader() {
        mHeaders = new HashMap<String, String>();

        mHeaders.put(HEADER_KEY_DATAHUB_CATALOG,    HEADER_VALUE_DATAHUB_CATALOG);
        mHeaders.put(HEADER_KEY_DATAHUB_LOCALE,     Constants.HEADER_VALUE_DATAHUB_LOCALE);
        mHeaders.put(HEADER_KEY_CLIENT_TOKEN,       getClientToken());
        mHeaders.put(HEADER_KEY_AUTHORIZATION,      KcpAccount.getInstance().getUserTokenWithBearer());

        //below two headers are specially needed for view_all_content
        mHeaders.put(HEADER_KEY_CONTENT_TYPE,       Constants.HEADER_VALUE_CONTENT_TYPE);
        mHeaders.put(HEADER_KEY_ACCEPT,             Constants.HEADER_VALUE_ACCEPT);
    }

    public static String getClientToken(){
        if(Constants.IS_APP_IN_PRODUCTION) return Constants.HEADER_VALUE_CLIENT_TOKEN_PRODUCTION;
        else return Constants.HEADER_VALUE_CLIENT_TOKEN_STAGING;
    }

    public static String getSearchIndexUrl(){
        if(Constants.IS_APP_IN_PRODUCTION) return SEARCH_INDEX_VM_PRODUCTION;
        else return SEARCH_INDEX_VM_STAGING;
    }
}
