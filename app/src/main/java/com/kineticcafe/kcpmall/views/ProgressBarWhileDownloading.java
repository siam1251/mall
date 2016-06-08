package com.kineticcafe.kcpmall.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-06-08.
 */
public class ProgressBarWhileDownloading {
    public interface DialogAnsweredListener{
        void okClicked();
    }

    public void insertProgressBar(Context context, ViewGroup viewGroup) {
        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
//        ProgressBar pb = new ProgressBar(context, null, R.style.progressBarMedium);
//        pb.setBackgroundColor(context.getResources().getColor(R.color.gray));
//        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                550, // Width in pixels
                RelativeLayout.LayoutParams.WRAP_CONTENT // Height of progress bar
        );*/
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewGroup.getLayoutParams();
        /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                100, // Width in pixels
                100 // Height of progress bar
        );
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        pb.setLayoutParams(lp);*/
        viewGroup.addView(pb);
    }
}
