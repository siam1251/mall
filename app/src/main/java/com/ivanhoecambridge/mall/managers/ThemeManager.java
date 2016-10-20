package com.ivanhoecambridge.mall.managers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.ivanhoecambridge.mall.R;

/**
 * Created by Kay on 2016-08-31.
 */
public class ThemeManager {

    public static Drawable getThemedMenuDrawable(Context context, int drawable){
        final Drawable menuDrawable = context.getResources().getDrawable(drawable);
        menuDrawable.setColorFilter(context.getResources().getColor(R.color.tabSelectedTextColor), PorterDuff.Mode.SRC_ATOP);
        return menuDrawable;
    }
}
