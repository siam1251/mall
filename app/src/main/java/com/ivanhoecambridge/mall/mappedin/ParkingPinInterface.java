package com.ivanhoecambridge.mall.mappedin;

/**
 * Created by Kay on 2017-01-17.
 */

public interface ParkingPinInterface {
    void removeParkingPinAtLocation(CustomLocation parkingLocationPin);
    void removeParkingPinAtCoordinate(Pin parkingCoordinatePin);


    void removeTempParkingPinAtCoordinate(Pin parkingCoordinatePin);
}
