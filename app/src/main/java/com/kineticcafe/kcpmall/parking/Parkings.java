
package com.kineticcafe.kcpmall.parking;

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

}
