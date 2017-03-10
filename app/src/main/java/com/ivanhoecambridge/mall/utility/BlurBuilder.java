package com.ivanhoecambridge.mall.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Kay on 2016-07-27.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class BlurBuilder {

    private static final float BITMAP_SCALE = 0.1f;
    private static final float BLUR_RADIUS = 25f;
    private static ImageView mImageView;
    private static Context mContext;

    public static void blur(Context context, View v, ImageView ivToApplyBlur) {
        mContext = context;
        mImageView = ivToApplyBlur;
        new BlurBitmap().execute(v);
    }

    private static class BlurBitmap extends AsyncTask<View, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(View... params) {
            if(params == null) return null;

            Bitmap image = getScreenshot(params[0]);
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(mContext);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            if(mContext != null) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mImageView != null) mImageView.setBackground(new BitmapDrawable(mContext.getResources(), result));
                    }
                });
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public static Bitmap getScreenshot(View v) {
        if(v.getWidth() == 0 || v.getHeight() == 0) return null;
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}
