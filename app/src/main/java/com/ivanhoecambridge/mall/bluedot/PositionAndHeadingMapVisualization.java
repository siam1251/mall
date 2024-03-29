package com.ivanhoecambridge.mall.bluedot;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.senionlab.slutilities.type.SLHeadingStatus;
import com.senionlab.slutilities.type.SLPixelPoint2D;

import slutilities.SLSettings;

/**
 * Created by Kay on 2017-01-06.
 */

public class PositionAndHeadingMapVisualization {

    private ValueAnimator headingAnimator = ValueAnimator.ofFloat();
    private ValueAnimator mAnimator;

    private double posX = SLSettings.latitude;
    private double posY = SLSettings.longitude;
    private float heading;

    protected static final int ANIMATION_TIME_MILLIS = 1000; //animator finishes before its gets updated - more instant update but it can stop and go stop and go, 2000 makes animator resets before it's over - more smooth but can lag
    protected static final int COMPASS_ANIMATION_TIME_MILLIS = 400;

    public enum LocationFindingMode { BEACON, GPS, NONE };
    public static LocationFindingMode sLocationFindingMode = LocationFindingMode.BEACON;
    public static String sGeofenceEntered = "";

    protected MapViewWithBlueDot mapViewWithBlueDot;
    private SLHeadingStatus headingStatus;
    public void init(MapViewWithBlueDot mapViewWithBlueDot) {
        this.mapViewWithBlueDot = mapViewWithBlueDot;
    }

    private class PositionTypeEvaluator implements TypeEvaluator<BlueDotPosition> {
        @Override
        public BlueDotPosition evaluate(float fraction, BlueDotPosition startValue, BlueDotPosition endValue) {
            posX = (startValue.getLatitude() + (endValue.getLatitude() - startValue.getLatitude()) * fraction);
            posY = (startValue.getLongitude() + (endValue.getLongitude() - startValue.getLongitude()) * fraction);
            mapViewWithBlueDot.dropBlueDot(posX, posY, endValue.getMappedInFloor());
            return new BlueDotPosition(posX, posY);
        }
    }

    protected final void setupFloatValueAnimation(final ValueAnimator animator, final ValueAnimator.AnimatorUpdateListener listener) {
        animator.setDuration(COMPASS_ANIMATION_TIME_MILLIS);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                listener.onAnimationUpdate(valueAnimator);
                mapViewWithBlueDot.dropHeading(posX, posY, heading, headingStatus);
            }
        });
    }

    public PositionAndHeadingMapVisualization() {
        if(mAnimator == null) {
            mAnimator = ValueAnimator.ofObject(new PositionTypeEvaluator(), new BlueDotPosition(SLSettings.latitude, SLSettings.longitude),
                    new BlueDotPosition(posX, posY));

        }
        mAnimator.setDuration(ANIMATION_TIME_MILLIS);
        mAnimator.setInterpolator(new LinearInterpolator());

        setupFloatValueAnimation(headingAnimator, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                heading = (Float)valueAnimator.getAnimatedValue();
            }
        });

    }

    //SET BLUEDOT POSITION AND RESTARTING ANIMATION
    public void setPos(final BlueDotPosition blueDotPosition) {
        if(mAnimator == null) return;
        try {
            mAnimator.cancel();
            mAnimator.setObjectValues(new BlueDotPosition(posX, posY), blueDotPosition);
            mAnimator.start();
        } catch (Exception e) {
            Log.d("BLUEDOTERROR", e.toString());
        }
    }

    //SET HEADING VALUE AND RESTARTING ANIMATOR
    public void setHeading(float heading, SLHeadingStatus headingStatus) {
        this.headingStatus = headingStatus;
        float end = heading % 360;
        float start = this.heading;
        float shortestAngle=((((end - start) % 360) + 540) % 360) - 180; // Ensure we don't spin when going from 360-0 or 0-360.
        float newHeading = this.heading + shortestAngle;
        restartAnimator(headingAnimator, this.heading, newHeading);
    }

    public void restartAnimator(ValueAnimator animator, double start, double end) {
        if(animator == null) return;
        try {
            animator.cancel();
            animator.setFloatValues((float) start, (float) end);
            animator.start();
        } catch (Exception e) {
            Log.d("BLUEDOTERROR", e.toString());
        }
    }
}
