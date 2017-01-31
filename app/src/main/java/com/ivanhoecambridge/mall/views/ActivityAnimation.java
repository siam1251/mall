package com.ivanhoecambridge.mall.views;

import android.app.Activity;
import android.content.Context;

import com.ivanhoecambridge.mall.R;

/**
 * Created by Kay on 2016-06-20.
 */
public class ActivityAnimation {

    public static void startActivityAnimation(Context context){
        ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void exitActivityAnimation(Context context){
        ((Activity)context).overridePendingTransition(R.anim.splash_fake, R.anim.anim_slide_out_right); //shifts out to the left, remaining one stays
    }


}
