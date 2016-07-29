package com.kineticcafe.kcpmall.mappedin;

import com.mappedin.sdk.ImageCollection;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.RawData;

/**
 * Created by paul on 2016-06-14.
 */
public class CustomLocation extends Location {
    public String description;
    public ImageCollection logo;

    public CustomLocation(RawData rawData) throws Exception {
        super(rawData);
        description = rawData.string("description");
        logo = rawData.imageCollection("logo");
    }

}
