package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Kay on 2016-05-05.
 */
public class KcpSquareTextView extends TextView {
    public KcpSquareTextView(Context context) {
        super(context);
    }

    public KcpSquareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KcpSquareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) width;
            setMeasuredDimension(width, height);
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
