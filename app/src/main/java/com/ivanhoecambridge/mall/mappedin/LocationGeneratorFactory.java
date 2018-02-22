package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.sdk.Location;
import com.mappedin.sdk.LocationGenerator;
import com.mappedin.sdk.Venue;

import java.nio.ByteBuffer;

/**
 * Created by petar on 2018-02-20.
 */

public class LocationGeneratorFactory {

    public interface LocationGeneratedListener {
        void onTenantGenerated(TenantOld tenant);
        void onAmenityGenerated(Amenity amenity);
        void onEscalatorStairsGenerated(EscalatorStairs escalatorStairs);
        void onElevatorGenerated(Elevator elevator);
    }

    public final static int TENANT = 1;
    public final static int AMENITY = 2;
    public final static int ESCALATOR_STAIRS = 3;
    public final static int ELEVATOR = 4;


    public static LocationGenerator prepareLocation(int locationType, final LocationGeneratedListener onLocationGeneratedListener) {
        switch (locationType) {
            case TENANT:
                return new LocationGenerator() {
                    @Override
                    public Location locationGenerator(ByteBuffer data, int _index, Venue venue) {
                        TenantOld tenant = new TenantOld(data, _index, venue);
                        onLocationGeneratedListener.onTenantGenerated(tenant);
                        return tenant;
                    }
                };
            case AMENITY:
                return new LocationGenerator() {
                    @Override
                    public Location locationGenerator(ByteBuffer data, int _index, Venue venue) {
                        Amenity amenity = new Amenity(data, _index, venue);
                        onLocationGeneratedListener.onAmenityGenerated(amenity);
                        return amenity;
                    }
                };
            case ESCALATOR_STAIRS:
                return new LocationGenerator() {
                    @Override
                    public Location locationGenerator(ByteBuffer byteBuffer, int i, Venue venue) {
                        EscalatorStairs escalatorStairs = new EscalatorStairs(byteBuffer, i, venue);
                        onLocationGeneratedListener.onEscalatorStairsGenerated(escalatorStairs);
                        return escalatorStairs;
                    }
                };
            case ELEVATOR:
                return new LocationGenerator() {
                    @Override
                    public Location locationGenerator(ByteBuffer byteBuffer, int i, Venue venue) {
                        Elevator elevator = new Elevator(byteBuffer, i, venue);
                        onLocationGeneratedListener.onElevatorGenerated(elevator);
                        return elevator;
                    }
                };
        }
        throw new IllegalArgumentException("Location type must be specified.");
    }
}
