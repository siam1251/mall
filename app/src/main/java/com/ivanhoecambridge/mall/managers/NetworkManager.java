package com.ivanhoecambridge.mall.managers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MainActivity;

/**
 * Created by Kay on 2016-11-29.
 */

public class NetworkManager {

    public static boolean isConnected(Context context){
        if(!KcpUtility.isNetworkAvailable(context)){
            if(context instanceof MainActivity) {
                ((MainActivity)context).showSnackBar(R.string.warning_no_internet_connection, 0, null);
                return false;
            } else {
                View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(rootView, R.string.warning_no_internet_connection, Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);

                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(context.getResources().getColor(R.color.white));
                snackbar.show();
                return false;
            }
        } return true;
    }
}
