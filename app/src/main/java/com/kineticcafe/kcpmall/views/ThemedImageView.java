package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-02.
 */
public class ThemedImageView extends ImageView {

    public ThemedImageView(Context context) {
        this(context, null);
        init();
    }

    public ThemedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();

    }

    public ThemedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

        // real work here
    }

    public void init(){
        setColorFilter(getResources().getColor(R.color.themeColor));
    }


}
