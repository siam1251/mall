package com.ivanhoecambridge.mall.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Created by Kay on 2016-05-16.
 * This imageview mimics the bitmap used by android:windowBackground
 * - that is it tries to fit the image to the height first and leave the sides with gap, having android:gravity="center|bottom|clip_horizontal"
 */
public class WindowBackgroundImageView extends ImageView {

    private Context mContext;
    public WindowBackgroundImageView(Context context) {
        super(context);
        mContext = context;
        setup();
    }

    public WindowBackgroundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setup();
    }

    public WindowBackgroundImageView(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        setup();
    }

    private void setup() {
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom) {
        float frameWidth = frameRight - frameLeft;
        float frameHeight = frameBottom - frameTop;

        float originalImageWidth = (float)getDrawable().getIntrinsicWidth();
        float originalImageHeight = (float)getDrawable().getIntrinsicHeight();

        float usedScaleFactor = 1;


        //to crop the image to fit the entire screen cropping the top/bot
        //in case for note 4, below should be used
        //in case for nexus 4, below's still being used thinking originalImagewidth is smaller than frameWidth but it should skip below
        if( (frameHeight / frameWidth) == (originalImageHeight / originalImageWidth)
                && (frameWidth > originalImageWidth) || (frameHeight > originalImageHeight)) {
            // If frame is bigger than image
            // => Crop it, keep aspect ratio and position it at the bottom and center horizontally
            //*NOTE => if frame ratio is not the same as image ratio, that means windowBackground wouldn't crop the image but leave space on the sides to keep the ratio
            //so in this case, it shouldn't crop the image so use (frameHeight / frameWidth) == (originalImageHeight / originalImageWidth)

            float fitHorizontallyScaleFactor = frameWidth/originalImageWidth;
            float fitVerticallyScaleFactor = frameHeight/originalImageHeight;

            usedScaleFactor = Math.max(fitHorizontallyScaleFactor, fitVerticallyScaleFactor);
        }

        float newImageWidth = originalImageWidth * usedScaleFactor;
        float newImageHeight = originalImageHeight * usedScaleFactor;

        Matrix matrix = getImageMatrix();

        matrix.setScale(usedScaleFactor, usedScaleFactor, 0, 0); // Replaces the old matrix completly

        float imageRatio = newImageHeight / newImageWidth;
        float screenRatio = getScreenRatio();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(screenRatio != imageRatio){
                matrix.postTranslate((frameWidth - newImageWidth) /2, 0); //nexus 4, API 23
            } else {
                matrix.postTranslate((frameWidth - newImageWidth) /2, frameHeight - newImageHeight); //for devices with the same screen ratio as the image - S7
            }
        } else {
            matrix.postTranslate((frameWidth - newImageWidth) /2, frameHeight - newImageHeight); //for devices with the same screen ratio as the image - LGE973 API 15
        }

        setImageMatrix(matrix);
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
    }

    private float getScreenRatio(){
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (float) metrics.heightPixels / metrics.widthPixels;
    }
}