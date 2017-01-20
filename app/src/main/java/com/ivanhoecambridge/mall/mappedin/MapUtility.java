package com.ivanhoecambridge.mall.mappedin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Map;
import com.mappedin.sdk.Overlay2DImage;
import com.mappedin.sdk.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    public static Map getGroundMap(Map[] maps){
        try {
            for(Map map : maps){
                if(map.getElevation() == 0) return map;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getGroundMapIndex(Map[] maps){
        try {

            for(int i = 0; i < maps.length; i++){
                if(maps[i].getElevation() == 0) return i;

            }
            return -50;
        } catch (Exception e) {
            e.printStackTrace();
            return -50;
        }
    }


    public static int getMapIndexWithShortName(Map[] maps, String shortName){
        for(int i = 0; i < maps.length; i++){
            if(maps[i].getShortName().equals(shortName)) return i;
        }
        return 0;
    }

    public static Map[] sortMapLevelByElevetaion(Map[] map){
        try {
            /*Arrays.sort(map, new Comparator<Map>() {
                @Override
                public int compare(Map a, Map b) {
                    return (int) (a.getElevation() - b.getElevation());
                }
            });*/
            List<Map> mapList = new ArrayList<Map>(Arrays.asList(map));
            Collections.sort(mapList, new MapElevationComparator());
            return (Map[]) mapList.toArray(new Map[map.length]);
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
    }

    public static class MapElevationComparator implements Comparator<Map> {
        @Override
        public int compare(Map o1, Map o2) {
            try {
                return Integer.valueOf((int)o1.getElevation()).compareTo(Integer.valueOf((int)o2.getElevation()));
            } catch (Exception e) {
                return 0;
            }
        }
    }

    /**
     *
     * @param context
     * @param pinDrawable
     * @return overlay2DImage with transparent pixel around it as a hack to adjust its sizes
     */
    private static Overlay2DImage getOverlayImageWithPadding(Context context, Drawable pinDrawable) {
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
        Overlay2DImage label = new Overlay2DImage(maxDimension, maxDimension, new BitmapDrawable(context.getResources(), bitmap), maxDimension/2, maxDimension/2);
        return label;
    }

    public static int getDp(Context context, int px){
        return KcpUtility.dpToPx((Activity) context , px);
    }

    public static Polygon getNearestParkingPolygonFromStorePolygon(Polygon polygon){
        try {
            HashMap<String, CustomLocation> parkingHashmap = CustomLocation.getParkingHashMap();
            Coordinate storeCoordinate = polygon.getLocations().get(0).getNavigatableCoordinates().get(0);

            double nearestDistance = 0;
            Polygon nearestParkingPolygon = null;
            for (CustomLocation parkingLocation : parkingHashmap.values()) {
                ArrayList<Coordinate> navigatableCoordinates = parkingLocation.getNavigatableCoordinates();
                for(Coordinate parkingLotCoord : navigatableCoordinates) {
                    double distance = parkingLotCoord.metersFrom(storeCoordinate);
                    if(nearestDistance == 0 || nearestDistance > distance) {
                        nearestParkingPolygon = parkingLocation.getPolygons().get(0);
                        nearestDistance = distance;
                    }
                }
                /*Coordinate parkingLotCoord = parkingLocation.getNavigatableCoordinates().get(0);
                double distance = parkingLotCoord.metersFrom(storeCoordinate);
                if(nearestDistance == 0 || nearestDistance > distance) {
                    nearestParkingPolygon = parkingLocation.getPolygons().get(0);
                    nearestDistance = distance;
                }*/
            }

            return nearestParkingPolygon;
        } catch (Exception e) {
            return null;
        }
    }

    public static Location getLocation(double latitude, double longitude){
        android.location.Location targetLocation = null;//provider name is unecessary
        try {
            targetLocation = new android.location.Location("");
            targetLocation.setLatitude(latitude);//your coords of course
            targetLocation.setLongitude(longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return targetLocation;
    }

    public static Drawable getRotatedDrawable(Context context, int drawable, float degrees){
        if(context == null) return null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmpOriginal = BitmapFactory.decodeResource(context.getResources(), drawable);
        Bitmap bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bmResult);
        tempCanvas.rotate(degrees, bmpOriginal.getWidth()/16, bmpOriginal.getHeight()/16);
        tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);


        return new BitmapDrawable(context.getResources(), bmResult);
    }

    public static Drawable rotate(Context context, int drawable, float degrees) {
        Drawable d = context.getResources().getDrawable(drawable);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inDither = true;


//        Bitmap iconBitmap = ((BitmapDrawable)d).getBitmap();
        Bitmap iconBitmap = BitmapFactory.decodeResource(context.getResources(), drawable, opts);


        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap targetBitmap = Bitmap.createBitmap(iconBitmap, 0, 0, iconBitmap.getWidth(), iconBitmap.getHeight(), matrix, true);

        return new BitmapDrawable(context.getResources(), targetBitmap);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int getIndexWithMapElevation(Map[] maps, double elevation){
        try {
            if(maps == null) return 0;
            for(int i = 0; i < maps.length; i++) {
                if(maps[i].getElevation() == elevation) return i;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
