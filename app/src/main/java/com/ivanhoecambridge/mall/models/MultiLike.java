package com.ivanhoecambridge.mall.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-10-21.
 */

public class MultiLike {
    private HashMap<String, Object> multiLikeBody;
    public MultiLike(ArrayList<String> likedStores, ArrayList<String> unlikedstores) {
        multiLikeBody = new HashMap<>();
        HashMap<String, Object> multiLikeMap = new HashMap<>();

        ArrayList<HashMap<String, String>> likeArray = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> unlikeArray = new ArrayList<HashMap<String, String>>();

        for (String value : likedStores) {
            HashMap<String, String> likeMap = new HashMap<String, String>();
            likeMap.put("like_link", value);
            likeArray.add(likeMap);
        }

        if (unlikedstores != null) {
            for (String value : unlikedstores) {
                HashMap<String, String> unlikeMap = new HashMap<String, String>();
                unlikeMap.put("like_link", value);
                unlikeArray.add(unlikeMap);
            }
        }

        multiLikeMap.put("like", likeArray);
        multiLikeMap.put("unlike", unlikeArray);
        multiLikeBody.put("multi_like", multiLikeMap);
    }

    public HashMap<String, Object> getMultiLikeBody(){
        if(multiLikeBody == null) multiLikeBody= new HashMap<>();
        return multiLikeBody;
    }
}
