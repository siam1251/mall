package com.kineticcafe.kcpmall.factory;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.GlideAnimationFactory;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.kineticcafe.kcpmall.R;
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
//                .override(Utility.getScreenWidth(context), (int) (Utility.getScreenWidth(context) / Utility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .crossFade(600)
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
        if(imageView == null) return;

        //if you use placeholder and crossfade together, it fails to scale the imageview - it rescales it right when the view's recreated
        Glide.with(context)
                .load(url)
                .override(Utility.getScreenWidth(context), (int) (Utility.getScreenWidth(context) / Utility.getFloat(context, R.dimen.ancmt_image_ratio)))
                .crossFade(800)
                .error(errorDrawable)
                /*.listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        imageView.setImageResource(errorDrawable);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })*/
                .into(imageView);
    }

    public class PaddingAnimationFactory<T extends GlideDrawable> implements GlideAnimationFactory<T> {
        private final DrawableCrossFadeFactory<T> realFactory;

        public PaddingAnimationFactory(DrawableCrossFadeFactory<T> factory) {
            this.realFactory = factory;
        }

        @Override public GlideAnimation<T> build(boolean isFromMemoryCache, boolean isFirstResource) {
            return new PaddingAnimation<>(realFactory.build(isFromMemoryCache, isFirstResource));
        }
    }

    public class PaddingAnimation<T extends GlideDrawable> implements GlideAnimation<T> {
        private final GlideAnimation<? super T> realAnimation;

        public PaddingAnimation(GlideAnimation<? super T> animation) {
            this.realAnimation = animation;
        }

        @Override public boolean animate(T current, final ViewAdapter adapter) {
            int width = current.getIntrinsicWidth();
            int height = current.getIntrinsicHeight();
            return realAnimation.animate(current, new PaddingViewAdapter(adapter, width, height));
        }
    }

    public class PaddingViewAdapter implements GlideAnimation.ViewAdapter {
        private final GlideAnimation.ViewAdapter realAdapter;
        private final int targetWidth;
        private final int targetHeight;

        public PaddingViewAdapter(GlideAnimation.ViewAdapter adapter, int targetWidth, int targetHeight) {
            this.realAdapter = adapter;
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
        }

        @Override public View getView() {
            return realAdapter.getView();
        }

        @Override public Drawable getCurrentDrawable() {
            Drawable drawable = realAdapter.getCurrentDrawable();
            if (drawable != null) {
                int padX = Math.max(0, targetWidth - drawable.getIntrinsicWidth()) / 2;
                int padY = Math.max(0, targetHeight - drawable.getIntrinsicHeight()) / 2;
                if (padX != 0 || padY != 0) {
                    drawable = new InsetDrawable(drawable, padX, padY, padX, padY);
                }
            }
            return drawable;
        }

        @Override public void setDrawable(Drawable drawable) {
            realAdapter.setDrawable(drawable);
        }
    }
}
