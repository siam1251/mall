package com.kineticcafe.kcpmall.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.InterestedCategoryActivity;
import com.kineticcafe.kcpmall.activities.MainActivity;
import com.kineticcafe.kcpmall.utility.Utility;

/**
 * Created by Kay on 2016-06-03.
 */
public class AlertDialogForInterest {

    public interface DialogAnsweredListener{
        void okClicked();
    }

    public interface DialogEditTextLinstener{
        void saveClicked(String text);
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

    public AlertDialog.Builder getEditTextAlertDialog (final Context context, String preFillText, String title, String positiveBtn, String negativeBtn, final DialogEditTextLinstener dialogEditTextLinstener){

        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.alertdialog_edittext, null);
        final EditText etAlertDialog = (EditText) v.findViewById(R.id.etAlertDialog);
        final TextView tvCharacterLeft = (TextView) v.findViewById(R.id.tvCharacterLeft);
        if(!preFillText.equals("")) {
            etAlertDialog.setText(preFillText);
            tvCharacterLeft.setText(50 - preFillText.toString().length() + "/50");
        }

        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        etAlertDialog.setTypeface(face);

        final InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        etAlertDialog.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        etAlertDialog.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {}

            @Override
            public void afterTextChanged(Editable s) {
                tvCharacterLeft.setText(50 - s.toString().length() + "/50");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogEditTextLinstener.saveClicked(etAlertDialog.getText().toString());
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                return;
            }
        });
        builder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                return;
            }
        });
        builder.setView(v);
        return builder;
    }
}