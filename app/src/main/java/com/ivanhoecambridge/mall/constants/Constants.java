package com.ivanhoecambridge.mall.constants;

public class Constants {



	public final static boolean IS_APP_IN_PRODUCTION = true;
//	public final static boolean IS_APP_IN_PRODUCTION = false;

	public final static int DURATION_MINIMUM_SPLASH_ANIMATION = 				1000;
//	public final static int DURATION_MINIMUM_SPLASH_ANIMATION = 				500;
	public final static int DURATION_SPLASH_ANIMATION = 						7000;
//	public final static int DURATION_SPLASH_ANIMATION = 						10000;
	public final static long DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION = 	100;
	public final static long DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION = 		100;
	public final static long DURATION_DETAIL_EXPIRY_DATE_TEXT = 				800;
	public final static long DURATION_MAIN_DRAWER_ACTIVE_MALL_DOT = 			1300;


	public enum DetailType { DEAL, STORE }
	public final static String KEY_BUNDLE_DEAL_TYPE = "key_bundle_deal_type";

	//HEADER
	public final static String HEADER_VALUE_DATAHUB_CATALOG_VM = 	"vaughan-mills";
	public final static String HEADER_VALUE_DATAHUB_CATALOG_MP = 	"metropolis-at-metrotown";
	public final static String HEADER_VALUE_DATAHUB_LOCALE = 		"en-CA";
	public final static String HEADER_VALUE_CLIENT_TOKEN_PRODUCTION = 	"ZmVmZTYxZGItNjhkYy00NjUyLTg0MGYtYzZlNGU2NTgxMTFhOmI3YTlkNjBlOTM0NTQ5OTdkZTUwZTdlNzQ4YjFmNTU5M2Y3ZjY3ZDRmNDA2YjkwOWNiZmUxZTdiZmZjMDlhMWRmNTY3YzVjNzAwMGYzNTMyYzU5NDc3NTJiYTViMGRiOTk2YWI4NTJlNjIwNWNlYTliM2QyOWE1MTA0N2E4M2Iw"; //PRODUCTION
	public final static String HEADER_VALUE_CLIENT_TOKEN_STAGING 	= 	"YmRkYWVjMmQtYjJiOS00YzdhLTllZDktMDg1NmI2M2E1NjA1OjM1ODg4M2M0OTk3YWM5ZTY3Y2EwNDMwMTE4M2RjODIzOTBlNWVhMmEyYTBkODIwOTFiZTY0MDE1NjZkYWY4YmQ0MTQzMzg4MzQ4Yzg0YzhjNzMwM2EzNzQ3YWEzOTJiNDYwYmJjMGIyMWYzZWM5OTIwNjIyODYyMDBmZDM5NjRm"; //STAGING
	public final static String HEADER_VALUE_CONTENT_TYPE = 		"application/json";
//	public final static String HEADER_VALUE_ACCEPT = 			"application/json";
	public final static String HEADER_VALUE_ACCEPT = 			"application/json, application/vnd.kcp.place+json; version=1.0, application/vnd.kcp.view-all-content+json; version=1.0, application/vnd.kcp.icmp-deal+json; version=1.0, application/vnd.kcp.icmp-event+json; version=1.0, application/vnd.kcp.icmp-announcement+json; version=1.0, application/vnd.kcp.blog-post+json; version=1.0, application/vnd.kcp.icmp-twitter+json; version=1.0, application/vnd.kcp.icmp-instagram+json; version=1.0, application/vnd.kcp.icmp-set-interests+json; version=1.0, application/vnd.kcp.icmp-movie+json; version=1.0";
	public final static String HEADER_VALUE_CONTENT_TYPE_MESSAGE_PACK = 		"application/octet-stream";


    //COMMON CONSTANTS AMONG THE MALLS
    public static final String HEADER_KEY_DATAHUB_CATALOG  = "Datahub-Catalog";
    public static final String HEADER_KEY_DATAHUB_LOCALE   = "Datahub-Locale";
    public static final String HEADER_KEY_CLIENT_TOKEN     = "Client-Token";
    public static final String HEADER_KEY_CONTENT_TYPE     = "Content-Type";
    public static final String HEADER_KEY_ACCEPT           = "Accept";
    public static final String HEADER_KEY_AUTHORIZATION    = "Authorization";

    public static String SEARCH_INDEX_URL_BASE = "https://kcp-pkg.s3-us-west-2.amazonaws.com/";
    public final static String MOVIE_URL_BASE = "http://ivanhoe.webservice.cinema-source.com/";

    public final static String MALL_INFO_OFFLINE_TEXT = "mallinfo.json";
    public final static String MALL_INFO_URL_BASE = "https://api.myjson.com/";
    public final static String AMENITIES_OFFLINE_TEXT = "amenities.json";
    public final static String PARKING_OFFLINE_TEXT = "parking.json";


	//DOWNLOAD TYPES
	public final static String EXTERNAL_CODE_DEAL = 		"deals-list";
	public final static String EXTERNAL_CODE_FEED = 		"feed";
	public final static String EXTERNAL_CODE_RECOMMENDED = 	"recommended-deals";
	public final static String EXTERNAL_CODE_SYSTEM_MSG = 	"system-message";
	public final static String EXTERNAL_CODE_CATEGORIES = 	"categories";
	public final static String EXTERNAL_CODE_PLACES = 		"places";


	//DATE FORMAT
 	public final static String DATE_FORMAT_EFFECTIVE = "EEEE, MMMM d";
 	public final static String DATE_FORMAT_HOLIDAY_DATE = "MMMM d";
 	public final static String DATE_FORMAT_HOLIDAY_HOUR = "MMMM d";
 	public final static String DATE_FORMAT_MALL_HOUR_DATE = "EEE. MMM d";
 	public final static String DATE_FORMAT_HOLIDAY = "EEEE MMMM d";
 	public final static String DATE_FORMAT_HOLIDAY_STORE = "EEE, MMMM d";
 	public final static String DATE_FORMAT_DAY = "EEE";
 	public final static String DATE_FORMAT_EVENT_HOUR = "h:mm aa";
 	public final static String DATE_FORMAT_ANNOUNCEMENT = "MMMM d, EEEE";


	//DETAIL
	public final static int DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE = 3; //if date left until (effective date) is equal or less than this number, show it

	//ARG_KEY
	public final static String ARG_CONTENT_PAGE = 			"arg_content_page";
	public final static String ARG_IMAGE_URL = 				"arg_image_url";
	public final static String ARG_IMAGE_BITMAP = 			"arg_image_bitmap";
	public static final String ARG_EXTERNAL_CODE = 			"external_code";
	public static final String ARG_CAT_NAME = 				"cat_name";
	public static final String ARG_CATEGORY_ACTIVITY_TYPE = "cat_activity_type";
	public static final String ARG_ACTIVITY_TYPE = 			"activity_type";
	public static final String ARG_MOVIE_ID = 				"movie_id";
	public enum CategoryActivityType { SUBCATEGORY, STORE }


	//REQUEST CODE
	public final static int REQUEST_CODE_CHANGE_INTEREST = 101;
	public final static int REQUEST_CODE_MY_PAGE_TYPE = 102;
	public final static int REQUEST_CODE_SAVE_PARKING_SPOT = 103;
	public final static int REQUEST_CODE_SHOW_PARKING_SPOT = 104;
	public final static int REQUEST_CODE_LOCATE_GUEST_SERVICE = 105;
	public final static int RESULT_DONE_PRESSED_WITH_CHANGE = 0;
	public final static int RESULT_DONE_PRESSED_WITHOUT_CHANGE = 1;
	public final static int RESULT_EXIT = 2;
	public final static int REQUEST_CODE_VIEW_STORE_ON_MAP = 543;
	public final static String REQUEST_CODE_KEY = "requestCode";
	public final static String REQUEST_CODE_KEY_PARKING_NAME = "parkingName";

	public final static int RESULT_DEALS = 1;
	public final static int RESULT_EVENTS = 2;
	public final static int RESULT_STORES = 3;

	//OTHERS
	public final static int NUMB_OF_DAYS = 7;
	public final static String PREF_KEY_WELCOME_MSG_TIME_SAVER = "welcome_message_time_saver";
	public final static String PREF_KEY_WELCOME_MSG_DID_APPEAR = "welcome_message_did_appear";
	public final static String PREF_KEY_ONBOARDING_DID_APPEAR = "onboarding_did_appear";


	public final static long DURATION_DAY = 1000*60*60*24;

	//PARKING
	public final static String KEY_GUEST_SERVICE = "information-counter";

	//BITMAP KEY
	public final static String KEY_PARKING_BLURRED = "parking_image";


}
