package com.kineticcafe.kcpmall.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

/**
 * Created by Kay on 2016-05-02.
 */
public class Utility {


    public static void makeCall(Context context, String number){
        if(number == null || number.equals("")) return;
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+ number));
        context.startActivity(callIntent);
    }

    public static void makeCallWithAlertDialog(final Context context, final int title, final int msg, final int positiveBtn, final int negativebtn, final String number){
        if(number == null || number.equals("")) return;
        AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
        alertDialogForInterest.getAlertDialog(
                context,
                title,
                msg,
                positiveBtn,
                negativebtn,
                new AlertDialogForInterest.DialogAnsweredListener() {
                    @Override
                    public void okClicked() {
                        makeCall(context, number);
                    }
                }).show();
    }

    public static void makeCallWithAlertDialog(final Context context, final String title, final String msg, final String positiveBtn, final String negativebtn, final String number){
        if(number == null || number.equals("")) return;
        AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
        alertDialogForInterest.getAlertDialog(
                context,
                title,
                msg,
                positiveBtn,
                negativebtn,
                new AlertDialogForInterest.DialogAnsweredListener() {
                    @Override
                    public void okClicked() {
                        makeCall(context, number);
                    }
                }).show();
    }

    public static void openWebPage(Context context, String url){
        if(url == null || url.equals("")) return;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }


    public static void openGoogleMap(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(i);
    }

    public static void openGoogleMapWithAddress(Context context, String address){
        String map = "https://maps.google.com/maps?q=" + address;
        openGoogleMap(context, map);
    }

    public static void openGoogleMapWithAddressWithDrivingMode(Context context, String address){
        String map = "https://maps.google.com/maps?daddr=" + address + "&dirflg=d";
        openGoogleMap(context, map);
    }

    public static void openGoogleMapWithAddressWithTransitMode(Context context, String address){
        String map = "https://maps.google.com/maps?daddr=" + address + "&dirflg=r";
        openGoogleMap(context, map);
    }

    public static void openGoogleMapWithAddressWithWalkingMode(Context context, String address){
        String map = "https://maps.google.com/maps?daddr=" + address + "&dirflg=w";
        openGoogleMap(context, map);
    }

    public static void sendEmail(Context context, String emailAddress, String subject){

        String version = null;
        String sOSVersion = null;
        String sManufacturer = null;
        String sModel = null;
        try {
            PackageInfo pInfo;
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            sOSVersion = Build.VERSION.RELEASE;
            sManufacturer = Build.MANUFACTURER;
            sModel = Build.MODEL;
        } catch (Exception e) {
        }

        String message = "";
        if(version != null && !version.equals("")) {
            message = "App Version : " + version + "\n";
        }
        if(sOSVersion != null && !sOSVersion.equals("")) {
            message += "OS Version : " + sOSVersion + "\n";
        }
        if((sManufacturer != null && !sManufacturer.equals("")) || (sModel != null && !sModel.equals(""))) {
            message += "Device : " + sManufacturer + " " + sModel + "\n";
        }
        message += "\n";

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailAddress});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , message + "\n");

        try {
            context.startActivity(Intent.createChooser(i, "Send Feedback"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setToolbarBackground(Toolbar toolbar, @Nullable Drawable drawable){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackgroundDrawable(drawable);
        } else {
            toolbar.setBackground(drawable);
        }
    }

    public static void startSqueezeAnimationForFav(SqueezeListener squeezeAnimationListener, Activity act, final View view){
        TypedValue typedValue = new TypedValue();
        act.getResources().getValue(R.dimen.squeeze_anim_scale_fav, typedValue, true);
        float scaleValue = typedValue.getFloat();
        Integer duration = act.getResources().getInteger(R.integer.squeeze_anim_duration_fav);
        startSqueezeAnimation(squeezeAnimationListener, act, view, scaleValue, duration);
    }

    public static void startSqueezeAnimationForInterestedCat(SqueezeListener squeezeAnimationListener, Activity act, final View view){
        TypedValue typedValue = new TypedValue();
        act.getResources().getValue(R.dimen.squeeze_anim_scale_cat, typedValue, true);
        float scaleValue = typedValue.getFloat();
        Integer duration = act.getResources().getInteger(R.integer.squeeze_anim_duration_cat);
        startSqueezeAnimation(squeezeAnimationListener, act, view, scaleValue, duration);
    }

    public static void startSqueezeAnimation(SqueezeListener squeezeAnimationListener, Activity act, final View view, float scale, long duration){
        mSqueezeListener = squeezeAnimationListener;
//        final Animation first= AnimationUtils.loadAnimation(act, R.anim.anim_squeeze_out);
//        final Animation second= AnimationUtils.loadAnimation(act, R.anim.anim_squeeze_in);

        final Animation first =  new ScaleAnimation(1, scale, 1, scale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
//        first.setDuration(90);
        first.setDuration(duration);
        first.setFillAfter(true);

        final Animation second = new ScaleAnimation(scale, 1, scale, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
//        second.setDuration(90);
        second.setDuration(duration);
        second.setFillAfter(true);

        first.reset();
        second.reset();
        view.clearAnimation();
        view.startAnimation(first);
        first.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(second);
            }
        });
        second.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mSqueezeListener != null) mSqueezeListener.OnSqueezeAnimationDone();
            }
        });
    }

    private static SqueezeListener mSqueezeListener;
    public static interface SqueezeListener {
        public void OnSqueezeAnimationDone();
    }


    public static Bitmap getBlurredImage(Context context, Bitmap sentBitmap, int radius){

        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}
