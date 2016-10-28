package com.ivanhoecambridge.mall.movies;

import com.ivanhoecambridge.mall.movies.models.Ratings;

import constants.MallConstants;

/**
 * Created by Kay on 2016-10-27.
 */

public class MovieUtility {

    private static final String RATINGS_PROVINCE_CODE_ON = "on-rating";
    private static final String RATINGS_PROVINCE_CODE_BC = "bc-rating";

    public static String getMovieRating(Ratings ratings, String provinceCode){
        if(provinceCode.equals(RATINGS_PROVINCE_CODE_ON)){
            return ratings.getOn_rating();
        } else if(provinceCode.equals(RATINGS_PROVINCE_CODE_BC)){
            return ratings.getBc_rating();
        } else {
            return "";
        }
    }

}
