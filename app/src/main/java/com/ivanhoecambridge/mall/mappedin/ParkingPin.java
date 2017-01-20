package com.ivanhoecambridge.mall.mappedin;

/**
 * Created by Kay on 2017-01-17.
 */

public class ParkingPin {

    private CustomLocation parkingLocationPin;
    private CustomLocation temporaryParkingLocationPin;
    private Pin parkingCoordinatePin;
    private Pin tempParkingCoordinatePin;

    private ParkingPinInterface mapFragment;

    public ParkingPin(ParkingPinInterface parkingPinInterface) {
        this.mapFragment = parkingPinInterface;
    }

    public void setParkingCoordinatePin(Pin parkingPin) {
//        removeExistingPin();
        this.parkingLocationPin = null;
        this.parkingCoordinatePin = parkingPin;
    }

    public void setParkingLocationPin(CustomLocation savedParkingLocation) {
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

    public CustomLocation getParkingLocationPin() {
        return parkingLocationPin;
    }

    public void setTempParkingCoordinatePin(Pin tempParkingCoordinatePin) {
//        if(tempParkingCoordinatePin != null) mapFragment.removeTempParkingPinAtCoordinate(tempParkingCoordinatePin);
        this.tempParkingCoordinatePin = tempParkingCoordinatePin;
    }

    public Pin getTempParkingCoordinatePin() {
        return tempParkingCoordinatePin;
    }


}
