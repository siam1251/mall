
package com.ivanhoecambridge.mall.parking;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parking {

    @SerializedName("parkingId")
    @Expose
    private String parkingId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("childParkings")
    @Expose
    private List<ChildParking> childParkings = new ArrayList<ChildParking>();

    /**
     * 
     * @return
     *     The parkingId
     */
    public String getParkingId() {
        return parkingId;
    }

    /**
     * 
     * @param parkingId
     *     The parkingId
     */
    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The childParkings
     */
    public List<ChildParking> getChildParkings() {
        return childParkings;
    }

    /**
     * 
     * @param childParkings
     *     The childParkings
     */
    public void setChildParkings(List<ChildParking> childParkings) {
        this.childParkings = childParkings;
    }

}
