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

    public List<Movie> getMovies()
    {
        if(movies == null) return new ArrayList<Movie>();
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

    public Movie getMovie(String movieId){
        for(Movie movie : getMovies()) {
            if(movie.getMovie_id().equals(movieId)) return movie;
        }
        return new Movie();
    }

}

