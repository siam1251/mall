package com.ivanhoecambridge.mall.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.service.ServiceFactory;
import com.ivanhoecambridge.mall.factory.HeaderFactory;
import com.ivanhoecambridge.mall.movies.models.House;
import com.ivanhoecambridge.mall.movies.models.Movie;
import com.ivanhoecambridge.mall.movies.models.Movies;
import com.ivanhoecambridge.mall.movies.models.Schedule;
import com.ivanhoecambridge.mall.movies.models.Theaters;

import java.util.List;

import constants.MallConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Kay on 2016-10-26.
 */

public class MovieManager {

    private final String RESPONSE_STATUS_COMPLETE = "complete";
    private ProgressDialog pd;

    public static String KEY_PARKING_LOT_POSITION= "key_parking_lot_position";
    public static String KEY_ENTRANCE_POSITION = "key_entrance_position";
    public static String KEY_PARKING_NOTES = "key_parking_notes";


    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    protected Handler mHandler;
    protected Logger logger = null;
    protected Context mContext;
    protected MovieService mMovieServices;
    protected int mLoadingLayout;
    public static Theaters sTheaters = new Theaters();
    public static Movies sMovies = new Movies();
    private MovieInterface mMovieInterface;

    public MovieService getKcpService(){
        ServiceFactory serviceFactory = new ServiceFactory();
        if(mMovieServices == null) mMovieServices = serviceFactory.createRetrofitService(mContext, MovieService.class, HeaderFactory.MOVIE_URL_BASE, SimpleXmlConverterFactory.create());
        return mMovieServices;
    }

    public MovieManager(Context context, int loadingLayout, Handler handler) {
        mContext = context;
        mHandler = handler;
        mLoadingLayout = loadingLayout;
        logger = new Logger(getClass().getName());
    }

    public void setMovieInterface(MovieInterface movieInterface){
        mMovieInterface = movieInterface;
    }

    public void downloadMovies(){
        Call<Theaters> call = getKcpService().getMovies(
                "4.0/",
                MallConstants.MOVIE_API_KEY,
                MallConstants.MOVIE_QUERY_THEATER,
                MallConstants.MOVIE_HOUSE_ID,
                MallConstants.MOVIE_SCHEDULE);

        call.enqueue(new Callback<Theaters>() {
            @Override
            public void onResponse(Call<Theaters> call, Response<Theaters> response) {
                try {
                    if(response.isSuccessful()){
                        sTheaters = response.body();

                        House house = sTheaters.getHouse();
                        Schedule schedule = house.getSchedule();
                        List<Movie> movies = schedule.getMovies();

                        String movieIds = "";
                        for(Movie movie : movies) {
                            String movieId = movie.getMovie_id();
                            movieIds = movieIds == "" ? movieId : movieIds + ", " + movieId;
                        }
                        if(!movieIds.equals(""))downloadMovieDetails(movieIds);

//                        handleState(DOWNLOAD_COMPLETE);
                    } else handleState(DOWNLOAD_FAILED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Theaters> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public void downloadMovieDetails(String movieId){

        Call<Movies> call = getKcpService().getMovieDetails(
                "4.0/",
                MallConstants.MOVIE_API_KEY,
                MallConstants.MOVIE_QUERY_MOVIE,
                movieId,
                MallConstants.MOVIE_MP4,
                MallConstants.MOVIE_PHOTOS);

        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if(response.isSuccessful()){
                    sMovies = response.body();
                    if(mMovieInterface != null) mMovieInterface.onMovieDownloaded();
                    handleState(DOWNLOAD_COMPLETE);
                } else handleState(DOWNLOAD_FAILED);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public interface MovieService {
        @GET
        Call<Theaters> getMovies(
                @Url String url,
                @Query("apikey") String apikey,
                           @Query("query") String query,
                           @Query("house_id") String house_id,
                           @Query("schedule") String schedule);

        @GET
        Call<Movies> getMovieDetails(
                @Url String url,
                @Query("apikey") String apikey,
                @Query("query") String query,
                @Query("movie_id") String movie_id,
                @Query("mp4") String mp4,
                @Query("photos") String photos);


    }

    private void handleState(int state){
        handleState(state, null);
    }

    private void handleState(int state, @Nullable String mode){
        if(mHandler == null) return;
        Message message = new Message();
        message.arg1 = state;
        message.obj = mode;
        switch (state){
            case DOWNLOAD_STARTED:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, true);
                break;
            case DOWNLOAD_FAILED:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, false);
                break;
            case DOWNLOAD_COMPLETE:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, false);
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

}
