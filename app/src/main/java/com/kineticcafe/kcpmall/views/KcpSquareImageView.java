package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.ZoomableImage;
import com.kineticcafe.kcpmall.utility.Utility;

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
