package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Kay on 2016-10-26.
 */

@Root(strict = false)
public class Movie
{
    @ElementList
    private List<String> showtimes;

    public List<String> getShowtimes ()
    {
        return showtimes;
    }

    public void setShowtimes (List<String> showTimes)
    {
        this.showtimes = showTimes;
    }


    @Element
    private String movie_id;

    @Element
    private String movie_name;

    @Element(required = false)
    private String movie_rating;

    public String getMovie_id ()
    {
        return movie_id;
    }

    public void setMovie_id (String movie_id)
    {
        this.movie_id = movie_id;
    }

    public String getMovie_name ()
    {
        return movie_name;
    }

    public void setMovie_name (String movie_name)
    {
        this.movie_name = movie_name;
    }

    public String getMovie_rating ()
    {
        return movie_rating;
    }

    public void setMovie_rating (String movie_rating)
    {
        this.movie_rating = movie_rating;
    }

    @Override
    public String toString()
    {
        return "";
    }
}