package com.ivanhoecambridge.mall.mappedin;

/**
 * Created by Kay on 2017-01-17.
 */

public class ParkingPin {

    private Amenity parkingLocationPin;
    private Amenity temporaryParkingLocationPin;
    private Pin parkingCoordinatePin;
    private Pin tempParkingCoordinatePin;

    private ParkingPinInterface mapFragment;

    public ParkingPin(ParkingPinInterface parkingPinInterface) {
        this.mapFragment = parkingPinInterface;
    }

    public void setParkingCoordinatePin(Pin parkingPin) {
        this.parkingLocationPin = null;
        this.parkingCoordinatePin = parkingPin;
    }

    public void setParkingLocationPin(Amenity savedParkingLocation) {
        removeExistingPin();
        this.parkingCoordinatePin = null;
        this.parkingLocationPin = savedParkingLocation;
    }

    public void removeExistingPin() {
        if(parkingLocationPin != null) mapFragment.removeParkingPinAtLocation(parkingLocationPin);
        if(parkingCoordinatePin != null) mapFragment.removeParkingPinAtCoordinate(parkingCoordinatePin);
    }

    public Pin getParkingCoordinatePin() {
        return parkingCoordinatePin;
    }

    public Amenity getParkingLocationPin() {
        return parkingLocationPin;
    }

    public void setTempParkingCoordinatePin(Pin tempParkingCoordinatePin) {
        this.tempParkingCoordinatePin = tempParkingCoordinatePin;
    }

    public Pin getTempParkingCoordinatePin() {
        return tempParkingCoordinatePin;
    }


}
