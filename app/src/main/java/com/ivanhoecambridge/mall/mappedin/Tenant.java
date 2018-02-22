package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.sdk.ImageSet;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.Utils;
import com.mappedin.sdk.Venue;

import java.nio.ByteBuffer;

public class Tenant extends Location {
    public String   externalId;
    public String   description;
    public ImageSet logo;

    public Tenant(ByteBuffer data, int _index, Venue venue) {
        super(data, _index, venue);
        externalId = Utils.encodingString(data);
        description = Utils.encodingString(data);
        logo = Utils.encodingImageSet(data);
    }
}
