package com.ivanhoecambridge.mall.bluedot;

/**
 * Created by Kay on 2017-01-10.
 */

public class MapViewFollowMeImpl {

    private MapViewFollowMode mapViewFollowMode;
    private FollowMode followMode = FollowMode.NONE;
    public MapViewFollowMeImpl(MapViewFollowMode mapViewFollowMode){
        this.mapViewFollowMode = mapViewFollowMode;

    }

    public FollowMode getFollowMode() {
        return followMode;
    }
}
