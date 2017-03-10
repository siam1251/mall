package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Kay on 2016-10-26.
 */

@Root(strict = false)
public class Movie
{

    private final String MOVIE_TIME_FORMAT = "h:mm";
    private final String MOVIE_TIME_FORMAT_WITH_AM_PM = "h:mm aaa";
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

    public List<String> getShowtimesList(boolean sort){
        List<String> showTimesCombined = null;
        try {
            showTimesCombined = new ArrayList<String>();
            if(getShowtimes().size() > 0 ){
                List<Showtimes> showTimesList = getShowtimes();
                showTimesCombined = getShowtimes().get(0).getShowtimes();
                if(showTimesList.size() > 1) {
                    for(int i = 1; i < showTimesList.size(); i++) {
                        showTimesCombined.removeAll(showTimesList.get(i).getShowtimes());
                        showTimesCombined.addAll(showTimesList.get(i).getShowtimes());
                    }
                }

                if(sort) {
                    Collections.sort(showTimesCombined, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            try {
                                return new SimpleDateFormat("H:mm").parse(o1).compareTo(new SimpleDateFormat("H:mm").parse(o2));
                            } catch (ParseException e) {
                                return 0;
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showTimesCombined;
    }

    /**
     *
     * 1. collects showTimes List. 2. combine all the show times. 3. sort the show times by time 4. format it with space in between
     * @return return show times in series ex) "12:45 3:45 7:05 10:00"
     */
    public String getShowtimesInFormat(){
        String showtimesString = "";
        List<String> showTimesCombined = getShowtimesList(true);
        for(String showtime : showTimesCombined) {
            showtime = convert24Hrto12Hr(showtime, MOVIE_TIME_FORMAT);
            if(showtimesString.equals("")) showtimesString = showtime;
            else showtimesString = showtimesString + "   " + showtime;
        }
        return showtimesString;
    }

    public String getNextShowTime(){
        String nextShowTime = "";
        List<String> showTimesCombined = getShowtimesList(true);
        if(showTimesCombined.size() > 0)
            nextShowTime = convert24Hrto12Hr(showTimesCombined.get(0), MOVIE_TIME_FORMAT_WITH_AM_PM);
        return nextShowTime;
    }

    public String convert24Hrto12Hr(String time, String format){
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat(format).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}