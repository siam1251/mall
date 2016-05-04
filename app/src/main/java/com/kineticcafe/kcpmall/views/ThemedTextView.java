package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-02.
 */
public class ThemedTextView extends TextView {
    public ThemedTextView(Context context) {
        this(context, null);
        init();
    }

    public ThemedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();

    }

    public ThemedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init(){
        this.setTextColor(getResources().getColor(R.color.themeColor));
    }
}
