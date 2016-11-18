package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Kay on 2016-10-27.
 */

@Root(strict = false)
public class Address {private String zip;

    @Element(required = false)
    private String phone;

    private String county;

    private String street;

    private String state;

    private String city;

    private String country;

    public String getZip ()
    {
        return zip;
    }

    public void setZip (String zip)
    {
        this.zip = zip;
    }

    public String getPhone () {
        if(phone == null) return "";
        return phone;
    }

    public void setPhone (String phone)
    {
        this.phone = phone;
    }

    public String getCounty ()
    {
        return county;
    }

    public void setCounty (String county)
    {
        this.county = county;
    }

    public String getStreet ()
    {
        return street;
    }

    public void setStreet (String street)
    {
        this.street = street;
    }

    public String getState ()
    {
        return state;
    }

    public void setState (String state)
    {
        this.state = state;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [zip = "+zip+", phone = "+phone+", county = "+county+", street = "+street+", state = "+state+", city = "+city+", country = "+country+"]";
    }
}
