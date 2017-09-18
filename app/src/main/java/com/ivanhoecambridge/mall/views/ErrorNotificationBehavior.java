package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.CustomFontView.CustomFontTextView;

/**
 * Created by petar on 2017-09-11.
 */

public class ErrorNotificationBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    private final String TAG = getClass().getSimpleName();
    private Handler delayHandler;
    private long delayDuration;
    private long slideDownDuration;
    private long slideUpDuration;

    public ErrorNotificationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        delayHandler = new Handler(context.getMainLooper());
        slideDownDuration = context.getResources().getInteger(R.integer.anim_slide_down_duration);
        slideUpDuration = context.getResources().getInteger(R.integer.anim_slide_up_duration);
        delayDuration = slideDownDuration / 4;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return dependency instanceof CustomFontTextView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final FrameLayout child, final View dependency) {
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float translateY;
                if (dependency.getVisibility() == View.VISIBLE) {
                    delayDuration = slideUpDuration;
                    translateY = dependency.getTranslationY() + dependency.getHeight();
                    child.setTranslationY(translateY);
                    child.getChildAt(0).setPadding(0, 0, 0, (int) translateY);
                } else {
                    delayDuration = slideDownDuration / 4;
                    child.setTranslationY(0);
                    child.getChildAt(0).setPadding(0, 0, 0, 0);
                }

            }
        }, delayDuration);

        return true;
    }


    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FrameLayout child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }
}
