package com.kineticcafe.kcpmall.activities;

import com.kineticcafe.kcpandroidsdk.constant.KcpConstants;

public class Constants {

//	public final static int DURATION_MINIMUM_SPLASH_ANIMATION = 				1000;
	public final static int DURATION_MINIMUM_SPLASH_ANIMATION = 				500;
	public final static int DURATION_SPLASH_ANIMATION = 						5000;
	public final static long DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION = 	100;
	public final static long DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION = 		100;
	public final static long DURATION_DETAIL_EXPIRY_DATE_TEXT = 				800;


	public enum DetailType { DEAL, STORE }
	public final static String KEY_BUNDLE_DEAL_TYPE = "key_bundle_deal_type";

	//HEADER
	public final static String HEADER_VALUE_DATAHUB_CATALOG_VM = 	"vaughan-mills";
	public final static String HEADER_VALUE_DATAHUB_CATALOG_MP = 	"metropolis-at-metrotown";
	public final static String HEADER_VALUE_DATAHUB_LOCALE = 	"en-CA";
	public final static String HEADER_VALUE_CLIENT_TOKEN = 		"YmRkYWVjMmQtYjJiOS00YzdhLTllZDktMDg1NmI2M2E1NjA1OjM1ODg4M2M0OTk3YWM5ZTY3Y2EwNDMwMTE4M2RjODIzOTBlNWVhMmEyYTBkODIwOTFiZTY0MDE1NjZkYWY4YmQ0MTQzMzg4MzQ4Yzg0YzhjNzMwM2EzNzQ3YWEzOTJiNDYwYmJjMGIyMWYzZWM5OTIwNjIyODYyMDBmZDM5NjRm"; //PRODUCTION
//public final static String HEADER_VALUE_CLIENT_TOKEN = 		"OGEyZDYyZTgtNzRmMS00ODNjLTg2YzMtYjQ0YWY1MDc5NWM1OjdjODhjZmY0ZTBjNDk5MTRiM2EzZTY3YjAzZGY1ZTE4ZDM5YTg0ZjM2YTE0ZjI0NWYzNDViN2JhNWY4YTE4YjJiMDUwMWIxOWRiYTExYzE1ZGIzNTJmNzhlM2EzYzI5MGM2MWQ4MmRlMGRjYTkyMzAzZTQ2NWRjZDU3NDJkNGRl"; //STAGING
	public final static String HEADER_VALUE_CONTENT_TYPE = 		"application/json";
	public final static String HEADER_VALUE_ACCEPT = 			"application/json, application/vnd.kcp.icmp-deal+json; version=1.0, application/vnd.kcp.place+json; version=1.0";
//	public final static String HEADER_VALUE_ACCEPT = 			"application/json";
	public final static String HEADER_VALUE_AUTHROZATION = 		"Bearer 33cce977-6891-4ca5-9df9-6b1e7b9f0d84_YzU5YWM0ZjctODZiOS00ZGJiLTkzYjYtNjZkYzdkYjZkOTE1"; //TESTING


	//TWITTER
	public final static String TWITTER_SCREEN_NAME = 		"Vaughan_Mills";
	public final static int 	NUMB_OF_TWEETS = 		5; //how many twitter tweets it will show at a time
	public final static String TWITTER_API_KEY = 		"bM8knFuF8iYVrYIULOZvyt2ew";
	public final static String TWITTER_API_SECRET = 	"8KzSsk0u1yhYU5RUrnV5T2O75KeIUUXicbXXOD2eUxnRxvc4DB";

	//INSTAGRAM
	public final static int 	NUMB_OF_INSTA = 		10; //how many twitter tweets it will show at a time
	public final static String INSTAGRAM_BASE_URL = 	"https://api.instagram.com/v1/";
	public final static String INSTAGRAM_USER_NAME = 		"Vaughan_Mills";
	public final static String INSTAGRAM_PACKAGE_NAME = 		"com.instagram.android"; //to see if app is installed
//    public final static String INSTAGRAM_ACCESS_TOKEN = "1327329361.faee812.fd4d8c7df4be4407b535d4d11a264ce3"; //METROPOLIS
//    public final static String INSTAGRAM_USER_ID = 		"231349563"; //METROPOLIS

	public final static String INSTAGRAM_ACCESS_TOKEN = "1327329361.ca8e27d.4b01a84969fa4229bd05726c22d41574"; //METROPOLIS
	public final static String INSTAGRAM_USER_ID = 		"249069342"; //VAUGHAN MILLS


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
 	public final static String DATE_FORMAT_HOLIDAY_STORE = "EEE MMMM d";
 	public final static String DATE_FORMAT_DAY = "EEE";


	//DETAIL
	public final static int DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE = 3; //if date left until (effective date) is equal or less than this number, show it

	//ARG_KEY
	public final static String ARG_CONTENT_PAGE = 			"arg_content_page";
	public final static String ARG_IMAGE_URL = 				"arg_image_url";
	public final static String ARG_IMAGE_BITMAP = 			"arg_image_bitmap";
	public static final String ARG_EXTERNAL_CODE = 			"external_code";
	public static final String ARG_CAT_NAME = 				"cat_name";
	public static final String ARG_CATEGORY_ACTIVITY_TYPE = "cat_activity_type";
	public static final String ARG_ACTIVITY_TYPE = "activity_type";
	public enum CategoryActivityType { SUBCATEGORY, STORE }


	//REQUEST CODE
	public final static int REQUEST_CODE_CHANGE_INTEREST = 101;
	public final static int REQUEST_CODE_MY_PAGE_TYPE = 102;
	public final static int RESULT_DONE_PRESSED_WITH_CHANGE = 0;
	public final static int RESULT_DONE_PRESSED_WITHOUT_CHANGE = 1;
	public final static int RESULT_EXIT = 2;

	public final static int RESULT_DEALS = 1;
	public final static int RESULT_EVENTS = 2;
	public final static int RESULT_STORES = 3;

	//OTHERS
	public final static int NUMB_OF_DAYS = 7;
}
