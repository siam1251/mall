package com.ivanhoecambridge.mall.onboarding;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Kay on 2016-10-19.
 */

public class TouchConnectedViewPager extends ViewPager {
    TouchConnectedViewPager mTouchConnectedViewPager;
    private boolean forSuper;

    public TouchConnectedViewPager(Context context) {
        super(context);
    }

    public TouchConnectedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!forSuper) {

            mTouchConnectedViewPager.forSuper(true);
            mTouchConnectedViewPager.onInterceptTouchEvent(arg0);
            mTouchConnectedViewPager.forSuper(false);
        }
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!forSuper) {
            mTouchConnectedViewPager.forSuper(true);
            mTouchConnectedViewPager.onTouchEvent(arg0);
            mTouchConnectedViewPager.forSuper(false);
        }
        return super.onTouchEvent(arg0);
    }

    public void setViewPager(TouchConnectedViewPager touchConnectedViewPager) {
        mTouchConnectedViewPager = touchConnectedViewPager;
    }

    public void forSuper(boolean forSuper) {
        this.forSuper = forSuper;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (!forSuper) {
            mTouchConnectedViewPager.forSuper(true);
            mTouchConnectedViewPager.setCurrentItem(item, smoothScroll);
            mTouchConnectedViewPager.forSuper(false);
        }
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (!forSuper) {
            mTouchConnectedViewPager.forSuper(true);
            mTouchConnectedViewPager.setCurrentItem(item);
            mTouchConnectedViewPager.forSuper(false);
        }
        super.setCurrentItem(item);

    }

}