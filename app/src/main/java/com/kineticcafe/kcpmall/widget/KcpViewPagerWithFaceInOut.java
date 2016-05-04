package com.kineticcafe.kcpmall.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.utility.Utility;

/**
 * Created by Kay on 2016-05-04.
 */
public class KcpViewPagerWithFaceInOut extends ViewPager implements ViewPager.OnPageChangeListener{

    private static final float thresholdOffset = 0.5f;
    private boolean scrollStarted, checkDirection;
    public enum ScrollDirection { RIGHT, LEFT, IDLE }
    private ScrollDirection mScrollDirection = ScrollDirection.IDLE;
    private boolean mTabStateHandledAlready = false;
    private int mCurrentTabPosition = 0;
    private TabLayout mTapLayout;

    public KcpViewPagerWithFaceInOut(Context context) {
        super(context);
    }

    public KcpViewPagerWithFaceInOut(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTabLayout(TabLayout tapLayout){
        mTapLayout = tapLayout;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if(positionOffset == 0) {
            mCurrentTabPosition = position;
        }

        View v = mTapLayout.getTabAt(mCurrentTabPosition).getCustomView();
        if (checkDirection) {
            if (thresholdOffset > positionOffset) {
                mScrollDirection = ScrollDirection.RIGHT;
            } else {
                mScrollDirection = ScrollDirection.LEFT;
            }
            checkDirection = false;
        } else {
            if(positionOffset == 0) {
                return;
            }

            if(mTabStateHandledAlready) return;
            if( mScrollDirection.equals(ScrollDirection.RIGHT)) {
                //current tab fade out
                changeTabStateAsScroll(v, R.color.themeColor, R.color.unselected, positionOffset);
                if(mTapLayout.getTabCount() > mCurrentTabPosition + 1 ) {
                    //right tab fade in
                    changeTabStateAsScroll(mTapLayout.getTabAt(mCurrentTabPosition + 1).getCustomView(), R.color.themeColor, R.color.unselected, 1 - positionOffset);
                }
            } else {
                //current tab fade out
                changeTabStateAsScroll(v, R.color.themeColor, R.color.unselected, 1 - positionOffset);
                if(0 <= mCurrentTabPosition - 1) {
                    //left fade in
                    changeTabStateAsScroll(mTapLayout.getTabAt(mCurrentTabPosition - 1).getCustomView(), R.color.themeColor, R.color.unselected, positionOffset);
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
            scrollStarted = true;
            checkDirection = true;
        } else {
            scrollStarted = false;
        }
        if(state == ViewPager.SCROLL_STATE_IDLE) {
            mTabStateHandledAlready = false;
        }
    }

    private void changeTabStateAsScroll(View v, int colorFrom, int colorTo, float proportion){
        TextView tvCurrentTab = (TextView) v.findViewById(R.id.tvTabTitle);
        ImageView ivCurrentTab = (ImageView) v.findViewById(R.id.ivTabIcon);

        ivCurrentTab.setColorFilter(Utility.interpolateColor(
                getResources().getColor(colorFrom), getResources().getColor(colorTo), proportion));
        tvCurrentTab.setTextColor(Utility.interpolateColor(
                getResources().getColor(colorFrom), getResources().getColor(colorTo), proportion));
    }

}
