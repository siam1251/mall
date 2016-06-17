package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-17.
 */
public class AlphaTextView  extends TextView {

    private int mAlpha;
    public AlphaTextView(Context context) {
        super(context);
//        init();
    }

    public AlphaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AlphaTextView);
        mAlpha = a.getColor(R.styleable.AlphaTextView_alpha, 133);
        getBackground().setAlpha(mAlpha);
    }

    public AlphaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}