package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Kay on 2016-05-14.
 */
public class BottomCropImageView extends ImageView {
    float mWidthPercent = 0, mHeightPercent = 0;

    public BottomCropImageView(Context context) {
        super(context);
        setup();
    }

    public BottomCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BottomCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    // How far right or down should we place the upper-left corner of the cropbox? [0, 1]
    public void setOffset(float widthPercent, float heightPercent) {
        mWidthPercent = widthPercent;
        mHeightPercent = heightPercent;
    }

    private void setup() {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix = getImageMatrix();

        float scale;
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();

        //Get the scale
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        //Define the rect to take image portion from
        RectF drawableRect = new RectF(0, drawableHeight - (viewHeight / scale), drawableWidth, drawableHeight);
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);

        setImageMatrix(matrix);

        return super.setFrame(l, t, r, b);
    }
}