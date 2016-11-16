
package com.ivanhoecambridge.mall.parking;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parkings {

    @SerializedName("parkings")
    @Expose
    private List<Parking> parkings = new ArrayList<Parking>();

    /**
     * 
     * @return
     *     The parkings
     */
    public List<Parking> getParkings() {
        return parkings;
    }

    /**
     * 
     * @param parkings
     *     The parkings
     */
    public void setParkings(List<Parking> parkings) {
        this.parkings = parkings;
    }

    public int getParkingPositionByName(String parkingName){
        for(int i = 0; i < parkings.size(); i++){
            if(parkings.get(i).getName().equals(parkingName)) return i;
        }
        return -1;
    }

    /**
     *
     * @param parkingId
     * @param positions integer array size of 2
     * @return integer array where first element is entrance position, second element is pillar position
     */
    public int[] getParkingPositionById(String parkingId, int[] positions){
        for(int i = 0; i < parkings.size(); i++){
            List<ChildParking> childParkings = parkings.get(i).getChildParkings();
            for(int j = 0; j < childParkings.size(); j++) {
                ChildParking childParking = childParkings.get(j);
                if(childParking.getParkingId().equals(parkingId)) {
                    positions[0] = i;
                    positions[1] = j;
                    return positions;
                }

            }
        }
        return null;
    }


    /**
     *
     * @param parkingId
     * @return ex. Entry 1, Pillar 1C
     */
    public String getChildParkingNameById(String parkingId){
        for(Parking parking : parkings) {
            List<ChildParking> childParkings = parking.getChildParkings();
            for(ChildParking childParking : childParkings) {
                if(childParking.getParkingId().equals(parkingId)) return parking.getName() + ", " + childParking.getName();
            }
        }
        return "";
    }



}
