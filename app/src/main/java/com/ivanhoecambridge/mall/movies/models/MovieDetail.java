package com.ivanhoecambridge.mall.movies.models;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-10-27.
 */

@Root(strict = false)
public class MovieDetail {

    protected final Logger logger = new Logger(getClass().getName());

    @Element
    private String movie_id;

    @ElementList
    private List<String> genres;

    @ElementList
    private List<String> photos;

    @ElementList
    private List<String> lgphotos;

    @ElementList
    private List<String> hiphotos;

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

    @ElementList
    private List<String> directors;

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
        if(genres == null) return new ArrayList<String>();
        return genres;
    }

    public void setGenres (List<String> genres)
    {
        this.genres = genres;
    }

    public List<String> getPhotos ()
    {
        if(photos == null) return new ArrayList<String>();
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

    public void sethighPhotos (List<String> hiphotos)
    {
        this.hiphotos = hiphotos;
    }

    public List<String> getHighPhotos ()
    {
        if(hiphotos == null) return new ArrayList<String>();
        return hiphotos;
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
        if(title == null) return "";
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
        if(name == null) return "";
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

    public List<String> getDirectors()
    {
        return directors;
    }

    public void setDirectors (List<String> directors)
    {
        this.directors = directors;
    }

    public Ratings getRating ()
    {
        if(ratings == null) return new Ratings();
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
        return "";
    }


    public String getLargePhotoUrl(){
        return getPhoto(getLargePhotos());
    }

    public String getHighPhotoUrl(){
        return getPhoto(getHighPhotos());
    }

    private String getPhoto(List<String> downloadedPhotos){
        List<String> photos = getPhotos();
        String photoUrl = "";
        if(downloadedPhotos != null && downloadedPhotos.size() > 0) {
            photoUrl = downloadedPhotos.get(0);
        } else if (photos != null && photos.size() > 0){
            photoUrl = photos.get(0);
        }
        return photoUrl;

    }

    public String getMovieTitle(){
        String movieTitle = getTitle();
        String movieName = getName();
        return movieName == "" ? movieTitle : movieName;
    }

    public String getMovieRating(String provinceCode){
        String ratings = "";
        if(provinceCode.equals(Ratings.RATINGS_PROVINCE_CODE_ON)){
            ratings = getRating().getOn_rating();
        } else if(provinceCode.equals(Ratings.RATINGS_PROVINCE_CODE_BC)){
            ratings = getRating().getBc_rating();
        }

        if(ratings == null) ratings = "";
        return ratings;
    }


    /**
     * @return return runTime hour in format ex) 118 -> 1hr 58min
     */
    public String getRuntimeInFormat(){
        String runTime = getRuntime();
        if(!runtime.equals("")){
            try {
                int runTimeInt = Integer.parseInt(runTime);
                //convert 118 to 1hr 58min
                int hours = (int)runTimeInt / 60;
                int minutes = (int)runTimeInt % 60;

                String hoursInFormat = hours == 0 ? "" : hours + "hr ";
                String minutesInFormat = minutes == 0 ? "" : minutes + "min ";


                return hoursInFormat + minutesInFormat;
            } catch(NumberFormatException nfe) {
                logger.error(nfe);
            }
        }
        return "";
    }

    /**
     * @return return genres in format ex) Action/Adventure, Drama
     */
    public String getGenresInFormat(){
        return convertListStringToSeriesWithComma(getGenres());
    }

    /**
     *
     * @param ratings
     * @return ratings will have round brackets ex) 14-A will become (14-A)
     */
    public String getStylishRatings(String ratings){
        if(ratings.equals("")) return ratings;
        else {
            return "(" + ratings + ")";
        }
    }

    public String getActorsInFormat(){
        return convertListStringToSeriesWithComma(getActors());
    }

    public String getDirectorsInFormat(){
        return convertListStringToSeriesWithComma(getDirectors());
    }

    public String convertListStringToSeriesWithComma(List<String> listStrings){
        String series = "";
        for(String string : listStrings) {
            if(series.equals("")) series = string;
            else series = string + ", " + string;
        }
        return series;
    }

}
