package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Kay on 2016-05-05.
 */
public class KcpSquareImageView extends ImageView {
    public KcpSquareImageView(Context context) {
        super(context);
    }

    public KcpSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KcpSquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);*/

        try {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                setMeasuredDimension(0, 0);
            } else {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) width;
                setMeasuredDimension(width, height);
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
