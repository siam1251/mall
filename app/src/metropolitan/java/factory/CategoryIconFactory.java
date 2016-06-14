package com.kineticcafe.kcpmall.factory;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-06-06.
 */
public class CategoryIconFactory {

    private static final String[] mCategoryExclusion = {"Parking", "ANC", "Common Area", "Common Areas"};

    private static final String EXTERNAL_CODE_BEAUTY_AND_HEALTH           = "7";
    private static final String EXTERNAL_BOOKS_CARDS_AND_SPECIALTY        = "9";
    private static final String EXTERNAL_CODE_DEPARTMENT_AND_VARIETY      = "11";
    private static final String EXTERNAL_ELECTRONICS                      = "12";
    private static final String EXTERNAL_ENTERTAINMENT                    = "13";
    private static final String EXTERNAL_FOOD                             = "14";
    private static final String EXTERNAL_CODE_HOME                        = "15";
    private static final String EXTERNAL_CODE_JEWELERY_AND_ACCESSORIES    = "16";
    private static final String EXTERNAL_CODE_KIDS_AND_BABY               = "17";
    private static final String EXTERNAL_CODE_MENS_CLOTHING               = "18";
    private static final String EXTERNAL_SERVICES                         = "19";
    private static final String EXTERNAL_CODE_SHEOS_AND_BAGS              = "20";
    private static final String EXTERNAL_CODE_SPORTS_AND_FITNESS          = "21";
    private static final String EXTERNAL_CODE_WOMENS_CLOTHING             = "22";
    /*private static final String EXTERNAL_PARKING                          = "108";
    private static final String EXTERNAL_COMMON_AREA                      = "109";
    private static final String EXTERNAL_AND                              = "110";*/


    private static HashMap<String, Integer> mCategoryMap;
    private static HashMap<String, Integer> getCategoryMap(){
        if(mCategoryMap == null) {
            constructCategoryMap();
        }
        return mCategoryMap;
    }

    public static void constructCategoryMap() {
        mCategoryMap = new HashMap<String, Integer>();

        mCategoryMap.put(EXTERNAL_CODE_BEAUTY_AND_HEALTH,           R.drawable.icn_beauty);
        mCategoryMap.put(EXTERNAL_BOOKS_CARDS_AND_SPECIALTY,        R.drawable.icn_books);
        mCategoryMap.put(EXTERNAL_CODE_DEPARTMENT_AND_VARIETY,      R.drawable.icn_department);
        mCategoryMap.put(EXTERNAL_ELECTRONICS,                      R.drawable.icn_electronics);
        mCategoryMap.put(EXTERNAL_ENTERTAINMENT,                    R.drawable.icn_entertainment);
        mCategoryMap.put(EXTERNAL_FOOD,                             R.drawable.icn_food);
        mCategoryMap.put(EXTERNAL_CODE_HOME,                        R.drawable.icn_home);
        mCategoryMap.put(EXTERNAL_CODE_JEWELERY_AND_ACCESSORIES,    R.drawable.icn_jewelry);
        mCategoryMap.put(EXTERNAL_CODE_KIDS_AND_BABY,               R.drawable.icn_kids);
        mCategoryMap.put(EXTERNAL_CODE_MENS_CLOTHING,               R.drawable.icn_mens);
        mCategoryMap.put(EXTERNAL_SERVICES,                         R.drawable.icn_services);
        mCategoryMap.put(EXTERNAL_CODE_SHEOS_AND_BAGS,              R.drawable.icn_shoes);
        mCategoryMap.put(EXTERNAL_CODE_SPORTS_AND_FITNESS,          R.drawable.icn_sports);
        mCategoryMap.put(EXTERNAL_CODE_WOMENS_CLOTHING,             R.drawable.icn_womens);
        /*mCategoryMap.put(EXTERNAL_PARKING,                          getDefaultCategoryIcon());
        mCategoryMap.put(EXTERNAL_COMMON_AREA,                      getDefaultCategoryIcon());
        mCategoryMap.put(EXTERNAL_AND,                              getDefaultCategoryIcon());*/
    }

    public static int getCategoryIcon(String externalCode){
        if(getCategoryMap().containsKey(externalCode)) return getCategoryMap().get(externalCode);
        else return getDefaultCategoryIcon();
    }

    /*private static int getDefaultCategoryIcon(){
        return R.drawable.icn_fav_selected;
    }*/

    private static int getDefaultCategoryIcon(){
        return 0;
    }

    public static ArrayList<KcpCategories> getFilteredKcpCategoryList(ArrayList<KcpCategories> categoriesArrayList){
        ArrayList<KcpCategories> filteredCategoriesList = new ArrayList<>();

        //filter the categorylist to exclude ANC, Common Area, Common Areas, Parking that are not defined in CategoryHashMap in CategoryIconFactory
        /*for(KcpCategories kcpCategory : categoriesArrayList){
            String externalCode = kcpCategory.getExternalCode();
            int drawableId = CategoryIconFactory.getCategoryIcon(externalCode);
            if(drawableId != 0) filteredCategoriesList.add(kcpCategory);
        }*/

        for(KcpCategories kcpCategory : categoriesArrayList){
            String name = kcpCategory.getCategoryName();

            boolean isExluded = false;
            for(String exclusion : mCategoryExclusion){
                if(name.equals(exclusion)) isExluded = true;
            }
            if(!isExluded) filteredCategoriesList.add(kcpCategory);
        }

        return filteredCategoriesList;
    }
}
