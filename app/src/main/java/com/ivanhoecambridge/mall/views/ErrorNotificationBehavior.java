package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.ivanhoecambridge.mall.views.CustomFontView.CustomFontTextView;

/**
 * Created by petar on 2017-09-11.
 */

public class ErrorNotificationBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    private final String TAG = getClass().getSimpleName();

    public ErrorNotificationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return dependency instanceof CustomFontTextView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        Log.i(TAG, "Normal");
        float translateY = dependency.getTranslationY() + dependency.getHeight();
        child.setTranslationY(translateY);

        return true;
    }


}
