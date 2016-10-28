package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-10-26.
 */

@Root(strict = false)
public class Schedule
{
    @ElementList(inline = true)
    private List<Movie> movies;

    public List<Movie> getMovie ()
    {
        return movies;
    }

    public void setMovie (List<Movie> movies)
    {
        this.movies = movies;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [movie = "+movies+"]";
    }
}

