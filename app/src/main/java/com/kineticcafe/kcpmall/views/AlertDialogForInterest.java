package com.kineticcafe.kcpmall.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.InterestedCategoryActivity;

/**
 * Created by Kay on 2016-06-03.
 */
public class AlertDialogForInterest {

    public interface DialogAnsweredListener{
        void okClicked();
    }

    public AlertDialog.Builder getAlertDialog (Context context, int title, int msg, int positiveBtn, int negativeBtn, final DialogAnsweredListener dialogAnsweredListener){
        return getAlertDialog(
                context,
                context.getResources().getString(title),
                context.getResources().getString(msg),
                context.getResources().getString(positiveBtn),
                context.getResources().getString(negativeBtn),
                dialogAnsweredListener);
    }

    public AlertDialog.Builder getAlertDialog (Context context, String title, String msg, String positiveBtn, String negativeBtn, final DialogAnsweredListener dialogAnsweredListener){

        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.alertdialog_interest, null);
        TextView tvAlertDialogInterest = (TextView) v.findViewById(R.id.tvAlertDialogInterest);
        tvAlertDialogInterest.setText(msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogAnsweredListener.okClicked();
                return;
            }
        });
        builder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setView(v);
        return builder;
    }
}
