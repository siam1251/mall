package com.kineticcafe.kcpmall.factory;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;

/**
 * Created by Kay on 2016-05-06.
 */
public class GlideFactory {

    public void glideWithNoDefaultRatio(Context context, int drawable, ImageView imageView){
        if(imageView == null) return;
        Glide.with(context)
                .load(drawable)
//                .override(KcpUtility.getScreenWidth(context), (int) (KcpUtility.getScreenWidth(context) / KcpUtility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .crossFade() //TODO: necessary?
                .into(imageView);

    }

    public void glideWithNoDefaultRatio(Context context, String url, ImageView imageView){
        if(imageView == null) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    public void glideWithNoDefaultRatio(Context context, String url, ImageView imageView, final int errorDrawable){
        if(imageView == null) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .placeholder(errorDrawable)
                .error(errorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    public void glideWithRatio(Context context, String url, ImageView imageView, float ratio){
        if(imageView == null) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .override(KcpUtility.getScreenWidth(context), (int) (KcpUtility.getScreenWidth(context) / ratio))
                .into(imageView);
    }


    public void glideWithRatio(Context context, String url, ImageView imageView, float ratio, final int errorDrawable){
        if(imageView == null) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .override(KcpUtility.getScreenWidth(context), (int) (KcpUtility.getScreenWidth(context) / ratio))
                .error(errorDrawable)
                .placeholder(errorDrawable)
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
                .override(KcpUtility.getScreenWidth(context), (int) (KcpUtility.getScreenWidth(context) / KcpUtility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(errorDrawable)
                .into(imageView);
    }

    public void glideWithDefaultRatio(final Context context, final String url, final ImageView imageView, final int errorDrawable, int width){
        if(imageView == null || url == null) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .error(errorDrawable)
                .override(KcpUtility.getScreenWidth(context), (int) (width / KcpUtility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(errorDrawable)
                .into(imageView);
    }
}
