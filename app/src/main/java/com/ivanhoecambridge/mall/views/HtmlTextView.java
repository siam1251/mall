package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivanhoecambridge.mall.R;

import org.xml.sax.XMLReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kay on 2016-06-28.
 */
public class HtmlTextView {

    public static void setHtmlTextView(Context context, TextView textView, String text, int linkTextColor){
        textView.setText(Html.fromHtml(text, new CustomImageGetter(context, textView), new HtmlTagHandler()));
        textView.setLinkTextColor(context.getResources().getColor(linkTextColor));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setAutoLinkMask(Linkify.ALL);
    }

    public static class HtmlTagHandler implements Html.TagHandler {
        boolean first= true;
        String parent=null;
        int index=1;
        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {

            if(tag.equals("ul")) parent="ul";
            else if(tag.equals("ol")) parent="ol";
            if(tag.equals("li")){
                if(parent.equals("ul")){
                    if(first){
                        output.append("\n\t• ");
                        first= false;
                    }else{
                        first = true;
                    }
                }
                else{
                    if(first){
                        output.append("\n\t"+index+". ");
                        first= false;
                        index++;
                    }else{
                        first = true;
                    }
                }
            }
        }
    }

    public static class CustomImageGetter implements Html.ImageGetter {
        Context context;
        TextView tv;
        public CustomImageGetter(Context context, TextView tv){
            this.context = context;
            this.tv = tv;
        }

        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable d = new LevelListDrawable();
            Drawable empty = context.getResources().getDrawable(R.drawable.placeholder_square);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

            new LoadImage().execute(source, d);

            return d;

        }

        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

            private LevelListDrawable mDrawable;

            @Override
            protected Bitmap doInBackground(Object... params) {
                String source = (String) params[0];
                mDrawable = (LevelListDrawable) params[1];
                try {
                    InputStream is = new URL(source).openStream();
                    return BitmapFactory.decodeStream(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    BitmapDrawable d = new BitmapDrawable(bitmap);
                    mDrawable.addLevel(1, 1, d);

                    float scaleWidthToHeight = (float) bitmap.getWidth() / bitmap.getHeight();
                    mDrawable.setBounds ( 0, 0, tv.getWidth(), (int) (tv.getWidth() / scaleWidthToHeight));
//                    mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mDrawable.setLevel(1);
                    // i don't know yet a better way to refresh TextView
                    // mTv.invalidate() doesn't work as expected
                    CharSequence t = tv.getText();
                    tv.setText(t);
                }
            }
        }
    }

}

