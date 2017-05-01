package com.ivanhoecambridge.mall.mappedin;

/**
 * Created by martin on 2017-04-12.
 */

import com.mappedin.sdk.ImageSet;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.Utils;
import com.mappedin.sdk.Venue;

import java.nio.ByteBuffer;

public class LocationData extends Location {
    String id;
    String externalId;
    String amenityType;
    String description;
    ImageSet logo;

    public LocationData(ByteBuffer data, int _index, Venue venue) {
        super(data, _index, venue);
        id = Utils.encodingString(data);
        externalId = Utils.encodingString(data);
        amenityType = Utils.encodingString(data);
        description = Utils.encodingString(data);
        logo = Utils.encodingImageSet(data);
    }
}
