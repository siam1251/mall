package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-11-04.
 */

@Root(strict = false)
public class Showtimes {


    @Attribute
    private String attributes;

    @Attribute
    private String sound;

    @Attribute
    private String allowpasses;

    @Attribute
    private String comments;

    @ElementList(entry = "showtime", inline = true, required = false)
    private List<String> showtimes;

    public List<String> getShowtimes(){
        if(showtimes == null) return new ArrayList<String>();
        return showtimes;
    }
}
