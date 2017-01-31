package com.ivanhoecambridge.mall.bluedot;

/**
 * Created by Kay on 2017-01-10.
 */

public interface MapViewFollowMode {
    void setFollowMode(FollowMode followMeMode);
    FollowMode getFollowMeMode();
    void updateFollowMeMode();
    void showBlueDotError();
}

