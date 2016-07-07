package com.kineticcafe.kcpmall.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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
}
