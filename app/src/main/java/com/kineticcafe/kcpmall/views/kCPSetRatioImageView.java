package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-05.
 */
public class kCPSetRatioImageView extends ImageView {

    private float mImageRatio;



    public kCPSetRatioImageView(Context context) {
        super(context);
    }

    public kCPSetRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.kCPSetRatioImageView);
            mImageRatio = a.getFloat(R.styleable.kCPSetRatioImageView_imageRatio, 0);

            a.recycle();
        }
    }

    public kCPSetRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                setMeasuredDimension(0, 0);
            } else {
                float imageSideRatio = mImageRatio; //Width = 344, Height = 215
                float viewSideRatio = (float) MeasureSpec.getSize(widthMeasureSpec) / (float) MeasureSpec.getSize(heightMeasureSpec); //1.8

                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) (width / imageSideRatio);
                setMeasuredDimension(width, height);
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }



}
