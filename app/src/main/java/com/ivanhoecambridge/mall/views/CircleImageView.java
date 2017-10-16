package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.support.v7.widget.AppCompatImageView;

import com.ivanhoecambridge.mall.R;

/**
 * Created by Kay on 2016-06-20.
 */
public class CircleImageView extends AppCompatImageView {

    private Context mContext;
    private int borderFilterColor, defaultFilterColor;
    private int srcFilterColor;
    private PorterDuff.Mode defaultPorterDuffMode, currentPorterDuffMode;
    
    public CircleImageView(Context context) {
        super(context);
        mContext = context;
        initDefaults(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        defaultFilterColor = a.getColor(R.styleable.CircleImageView_borderFilterColor, Color.WHITE);
        a.recycle();
        initDefaults(context);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDefaults(context);
    }

    private void initDefaults(Context context) {
        borderFilterColor = defaultFilterColor;
        srcFilterColor = ContextCompat.getColor(context, R.color.profile_default_img_filter_color);
        defaultPorterDuffMode = PorterDuff.Mode.DST_IN;
        currentPorterDuffMode = defaultPorterDuffMode;
    }


    /**
     * Set the border color.
     * @param color Color resource
     */
    public void setBorderFilterColor(@ColorRes int color) {
        borderFilterColor = ContextCompat.getColor(mContext, color);
        invalidate();
    }

    /**
     * Sets the src filter PorterDuff.Mode
     * @param mode PorterDuff.Mode
     */
    public void setSrcFilterMode(PorterDuff.Mode mode) {
        currentPorterDuffMode = mode;
        invalidate();
    }

    /**
     * Resets the border and default src image to it's original color.
     */
    public void resetToDefaultColors(boolean shouldResetPorterDuff) {
        if (shouldResetPorterDuff) {
            currentPorterDuffMode = defaultPorterDuffMode;
        }
        borderFilterColor = defaultFilterColor;
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        roundBitmap = getCircularBitmapWithWhiteBorder(roundBitmap, dpToPx(mContext, 2));
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    public static int dpToPx(Context context, float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float) dp, context.getResources().getDisplayMetrics());
    }

    public Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
                                                          int borderWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(borderFilterColor);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factor), (int)(bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(srcFilterColor);
        canvas.drawCircle(radius / 2 + 0.7f,
                radius / 2 + 0.7f, radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(currentPorterDuffMode));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

}