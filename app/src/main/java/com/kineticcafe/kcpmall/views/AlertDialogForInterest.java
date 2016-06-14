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

    public AlertDialog.Builder getAlertDialog (Context context, final DialogAnsweredListener dialogAnsweredListener){

        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.alertdialog_interest, null);
        TextView tvAlertDialogInterest = (TextView) v.findViewById(R.id.tvAlertDialogInterest);
        tvAlertDialogInterest.setText(context.getResources().getString(R.string.warning_exit_interest));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.action_unsaved_changes));
        builder.setPositiveButton(context.getResources().getString(R.string.action_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogAnsweredListener.okClicked();
                return;
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setView(v);
        return builder;
    }
}
