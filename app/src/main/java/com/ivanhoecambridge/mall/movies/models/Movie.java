package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kay on 2016-10-26.
 */

@Root(strict = false)
public class Movie
{
    @Element(required = false)
    private String movie_id;

    @Element(required = false)
    private String movie_name;

    @Element(required = false)
    private String movie_rating;



    @ElementList(entry = "showtimes", inline = true)
    private List<Showtimes> showtimes; //change all the getters, setters

    public List<Showtimes> getShowtimes()
    {
        if(showtimes == null) return new ArrayList<Showtimes>();
        return showtimes;
    }

    public void setShowtimes(List<Showtimes> showTimes)
    {
        this.showtimes = showTimes;
    }

    public String getMovie_id ()
    {
        if(movie_id == null) return "";
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

    /**
     *
     * @return return show times in series ex) "12:45 3:45 7:05 10:00"
     */
    public String getShowtimesString(){
        String showtimesString = "";
        if(getShowtimes().size() > 0 ){
            Showtimes showtimes = getShowtimes().get(0);
            for(String showtime : showtimes.getShowtimes()) {
                showtime = convert24Hrto12Hr(showtime);
                if(showtimes.equals("")) showtimesString = showtime;
                else showtimesString = showtimesString + "   " + showtime;
            }
        }
        return showtimesString;
    }

    public String convert24Hrto12Hr(String time){
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("K:mm").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}