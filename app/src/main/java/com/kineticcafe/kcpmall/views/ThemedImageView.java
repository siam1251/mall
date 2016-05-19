package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-02.
 */
public class ThemedImageView extends ImageView implements View.OnClickListener{

    private int mFilterColor;
    private int mSelectedFilterColor;
    public ThemedImageView(Context context) {
        this(context, null);
    }

    public ThemedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThemedImageView);
        mFilterColor = a.getColor(R.styleable.ThemedImageView_filterColor, context.getResources().getColor(R.color.themeColor));
        mSelectedFilterColor = a.getColor(R.styleable.ThemedImageView_filterColorUnselected, context.getResources().getColor(R.color.transparent));
        setColorFilter(mFilterColor);
        this.setOnClickListener(this);
    }

    public ThemedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(View v) {
        if(isSelected()){
            setColorFilter(mSelectedFilterColor);
        } else {
            setColorFilter(mFilterColor);
        }
    }
}
