package com.kineticcafe.kcpmall.views;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.kineticcafe.kcpmall.activities.Constants;

/**
 * Created by Kay on 2016-05-25.
 */
public class ExpiryDateAnimation {
    public Animation getAnimation(){
        if(anim == null) {
            anim = new ScaleAnimation(
                    1f, 1f, // Start and end values for the X axis scaling
                    0f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 1f);
            anim.setFillAfter(true);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(Constants.DURATION_DETAIL_EXPIRY_DATE_TEXT);
        }
        return anim;
    }

    private Animation anim; // Pivot point of Y scaling
}
