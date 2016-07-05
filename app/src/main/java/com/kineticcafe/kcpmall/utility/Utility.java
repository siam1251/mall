package com.kineticcafe.kcpmall.utility;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
}
