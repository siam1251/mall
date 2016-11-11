package com.ivanhoecambridge.mall.mappedin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Overlay2DImage;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-11-09.
 */

public class MapUtility {

    public static int getNearestCoordinatePositionFromStore(ArrayList<Coordinate> navigatableCoords, Coordinate store) {
        int position = 0;
        double nearestDistance = 0;
        for(int i = 0; i < navigatableCoords.size(); i++) {
            Coordinate coordinate = navigatableCoords.get(i);
            double distance = coordinate.metersFrom(store);
            if(nearestDistance == 0 || nearestDistance > distance) {
                position = i;
                nearestDistance = distance;
            }
        }
        return position;
    }

    /**
     *
     * @param context
     * @param pinDrawable
     * @return overlay2DImage with transparent pixel around it as a hack to adjust its sizes
     */
    public static Overlay2DImage getOverlayImageWithPadding(Context context, Drawable pinDrawable) {
        // Actual size of logo
        int width = pinDrawable.getIntrinsicWidth();
        int height = pinDrawable.getIntrinsicHeight();

        // Get next power of two for width and height dimensions
        int widthPow2 = Integer.highestOneBit(width - 1) * 2;
        int heightPow2 = Integer.highestOneBit(height - 1) * 2;

        int maxDimension = Math.max(widthPow2, heightPow2);  //Makes the texture a square so OpenGL doesn't complain

        // Pad it out with transparent pixels
        Bitmap bitmap = Bitmap.createBitmap(maxDimension, maxDimension, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Set the bounds of the drawable to draw where we actually want to draw the logo.
        // It's anchored on the map to the bottom center, so we need to shift the drawable so it draws there
        pinDrawable.setBounds(maxDimension / 2 - width / 2, maxDimension - height, maxDimension / 2 + width / 2, maxDimension);
        pinDrawable.draw(canvas);

        // Create an Overlay2DImage using a new drawable derived from the bitmap we made
        Overlay2DImage label = new Overlay2DImage(maxDimension, maxDimension, new BitmapDrawable(context.getResources(), bitmap));
        return label;
    }

    public static int getDp(Context context, int px){
        return KcpUtility.dpToPx((Activity) context , px);
    }

}
