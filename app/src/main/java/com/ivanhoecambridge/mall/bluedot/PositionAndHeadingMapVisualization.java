package com.ivanhoecambridge.mall.bluedot;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.senionlab.slutilities.type.SLPixelPoint2D;

/**
 * Created by Kay on 2017-01-06.
 */

//public class PositionAndHeadingMapVisualization extends MapVisualizationBase{
public class PositionAndHeadingMapVisualization /*extends MapVisualizationBase*/{
    private ValueAnimator posXAnimator = ValueAnimator.ofFloat();
    private ValueAnimator posYAnimator = ValueAnimator.ofFloat();

    private float posX;
    private float posY;

    protected static final int ANIMATION_TIME_MILLIS = 400;

    protected MapViewWithBlueDot mapViewWithBlueDot;

    /*@Override
    public void init(MapViewWithBlueDot mapViewWithBlueDot) {
        super.init(mapViewWithBlueDot);
        this.mapViewWithBlueDot = mapViewWithBlueDot;
    }*/

    public void init(MapViewWithBlueDot mapViewWithBlueDot) {
        this.mapViewWithBlueDot = mapViewWithBlueDot;
    }

    protected final void setupFloatValueAnimation(final ValueAnimator animator, final ValueAnimator.AnimatorUpdateListener listener) {
        animator.setDuration(ANIMATION_TIME_MILLIS);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                listener.onAnimationUpdate(valueAnimator);
                mapViewWithBlueDot.translateBlueDot(posX, posY);
            }
        });
    }


    public PositionAndHeadingMapVisualization() {
        setupFloatValueAnimation(posXAnimator, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                posX = (Float)valueAnimator.getAnimatedValue();
            }
        });

        setupFloatValueAnimation(posYAnimator, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                posY = (Float)valueAnimator.getAnimatedValue();
            }
        });

        /*setupFloatValueAnimation(headingAnimator, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                heading = (Float)valueAnimator.getAnimatedValue();
            }
        });
        setupFloatValueAnimation(uncertaintyRadiusAnimator, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                uncertaintyRadius = (Float)valueAnimator.getAnimatedValue();
            }
        });*/
    }

    public void setPos(BlueDotPosition blueDotPosition) {
        restartAnimator(posXAnimator, posX, (float)blueDotPosition.getLatitude());
        restartAnimator(posYAnimator, posY, (float)blueDotPosition.getLongitude());
    }

    protected final void restartAnimator(ValueAnimator animator, float from, float to) {
        animator.cancel();
        animator.setFloatValues(from, (float)to);
        animator.start();
    }
}
