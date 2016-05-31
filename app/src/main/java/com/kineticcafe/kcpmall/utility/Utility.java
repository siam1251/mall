package com.kineticcafe.kcpmall.utility;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    public static void applyBlur(final Context context, final ImageView imageView, final TextView textView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                imageView.buildDrawingCache();

                Bitmap bmp = imageView.getDrawingCache();
                blur(context, bmp, textView);
                return true;
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void blur(Context context, Bitmap bkg, View view) {
        if(view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0 ) return;
//        float radius = 20;
        float radius = 25;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()),
                (int) (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(
                context.getResources(), overlay));

        rs.destroy();
    }
}
