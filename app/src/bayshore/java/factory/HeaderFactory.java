package factory;


import com.ivanhoecambridge.mall.account.KcpAccount;
import com.ivanhoecambridge.mall.constants.Constants;
import java.util.HashMap;

import constants.MallConstants;

import static com.ivanhoecambridge.mall.constants.Constants.*;
import static constants.MallConstants.MALL_NAME_URL;


/**
 * Created by petar on 2017-09-22.
 */

public class HeaderFactory  {
    //------------------------------ END POINT ------------------------------
    public static String HEADER_VALUE_DATAHUB_CATALOG = MallConstants.MALL_NAME_URL;
    //------------------------------ END POINT ------------------------------

    public static String MALL_NAME = MallConstants.MALL_NAME;
    public static String MAP_VENUE_NAME = MallConstants.MAP_VENUE_NAME;
    private final static String SEARCH_INDEX_MP_STAGING = String.format("indexes/staging/%s-index.msgpack", MALL_NAME_URL);
    private final static String SEARCH_INDEX_MP_PRODUCTION = String.format("indexes/production/%s-index.msgpack", MALL_NAME_URL);

    public final static String HEADER_VALUE_ACCEPT = "application/json, application/vnd.kcp.place+json; version=1.0, application/vnd.kcp.view-all-content+json; version=1.0, application/vnd.kcp.icmp-deal+json; version=1.0, application/vnd.kcp.icmp-event+json; version=1.0, application/vnd.kcp.icmp-announcement+json; version=1.0, application/vnd.kcp.blog-post+json; version=1.0, application/vnd.kcp.icmp-twitter+json; version=1.0, application/vnd.kcp.icmp-instagram+json; version=1.0, application/vnd.kcp.icmp-set-interests+json; version=1.0, application/vnd.kcp.icmp-movie+json; version=1.0";

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
        mHeaders = new HashMap<>();

        mHeaders.put(HEADER_KEY_DATAHUB_CATALOG,    HEADER_VALUE_DATAHUB_CATALOG);
        mHeaders.put(HEADER_KEY_DATAHUB_LOCALE,     Constants.HEADER_VALUE_DATAHUB_LOCALE);
        mHeaders.put(HEADER_KEY_CLIENT_TOKEN,       getClientToken());
        mHeaders.put(HEADER_KEY_AUTHORIZATION,      KcpAccount.getInstance().getUserTokenWithBearer());

        //below two headers are specially needed for view_all_content
        mHeaders.put(HEADER_KEY_CONTENT_TYPE,       Constants.HEADER_VALUE_CONTENT_TYPE);
        mHeaders.put(HEADER_KEY_ACCEPT,             HEADER_VALUE_ACCEPT);
    }

    public static String getClientToken(){
        if(Constants.IS_APP_IN_PRODUCTION) return Constants.HEADER_VALUE_CLIENT_TOKEN_PRODUCTION;
        else return Constants.HEADER_VALUE_CLIENT_TOKEN_STAGING;
    }

    public static String getSearchIndexUrl(){
        if(Constants.IS_APP_IN_PRODUCTION) return SEARCH_INDEX_MP_PRODUCTION;
        else return SEARCH_INDEX_MP_STAGING;
    }
}
