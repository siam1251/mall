package com.kineticcafe.kcpmall.utility;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-02.
 */
public class Utility {

    public Drawable getDrawableWithColorChange(Context context, int drawableId){
        Drawable drawable = context.getResources().getDrawable(drawableId);
        try {
            drawable.setColorFilter(new
                    PorterDuffColorFilter(0xffff00, PorterDuff.Mode.MULTIPLY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;

//        Drawable icon = ContextCompat.getDrawable(context, drawableId).mutate();
//        TypedValue typedValue = new TypedValue();
//        context.getTheme().resolveAttribute(R.attr.colorIcon, typedValue, true);
//        icon.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP);


    }

    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    /** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
    public static int interpolateColor(int a, int b, float proportion) {
//        float[] hsva = new float[3];
//        float[] hsvb = new float[3];
//        Color.colorToHSV(a, hsva);
//        Color.colorToHSV(b, hsvb);
//        for (int i = 0; i < 3; i++) {
//            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
//        }
//        return Color.HSVToColor(hsvb);

        return (int) new ArgbEvaluator().evaluate(proportion, a, b);
    }

    public static int dpToPx(Activity activity, float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float) dp, activity.getResources().getDisplayMetrics());
    }

    //convert px to corresponding dp
    public static int pxToDp(Activity activity, float px){
        return (int) ((px/activity.getResources().getDisplayMetrics().density)+0.5);
    }

}
