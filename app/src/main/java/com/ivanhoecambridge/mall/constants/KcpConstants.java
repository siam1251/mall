package com.ivanhoecambridge.mall.constants;

/**
 * Created by Kay on 2016-05-12.
 */
class KcpConstants {

    //DATE FORMAT
    public final static String EFFECTIVE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public final static String OVERRIDE_HOUR_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"; //Z = -04:00, z = EDT, SSSZ = 000-04:00

    //MALL SPECIFIC
//	public final static String URL_BASE_DEVELOPMENT = 							"https://dit-kcp.kineticcafetech.com/"; //development
    private final static String URL_BASE_STAGING = 							"https://staging-kcp.kineticcafetech.com/"; //staging
    private final static String URL_BASE_PRODUCTION = 							"https://icmp.kineticcommerce.com/"; //production
//    public final static String URL_BASE_BETA = 							"https://beta-kcp.kineticcafetech.com/";

    public final static String URL_NAVIGATION_ROOT = 				"core/navigation_roots";
    public final static String URL_CATEGORIES = 					"core/categories/roots";
    public final static String URL_FINGERPRINTING_CATEGORIES = 		"core/categories";
    public final static String URL_PLACES = 						"core/places";
    public final static String URL_POST_INTERESTED_STORES = 		"core/user/multi_like";
    public final static String URL_VIEW_ALL_CONTENT =				"/view_all_content";
    public final static String QUERY_PAGE = 						"1";
    public final static String QUERY_PER_PAGE = 					"500";

    public final static String URL_POST_CREATE_TOKEN = 		        "a/token";
    public final static String URL_POST_ADD_CREDENTIAL = 		    "a/user/credentials";
    public final static String URL_POST_CREATE_USER = 		        "a/users";


    //SHARED PREFERNCE DATABASE
    public final static String PREFS_KEY_FAV_STORE_LIKE_LINK = 	"prefs_key_store_like_link";

    public static String getBaseURL(boolean isAppInProduction){
        if(isAppInProduction) return URL_BASE_PRODUCTION;
        else return URL_BASE_STAGING;
    }
}
