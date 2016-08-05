package com.kineticcafe.kcpmall.mappedin;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.mappedin.jpct.Texture;
import com.mappedin.jpct.TextureManager;
import com.mappedin.jpct.util.BitmapHelper;
import com.mappedin.sdk.Overlay;

/**
 * Created by Kay on 2016-08-03.
 */
public class Overlay2DBitmap extends Overlay {
    String textureName = "";

    int width = -1;
    int height = -1;


    public Overlay2DBitmap(int width, int height, Bitmap src) {
        this.textureName = src.toString().split("@")[1];
        this.width = this.adjustSize(width);
        this.height = this.adjustSize(height);

        if(!TextureManager.getInstance().containsTexture(this.textureName)) {
            Texture imageLabel = new Texture(BitmapHelper.rescale(src, this.width, this.height));
            TextureManager.getInstance().addTexture(this.textureName, imageLabel);
        }

    }

    private int adjustSize(int length) {
        int result;
        for(result = 8; result < length; result *= 2) {
            ;
        }

        return (double)result * 1.5D - (double)(length * 2) > 0.0D?result / 2:result;
    }
}
