package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.movies.MovieManager;
import com.ivanhoecambridge.mall.movies.models.Movie;
import com.ivanhoecambridge.mall.movies.models.MovieDetail;
import com.ivanhoecambridge.mall.utility.BlurBuilder;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import constants.MallConstants;

/**
 * Created by Kay on 2016-10-28.
 */

public class MovieDetailActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private ViewGroup mParentView;
    private ImageView ivDetailImage;
    private String mMovieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle bundle = getIntent().getExtras();
        mMovieId = bundle.getString(Constants.ARG_MOVIE_ID);

        init();
        showContentsWithCTL();
    }

    private void init(){
        setContentView(R.layout.activity_movie_detail);
        mParentView = (ViewGroup) findViewById(R.id.svDetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showContentsWithCTL(){
        try {

            //TOOLBAR
            final CollapsingToolbarLayout ctlDetail = (CollapsingToolbarLayout) findViewById(R.id.ctlDetail);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            final TextView tvToolbar = (TextView) toolbar.findViewById(R.id.tvToolbar);
            getSupportActionBar().setTitle("");

            final View backdrop = (View) findViewById(R.id.backdrop);
            int height = (int) (KcpUtility.getScreenWidth(this) / KcpUtility.getFloat(this, R.dimen.movie_detail_top_layout_ratio));
            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) backdrop.getLayoutParams();
            lp.height = height;
            backdrop.setLayoutParams(lp);

            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.ablDetail);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                private int mToolBarHeight;
                private int mAppBarHeight;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(mToolBarHeight == 0) mToolBarHeight = toolbar.getMeasuredHeight();
                    if(mAppBarHeight == 0) mAppBarHeight = appBarLayout.getMeasuredHeight();

                    Float f = ((((float) mAppBarHeight - mToolBarHeight) + verticalOffset) / ( (float) mAppBarHeight - mToolBarHeight)) * 255;
                    int alpha = 255 - Math.round(f);
                    backdrop.getBackground().setAlpha(alpha);
                    tvToolbar.setTextColor(Color.argb(alpha, 255, 255, 255));
                    toolbar.getBackground().setAlpha(255 - alpha);
                }
            });


            MovieDetail movieDetail = MovieManager.sMovies.getMovieDetailWithId(mMovieId);

            String showtimes = MovieManager.sTheaters.getHouse().getShowtimes(movieDetail.getMovie_id());
            TextView tvShowtimes = (TextView) findViewById(R.id.tvShowtimes);
            tvShowtimes.setText(showtimes);


            final String trailerLink = movieDetail.getMp4().getMovieUrlHighestQuality();
            LinearLayout llWatchTrailer = (LinearLayout) findViewById(R.id.llWatchTrailer);
            if(trailerLink.equals("")) llWatchTrailer.setVisibility(View.GONE);
            llWatchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.playVideo(MovieDetailActivity.this, trailerLink);
                }
            });

            String title = movieDetail.getMovieTitle();
            TextView tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
            tvToolbar.setText(title);
            tvMovieTitle.setText(title);

            String rating = movieDetail.getMovieRating(MallConstants.RATINGS_PROVINCE_CODE);
            TextView tvMovieRating = (TextView) findViewById(R.id.tvMovieRating);
            tvMovieRating.setText(rating);

            String runTime = movieDetail.getRuntimeInFormat();
            TextView tvMovieRuntime = (TextView) findViewById(R.id.tvMovieRuntime);
            tvMovieRuntime.setText(runTime);


            String genre = movieDetail.getGenresInFormat();
            TextView tvMovieGenre = (TextView) findViewById(R.id.tvMovieGenre);
            tvMovieGenre.setText(genre);


            String language = movieDetail.getmLang();
            TextView tvMovieLang = (TextView) findViewById(R.id.tvMovieLang);
            tvMovieLang.setText(language);


            String synopsis = movieDetail.getSynopsis();
            TextView tvMovieSynopsis = (TextView) findViewById(R.id.tvMovieSynopsis);
            tvMovieSynopsis.setText(synopsis);

            String starring = movieDetail.getActorsInFormat();
            TextView tvMovieStarring = (TextView) findViewById(R.id.tvMovieStarring);
            tvMovieStarring.setText(starring);

            String directors = movieDetail.getDirectorsInFormat();
            TextView tvMovieDirectors = (TextView) findViewById(R.id.tvMovieDirectors);
            tvMovieDirectors.setText(directors);

            String advisory = movieDetail.getAdvisory();
            TextView tvMovieAdvisory = (TextView) findViewById(R.id.tvMovieAdvisory);
            tvMovieAdvisory.setText(advisory);


            final String photoUrl = movieDetail.getHighPhotoUrl();
            final String photoLargeUrl = movieDetail.getLargePhotoUrl();
            ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
            ivDetailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieDetailActivity.this, ZoomableImage.class);
                    intent.putExtra(Constants.ARG_IMAGE_URL, photoUrl);
                    MovieDetailActivity.this.startActivity(intent);
                    MovieDetailActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });


            final ImageView ivBlur = (ImageView) findViewById(R.id.ivBlur);
            Glide.with(this)
                    .load(photoUrl)
                    .listener(new    RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Utility.blurImageView(MovieDetailActivity.this, ivDetailImage, ivBlur);
                            ImageView ivDetailImageLargePhoto = (ImageView) findViewById(R.id.ivDetailImageLargePhoto);
                            ivDetailImageLargePhoto.setVisibility(View.VISIBLE);
                            Glide.with(MovieDetailActivity.this)
                                    .load(photoLargeUrl)
                                    .crossFade()
                                    .centerCrop()
                                    .into(ivDetailImageLargePhoto);
                            return false;
                        }
                    })
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.icn_movies_placeholder)
                    .into(ivDetailImage);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityAnimation.exitActivityAnimation(this);
    }

    public void onFinish(int resultCode){
        setResult(resultCode, new Intent());
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
                if(data == null) {
                } else {
                    int code = data.getIntExtra(Constants.REQUEST_CODE_KEY, 0);
                    if(code == Constants.REQUEST_CODE_SHOW_PARKING_SPOT){
                        String parkingName = data.getStringExtra(Constants.REQUEST_CODE_KEY_PARKING_NAME);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.REQUEST_CODE_KEY, Constants.REQUEST_CODE_SHOW_PARKING_SPOT);
                        intent.putExtra(Constants.REQUEST_CODE_KEY_PARKING_NAME, parkingName);
                        setResult(Integer.valueOf(resultCode), intent);
                        onBackPressed();
                    } else if(code == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP){
                        Intent intent = new Intent();
                        intent.putExtra(Constants.REQUEST_CODE_KEY, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                        setResult(Integer.valueOf(resultCode), intent);
                        onBackPressed();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

}
