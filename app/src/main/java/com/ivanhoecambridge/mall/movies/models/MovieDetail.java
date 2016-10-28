package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Kay on 2016-10-27.
 */

@Root(strict = false)
public class MovieDetail {

    @Element
    private String movie_id;

    @ElementList
    private List<String> genres;

    @ElementList
    private List<String> photos;

    @ElementList
    private List<String> lgphotos;

    @Element
    private Mp4 mp4;

    @Element
    private String runtime;

    @Element
    private String title;

    @Element(required = false)
    private String advisory;

    @Element
    private String name;

    @Element
    private String synopsis;

    @Element
    private String mlang;

    @ElementList
    private List<String> actors;

    @Element
    private Ratings ratings;

    @ElementList
    private List<String> producers;

    private String parent_id;

    public String getMovie_id ()
    {
        return movie_id;
    }

    public void setMovie_id (String movie_id)
    {
        this.movie_id = movie_id;
    }

    public List<String> getGenres ()
    {
        return genres;
    }

    public void setGenres (List<String> genres)
    {
        this.genres = genres;
    }

    public List<String> getPhotos ()
    {
        return photos;
    }

    public void setPhotos (List<String> photos)
    {
        this.photos = photos;
    }

    public List<String> getLargePhotos ()
    {
        return lgphotos;
    }

    public void setLargePhotos (List<String> lgphotos)
    {
        this.lgphotos = lgphotos;
    }

    public Mp4 getMp4 ()
    {
        return mp4;
    }

    public void setMp4 (Mp4 mp4)
    {
        this.mp4 = mp4;
    }

    public String getRuntime ()
    {
        return runtime;
    }

    public void setRuntime (String runtime)
    {
        this.runtime = runtime;
    }

    public String getmLang ()
    {
        return mlang;
    }

    public void setMLang(String mlang)
    {
        this.mlang = mlang;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getAdvisory ()
    {
        return advisory;
    }

    public void setAdvisory (String advisory)
    {
        this.advisory = advisory;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getSynopsis ()
    {
        return synopsis;
    }

    public void setSynopsis (String synopsis)
    {
        this.synopsis = synopsis;
    }

    public String getMlang ()
    {
        return mlang;
    }

    public void setMlang (String mlang)
    {
        this.mlang = mlang;
    }

    public List<String> getActors ()
    {
        return actors;
    }

    public void setActors (List<String> actors)
    {
        this.actors = actors;
    }

    public Ratings getRating ()
    {
        return ratings;
    }

    public void setRating (Ratings rating)
    {
        this.ratings = ratings;
    }

    public List<String> getProducers ()
    {
        return producers;
    }

    public void setProducers (List<String> producers)
    {
        this.producers = producers;
    }

    public String getParent_id ()
    {
        return parent_id;
    }

    public void setParent_id (String parent_id)
    {
        this.parent_id = parent_id;
    }

    @Override
    public String toString()
    {
//        return "ClassPojo [movie_id = "+movie_id+", genres = "+genres+", website = "+website+", hiphotos = "+hiphotos+", lgphotos = "+lgphotos+", mp4 = "+mp4+", runtime = "+runtime+", imdb = "+imdb+", release_dates = "+release_dates+", photos = "+photos+", title = "+title+", advisory = "+advisory+", name = "+name+", synopsis = "+synopsis+", mlang = "+mlang+", actors = "+actors+", writers = "+writers+", distributor = "+distributor+", rating = "+rating+", directors = "+directors+", producers = "+producers+", ratings = "+ratings+", parent_id = "+parent_id+"]";
        return "";
    }
}
