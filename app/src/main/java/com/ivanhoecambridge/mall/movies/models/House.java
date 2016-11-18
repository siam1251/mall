package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Kay on 2016-10-26.
 */

@Root(strict = false)
public class House
{
    @Element(required = false)
    private String location;

    @Element(required = false)
    private Address address;

    @Element(required = false)
    private Schedule schedule;

    public String getLocation ()
    {
        if(location == null) return "";
        return location;
    }

    public void setLocation (String location)
    {
        this.location = location;
    }

    public Address getAddress ()
    {
        if(address == null) return new Address();
        return address;
    }

    public void setAddress (Address address)
    {
        this.address = address;
    }

    public Schedule getSchedule ()
    {
        if(schedule == null) return new Schedule();
        return schedule;
    }

    public void setSchedule (Schedule schedule)
    {
        this.schedule = schedule;
    }

    @Override
    public String toString()
    {
        return "";
    }

    public String getShowtimes(String movieId){
        return getSchedule().getMovie(movieId).getShowtimesString();
    }
}