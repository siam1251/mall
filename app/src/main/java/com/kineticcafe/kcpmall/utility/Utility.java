package com.kineticcafe.kcpmall.utility;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

    private static boolean exist = false;
    public synchronized static boolean existsInServer(final String URLName){


        new Thread(){
            @Override
            public void run(){
                try {
                    //TODO: look into this
                    new RetrieveFeedTask().execute(URLName);

                    long CONNECT_TIMEOUT_SEC = 20;
                    // Use okhttp library to check existsInServer(). The performance speed increased.
                    OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(timeout, TimeUnit.SECONDS)
//                    .writeTimeout(timeout, TimeUnit.SECONDS)
//                    .readTimeout(timeout, TimeUnit.SECONDS)
                            .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                            .writeTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                            .readTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                            .build();

                    Request request = new Request.Builder()

                            .url(URLName)
                            .build();

                    Response response = client.newCall(request).execute();
                    if(response.code() == 200)
                        exist = true;
                    else
                        exist = false;
                }
                catch (Exception e) {
                    exist = false;
                }
            }
        }.start();
        return exist;


        /*try {
            //TODO: look into this
            new RetrieveFeedTask().execute(URLName);

            long CONNECT_TIMEOUT_SEC = 20;
            // Use okhttp library to check existsInServer(). The performance speed increased.
            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(timeout, TimeUnit.SECONDS)
//                    .writeTimeout(timeout, TimeUnit.SECONDS)
//                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .writeTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .readTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()

                    .url(URLName)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.code() == 200)
                return true;
            else
                return false;
        }
        catch (Exception e) {
            return false;
        }*/
       /* try {
            return new RetrieveFeedTask().execute(URLName).get();
        }
        catch (Exception e) {
            return false;
        }*/
    }

    public static class RetrieveFeedTask extends AsyncTask<String, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(String... urls) {
            try {

                String URLName = urls[0].toString();

                long CONNECT_TIMEOUT_SEC = 20;
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                        .writeTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                        .readTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(URLName)
                        .build();

                Response response = client.newCall(request).execute();
                if(response.code() == 200)
                    return true;
                else
                    return false;
            } catch (Exception e) {
                this.exception = e;
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }







}
