package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ivanhoecambridge.mall.R;

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
        mSelectedFilterColor = a.getColor(R.styleable.ThemeColorImageView_filterColorSelected, context.getResources().getColor(R.color.transparent));
        setColorFilter(mFilterColor);
    }

    public ThemeColorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * @param filterColor normal state color (setSelected(false))
     * @param selectedFilterColor selected color (setSelected(true))
     */
    public void setColor(int filterColor, int selectedFilterColor){
        mFilterColor = filterColor;
        mSelectedFilterColor = selectedFilterColor;
        setColorFilter(mFilterColor);
    }

    public int getFilterColor(){
        return mFilterColor;
    }

    public int getSelectedFilterColor(){
        return mSelectedFilterColor;
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
