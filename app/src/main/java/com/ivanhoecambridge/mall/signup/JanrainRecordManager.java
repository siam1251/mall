package com.ivanhoecambridge.mall.signup;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by petar on 2017-09-01.
 */

public class JanrainRecordManager {

    private CaptureRecord captureRecord;
    private Context context;

    /**
     * Manages the signed in Janrain user and provides methods to retrieve profile attributes.
     */
    public JanrainRecordManager(Context context) {
        this.context = context;
        captureRecord = Jump.getSignedInUser();
    }

    /**
     * Signs out the current user.
     *
     */
    public void signOut() {
        Jump.signOutCaptureUser(context.getApplicationContext());
        captureRecord = null;
    }

    /**
     * Checks to see if a user is currently signed in through Janrain.
     * @return true if user is signed in, false if not.
     */
    public boolean isUserSignedIn() {
        return captureRecord != null;
    }

    /**
     * Checks if the user has a set profile image.
     * @return true if an image exists, false if not.
     */
    public boolean userHasProfileImage() {
        return getProfileImageUrl() != null;
    }

    /**
     * Gets the user profile image URL stored in janrain. Will select the first available photo.
     * @return URL location for the user image or an null value if none is found.
     */
    public String getProfileImageUrl() {
        if (captureRecord != null) {
            try {
                JSONArray photos = (JSONArray) captureRecord.get("photos");
                JSONObject photoObject = photos.getJSONObject(0);
                return photoObject.getString("value");
            } catch (JSONException e) {
                Log.i("JSON", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Loads the user profile image into the specified target.
     * @param bitmapSimpleTarget Bitmap resource for user image.
     */
    public void loadProfileImage(SimpleTarget<Bitmap> bitmapSimpleTarget) {
        if (getProfileImageUrl() != null) {
            Glide.with(context)
                    .load(getProfileImageUrl())
                    .asBitmap()
                    .into(bitmapSimpleTarget);
        }
    }

    /**
     * Gets the user's full name composed of givenName and familyName concatenated.
     * @return User full name or an empty String value if the name was unable to be produced.
     */
    public String getFullName() {
        if (captureRecord != null) {
            try {
                return captureRecord.get("givenName").toString() + " " + captureRecord.get("familyName").toString();
            } catch (JSONException e) {
                Log.i("JSON", e.getMessage());
            }
        }
        return "";
    }
}
