package com.ivanhoecambridge.mall.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Kay on 2016-05-19.
 */
public class AppBarBehavior extends CoordinatorLayout.Behavior<AppBarLayout> {

    public AppBarBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child, View dependency) {

        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, AppBarLayout child, View dependency) {

        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
