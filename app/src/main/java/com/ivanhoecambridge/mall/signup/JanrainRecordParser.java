package com.ivanhoecambridge.mall.signup;

import android.util.Log;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by petar on 2017-09-01.
 */

public class JanrainRecordParser {

    private CaptureRecord captureRecord;

    public JanrainRecordParser() {
        captureRecord = Jump.getSignedInUser();
    }
    //TODO: sanitize get methods
    public String getProfileImage() {
        if (captureRecord != null) {
            try {
                JSONArray photos = (JSONArray) captureRecord.get("photos");
                JSONObject photoObject = photos.getJSONObject(0);
                return photoObject.getString("value");
            } catch (JSONException e) {
                Log.i("JSON", e.getMessage());
            }
        }
        return "";
    }

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
