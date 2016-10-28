package com.ivanhoecambridge.mall.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.MoviesRecyclerViewAdapter;
import com.ivanhoecambridge.mall.movies.MovieInterface;
import com.ivanhoecambridge.mall.movies.MovieManager;
import com.ivanhoecambridge.mall.movies.models.Address;
import com.ivanhoecambridge.mall.movies.models.House;
import com.ivanhoecambridge.mall.movies.models.MovieDetail;
import com.ivanhoecambridge.mall.views.CTA;
import com.ivanhoecambridge.mall.views.DealRecyclerItemDecoration;
import com.ivanhoecambridge.mall.views.MovieRecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-10-26.
 */

public class MoviesActivity extends AppCompatActivity implements MovieInterface{

    protected final Logger logger = new Logger(getClass().getName());
    private ViewGroup mParentView;
    private View mLayoutStoreHours;
    private ImageView ivDetailImage;
    private MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;
    private boolean mHasTransitionStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        showContentsWithCTL();
        setUpCTA();
        setRecyclerView();
    }

    private void init(){
        setContentView(R.layout.activity_detail_movie);
        mParentView = (ViewGroup) findViewById(R.id.svDetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    private void showContentsWithCTL(){
        try {

            //TOOLBAR
            final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctlDetail);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            final TextView tvToolbar = (TextView) toolbar.findViewById(R.id.tvToolbar);
            getSupportActionBar().setTitle("");

            final View backdrop = (View) findViewById(R.id.backdrop);
            int height = (int) (KcpUtility.getScreenWidth(this) / KcpUtility.getFloat(this, R.dimen.ancmt_image_ratio));
            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) backdrop.getLayoutParams();
            lp.height = height;
            backdrop.setLayoutParams(lp);

            final String toolbarTitle = getResources().getString(R.string.movie_title);
            tvToolbar.setText(toolbarTitle);

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

            ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
            Glide.with(this)
                    .load(R.drawable.img_movies)
                    .crossFade()
                    .error(R.drawable.placeholder)
                    .override(KcpUtility.getScreenWidth(this), (int) (KcpUtility.getScreenWidth(this) / KcpUtility.getFloat(this, R.dimen.ancmt_image_ratio)))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.placeholder)
                    .into(ivDetailImage);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        System.out.println("#¤ ActivityB.onTransitionStart - Enter");
                        fadeOutTransitionImage();
                        mHasTransitionStarted = true;
                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        System.out.println("#¤ ActivityB.onTransitionEnd - Enter");
                    }
                    @Override
                    public void onTransitionCancel(Transition transition) {}
                    @Override
                    public void onTransitionPause(Transition transition) {}
                    @Override
                    public void onTransitionResume(Transition transition) {}
                });
            }

            LinearLayout llViewShowtimes = (LinearLayout) findViewById(R.id.llViewShowtimes);
            llViewShowtimes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MoviesActivity.this, "View All Showtimes", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mHasTransitionStarted) {
            ivDetailImage.setVisibility(View.GONE);
//            fadeOutTransitionImage();
        }
    }

    private void fadeOutTransitionImage(){
        final Animation fadeOut = AnimationUtils.loadAnimation(MoviesActivity.this, R.anim.fadeout);
        fadeOut.reset();
        ivDetailImage.clearAnimation();
        ivDetailImage.startAnimation(fadeOut);
        fadeOut.setFillAfter(true);
        ivDetailImage.setVisibility(View.INVISIBLE);
    }

    private void setUpCTA(){
        try {
            final List<CTA> cTAList = new ArrayList<>();

            //Store Location
            CTA location = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_menu_map,
                    getString(R.string.movie_find_on_map),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, true);

            //Store Parking
            CTA parking = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_parking,
                    getResources().getString(R.string.parking_nearest_spot),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, true);


            String todaysHour = null;
            try {
                    mLayoutStoreHours = getLayoutInflater().inflate(
                            R.layout.layout_store_hours,
                            mParentView,
                            false);

            } catch (Exception e) {
                logger.error(e);
            }


            //Store hours
            CTA storeHours = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_hours,
                    "",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*if(mLayoutStoreHours == null) return;
                            Animation animation;

                            Display display = getWindowManager().getDefaultDisplay();
                            mLayoutStoreHours.measure(display.getWidth(), display.getHeight());

                            if(v.getHeight() == (int) getResources().getDimension(R.dimen.detail_button_height)){
                                animation = new CustomAnimation.ExpandCollapseAnimation(v, (int) getResources().getDimension(R.dimen.detail_button_height), mLayoutStoreHours.getMeasuredHeight() + (int) getResources().getDimension(R.dimen.detail_button_height));
                            } else {
                                animation = new CustomAnimation.ExpandCollapseAnimation(v, mLayoutStoreHours.getMeasuredHeight(), (int) getResources().getDimension(R.dimen.detail_button_height));
                            }

                            animation.setDuration(300);
                            v.clearAnimation();
                            v.startAnimation(animation);*/
                        }
                    }, false);

            if(mLayoutStoreHours != null) {
                LinearLayout llSubCTA = (LinearLayout) storeHours.getView().findViewById(R.id.llSubCTA);
                llSubCTA.removeAllViews();
                llSubCTA.addView(mLayoutStoreHours);
                storeHours.getView().getLayoutParams().height = (int) getResources().getDimension(R.dimen.detail_button_height);
            }


            House house = MovieManager.sTheaters.getHouse();
            String phoneNumber = "";
            if(house != null) {
                Address address = house.getAddress();
                if(address != null) {
                    phoneNumber = address.getPhone();
                }
            }

            //Store Phone number
            CTA phone = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_phone,
                    phoneNumber,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, false);



            cTAList.add(location);
            cTAList.add(parking);
//            cTAList.add(storeHours);
            cTAList.add(phone);

            View llCTA = findViewById(R.id.llCTA);
            ((ViewGroup) llCTA).removeAllViews();
            for(int i = 0; i < cTAList.size(); i++){
                ((ViewGroup) llCTA).addView(cTAList.get(i).getView());
            }
        } catch (Resources.NotFoundException e) {
            logger.error(e);
        } catch (Exception e){
            logger.error(e);
        }
    }

    private void setRecyclerView(){
        ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        RecyclerView rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        LinearLayoutManager staggeredGridLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMovies.setLayoutManager(staggeredGridLayoutManager);

        ArrayList<MovieDetail> movieDetails = (ArrayList) MovieManager.sMovies.getMovie();
        if(movieDetails != null) {
            mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(this, movieDetails);
            rvMovies.setAdapter(mMoviesRecyclerViewAdapter);
            rvMovies.setNestedScrollingEnabled(false);

//            MovieRecyclerItemDecoration itemDecoration = new MovieRecyclerItemDecoration(this, R.dimen.movie_poster_margin, mMoviesRecyclerViewAdapter);
            MovieRecyclerItemDecoration itemDecoration = new MovieRecyclerItemDecoration(this, R.dimen.movie_poster_horizontal_margin, mMoviesRecyclerViewAdapter);
            rvMovies.addItemDecoration(itemDecoration);

            pb.setVisibility(View.GONE);
        } else {
            MainActivity.sMovieManager.setMovieInterface(this);
        }
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
    public void onMovieDownloaded() {
        setRecyclerView();
        setUpCTA();
    }
}
