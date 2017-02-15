package com.ivanhoecambridge.mall.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;
import com.mappedin.jpct.RGBColor;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    public static void openFacebook(Context context, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    public static void openInstagramWithDialog(final Context context, final String title, final String msg, final String positiveBtn, final String negativebtn, final String userName){
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
                        openInstagramWithName(context, userName);
                    }
                }).show();
    }

    public static void openInstagramWithName(Context context, String userName){
        Uri uri = Uri.parse("http://instagram.com/_u/" + userName);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            context.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + userName)));
        }
    }

    public static void openTwitter(Context context, String url){
        Intent intent = null;
        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }
        context.startActivity(intent);
    }

    public static void playVideo(Context context, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setDataAndType(Uri.parse(url), "video/*");
        context.startActivity(intent);
    }

    public static boolean isAppInstalled(Context context, String packageName){
        try{
            ApplicationInfo info = context.getPackageManager().
                    getApplicationInfo(packageName, 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
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

    public static void startSqueezeAnimationForInterestedParking(SqueezeListener squeezeAnimationListener, Activity act, final View view){
        TypedValue typedValue = new TypedValue();
        act.getResources().getValue(R.dimen.squeeze_anim_scale_cat, typedValue, true);
        float scaleValue = typedValue.getFloat();
        Integer duration = act.getResources().getInteger(R.integer.squeeze_anim_duration_parking);
        startSqueezeAnimation(squeezeAnimationListener, act, view, scaleValue, duration);
    }

    public static void startSqueezeAnimation(SqueezeListener squeezeAnimationListener, Activity act, final View view, float scale, long duration){
        mSqueezeListener = squeezeAnimationListener;
//        final Animation first= AnimationUtils.loadAnimation(act, R.anim.anim_squeeze_out);
//        final Animation second= AnimationUtils.loadAnimation(act, R.anim.anim_squeeze_in);

        final Animation first =  new ScaleAnimation(1, scale, 1, scale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        first.setDuration(duration);
        first.setFillAfter(true);

        final Animation second = new ScaleAnimation(scale, 1, scale, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
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

    public static void closeKeybaord(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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


    public static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static CharSequence setSpanBetweenTokens(CharSequence text,
                                                    String token, CharacterStyle... cs)
    {
        // Start and end refer to the points where the span will apply
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);

        if (start > -1 && end > -1)
        {
            // Copy the spannable string to a mutable spannable string
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            for (CharacterStyle c : cs)
                ssb.setSpan(c, start, end, 0);

            // Delete the tokens before and after the span
            ssb.delete(end, end + tokenLen);
            ssb.delete(start - tokenLen, start);

            text = ssb;
        }

        return text;
    }

    public static int convert2IntColor(RGBColor color) {
        if(color != null) {
            int alpha = color.getAlpha();
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            return Color.argb(alpha, red, green, blue);
        } else {
            return 0;
        }
    }

    public static HostnameVerifier getTrustToCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        return allHostsValid;
    }

    public static void blurImageView(final Context context, final ImageView ivToBlur, final ImageView ivToApplyBlur){
        if(context == null || ivToBlur == null || ivToApplyBlur == null) return;
        if (ivToBlur.getWidth() > 0) {
            blurImage(context, ivToBlur, ivToApplyBlur);
        } else {
            ivToBlur.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    blurImage(context, ivToBlur, ivToApplyBlur);
                }
            });
        }
    }

    private static void blurImage(Context context, final ImageView ivToBlur, final ImageView ivToApplyBlur){
        Bitmap image = BlurBuilder.blur(ivToBlur);
        if(image != null) ivToApplyBlur.setBackground(new BitmapDrawable(context.getResources(), image));
    }


    private static LruCache<String, Bitmap> mMemoryCache;

    public static void addBlurredBitmapToMemoryCache(final View v, final String key){
        if( v == null) return;
        if (v.getWidth() > 0) {
            Bitmap blurred = BlurBuilder.blur(v);
            addBitmapToMemoryCache(key, blurred);
        } else {
            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Bitmap blurred = BlurBuilder.blur(v);
                    addBitmapToMemoryCache(key, blurred);
                }
            });
        }
    }

    private static void cacheBitmap(Bitmap bitmap){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if(mMemoryCache == null) cacheBitmap(bitmap);
        if (getBitmapFromMemCache(key, true) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key, boolean removeAfter) {
        if(mMemoryCache == null) return null;
        Bitmap bitmap = mMemoryCache.get(key);
        if(removeAfter) mMemoryCache.remove(key);
        return bitmap;

    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static int getColorWithAlpha(int color, int alpha){
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int getColorWithAlpha(Context context, @ColorRes int colorResource, int alpha){
        int color = context.getResources().getColor(colorResource);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static float getFloatValue(Context context, int floatResource){
        try {
            TypedValue outValue = new TypedValue();
            context.getResources().getValue(floatResource, outValue, true);
            float value = outValue.getFloat();
            return value;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    public static Drawable getAdaptiveRippleDrawable(int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor),
                    null, getRippleMask(normalColor));
        } else {
            return getStateListDrawable(normalColor, pressedColor);
        }
    }

    private static Drawable getRippleMask(int color) {
        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, 3);// 3 is radius of final ripple, instead of 3 you can give required final radius

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);

        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);

        return shapeDrawable;
    }

    public static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(pressedColor));
        states.addState(new int[]{}, new ColorDrawable(normalColor));
        return states;
    }


}
