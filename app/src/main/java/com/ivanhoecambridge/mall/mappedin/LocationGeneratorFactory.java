package com.ivanhoecambridge.mall.mappedin;

import com.mappedin.sdk.Location;
import com.mappedin.sdk.LocationGenerator;
import com.mappedin.sdk.Venue;

import java.nio.ByteBuffer;

/**
 * Created by petar on 2018-02-20.
 */

public class LocationGeneratorFactory {

    /**
     * Listener to notify when a location has been generated.
     */
    public interface LocationGeneratedListener {
        /**
         * Callback that will be invoked when a Tenant has been successfully generated through MappedIn.
         * @param tenant Tenant
         */
        void onTenantGenerated(Tenant tenant);

        /**
         * Callback that will be invoked when an Amenity has been successfully generated through MappedIn.
         * @param amenity Amenity
         */
        void onAmenityGenerated(Amenity amenity);

        /**
         * Callback that will be invoked when EscalatorStairs has been successfully generated through MappedIn.
         * @param escalatorStairs EscalatorStairs, this can be either Escalators or Stairs.
         */
        void onEscalatorStairsGenerated(EscalatorStairs escalatorStairs);

        /**
         * Callback that will be invoked when an Elevator has been successfully generated through MappedIn.
         * @param elevator Elevator
         */
        void onElevatorGenerated(Elevator elevator);
    }

    public final static int TENANT = 1;
    public final static int AMENITY = 2;
    public final static int ESCALATOR_STAIRS = 3;
    public final static int ELEVATOR = 4;


    /**
     * Prepares the specified Location for MappedIn, these are callbacks that are triggered by MappedIn's SDK which provides data
     * that is transformed into the specified type. When the location is generated onLocationGeneratedListener will issue a callback
     * for the given location.
     * @param locationType Location type to prepare.
     * @param onLocationGeneratedListener LocationGeneratedListener will provide a callback when the specified location has been generated.
     * @return LocationGenerator for the given type.
     * @throws IllegalArgumentException if no location type is given or is incorrect.
     */
    public static LocationGenerator prepareLocation(int locationType, final LocationGeneratedListener onLocationGeneratedListener) {
        switch (locationType) {
            case TENANT:
                return new LocationGenerator() {
                    @Override
                    public Location locationGenerator(ByteBuffer data, int _index, Venue venue) {
                        Tenant tenant = new Tenant(data, _index, venue);
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
