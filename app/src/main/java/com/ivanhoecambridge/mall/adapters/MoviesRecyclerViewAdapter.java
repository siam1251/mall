package com.ivanhoecambridge.mall.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MovieDetailActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.movies.models.House;
import com.ivanhoecambridge.mall.movies.models.MovieDetail;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

import constants.MallConstants;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    protected final Logger logger = new Logger(getClass().getName());
    private int mRecyclerType = -1;
    public static final int ITEM_TYPE_THEATER_VIEWER =             0;
    public static final int ITEM_TYPE_SHOWTIMES_VIEWER =               1;

    private ArrayList<MovieDetail> mMovieDetails;
    private House mHouse;

    public MoviesRecyclerViewAdapter(Context context, @Nullable House house, ArrayList<MovieDetail> movieDetails, int recyclerType) {
        mContext = context;
        mHouse = house == null ? new House() : house;
        mMovieDetails = movieDetails == null ? new ArrayList<MovieDetail>() : new ArrayList<MovieDetail>(movieDetails);
        mRecyclerType = recyclerType;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public class TheaterViewer extends MainViewHolder {
        public CardView cvMoviePoster;
        public ImageView ivMoviePoster;
        public TextView tvMovieTitle;
        public TextView tvMovieRating;

        public TheaterViewer(View itemView) {
            super(itemView);
            cvMoviePoster = (CardView) itemView.findViewById(R.id.cvMoviePoster);
            ivMoviePoster = (ImageView) itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = (TextView) itemView.findViewById(R.id.tvMovieRating);
        }
    }

    public class ShowtimesViewer extends MainViewHolder {
        public CardView cvMoviePoster;
        public ImageView ivMoviePoster;
        public TextView tvMovieTitleRating;
        public TextView tvMovieLengthGenre;
        public TextView tvMovieShowtimes;

        public ShowtimesViewer(View itemView) {
            super(itemView);
            cvMoviePoster = (CardView) itemView.findViewById(R.id.cvMoviePoster);
            ivMoviePoster = (ImageView) itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitleRating = (TextView) itemView.findViewById(R.id.tvMovieTitleRating);
            tvMovieLengthGenre = (TextView) itemView.findViewById(R.id.tvMovieLengthGenre);
            tvMovieShowtimes = (TextView) itemView.findViewById(R.id.tvMovieShowtimes);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case ITEM_TYPE_THEATER_VIEWER:
                return new TheaterViewer(LayoutInflater.from(mContext).inflate(R.layout.list_item_movie, parent, false));
            case ITEM_TYPE_SHOWTIMES_VIEWER:
                return new ShowtimesViewer(LayoutInflater.from(mContext).inflate(R.layout.list_item_showtimes, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final MovieDetail movieDetail = mMovieDetails.get(position);
            if (holder instanceof TheaterViewer) {
                final TheaterViewer theaterViewer = (TheaterViewer) holder;
                //Poster
                String photoUrl = movieDetail.getLargePhotoUrl();
                if(!photoUrl.equals("")) {
                    Glide.with(mContext)
                            .load(photoUrl)
                            .crossFade()
                            .centerCrop()
                            .placeholder(R.drawable.icn_movies_placeholder)
                            .into(theaterViewer.ivMoviePoster);

                }

                //Title
                String title = movieDetail.getMovieTitle();
                theaterViewer.tvMovieTitle.setText(title);

                //Rating
                String rating = movieDetail.getMovieRating(MallConstants.RATINGS_PROVINCE_CODE);
                rating = movieDetail.getStylishRatings(rating);
                theaterViewer.tvMovieRating.setText(rating);

                theaterViewer.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMovieDetailActivity(theaterViewer.cvMoviePoster, movieDetail.getMovie_id());
                        ActivityAnimation.startActivityAnimation(mContext);
                    }
                });


            } else if(holder instanceof ShowtimesViewer){
                final ShowtimesViewer showtimesViewer = (ShowtimesViewer) holder;
                //Poster
//                String photoUrl = movieDetail.getHighPhotoUrl();
                String photoUrl = movieDetail.getLargePhotoUrl();
                if(!photoUrl.equals("")) {
                    Glide.with(mContext)
                            .load(photoUrl)
                            .crossFade()
                            .centerCrop()
                            .placeholder(R.drawable.icn_movies_placeholder)
                            .into(showtimesViewer.ivMoviePoster);
                }

                //Title and Rating
                String title = movieDetail.getMovieTitle();
                String rating = movieDetail.getMovieRating(MallConstants.RATINGS_PROVINCE_CODE);
                rating = movieDetail.getStylishRatings(rating);

                String movieTitleRating = title + " " + rating;
                showtimesViewer.tvMovieTitleRating.setText(movieTitleRating);

                //Runtime and Genre
                String runTime = movieDetail.getRuntimeInFormat();
                String genre = movieDetail.getGenresInFormat();

                String runTimeGenre = runTime + " • " + genre;
                if(runTimeGenre.equals("")) showtimesViewer.tvMovieLengthGenre.setVisibility(View.GONE);
                showtimesViewer.tvMovieLengthGenre.setText(runTimeGenre);

                //showtimes
                String showtimes = mHouse.getShowtimes(movieDetail.getMovie_id());
                if(showtimes.equals("")) showtimesViewer.tvMovieShowtimes.setVisibility(View.GONE);
                showtimesViewer.tvMovieShowtimes.setText(showtimes);

                showtimesViewer.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMovieDetailActivity(showtimesViewer.cvMoviePoster, movieDetail.getMovie_id());
                        ActivityAnimation.startActivityAnimation(mContext);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void startMovieDetailActivity(View v, String movieId){

        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra(Constants.ARG_MOVIE_ID, movieId);

        String transitionNameImage = mContext.getResources().getString(R.string.transition_movie_poster);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity)mContext,
                Pair.create(v, transitionNameImage));

        ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
        ActivityAnimation.startActivityAnimation(mContext);
    }



    @Override
    public int getItemCount() {
        return mMovieDetails.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mRecyclerType;
    }



}
