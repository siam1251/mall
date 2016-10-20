package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Kay on 2016-05-03.
 */
//public class ScrollingTabBarBehavior extends CoordinatorLayout.Behavior<TabLayout> {
public class ScrollingTabBarBehavior extends CoordinatorLayout.Behavior<ViewPager> {

    public ScrollingTabBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewPager child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
//        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewPager child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
//        if (dependency instanceof AppBarLayout) {
//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//            int fabBottomMargin = lp.bottomMargin;
//            int distanceToScroll = child.getHeight() + fabBottomMargin;
//            float ratio = (float)dependency.getY()/(float)toolbarHeight;
//            child.setTranslationY(-distanceToScroll * ratio);
//        }
//        return true;
    }
}
