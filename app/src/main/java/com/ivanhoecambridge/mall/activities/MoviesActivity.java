package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
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
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpOverrides;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpTimeConverter;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.MoviesRecyclerViewAdapter;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.movies.MovieInterface;
import com.ivanhoecambridge.mall.movies.MovieManager;
import com.ivanhoecambridge.mall.movies.models.Address;
import com.ivanhoecambridge.mall.movies.models.House;
import com.ivanhoecambridge.mall.movies.models.MovieDetail;
import com.ivanhoecambridge.mall.movies.models.Movies;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.CTA;
import com.ivanhoecambridge.mall.views.CustomAnimation;
import com.ivanhoecambridge.mall.views.MovieRecyclerItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import constants.MallConstants;

import static com.ivanhoecambridge.mall.adapters.MoviesRecyclerViewAdapter.ITEM_TYPE_THEATER_VIEWER;

/**
 * Created by Kay on 2016-10-26.
 */

public class MoviesActivity extends AppCompatActivity implements MovieInterface{

    protected final Logger logger = new Logger(getClass().getName());
    private ViewGroup mParentView;
    private View mLayoutStoreHours;
    private ImageView ivDetailImage;
    private ImageView ivDetailImageBg;
    private MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        showContentsWithCTL();
        setUpCTA();
        setRecyclerView();
    }

    private void init(){
        setContentView(R.layout.activity_movie);
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
                    tvToolbar.setTextColor(Utility.getColorWithAlpha(MoviesActivity.this, R.color.toolbarTextColor, alpha));
                    toolbar.getBackground().setAlpha(255 - alpha);
                }
            });

            ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
            ivDetailImageBg = (ImageView) findViewById(R.id.ivDetailImageBg);
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
            } else {
                ivDetailImage.setVisibility(View.INVISIBLE);
            }

            if(!getIntent().getExtras().getBoolean(Constants.ARG_TRANSITION_ENABLED)) {
                ivDetailImage.setVisibility(View.INVISIBLE);
            }

            LinearLayout llViewShowtimes = (LinearLayout) findViewById(R.id.llViewShowtimes);
            llViewShowtimes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MoviesActivity.this, ShowtimesActivity.class);
                    MoviesActivity.this.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                    ActivityAnimation.startActivityAnimation(MoviesActivity.this);
                }
            });


        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                            Intent intent = new Intent();
                            intent.putExtra(Constants.REQUEST_CODE_KEY, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                            ArrayList<KcpPlaces> kcpPlaces = KcpPlacesRoot.getInstance().getPlaceByName(MallConstants.CINEMA_NAME);
                            String externalCode = null;
                            if(kcpPlaces.size() > 0) {
                                externalCode = kcpPlaces.get(0).getExternalCode();
                            }
                            if(externalCode != null) {
                                setResult(Integer.valueOf(externalCode), intent);
                                onBackPressed();
                            }
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
                            Intent intent = new Intent();

                            ArrayList<KcpPlaces> kcpPlaces = KcpPlacesRoot.getInstance().getPlaceByName(MallConstants.CINEMA_NAME);
                            String storeParking = null;
                            String externalCode = null;
                            if(kcpPlaces.size() > 0) {
                                storeParking = kcpPlaces.get(0).location.getParking();
                                externalCode = kcpPlaces.get(0).getExternalCode();
                            }
                            if(storeParking != null) {
                                intent.putExtra(Constants.REQUEST_CODE_KEY, Constants.REQUEST_CODE_SHOW_PARKING_SPOT);
                                intent.putExtra(Constants.REQUEST_CODE_KEY_PARKING_NAME, storeParking);
                                setResult(Integer.valueOf(externalCode), intent);
                                onBackPressed();
                            }
                        }
                    }, true);

            String todaysHour = null;
            try {
                ArrayList<KcpPlaces> kcpPlacesList = KcpPlacesRoot.getInstance().getPlaceByName(MallConstants.CINEMA_NAME);
                if(kcpPlacesList.size() > 0) {
                    if(kcpPlacesList != null){
                        KcpPlaces kcpPlaces = kcpPlacesList.get(0);
                        todaysHour = kcpPlaces.getOpeningAndClosingHoursForThisDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));


                        List<KcpOverrides.ContinuousOverride> comingHolidays = kcpPlaces.getHolidaysWithin(Constants.NUMB_OF_DAYS);
                        if(comingHolidays == null || comingHolidays.size() == 0) comingHolidays = KcpPlacesRoot.getInstance().getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL).getHolidaysWithin(7);
                        KcpUtility.sortHoursList((ArrayList) comingHolidays);

                        String overrideHour = kcpPlaces.getOpeningAndClosingHoursForThisDayWithOverrideHours(comingHolidays, Calendar.getInstance());
                        if(!overrideHour.equals("")) todaysHour = overrideHour;
                        todaysHour = todaysHour.replace("-", "to");
                        mLayoutStoreHours = getLayoutInflater().inflate(
                                R.layout.layout_store_hours,
                                mParentView,
                                false);

                        LinearLayout llHolidays = (LinearLayout) mLayoutStoreHours.findViewById(R.id.llHolidays);

                        if(comingHolidays.size() != 0){
                            llHolidays.setVisibility(View.VISIBLE);
                            TextView tvHolidayName = (TextView) mLayoutStoreHours.findViewById(R.id.tvHolidayName);

                            String holidayNames = "";
                            for(int i = 0; i < comingHolidays.size(); i++) {
                                holidayNames = holidayNames + comingHolidays.get(i).getName() + " " + KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHolidays.get(i).getEndDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, Constants.DATE_FORMAT_HOLIDAY_STORE);
                                if(i != comingHolidays.size() - 1 ) holidayNames = holidayNames + "\n";
                            }
                            tvHolidayName.setText(holidayNames);
                        }

                        LinearLayout llMallHour = (LinearLayout) mLayoutStoreHours.findViewById(R.id.llMallHour);
                        ((ViewGroup) llMallHour).removeAllViews();
                        for(int i = 0; i < Constants.NUMB_OF_DAYS; i++){
                            ((ViewGroup) llMallHour).addView(getMallHourListItem(i, comingHolidays));
                        }
                    }
                }


            } catch (Exception e) {
                logger.error(e);
            }

            //Store hours
            CTA storeHours = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_hours,
                    todaysHour,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mLayoutStoreHours == null) return;
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
                            v.startAnimation(animation);
                        }
                    }, true);

            if(mLayoutStoreHours != null) {
                LinearLayout llSubCTA = (LinearLayout) storeHours.getView().findViewById(R.id.llSubCTA);
                llSubCTA.removeAllViews();
                llSubCTA.addView(mLayoutStoreHours);
                storeHours.getView().getLayoutParams().height = (int) getResources().getDimension(R.dimen.detail_button_height);
            }


            House house = MovieManager.sTheaters.getHouse();
            final String phoneNumber = house.getAddress().getPhone();

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
                                Utility.makeCallWithAlertDialog(
                                        MoviesActivity.this,
                                        getResources().getString(R.string.title_make_calls),
                                        getResources().getString(R.string.warning_make_call) + phoneNumber + "?",
                                        getResources().getString(R.string.action_call),
                                        getResources().getString(R.string.action_cancel),
                                        phoneNumber);
                        }
                    }, false);



            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(storeHours);
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

    public View getMallHourListItem(int daysPastToday, List<KcpOverrides.ContinuousOverride> comingHolidays){
        View v = getLayoutInflater().inflate(R.layout.list_item_store_hour, null, false);
        try {
            TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
            TextView tvMallHour = (TextView) v.findViewById(R.id.tvHour);
            ImageView ivHolidayIndicator = (ImageView) v.findViewById(R.id.ivHolidayIndicator);

            Calendar today = Calendar.getInstance();
            long todayInMillisPlusDays = daysPastToday * 24 * 60 * 60 * 1000 + today.getTimeInMillis();
            today.setTimeInMillis(todayInMillisPlusDays);

            ArrayList<KcpPlaces> kcpPlacesList = KcpPlacesRoot.getInstance().getPlaceByName(MallConstants.CINEMA_NAME);
            if(kcpPlacesList.size() > 0) {
                KcpPlaces kcpPlaces = kcpPlacesList.get(0);

                String openAndClosingHour = kcpPlaces.getOpeningAndClosingHoursForThisDay(today.get(Calendar.DAY_OF_WEEK));
                openAndClosingHour = openAndClosingHour.replace("-", "to").replace("AM", "am").replace("PM", "pm");
                tvMallHour.setText(openAndClosingHour);

                if (daysPastToday == 0) {
                    v.setBackgroundColor(getResources().getColor(R.color.store_hour_selected_bg));
                }
                String mallHourDate = KcpTimeConverter.convertDateFormat(today.getTime(), Constants.DATE_FORMAT_DAY);
                tvDate.setText(mallHourDate.toUpperCase());

                //overriding holidays
                openAndClosingHour = kcpPlaces.getOpeningAndClosingHoursForThisDayWithOverrideHours(comingHolidays, today);
                if (!openAndClosingHour.equals("")) {
                    openAndClosingHour = openAndClosingHour.replace("-", "to").replace("AM", "am").replace("PM", "pm");
                    tvMallHour.setText(openAndClosingHour);
                    ivHolidayIndicator.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            logger.error(e);
        }

        return v;
    }

    private void setRecyclerView(){
        ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        LinearLayout llViewShowtimes = (LinearLayout) findViewById(R.id.llViewShowtimes);
        RecyclerView rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMovies.setLayoutManager(linearLayoutManager);

        ArrayList<MovieDetail> movieDetails = (ArrayList) MovieManager.sMovies.getMovies();
        if(movieDetails.size() != 0) {
            Movies.sortMoviesList(movieDetails);
            mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(this, null, movieDetails, ITEM_TYPE_THEATER_VIEWER);
            rvMovies.setAdapter(mMoviesRecyclerViewAdapter);
            rvMovies.setNestedScrollingEnabled(false);

            MovieRecyclerItemDecoration itemDecoration = new MovieRecyclerItemDecoration(this, R.dimen.movie_poster_horizontal_margin);
            rvMovies.addItemDecoration(itemDecoration);

            pb.setVisibility(View.GONE);
            llViewShowtimes.setVisibility(View.VISIBLE);
        } else {
            MainActivity.sMovieManager.setMovieInterface(this);
            llViewShowtimes.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ivDetailImageBg.setVisibility(View.INVISIBLE);
        ivDetailImage.setVisibility(View.VISIBLE);
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
