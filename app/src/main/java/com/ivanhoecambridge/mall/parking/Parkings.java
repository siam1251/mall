
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

}
