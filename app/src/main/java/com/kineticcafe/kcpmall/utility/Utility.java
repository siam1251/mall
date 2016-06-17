package com.kineticcafe.kcpmall.utility;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    /*public Drawable getDrawableWithColorChange(Context context, int drawableId){
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

    *//** Returns an interpoloated color, between <code>a</code> and <code>b</code> *//*
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



    public static void cacheFavs(Context context, String key, String value) {
        String oldFavs = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(key, "");
        String[] oldFavsArray = {};

        if(oldFavs.equals("")) {
            cacheToPreferences(context, key, value);
            return;
        } else {
            oldFavsArray = oldFavs.split(",");
            String newFavs = "";

            boolean valueExist = false;
            for(int i = 0; i < oldFavsArray.length; i++){
                if(oldFavsArray[i].equals(value)){
                    valueExist = true;
                } else {
                    newFavs = newFavs + oldFavsArray[i] + ",";
                }
            }

            if(!valueExist){
                oldFavs = oldFavs + "," + value;
                cacheToPreferences(context, key, oldFavs);
            } else {
                if(newFavs != "" && newFavs.substring(newFavs.length() - 1).equals(",")) newFavs = newFavs.substring(0, newFavs.length() - 1);
                cacheToPreferences(context, key, newFavs);
            }
        }
    }

    private static void cacheToPreferences(Context context, String key, String value){
        Log.d("cacheToPreferences", value);
        SharedPreferences.Editor editor = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean isFavs(Context context, String key, String storeId){
        return isFavs(context, key, Integer.valueOf(storeId));
    }

    public static boolean isFavs(Context context, String key, int storeId){
        try {
            int[] favStoreList = loadFromCache(context, key, 0);
            for(int i = 0; i < favStoreList.length; i++){
                if(favStoreList[i] == storeId){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static int[] loadFromCache(Context context, String key, int defaultValue) {
        String favStores = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(key, "");
        String[] _favStores = {};

        if (favStores.length() > 0)
            _favStores = favStores.split(",");

        final int[] ints = new int[_favStores.length];
        for (int i=0; i < _favStores.length; i++) {
            ints[i] = Integer.parseInt(_favStores[i]);
        }

        return ints;
    }

    public static String loadFromCache(Context context, String key, @Nullable String defaultValue) {
        if(key == null || context == null) return "";
        return context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(key, defaultValue == null ? "" : defaultValue);
    }


    public static void saveGson(Context context, String key, Object obj) {
        SharedPreferences editor = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = editor.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }

    public static ArrayList<Integer> loadGsonArrayList(Context context, String key){
        Gson gson = new Gson();
        String json = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(key, "");
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer> obj = gson.fromJson(json, listType);
        if(obj == null) return new ArrayList<Integer>();
        else return obj;
    }

    public static ArrayList<String> loadGsonArrayListString(Context context, String key){
        Gson gson = new Gson();
        String json = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(key, "");
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> obj = gson.fromJson(json, listType);
        if(obj == null) return new ArrayList<String>();
        else return obj;
    }

    public static HashMap<Integer, String> loadGsonHashMap(Context context, String key){
        Gson gson = new Gson();
        String json = context.getSharedPreferences("PreferenceManager", Context.MODE_PRIVATE).getString(key, "");
        Type listType = new TypeToken<HashMap<Integer, String>>() {}.getType();
        HashMap<Integer, String> obj = gson.fromJson(json, listType);
        if(obj == null) return new HashMap<Integer, String>();
        else return obj;
    }


    public static String convertArrayListToStringWithComma(ArrayList arrayList){
        return android.text.TextUtils.join(",", arrayList);
    }

    public static ArrayList<String> getRemovedObjectFromCache(ArrayList<String> cachedHM, ArrayList<String> newHM){
        ArrayList<String> differenceHM = new ArrayList<String>();
        for (final String value : cachedHM) {
            if (!newHM.contains(value)) {
                differenceHM.add(value);
            }
        }
        return differenceHM;
    }



    public static boolean isTwoIntegerListsEqual(List<Integer> one, List<Integer> two){
        if (one == null && two == null){
            return true;
        }
        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }
        one = new ArrayList<Integer>(one);
        two = new ArrayList<Integer>(two);

        Collections.sort(one);
        Collections.sort(two);

        return one.equals(two);
    }

    public static boolean isTwoStringListsEqual(List<String> one, List<String> two){
        if (one == null && two == null){
            return true;
        }

        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }

        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);

        return one.equals(two);
    }*/
}
