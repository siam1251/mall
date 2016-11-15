package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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


    public static void sortMoviesList(ArrayList<MovieDetail> movies){
        try {
            if(movies == null) return;
            Collections.sort(movies, new NameComparator());
        } catch (Exception e) {
        }
    }

    public static class NameComparator implements Comparator<MovieDetail> {
        @Override
        public int compare(MovieDetail o1, MovieDetail o2) {
            try {
                return (o1.getName().toString().toLowerCase()).compareTo(o2.getName().toString().toLowerCase());
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
