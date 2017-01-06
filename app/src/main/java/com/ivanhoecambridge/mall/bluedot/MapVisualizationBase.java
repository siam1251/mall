package com.ivanhoecambridge.mall.bluedot;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Kay on 2017-01-06.
 */

public abstract class MapVisualizationBase implements MapVisualization {

    protected static final int ANIMATION_TIME_MILLIS = 400;
    private boolean isEnabled;
    protected MapViewWithBlueDot mapViewWithBlueDot;

    public void init(MapViewWithBlueDot mapViewWithBlueDot) {
        this.mapViewWithBlueDot = mapViewWithBlueDot;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    protected final void setupFloatValueAnimation(final ValueAnimator animator, final ValueAnimator.AnimatorUpdateListener listener) {
        animator.setDuration(ANIMATION_TIME_MILLIS);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                listener.onAnimationUpdate(valueAnimator);
//                mapViewWithBlueDot.translateBlueDot(positionAndHeadingMapVisualization.getPosX(), positionAndHeadingMapVisualization.getPosY());
            }
        });
    }

}
