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
    public static String HEADER_VALUE_DATAHUB_CATALOG = "metropolis-at-metrotown";
    //------------------------------ END POINT ------------------------------

    public static String MALL_NAME = "Metropolis at Metrotown";
    public static String MAP_VENUE_NAME = "Metropolis";
    private final static String SEARCH_INDEX_MP_STAGING = "indexes/staging/metropolis-at-metrotown-index.msgpack";
    private final static String SEARCH_INDEX_MP_PRODUCTION = "indexes/production/metropolis-at-metrotown-index.msgpack";

    public static String MALL_INFO_URL = "bins/tlfbz";
    public static String AMENITIES_URL = "bins/wzczr"; //externalIds for Stairs & Escalators were manually changed
    public static String PARKING_URL = "bins/2gidf";

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
        mHeaders = new HashMap<String, String>();

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
