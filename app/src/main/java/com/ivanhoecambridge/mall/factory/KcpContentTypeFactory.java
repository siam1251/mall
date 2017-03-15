package com.ivanhoecambridge.mall.factory;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;

/**
 * Created by Kay on 2016-05-24.
 */
public class KcpContentTypeFactory {


    public static final String CONTENT_TYPE_ANNOUNCEMENT = "announcement";
    public static final String CONTENT_TYPE_EVENT = "event";
    public static final String CONTENT_TYPE_DEAL = "deal";
    public static final String CONTENT_TYPE_INTEREST = "interest";
    public static final String CONTENT_TYPE_TWITTER = "twitter";
    public static final String CONTENT_TYPE_INSTAGRAM = "instagram";
    public static final String CONTENT_TYPE_STORE = "store";
    public static final String CONTENT_TYPE_CATEGORY = "category";
    public static final String CONTENT_TYPE_FOOTER = "footer";
    public static final String CONTENT_TYPE_MOVIE = "movie";

    public static final String CONTENT_TYPE_EMPTY_ANNOUNCEMENT = "empty_ancmt";
    public static final String CONTENT_TYPE_EMPTY_EVENT = "empty_eve";


    //NEWS & DEAL ADAPTER ITEM TYPE
    public static final int ITEM_TYPE_LOADING =                             0;
    public static final int ITEM_TYPE_ANNOUNCEMENT =                        1;
    public static final int ITEM_TYPE_EVENT =                               2;
    public static final int ITEM_TYPE_SET_MY_INTEREST =                     3;
    public static final int ITEM_TYPE_TWITTER =                             4;
    public static final int ITEM_TYPE_INSTAGRAM =                           5;
    public static final int ITEM_TYPE_STORE =                               6;
    public static final int ITEM_TYPE_SECTION_HEADER_RECOMMENDED_DEALS =    7;
    public static final int ITEM_TYPE_SECTION_HEADER_OTHER_DEALS =          8;
    public static final int ITEM_TYPE_ADJUST_MY_INTEREST =                  9;
    public static final int ITEM_TYPE_DEAL =                                10;
    public static final int ITEM_TYPE_MOVIE =                               11;
    public static final int ITEM_TYPE_EMPTY_ANNOUNCEMENT =                  12;
    public static final int ITEM_TYPE_EMPTY_EVENT =                         13;

    public static final int ITEM_TYPE_FOOTER =                              100;
    public static final int ITEM_TYPE_HEADER =                              101;

    public static final int ITEM_TYPE_SECTION_HEADER_CATEGORY =   11;
    public static final int ITEM_TYPE_SECTION_HEADER_RECOMMENDED_STORES =   12;
    public static final int ITEM_TYPE_SECTION_HEADER_OTHER_STORES =         13;


    //PREFERENCE ADAPTER ITEM TYPE
    public static final int PREF_ITEM_TYPE_CAT =        0;
    public static final int PREF_ITEM_TYPE_PLACE =      1;
    public static final int PREF_ITEM_TYPE_SUB_CAT =    2;
    public static final int PREF_ITEM_TYPE_ALL_PLACE =      3;

    public static final String TYPE_LOADING_TITLE =         "";
    public static final String TYPE_ANNOUNCEMENT_TITLE =    "Announcement";
    public static final String TYPE_EVENT_TITLE =           "Event";
    public static final String TYPE_SET_MY_INTEREST_TITLE = "Interest";
    public static final String TYPE_TWITTER_TITLE =         "Title";
    public static final String TYPE_INSTAGRAM_TITLE =       "Instagram";
    public static final String TYPE_DEAL_TITLE =            "Deal";
    public static final String TYPE_DEAL_STORE =            "Store";


    public static String getContentTypeTitle(KcpContentPage kcpContentPage){
        if(kcpContentPage == null){
            return TYPE_LOADING_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_ANNOUNCEMENT)){
            return TYPE_ANNOUNCEMENT_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EVENT)){
            return TYPE_EVENT_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_INTEREST)){
            return TYPE_SET_MY_INTEREST_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_TWITTER)){
            return TYPE_TWITTER_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_INSTAGRAM)){
            return TYPE_INSTAGRAM_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_DEAL)){
            return TYPE_DEAL_TITLE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_STORE)){
            return TYPE_DEAL_STORE;
        } else {
            return TYPE_ANNOUNCEMENT_TITLE;
        }
    }

    public static int getContentType(KcpContentPage kcpContentPage){
        if(kcpContentPage == null){
            return ITEM_TYPE_LOADING;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_ANNOUNCEMENT)){
            return ITEM_TYPE_ANNOUNCEMENT;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EVENT)){
            return ITEM_TYPE_EVENT;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_INTEREST)){
            return ITEM_TYPE_SET_MY_INTEREST;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_TWITTER)){
            return ITEM_TYPE_TWITTER;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_INSTAGRAM)){
            return ITEM_TYPE_INSTAGRAM;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_STORE)){
            return ITEM_TYPE_STORE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_DEAL)){
            return ITEM_TYPE_DEAL;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_MOVIE)){
            return ITEM_TYPE_MOVIE;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EMPTY_ANNOUNCEMENT)){
            return ITEM_TYPE_EMPTY_ANNOUNCEMENT;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_EMPTY_EVENT)){
            return ITEM_TYPE_EMPTY_EVENT;
        } else if(kcpContentPage.content_type.contains(CONTENT_TYPE_FOOTER)){
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_ANNOUNCEMENT;
        }
    }
}
