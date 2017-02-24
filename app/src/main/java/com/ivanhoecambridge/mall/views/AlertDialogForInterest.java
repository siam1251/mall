package com.ivanhoecambridge.mall.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.adapterHelper.GiftCardRecyclerViewAdapter;
import com.ivanhoecambridge.mall.managers.GiftCardManager;

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

    public AlertDialog.Builder getAlertDialog (Context context, @Nullable String title, String msg, String positiveBtn, @Nullable String negativeBtn, final DialogAnsweredListener dialogAnsweredListener){

        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.alertdialog_interest, null);
        TextView tvAlertDialogInterest = (TextView) v.findViewById(R.id.tvAlertDialogInterest);
        tvAlertDialogInterest.setText(msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(title != null) builder.setTitle(title);
        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogAnsweredListener.okClicked();
                return;
            }
        });
        if(negativeBtn != null) builder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.html_link_text_color));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.html_link_text_color));


        return builder;
    }


    public AlertDialog.Builder showEditTextAlertDialog(final Context context, String preFillText, String title, String positiveBtn, String negativeBtn, final DialogEditTextLinstener dialogEditTextLinstener){
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
                tvCharacterLeft.setText(50 - s.toString().length() + "");
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
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.html_link_text_color));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.html_link_text_color));

        return builder;
    }

    public AlertDialog.Builder showGCAlertDialog(final Context context, GiftCardRecyclerViewAdapter giftCardRecyclerViewAdapter, DialogInterface.OnClickListener positiveButton, DialogInterface.OnClickListener negativeButton){
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.alertdialog_gc, null);

        RecyclerView rvGiftCard = (RecyclerView) v.findViewById(R.id.rvGiftCard);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvGiftCard.setLayoutManager(linearLayoutManager);
        rvGiftCard.setAdapter(giftCardRecyclerViewAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_remove_gc));
        builder.setPositiveButton(context.getString(R.string.action_ok), positiveButton);
        builder.setNegativeButton(context.getString(R.string.action_cancel), negativeButton);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.html_link_text_color));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.html_link_text_color));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = KcpUtility.dpToPx((Activity) context, 400);
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        return builder;
    }
}