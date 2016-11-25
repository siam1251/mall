package com.ivanhoecambridge.mall.mappedin;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-07-25.
 */
public class Amenities {


    public static String GSON_KEY_AMENITY = "gson_key_amenity";
    public static String GSON_KEY_DEAL = "gson_key_deal";
    public static String GSON_KEY_PARKING = "gson_key_parking";


    @SerializedName("amenities")
    private List<Amenity> amenityList = new ArrayList<Amenity>();

    public List<Amenity> getAmenityList (){
        if(amenityList == null) return new ArrayList<Amenity>();
        return amenityList;
    }

    public Amenity getAmenityWithExternalId(String exteralId){
        List<Amenity> amenities = getAmenityList();
        if(amenities == null) return null;
        for(Amenity amenity : amenities){

            String[] externalIds = amenity.getExternalIds();
            for(String id : externalIds) {
                if(id.equals(exteralId)) return amenity;
            }
        }
        return null;
    }

    public class Amenity {

        @SerializedName("type")
        private String type;

        @SerializedName("enabled")
        private boolean enabled;

        @SerializedName("title")
        private String title;

        @SerializedName("externalIds")
        private String[] externalIds;

        public String getType() {
            return type;
        }

        public boolean isEnabled(){
            return enabled;
        }

        public String getTitle() {
            return title;
        }

        public String[] getExternalIds() {
            return externalIds;
        }
    }

    /**
     *
     * @param context
     * @param key for Amenity, key is GSON_KEY_AMENITY + title
     * @param isToggled
     */
    public static void saveToggle(final Context context, String key, boolean isToggled){
        KcpUtility.saveToSharedPreferences(context, key, isToggled);
    }

    public static boolean isToggled(Context context, String key, boolean defaultValue){
        return KcpUtility.loadFromSharedPreferences(context, key, defaultValue);
    }

    public static class AmenityLayout {
        private View mView;
        private SwitchCompat swAmenity;
        public String title;
        private TextView tvAmenity;

        public AmenityLayout(Activity activity, ViewGroup parentView, int layout,
                             String title, boolean isToggled,
                             CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            mView = activity.getLayoutInflater().inflate(
                    layout,
                    parentView,
                    false);
            this.title = title;
            tvAmenity = (TextView) mView.findViewById(R.id.tvAmenity);
            swAmenity = (SwitchCompat) mView.findViewById(R.id.swAmenity);
            tvAmenity.setText(title);
            swAmenity.setChecked(isToggled);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swAmenity.performClick();
                }
            });
            swAmenity.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        public View getView(){
            return mView;
        }
        public SwitchCompat getSwitch() {return swAmenity;}
    }

    public interface OnAmenityClickListener {
        public void onAmenityClick(boolean enabled, String externalCode, boolean focusPin);
    }

    public interface OnDealsClickListener {
        public void onDealsClick(boolean enabled);
    }

    public interface OnParkingClickListener {
        public void onParkingClick(boolean enabled, boolean focus);
    }







}
