package com.kineticcafe.kcpmall.factory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.GlideAnimationFactory;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.activities.ZoomableImage;
import com.kineticcafe.kcpmall.utility.Utility;

/**
 * Created by Kay on 2016-05-06.
 */
public class GlideFactory {

    public void glideWithDefaultRatio(Context context, int drawable, ImageView imageView){
        if(imageView == null) return;
        Glide.with(context)
                .load(drawable)
//                .override(Utility.getScreenWidth(context), (int) (Utility.getScreenWidth(context) / Utility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .crossFade() //TODO: necessary?
                .into(imageView);

    }

    public void glideWithDefaultRatio(Context context, String url, ImageView imageView){
        if(imageView == null) return;
        Glide.with(context)
                .load(url)
                .override(Utility.getScreenWidth(context), (int) (Utility.getScreenWidth(context) / Utility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .crossFade()
                .into(imageView);
    }

    /**
     * This will first check whether the image is available at the url. if not, it will just set the errorDrawable. The reason for not using .error(errorDrawable)
     * is because it takes longer for glide to figure whether the image is available and decide to use the errorDrawable.
     * @param context
     * @param url
     * @param imageView
     * @param errorDrawable errorDrawable is passed
     */
    public void glideWithDefaultRatio(final Context context, final String url, final ImageView imageView, final int errorDrawable){
        Glide.with(context)
                .load(url)
                .override(Utility.getScreenWidth(context), (int) (Utility.getScreenWidth(context) / Utility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .crossFade()
                .error(errorDrawable)
                .placeholder(errorDrawable)
                .into(imageView);
    }
}
