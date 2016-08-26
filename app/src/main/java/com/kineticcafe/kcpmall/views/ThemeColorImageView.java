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
public class ThemeColorImageView extends ImageView {

    private int mFilterColor;
    private int mSelectedFilterColor;
    public ThemeColorImageView(Context context) {
        this(context, null);
    }

    public ThemeColorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThemeColorImageView);
        mFilterColor = a.getColor(R.styleable.ThemeColorImageView_filterColor, context.getResources().getColor(R.color.transparent));
        mSelectedFilterColor = a.getColor(R.styleable.ThemeColorImageView_filterColorUnselected, context.getResources().getColor(R.color.transparent));
        setColorFilter(mFilterColor);
    }

    public ThemeColorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * @param filterColor normal state color
     * @param selectedFilterColor selected color
     */
    public void setColor(int filterColor, int selectedFilterColor){
        mFilterColor = filterColor;
        mSelectedFilterColor = selectedFilterColor;
        setColorFilter(mFilterColor);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(isSelected()){
            setColorFilter(mSelectedFilterColor);
        } else {
            setColorFilter(mFilterColor);
        }
    }
}
