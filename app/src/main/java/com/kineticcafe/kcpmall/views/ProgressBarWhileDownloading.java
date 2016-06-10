package com.kineticcafe.kcpmall.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-06-08.
 */
public class ProgressBarWhileDownloading {
    private static ProgressDialog pd;
    private static View progressBarView;
    private static FrameLayout parentLayout;
    private static Context mContext;

    public static void showProgressDialog(Context context, final boolean enabled){
        /*if(pd == null) {
            pd = new ProgressDialog(context, R.style.ProgressBarStyle);
            pd.setProgressStyle(android.R.attr.progressBarStyleLarge);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        if(enabled){
            pd.show();
        } else {
            pd.dismiss();
        }*/

        if(mContext != null && parentLayout != null && progressBarView != null) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parentLayout.removeView(progressBarView);
                }
            });
        }

        mContext = context;
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                parentLayout = (FrameLayout)((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                progressBarView = vi.inflate(R.layout.layout_loading_item, null);
                progressBarView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        parentLayout.removeView(progressBarView);
                        return false;
                    }
                });

                if(enabled){
                    parentLayout.addView(progressBarView);
                } else {
                    parentLayout.removeView(progressBarView);
                }
            }
        });


    }
}
