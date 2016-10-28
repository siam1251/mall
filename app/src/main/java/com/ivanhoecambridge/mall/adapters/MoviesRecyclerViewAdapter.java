package com.ivanhoecambridge.mall.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Rating;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.activities.InterestedCategoryActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.movies.MovieUtility;
import com.ivanhoecambridge.mall.movies.models.MovieDetail;
import com.ivanhoecambridge.mall.movies.models.Ratings;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.RecyclerViewFooter;

import java.util.ArrayList;
import java.util.List;

import constants.MallConstants;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private static final int ITEM_TYPE_HORIZONTAL_MOVIE_VIEWER =             0;
    private static final int ITEM_TYPE_VERTICAL_MOVIE_VIEWER =               1;

    private ArrayList<MovieDetail> mMovieDetails;

    public MoviesRecyclerViewAdapter(Context context, ArrayList<MovieDetail> movieDetails) {
        mContext = context;
        mMovieDetails = movieDetails == null ? new ArrayList<MovieDetail>() : new ArrayList<MovieDetail>(movieDetails);
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public class HorizontalMovieViewer extends MainViewHolder {
        public ImageView ivMoviePoster;
        public TextView tvMovieTitle;
        public TextView tvMovieRating;

        public HorizontalMovieViewer(View itemView) {
            super(itemView);

            ivMoviePoster = (ImageView) itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = (TextView) itemView.findViewById(R.id.tvMovieRating);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case ITEM_TYPE_HORIZONTAL_MOVIE_VIEWER:
                return new HorizontalMovieViewer(LayoutInflater.from(mContext).inflate(R.layout.list_item_movie, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HorizontalMovieViewer) {
            final HorizontalMovieViewer horizontalMovieViewer = (HorizontalMovieViewer) holder;

            MovieDetail movieDetail = mMovieDetails.get(position);
            List<String> lgphotos = movieDetail.getLargePhotos();
            List<String> photos = movieDetail.getPhotos();

            String photoUrl = "";
            if(lgphotos != null && lgphotos.size() > 0) {
                photoUrl = lgphotos.get(0);
            } else if (photos != null && photos.size() > 0){
                photoUrl = photos.get(0);
            }

            if(!photoUrl.equals("")) {
                Glide.with(mContext)
                        .load(photoUrl)
                        .asBitmap()
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder_movie)
                        .override(KcpUtility.dpToPx((Activity) mContext, 150), KcpUtility.dpToPx((Activity) mContext, 222))
                        .into(new BitmapImageViewTarget(horizontalMovieViewer.ivMoviePoster) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(KcpUtility.dpToPx((Activity) mContext, 4));
                        horizontalMovieViewer.ivMoviePoster.setImageDrawable(circularBitmapDrawable);
                    }
                });


            }

            String movieTitle = movieDetail.getTitle();
            String movieName = movieDetail.getName();


            String title = movieName == null ? movieTitle : movieName;
            Ratings ratings = movieDetail.getRating();
            String rating = MovieUtility.getMovieRating(ratings, MallConstants.RATINGS_PROVINCE_CODE);
            if(rating != null && !rating.equals("")) rating = "(" + rating + ")";
            /*if(movieTitle != null && rating != null) {
                String footerText = title + " @@" + rating + "@@ ";
//                CharSequence cs = Utility.setSpanBetweenTokens((CharSequence)footerText, "@@", new StyleSpan(Typeface.NORMAL));
                CharSequence cs = Utility.setSpanBetweenTokens((CharSequence)footerText, "@@", new ForegroundColorSpan(mContext.getResources().getColor(R.color.themeColor)));
                horizontalMovieViewer.tvMovieTitle.setText(cs);
            }*/
            if(title != null) horizontalMovieViewer.tvMovieTitle.setText(title);
            horizontalMovieViewer.tvMovieRating.setText(rating);


        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST){
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST){

        }
    }

    @Override
    public int getItemCount() {
        return mMovieDetails.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE_HORIZONTAL_MOVIE_VIEWER;

    }



}
