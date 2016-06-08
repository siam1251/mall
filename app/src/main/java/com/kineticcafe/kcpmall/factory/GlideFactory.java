package com.kineticcafe.kcpmall.factory;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
        if(imageView == null || url == null) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .error(errorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .placeholder(errorDrawable)
                .into(imageView);
    }
}
