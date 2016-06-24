package com.kineticcafe.kcpmall.activities;

public class Constants {

	public final static int DURATION_MINIMUM_SPLASH_ANIMATION = 				1000;
	public final static int DURATION_SPLASH_ANIMATION = 						5000;
	public final static long DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION = 	100;
	public final static long DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION = 		100;
	public final static long DURATION_DETAIL_EXPIRY_DATE_TEXT = 				800;


	public enum DetailType { DEAL, STORE }
	public final static String KEY_BUNDLE_DEAL_TYPE = "key_bundle_deal_type";

	//HEADER
	public final static String HEADER_VALUE_DATAHUB_CATALOG = 	"vaughan-mills";
//	public final static String HEADER_VALUE_DATAHUB_CATALOG = 	"metropolis-metrotown";
	public final static String HEADER_VALUE_DATAHUB_LOCALE = 	"en-CA";
	public final static String HEADER_VALUE_CLIENT_TOKEN = 		"OGEyZDYyZTgtNzRmMS00ODNjLTg2YzMtYjQ0YWY1MDc5NWM1OjdjODhjZmY0ZTBjNDk5MTRiM2EzZTY3YjAzZGY1ZTE4ZDM5YTg0ZjM2YTE0ZjI0NWYzNDViN2JhNWY4YTE4YjJiMDUwMWIxOWRiYTExYzE1ZGIzNTJmNzhlM2EzYzI5MGM2MWQ4MmRlMGRjYTkyMzAzZTQ2NWRjZDU3NDJkNGRl";
	public final static String HEADER_VALUE_CONTENT_TYPE = 		"application/json";
	public final static String HEADER_VALUE_ACCEPT = 			"application/json, application/vnd.kcp.icmp-deal+json; version=1.0, application/vnd.kcp.place+json; version=1.0";
//	public final static String HEADER_VALUE_ACCEPT = 			"application/json";
	public final static String HEADER_VALUE_AUTHROZATION = 		"Bearer 33cce977-6891-4ca5-9df9-6b1e7b9f0d84_YzU5YWM0ZjctODZiOS00ZGJiLTkzYjYtNjZkYzdkYjZkOTE1"; //TESTING

	//DOWNLOAD TYPES
	public final static String EXTERNAL_CODE_DEAL = 		"deals-list";
	public final static String EXTERNAL_CODE_FEED = 		"feed";
	public final static String EXTERNAL_CODE_RECOMMENDED = 	"recommended-deals";
	public final static String EXTERNAL_CODE_SYSTEM_MSG = 	"system-message";
	public final static String EXTERNAL_CODE_CATEGORIES = 	"categories";
	public final static String EXTERNAL_CODE_PLACES = 		"places";


	//DATE FORMAT
 	public final static String DATE_FORMAT_EFFECTIVE = "EEEE, MMMM d";


	//DETAIL
	public final static int DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE = 3; //if date left until (effective date) is equal or less than this number, show it

	//ARG_KEY
	public final static String ARG_CONTENT_PAGE = 			"arg_content_page";
	public final static String ARG_IMAGE_URL = 				"arg_image_url";
	public final static String ARG_IMAGE_BITMAP = 			"arg_image_bitmap";
	public static final String ARG_EXTERNAL_CODE = 			"external_code";
	public static final String ARG_CAT_NAME = 				"cat_name";
	public static final String ARG_CATEGORY_ACTIVITY_TYPE = "cat_activity_type";
	public enum CategoryActivityType { SUBCATEGORY, STORE }

	//SHARED PREFERNCE DATABASE
//	public final static String PREFS_KEY_INTRSTD_CATEGORY = 	"prefs_key_intrstd_category";
	public final static String PREFS_KEY_CATEGORY = 			"prefs_key_category";
//	public final static String PREFS_KEY_STORE = 				"prefs_key_store";
	public final static String PREFS_KEY_FAV_STORE_LIKE_LINK = 	"prefs_key_store_like_link";

	//REQUEST CODE
	public final static int REQUEST_CODE_CHANGE_INTEREST = 101;
	public final static int RESULT_DONE_PRESSED_WITH_CHANGE = 0;
	public final static int RESULT_DONE_PRESSED_WITHOUT_CHANGE = 1;
	public final static int RESULT_EXIT = 2;


	//MALL INFO
	public final static String MALL_INFO_OFFLINE_TEXT = "mallinfo.json";
	public final static String MALL_INFO_URL_BASE = "https://api.myjson.com/";
//	public final static String MALL_INFO_URL = "bins/2zv9f"; //metropolis
	public final static String MALL_INFO_URL = "bins/3ng0z"; //vaughan mills

}
