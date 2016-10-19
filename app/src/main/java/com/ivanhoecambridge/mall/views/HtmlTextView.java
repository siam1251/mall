package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

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

