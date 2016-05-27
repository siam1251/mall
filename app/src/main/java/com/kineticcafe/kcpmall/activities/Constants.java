package com.kineticcafe.kcpmall.activities;

public class Constants {

	public final static int DURATION_MINIMUM_SPLASH_ANIMATION = 1000;
	public final static int DURATION_SPLASH_ANIMATION = 8000;
	public final static long DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION = 100;
	public final static long DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION = 100;
	public final static long DURATION_DETAIL_EXPIRY_DATE_TEXT = 800;

	public final static float PARALLAX_PARAM = 2.5f;


	public enum DetailType { DEAL, STORE }

	public final static String KEY_BUNDLE_DEAL_TYPE = "key_bundle_deal_type";



	//MALL SPECIFIC
	public final static String URL_BASE = "https://dit-kcp.kineticcafetech.com/";
//	public final static String URL_BASE = "https://staging-kcp.kineticcafetech.com/";
	public final static String URL_NAVIGATION_ROOT = "core/navigation_roots";
	public final static String URL_VIEW_ALL_CONTENT = "/view_all_content";
//	public final static String URL_PER_PAGE = "10";

	//HEADER
	public final static String HEADER_VALUE_DATAHUB_CATALOG = "vaughan-mills";
//	public final static String HEADER_VALUE_DATAHUB_CATALOG = "metropolis-metrotown";
	public final static String HEADER_VALUE_DATAHUB_LOCALE = "en-CA";
	public final static String HEADER_VALUE_CLIENT_TOKEN = "OGEyZDYyZTgtNzRmMS00ODNjLTg2YzMtYjQ0YWY1MDc5NWM1OjdjODhjZmY0ZTBjNDk5MTRiM2EzZTY3YjAzZGY1ZTE4ZDM5YTg0ZjM2YTE0ZjI0NWYzNDViN2JhNWY4YTE4YjJiMDUwMWIxOWRiYTExYzE1ZGIzNTJmNzhlM2EzYzI5MGM2MWQ4MmRlMGRjYTkyMzAzZTQ2NWRjZDU3NDJkNGRl";
	public final static String HEADER_VALUE_CONTENT_TYPE = "application/json";
	public final static String HEADER_VALUE_ACCEPT = "application/json, application/vnd.kcp.icmp-deal+json; version=1.0, application/vnd.kcp.place+json; version=1.0";
	public final static String HEADER_VALUE_AUTHROZATION = "Bearer 33cce977-6891-4ca5-9df9-6b1e7b9f0d84_YzU5YWM0ZjctODZiOS00ZGJiLTkzYjYtNjZkYzdkYjZkOTE1"; //TESTING

	public final static String EXTERNAL_CODE_DEAL = "deals-list";
	public final static String EXTERNAL_CODE_FEED = "feed";
	public final static String EXTERNAL_CODE_RECOMMENDED = "recommended-deals";
	public final static String EXTERNAL_CODE_SYSTEM_MSG = "system-message";


	//DATE FORMAT
 	public final static String DATE_FORMAT_EFFECTIVE = "EEEE, MMMM d";

	//TWITTER
	public final static int 	NUMB_OF_TWEETS = 5; //how many twitter tweets it will show at a time
	public final static String TWITTER_SCREEN_NAME = "Vaughan_Mills";
	public final static String TWITTER_API_KEY = "bM8knFuF8iYVrYIULOZvyt2ew";
	public final static String TWITTER_API_SECRET = "8KzSsk0u1yhYU5RUrnV5T2O75KeIUUXicbXXOD2eUxnRxvc4DB";

	//INSTAGRAM
	public final static int 	NUMB_OF_INSTA = 5; //how many twitter tweets it will show at a time
	public final static String INSTAGRAM_BASE_URL = "https://api.instagram.com/v1/";
	public final static String INSTAGRAM_USER_NAME = "Vaughan_Mills";
	public final static String INSTAGRAM_USER_ID = "249069342"; //VAUGHAN MILLS
	public final static String INSTAGRAM_CLIENT_ID = "038e1210bc3441f780a8c3f3562b2b41"; //VAUGHAN MILLS
//	public final static String INSTAGRAM_USER_ID = "1048431023"; //yjw


	//DETAIL
	public final static int DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE = 3; //if date left until (effective date) is equal or less than this number, show it

	//ARG_KEY
	public final static String ARG_CONTENT_PAGE = "arg_content_page";

}
