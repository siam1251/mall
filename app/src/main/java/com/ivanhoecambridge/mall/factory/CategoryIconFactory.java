package com.ivanhoecambridge.mall.factory;

import android.content.Context;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by petar on 2017-09-22.
 */

public class CategoryIconFactory {
    private static final int      CATEGORY_EXCLUSION_CODE                = R.array.excluded;
    private static       String[] mCategoryExclusion                     = {};
    private static final int      EXTERNAL_BOOKS_CARDS_AND_SPECIALTY     = R.string.dir_books_cards_and_specialty;
    private static final int      EXTERNAL_CODE_DEPARTMENT_AND_VARIETY   = R.string.dir_department_and_variety;
    private static final int      EXTERNAL_ELECTRONICS                   = R.string.dir_electronics;
    private static final int      EXTERNAL_ENTERTAINMENT                 = R.string.dir_entertainment;
    private static final int      EXTERNAL_FOOD                          = R.string.dir_food;
    private static final int      EXTERNAL_CODE_HOME                     = R.string.dir_home;
    private static final int      EXTERNAL_CODE_JEWELERY_AND_ACCESSORIES = R.string.dir_jewelery_and_accessories;
    private static final int      EXTERNAL_CODE_KIDS_AND_BABY            = R.string.dir_kids_and_baby;
    private static final int      EXTERNAL_CODE_MENS_CLOTHING            = R.string.dir_mens;
    private static final int      EXTERNAL_SERVICES                      = R.string.dir_services;
    private static final int      EXTERNAL_CODE_SHOES_AND_BAGS           = R.string.dir_shoes_and_bags;
    private static final int      EXTERNAL_CODE_SPORTS_AND_FITNESS       = R.string.dir_sports_and_fitness;
    private static final int      EXTERNAL_CODE_BEAUTY_AND_HEALTH        = R.string.dir_beauty_and_health;
    private static final int      EXTERNAL_CODE_WOMENS_CLOTHING          = R.string.dir_womens;


    private static HashMap<String, Integer> mCategoryMap;

    private static HashMap<String, Integer> getCategoryMap(Context context) {
        if (mCategoryMap == null) {
            constructCategoryMap(context);
        }
        return mCategoryMap;
    }

    public static void constructCategoryMap(Context context) {
        mCategoryMap = new HashMap<>();

        mCategoryMap.put(getCode(context, EXTERNAL_CODE_BEAUTY_AND_HEALTH), R.drawable.icn_beauty);
        mCategoryMap.put(getCode(context, EXTERNAL_BOOKS_CARDS_AND_SPECIALTY), R.drawable.icn_books);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_DEPARTMENT_AND_VARIETY), R.drawable.icn_department);
        mCategoryMap.put(getCode(context, EXTERNAL_ELECTRONICS), R.drawable.icn_electronics);
        mCategoryMap.put(getCode(context, EXTERNAL_ENTERTAINMENT), R.drawable.icn_entertainment);
        mCategoryMap.put(getCode(context, EXTERNAL_FOOD), R.drawable.icn_food);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_HOME), R.drawable.icn_home);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_JEWELERY_AND_ACCESSORIES), R.drawable.icn_jewelry);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_KIDS_AND_BABY), R.drawable.icn_kids);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_MENS_CLOTHING), R.drawable.icn_mens);
        mCategoryMap.put(getCode(context, EXTERNAL_SERVICES), R.drawable.icn_services);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_SHOES_AND_BAGS), R.drawable.icn_shoes);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_SPORTS_AND_FITNESS), R.drawable.icn_sports);
        mCategoryMap.put(getCode(context, EXTERNAL_CODE_WOMENS_CLOTHING), R.drawable.icn_womens);

        mCategoryExclusion = KcpUtility.getStringArray(context, CATEGORY_EXCLUSION_CODE);
    }

    public static int getCategoryIcon(String externalCode, Context context) {
        if (getCategoryMap(context).containsKey(externalCode))
            return getCategoryMap(context).get(externalCode);
        else return getDefaultCategoryIcon();
    }

    private static String getCode(Context context, int resValue) {
        return KcpUtility.getString(context, resValue);
    }

    private static int getDefaultCategoryIcon() {
        return R.drawable.icn_home;
    }

    public static ArrayList<KcpCategories> getFilteredKcpCategoryList(ArrayList<KcpCategories> categoriesArrayList) {
        ArrayList<KcpCategories> filteredCategoriesList = new ArrayList<>();

        for (KcpCategories kcpCategory : categoriesArrayList) {
            String name = kcpCategory.getCategoryName();

            boolean isExcluded = false;
            for (String exclusion : mCategoryExclusion) {
                if (name.toLowerCase().equals(exclusion.toLowerCase())) isExcluded = true;
            }
            if (!isExcluded) filteredCategoriesList.add(kcpCategory);
        }

        return filteredCategoriesList;
    }
}
