package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;

/**
 * Created by Kay on 2016-05-04.
 */

public class KcpAnimatedViewPager extends ViewPager /*implements ViewPager.OnPageChangeListener*//*, TabLayout.OnTabSelectedListener*/{


    private Context mContext;
    private static final float thresholdOffset = 0.5f;
    public boolean scrollStarted, checkDirection;
    public enum ScrollDirection { RIGHT, LEFT, IDLE }
    private ScrollDirection mScrollDirection = ScrollDirection.IDLE;
    public boolean mTabStateHandledAlready = false;
    private int mCurrentTabPosition = 0;
    private TabLayout mTabLayout;

    private int mSelectedColor;
    private int mUnselectedColor;
    private boolean enabled = true;

    public KcpAnimatedViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public KcpAnimatedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KcpAnimatedViewPager);
            mSelectedColor = a.getColor(R.styleable.KcpAnimatedViewPager_selectedColor, getResources().getColor(R.color.themeColor));
            mUnselectedColor = a.getColor(R.styleable.KcpAnimatedViewPager_unSelectedColor, getResources().getColor(R.color.unselected));
        }

        //implementing onPnageChangeListener won't call onPageScrollStateChanged because tapLayout seems to consume the event. below fixes the issue
        this.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset == 0) {
                    mCurrentTabPosition = position;
                }

                View v = mTabLayout.getTabAt(mCurrentTabPosition).getCustomView();
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
                        changeTabStateAsScroll(v, mSelectedColor, mUnselectedColor, positionOffset);
                        if(mTabLayout.getTabCount() > mCurrentTabPosition + 1 ) {
                            //right tab fade in
                            changeTabStateAsScroll(mTabLayout.getTabAt(mCurrentTabPosition + 1).getCustomView(), mSelectedColor, mUnselectedColor, 1 - positionOffset);
                        }
                    } else {
                        //current tab fade out
                        changeTabStateAsScroll(v, mSelectedColor, mUnselectedColor, 1 - positionOffset);
                        if(0 <= mCurrentTabPosition - 1) {
                            //left fade in
                            changeTabStateAsScroll(mTabLayout.getTabAt(mCurrentTabPosition - 1).getCustomView(), mSelectedColor, mUnselectedColor, positionOffset);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {}

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
        });
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setTabLayout(TabLayout tapLayout){
        mTabLayout = tapLayout;
        changeTabStateWhenSelected(mTabLayout.getTabAt(0).getCustomView(), true);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabStateHandledAlready = true;
                for(int i = 0; i < mTabLayout.getTabCount(); i++){
                    changeTabStateWhenSelected(mTabLayout.getTabAt(i).getCustomView(), tab.getPosition() == i);
                }
                KcpAnimatedViewPager.this.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

//    private void changeTabStateAsScroll(View v, int colorFrom, int colorTo, float proportion){
    private void changeTabStateAsScroll(View v, int colorFrom, int colorTo, float proportion){
        TextView tvCurrentTab = (TextView) v.findViewById(R.id.tvTabTitle);
        ImageView ivCurrentTab = (ImageView) v.findViewById(R.id.ivTabIcon);

        ivCurrentTab.setColorFilter(KcpUtility.interpolateColor(
                colorFrom, colorTo, proportion));
        tvCurrentTab.setTextColor(KcpUtility.interpolateColor(
                colorFrom, colorTo, proportion));
    }

    /** changes the color and shows on/off tab title when selected/unselected v*/
    public void changeTabStateWhenSelected(View v, boolean selected){
        TextView tvTabTitle = (TextView) v.findViewById(R.id.tvTabTitle);
        int color = selected == true ? mSelectedColor : mUnselectedColor;
        tvTabTitle.setTextColor(color);

        ImageView ivTabIcon = (ImageView) v.findViewById(R.id.ivTabIcon);
        ivTabIcon.setColorFilter(color);


        float alpha = selected == true ? 1.0f : 0.0f;
        tvTabTitle.setAlpha(alpha);

        Animation faceInAnim = new AlphaAnimation(0.0f, 1.0f);
        Animation faceOutAnim = new AlphaAnimation(1.0f, 0.0f);
        faceInAnim.setDuration(Constants.DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION);
        faceInAnim.setStartOffset(Constants.DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION);
        faceInAnim.setFillAfter(true);
        faceInAnim.setFillEnabled(true);

        faceOutAnim.setDuration(Constants.DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION);
        faceOutAnim.setStartOffset(Constants.DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION);
        faceOutAnim.setFillAfter(true);
        faceOutAnim.setFillEnabled(true);

        if(selected){
            ivTabIcon.animate().translationY((int) getResources().getDimension(R.dimen.home_tab_icon_top_margin_when_selected)).setDuration(Constants.DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION).start();
            tvTabTitle.startAnimation(faceInAnim);
        } else {
            ivTabIcon.animate().translationY((int) getResources().getDimension(R.dimen.home_tab_icon_top_margin_when_not_selected)).setDuration(100).start();
            tvTabTitle.startAnimation(faceOutAnim);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }


}
