package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Root;

/**
 * Created by Kay on 2016-10-27.
 */

@Root(strict = false)
public class Mp4 {
    private String med;

    private String high;

    private String low;

    public String getMed ()
    {
        return med;
    }

    public void setMed (String med)
    {
        this.med = med;
    }

    public String getHigh ()
    {
        return high;
    }

    public void setHigh (String high)
    {
        this.high = high;
    }

    public String getLow ()
    {
        return low;
    }

    public void setLow (String low)
    {
        this.low = low;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [med = "+med+", high = "+high+", low = "+low+"]";
    }
}
