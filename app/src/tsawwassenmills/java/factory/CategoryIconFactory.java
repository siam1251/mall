package factory;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.mall.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-06-06.
 */
public class CategoryIconFactory {

    private static final String[] mCategoryExclusion = {"Parking", "ANC", "Common Area", "Common Areas"};

    //metropolitan
    private static final String EXTERNAL_BOOKS_CARDS_AND_SPECIALTY        = "38";
    private static final String EXTERNAL_CODE_DEPARTMENT_AND_VARIETY      = "91";
    private static final String EXTERNAL_ELECTRONICS                      = "1";
    private static final String EXTERNAL_ENTERTAINMENT                    = "11";
    private static final String EXTERNAL_FOOD                             = "44";
    private static final String EXTERNAL_CODE_HOME                        = "15";
    private static final String EXTERNAL_CODE_JEWELERY_AND_ACCESSORIES    = "70";
    private static final String EXTERNAL_CODE_KIDS_AND_BABY               = "6";
    private static final String EXTERNAL_CODE_MENS_CLOTHING               = "51";
    private static final String EXTERNAL_SERVICES                         = "16";
    private static final String EXTERNAL_CODE_SHEOS_AND_BAGS              = "64";
    private static final String EXTERNAL_CODE_SPORTS_AND_FITNESS          = "77";
    private static final String EXTERNAL_CODE_WOMENS_CLOTHING             = "82";
    private static final String EXTERNAL_CODE_BEAUTY_AND_HEALTH           = "30";

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
    }

    public static int getCategoryIcon(String externalCode){
        if(getCategoryMap().containsKey(externalCode)) return getCategoryMap().get(externalCode);
        else return getDefaultCategoryIcon();
    }

    private static int getDefaultCategoryIcon(){
        return R.drawable.icn_home;
    }

    public static ArrayList<KcpCategories> getFilteredKcpCategoryList(ArrayList<KcpCategories> categoriesArrayList){
        ArrayList<KcpCategories> filteredCategoriesList = new ArrayList<>();

        for(KcpCategories kcpCategory : categoriesArrayList){
            String name = kcpCategory.getCategoryName();

            boolean isExluded = false;
            for(String exclusion : mCategoryExclusion){
                if(name.toLowerCase().equals(exclusion.toLowerCase())) isExluded = true;
            }
            if(!isExluded) filteredCategoriesList.add(kcpCategory);
        }

        return filteredCategoriesList;
    }
}
