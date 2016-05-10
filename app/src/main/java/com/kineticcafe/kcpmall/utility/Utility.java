package com.kineticcafe.kcpmall.utility;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

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
    }

    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    /** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
    public static int interpolateColor(int a, int b, float proportion) {
        return (int) new ArgbEvaluator().evaluate(proportion, a, b);
    }

    public static int dpToPx(Activity activity, float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float) dp, activity.getResources().getDisplayMetrics());
    }

    public static int pxToDp(Activity activity, float px){
        return (int) ((px/activity.getResources().getDisplayMetrics().density)+0.5);
    }

    public static float getFloat(Context context, int id){
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(id, outValue, true);
        float value = outValue.getFloat();
        return value;
    }


    public static int getScreenHeight(Context context) {
        int height;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;

        return height;
    }

    public static int getScreenWidth(Context context) {
        int width;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        return width;
    }






}
