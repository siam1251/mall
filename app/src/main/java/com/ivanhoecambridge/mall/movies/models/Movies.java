package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-10-27.
 */

@Root(strict = false)
public class Movies {

    @ElementList(entry = "movie", inline = true)
    private List<MovieDetail> movieDetail;

    public List<MovieDetail> getMovies()
    {
        if(movieDetail == null) return new ArrayList<MovieDetail>();
        return movieDetail;
    }

    public MovieDetail getMovieDetailWithId(String movieId){
        for(MovieDetail movieDetail : getMovies()) {
            if(movieDetail.getMovie_id().equals(movieId)) return movieDetail;
        }
        return null;
    }

    public void setMovie (List<MovieDetail> movie)
    {
        this.movieDetail = movie;
    }

    @Override
    public String toString()
    {
        return "";
    }
}
