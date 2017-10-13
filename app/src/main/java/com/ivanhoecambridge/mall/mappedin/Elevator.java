package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.sdk.ImageSet;

import com.mappedin.sdk.Location;
import com.mappedin.sdk.Utils;
import com.mappedin.sdk.Venue;

import java.nio.ByteBuffer;

/**
 * Created by petar on 2017-10-11.
 */

public class Elevator extends Location{
    public String   id;
    public String   externalId;
    public String   description;
    public ImageSet logo;

    public Elevator(ByteBuffer data, int _index, Venue venue) {
        super(data, _index, venue);
        id = Utils.encodingString(data);
        externalId = Utils.encodingString(data);
        description = Utils.encodingString(data);
        logo = Utils.encodingImageSet(data);
    }
}
