package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-06-28.
 */
public class HtmlTextView {

    public static void setHtmlTextView(Context context, TextView textView, String text, int linkTextColor){
        textView.setText(Html.fromHtml(text));
        textView.setLinkTextColor(context.getResources().getColor(linkTextColor));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setAutoLinkMask(Linkify.ALL);
    }

}

