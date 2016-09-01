package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.RSRuntimeException;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.kineticcafe.kcpandroidsdk.constant.KcpConstants;
import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.managers.KcpPlaceManager;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpOverrides;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpandroidsdk.utils.KcpTimeConverter;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.fragments.DealsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.fragments.HomeFragment;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.Blur.FastBlur;
import com.kineticcafe.kcpmall.views.Blur.RSBlur;
import com.kineticcafe.kcpmall.views.BlurTransformation;
import com.kineticcafe.kcpmall.views.CTA;
import com.kineticcafe.kcpmall.views.CustomAnimation;
import com.kineticcafe.kcpmall.views.HtmlTextView;
import com.kineticcafe.kcpmall.views.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DetailActivity extends AppCompatActivity {
//public class DetailActivity extends SwipeBackActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private Rect mRect = new Rect();
    private ViewGroup mParentView;
    private ImageView ivDetailImage; //transition image
    private ImageView ivDetailLogo; //transition logo
    private String mLikeLink = "";
    private View mLayoutStoreHours;

    private int mContentPageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KcpContentPage kcpContentPage = (KcpContentPage) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);

        mContentPageType = KcpContentTypeFactory.getContentType(kcpContentPage);
        if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE || kcpContentPage.getStoreId() != 0){
            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlace = kcpPlacesRoot.getPlaceById(kcpContentPage.getStoreId());
            if(kcpPlace != null) { //there's a store detail already downloaded
//                kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
                kcpContentPage.setPlaceList(null, kcpPlace);
            }

            ArrayList<KcpContentPage> kcpContentPages = kcpPlacesRoot.getContentPagesById(kcpContentPage.getStoreId());
            if(kcpContentPages != null) { //there's a store content pages (events/deals/ancmt) already downloaded
                kcpContentPage.setContentPageList(kcpContentPages);
            }
        }

        init(kcpContentPage);
        showContentsWithCTL(kcpContentPage);
        setUpCTA(kcpContentPage);
        setUpDealsAndEvents(kcpContentPage);
        downloadIfNecessary(kcpContentPage);
    }

    public void init(final KcpContentPage kcpContentPage){
        setContentView(R.layout.activity_detail_with_ctl);
        mParentView = (ViewGroup) findViewById(R.id.svDetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        mLikeLink = kcpContentPage.getLikeLink();
        if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){ //the like links should be given from place if the type is ITEM_TYPE_STORE
            mLikeLink = kcpContentPage.getStore().getLikeLink();
        }

        final ImageView ivFav = (ImageView) toolbar.findViewById(R.id.ivFav);
        ivFav.setSelected(FavouriteManager.getInstance(DetailActivity.this).isLiked(mLikeLink, kcpContentPage));
        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.startSqueezeAnimationForFav(new Utility.SqueezeListener() {
                    @Override
                    public void OnSqueezeAnimationDone() {
                        ivFav.setSelected(!ivFav.isSelected());
                        FavouriteManager.getInstance(DetailActivity.this).addOrRemoveFavContent(mLikeLink, kcpContentPage);
                    }
                }, DetailActivity.this, ivFav);
            }
        });


        NestedScrollView nsvDetail = (NestedScrollView) findViewById(R.id.nsvDetail);
        nsvDetail.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });

        nsvDetail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    public void downloadIfNecessary(final KcpContentPage kcpContentPage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_LOADING){
                } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
                } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_EVENT){
                    if(kcpContentPage.getStoreNumber().equals("")){
                        downloadPlace(kcpContentPage);
                    }
                } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_DEAL){
                    if(kcpContentPage.getStoreNumber().equals("")){
                        downloadPlace(kcpContentPage);
                    }
                } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
                    if(kcpContentPage.getStoreNumber().equals("")){
                        downloadPlace(kcpContentPage);
                    }

                    if(kcpContentPage.getContentPageList(true) == null){
                        downloadContentList(kcpContentPage);
                    }
                }
            }
        }).start();
    }

    public void setUpCTA(final KcpContentPage kcpContentPage){
        try {
            final List<CTA> cTAList = new ArrayList<>();

            //Store Location
            CTA location = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_menu_map,
                    getString(R.string.cta_view_on_map),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult(Integer.valueOf(kcpContentPage.getExternalCode()), new Intent());
                            onBackPressed();
                        }
                    }, true);

            //Store Parking
            CTA parking = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_parking,
                    kcpContentPage.getStoreParking(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "clicked ", Toast.LENGTH_SHORT).show();
                        }
                    }, true);


            //Store Phone number
            CTA phone = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_phone,
                    kcpContentPage.getStoreNumber(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.makeCallWithAlertDialog(
                                    DetailActivity.this,
                                    getResources().getString(R.string.title_make_calls),
                                    getResources().getString(R.string.warning_make_call) + kcpContentPage.getStoreNumber() + "?",
                                    getResources().getString(R.string.action_call),
                                    getResources().getString(R.string.action_cancel),
                                    kcpContentPage.getStoreNumber()
                            );
                        }
                    }, false);

            //Store Info
            CTA info = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_storeinfo,
                    "Store Information",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                            if(kcpPlace != null) {
                                KcpContentPage kcpContentPage = new KcpContentPage();
                                kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
                                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                                intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                                String imageUrl = kcpContentPage.getHighestResImageUrl();
                                String logoUrl = kcpContentPage.getStoreLogo();

                                ActivityOptionsCompat options = null;
                                if(ivDetailImage != null && !imageUrl.equals("")){
                                    String transitionNameImage = DetailActivity.this.getResources().getString(R.string.transition_news_image);
                                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            DetailActivity.this,
                                            Pair.create((View)ivDetailImage, transitionNameImage));
                                } else if(ivDetailLogo.getVisibility() == View.VISIBLE && !logoUrl.equals("")){
                                    String transitionNameLogo = DetailActivity.this.getResources().getString(R.string.transition_news_logo);
                                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            DetailActivity.this,
                                            Pair.create((View)ivDetailLogo, transitionNameLogo));
                                }

                                ActivityCompat.startActivityForResult(DetailActivity.this, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                                DetailActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                downloadIfNecessary(kcpContentPage);
                            }
                        }
                    }, false);

            //Add to Calendar
            CTA addToCalendar = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_addcal,
                    "Add to Calendar",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }, false);


            String todaysHour = null;
            try {
                KcpPlaces kcpPlaces = kcpContentPage.getStore();
                if(kcpPlaces != null){
                    todaysHour = kcpPlaces.getOpeningAndClosingHoursForThisDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                    List<KcpOverrides.ContinuousOverride> comingHolidays = kcpPlaces.getHolidaysWithin(Constants.NUMB_OF_DAYS);
                    if(comingHolidays == null || comingHolidays.size() == 0) comingHolidays = KcpPlacesRoot.getInstance().getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL).getHolidaysWithin(7);
                    String overrideHour = kcpPlaces.getOpeningAndClosingHoursForThisDayWithOverrideHours(comingHolidays, Calendar.getInstance());
                    if(!overrideHour.equals("")) todaysHour = overrideHour;
                    todaysHour = todaysHour.replace("-", "to");
                    mLayoutStoreHours = DetailActivity.this.getLayoutInflater().inflate(
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
                    }, false);

            if(mLayoutStoreHours != null) {
                LinearLayout llSubCTA = (LinearLayout) storeHours.getView().findViewById(R.id.llSubCTA);
                llSubCTA.removeAllViews();
                llSubCTA.addView(mLayoutStoreHours);
                storeHours.getView().getLayoutParams().height = (int) getResources().getDimension(R.dimen.detail_button_height);
            }



            if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_LOADING){

            } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
                if(kcpContentPage.getStore() != null) {
                    cTAList.add(location);
                    cTAList.add(parking);
                    cTAList.add(info);
                }
            } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_EVENT){
                if(kcpContentPage.getStore() != null) {
                    cTAList.add(location);
                    cTAList.add(parking);
                    cTAList.add(info);
                    cTAList.add(phone);
                }
                cTAList.add(addToCalendar);

            } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_DEAL){
                cTAList.add(location);
                cTAList.add(parking);
                cTAList.add(info);
                cTAList.add(phone);

            } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
                cTAList.add(location);
                cTAList.add(parking);
                cTAList.add(storeHours);
                cTAList.add(phone);

                CTA facebook = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button,
                        R.drawable.icn_facebook,
                        getResources().getString(R.string.social_facebook),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                                Utility.openWebPage(DetailActivity.this, kcpPlace.getFacebookLink());
                            }
                        }, false);

                CTA twiter = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button,
                        R.drawable.icn_twitter,
                        getResources().getString(R.string.social_twitter),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                                Utility.openWebPage(DetailActivity.this, kcpPlace.getTwitterLink());
                            }
                        }, false);

                CTA instagram = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button,
                        R.drawable.icn_instagram,
                        getResources().getString(R.string.social_instagram),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                                Utility.openWebPage(DetailActivity.this, kcpPlace.getInstagramLink());
                            }
                        }, false);

                CTA webpage = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button,
                        R.drawable.icn_web,
                        getResources().getString(R.string.social_instagram),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                                Utility.openWebPage(DetailActivity.this, kcpPlace.getMainWebsiteLink());
                            }
                        }, false);


                //SOCIAL SHARING

                KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                if(kcpPlace != null && kcpPlace.getFacebookLink() != null || kcpPlace.getTwitterLink() != null || kcpPlace.getInstagramLink() != null || kcpPlace.getMainWebsiteLink() != null){
                    LinearLayout llSharing = (LinearLayout) findViewById(R.id.llSharing);
                    llSharing.setVisibility(View.VISIBLE);

                    RelativeLayout layoutSocialSharing = (RelativeLayout) findViewById(R.id.layoutSocialSharing); //FOLLOW ON
                    layoutSocialSharing.setVisibility(View.VISIBLE);

                    TextView tvDetailSocialSharingBtnHeader = (TextView) layoutSocialSharing.findViewById(R.id.tvDetailSocialSharingBtnHeader);
                    tvDetailSocialSharingBtnHeader.setText("Follow " + kcpContentPage.getStoreName() + " on...");

                    ((ViewGroup) llSharing).removeAllViews();

                    if(kcpPlace.getFacebookLink() != null) ((ViewGroup) llSharing).addView(facebook.getView());
                    if(kcpPlace.getTwitterLink() != null) ((ViewGroup) llSharing).addView(twiter.getView());
                    if(kcpPlace.getInstagramLink() != null) ((ViewGroup) llSharing).addView(instagram.getView());
                    if(kcpPlace.getMainWebsiteLink() != null) ((ViewGroup) llSharing).addView(webpage.getView());
                }
            }

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

            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);

            String openAndClosingHour = kcpPlaces.getOpeningAndClosingHoursForThisDay(today.get(Calendar.DAY_OF_WEEK));
            openAndClosingHour = openAndClosingHour.replace("-", "to").replace("AM", "am").replace("PM","pm");
            tvMallHour.setText(openAndClosingHour);

            if(daysPastToday == 0) {
                v.setBackgroundColor(getResources().getColor(R.color.store_hour_selected_bg));
            }
            String mallHourDate = KcpTimeConverter.convertDateFormat(today.getTime(), Constants.DATE_FORMAT_DAY);
            tvDate.setText(mallHourDate.toUpperCase());

            //overriding holidays
            openAndClosingHour = kcpPlaces.getOpeningAndClosingHoursForThisDayWithOverrideHours(comingHolidays, today);
            if(!openAndClosingHour.equals("")){
                openAndClosingHour = openAndClosingHour.replace("-", "to").replace("AM", "am").replace("PM","pm");
                tvMallHour.setText(openAndClosingHour);
                ivHolidayIndicator.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            logger.error(e);
        }

        return v;
    }

    public void downloadPlace(final KcpContentPage kcpContentPage){
        KcpPlaceManager kcpPlaceManager = new KcpPlaceManager(DetailActivity.this, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
                        KcpPlaces kcpPlace = kcpPlacesRoot.getPlaceById(kcpContentPage.getStoreId());
                        kcpContentPage.setPlaceList(null, kcpPlace);

                        setUpCTA(kcpContentPage);

                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpPlaceManager.downloadPlace(kcpContentPage.getStoreId());
    }

    public void downloadContentList(final KcpContentPage kcpContentPage){
        KcpPlaceManager kcpPlaceManager = new KcpPlaceManager(DetailActivity.this, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
                        ArrayList<KcpContentPage> kcpContentPages = kcpPlacesRoot.getContentPagesById(kcpContentPage.getStoreId());
                        kcpContentPage.setContentPageList(kcpContentPages);

                        setUpDealsAndEvents(kcpContentPage);

                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpPlaceManager.downloadContents(kcpContentPage.getStoreId());
    }

    public void showContentsWithCTL(final KcpContentPage kcpContentPage){
        try {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            final TextView tvToolbar = (TextView) toolbar.findViewById(R.id.tvToolbar);
            getSupportActionBar().setTitle("");

            final String toolbarTitle = KcpContentTypeFactory.getContentTypeTitle(kcpContentPage);

            String imageUrlTemp = kcpContentPage.getHighestResImageUrl();
            if(imageUrlTemp.equals("")) imageUrlTemp = kcpContentPage.getHighestResFallbackImageUrl();
            final String imageUrl = imageUrlTemp;
            if(imageUrl.equals("")){
                //TODO: if it's necessary to have toolbar in white, change the theme here (now it's in themeColor)
                if(toolbarTitle.equals(KcpContentTypeFactory.TYPE_DEAL_STORE)) tvToolbar.setText(kcpContentPage.getStoreName());
                else tvToolbar.setText(toolbarTitle);
                Utility.setToolbarBackground(toolbar, null);

                RelativeLayout rlDetailImage = (RelativeLayout) findViewById(R.id.rlDetailImage);
                rlDetailImage.setVisibility(View.GONE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.themeColor));
            } else {
                final View backdrop = (View) findViewById(R.id.backdrop);
                int height = (int) (KcpUtility.getScreenWidth(this) / KcpUtility.getFloat(this, R.dimen.ancmt_image_ratio));
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) backdrop.getLayoutParams();
                lp.height = height;
                backdrop.setLayoutParams(lp);

                ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
                ivDetailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailActivity.this, ZoomableImage.class);
                        intent.putExtra(Constants.ARG_IMAGE_URL, imageUrl);
                        DetailActivity.this.startActivity(intent);
                        DetailActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                new GlideFactory().glideWithDefaultRatio(
                        ivDetailImage.getContext(),
                        imageUrl,
                        ivDetailImage,
                        R.drawable.placeholder);


                if(toolbarTitle.equals(KcpContentTypeFactory.TYPE_DEAL_STORE)) tvToolbar.setText(kcpContentPage.getStoreName());
                else tvToolbar.setText(toolbarTitle);


                //to show toolbar title only when collapsed
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

                //BLURRED TOP BAR
                /*int width = KcpUtility.getScreenWidth(ivDetailImage.getContext());
                int height = (int) (KcpUtility.getScreenWidth(ivDetailImage.getContext()) / KcpUtility.getFloat(ivDetailImage.getContext(), R.dimen.ancmt_image_ratio));

                Glide.with(ivDetailImage.getContext())
                        .load(imageUrl)
                        .crossFade()
                        .error(R.drawable.placeholder)
                        .override(width, height)
                        .placeholder(R.drawable.placeholder)
                        .into(ivDetailImage);

                Glide.with(this).
                        load(imageUrl)
                        .crossFade()
                        .override(width, height)
                        .centerCrop()
                        .bitmapTransform(new BlurTransformation(ivDetailImage.getContext()))
                        .into((ImageView) findViewById(R.id.ivBlurred));*/

                TextView tvExpiryDate = (TextView) findViewById(R.id.tvExpiryDate);
//                tvExpiryDate.setBackgroundColor(getResources().getColor(R.color.themeColor));
                tvExpiryDate.getBackground().setAlpha(241);

                //TODO: daysLeft shows 1 less date (ex) 2016-05-27T00:00:00.000+00:00 shows date as 26 EST see if this is right
                int daysLeftUntilEffectiveDate = KcpUtility.getDaysLeftUntil(kcpContentPage.effectiveEndTime, KcpConstants.EFFECTIVE_DATE_FORMAT);
                String daysLeft = kcpContentPage.getDaysLeftText(daysLeftUntilEffectiveDate, Constants.DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE);

                if(daysLeft.equals("")){
                    tvExpiryDate.setVisibility(View.GONE);
                } else {
                    tvExpiryDate.setVisibility(View.VISIBLE);
                    tvExpiryDate.setText(daysLeft);
                }
            }

            String logoUrl = kcpContentPage.getStoreLogo();
            TextView tvDetailLogoText = (TextView) findViewById(R.id.tvDetailLogoText);
            ivDetailLogo = (ImageView) findViewById(R.id.ivDetailLogo);

            if(logoUrl.equals("")){
                String placeName = kcpContentPage.getStoreName();
                ivDetailLogo.setVisibility(View.GONE);
                if(!placeName.equals("")) tvDetailLogoText.setVisibility(View.VISIBLE);
                tvDetailLogoText.setText(placeName);

            } else {
                tvDetailLogoText.setVisibility(View.GONE);

                new GlideFactory().glideWithNoDefaultRatio(
                        ivDetailLogo.getContext(),
                        logoUrl,
                        ivDetailLogo);

            }

            TextView tvDetailTitle = (TextView) findViewById(R.id.tvDetailTitle);
            String title = "";
            if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
                title = kcpContentPage.getStoreName();
            } else {
                title = kcpContentPage.getTitle();
            }
            if(title.equals("")) tvDetailTitle.setVisibility(View.INVISIBLE);
            else tvDetailTitle.setText(title);

            TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
            String time = "";
            if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
                String[] timeArray = new String[2];
                time = kcpContentPage.getStoreHourForToday(timeArray, KcpPlacesRoot.getInstance().getMallContinuousOverrides());
                time = timeArray[0].toUpperCase() + " " + timeArray[1];
                if(time.toLowerCase().contains("closed")) {
                    tvDetailDate.setBackgroundResource(R.drawable.btn_style_corner_radius_gray);
                    tvDetailDate.setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                time =
                        kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EFFECTIVE) +
                                " - " +
                                kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EFFECTIVE);
            }

            if(time.equals(" - ")) tvDetailDate.setVisibility(View.GONE);
            tvDetailDate.setText(time);

            TextView tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
            String body = kcpContentPage.getBody();
            if(body.equals("")) tvDetailBody.setVisibility(View.GONE);
            else HtmlTextView.setHtmlTextView(this, tvDetailBody, body, R.color.html_link_text_color);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void setUpDealsAndEvents(final KcpContentPage kcpContentPage){
        ArrayList<KcpContentPage> kcpContentPages = kcpContentPage.getContentPageList(true);
        if(kcpContentPages != null && mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
            ArrayList<KcpContentPage> dealContentpages = new ArrayList<KcpContentPage>();
            ArrayList<KcpContentPage> eventContentpages = new ArrayList<KcpContentPage>();

            //Separating the content page list into Deal / Event
            for(KcpContentPage dealEventContentPage : kcpContentPages){
                if(KcpContentTypeFactory.getContentType(dealEventContentPage) == KcpContentTypeFactory.ITEM_TYPE_DEAL){
                    dealContentpages.add(dealEventContentPage);
                } else if(KcpContentTypeFactory.getContentType(dealEventContentPage) == KcpContentTypeFactory.ITEM_TYPE_EVENT){
                    eventContentpages.add(dealEventContentPage);
                }
            }

            //Set up RecyclerView for DEAL
            LinearLayout llStoreDeals = (LinearLayout) findViewById(R.id.llStoreDeals);
            RecyclerView rvStoreDeals = (RecyclerView) findViewById(R.id.rvStoreDeals);
            setUpRecyclerView(llStoreDeals, rvStoreDeals, dealContentpages);

            //Set up RecyclerView for EVENT
            LinearLayout llStoreEvents = (LinearLayout) findViewById(R.id.llStoreEvents);
            RecyclerView rvStoreEvents = (RecyclerView) findViewById(R.id.rvStoreEvents);
            setUpRecyclerView(llStoreEvents, rvStoreEvents, eventContentpages);
        }
    }

    private void setUpRecyclerView(LinearLayout rvLinearLayout, RecyclerView rv, ArrayList<KcpContentPage> rvItems){
        if(rvItems.size() > 0 ) rvLinearLayout.setVisibility(View.VISIBLE);
        LinearLayoutManager staggeredGridLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(staggeredGridLayoutManager);
        DealsRecyclerViewAdapter dealsRecyclerViewAdapter = new DealsRecyclerViewAdapter(
                this,
                false,
                R.layout.list_item_store_detail_deals,
                rvItems,
                new ArrayList<KcpContentPage>());
        rv.setAdapter(dealsRecyclerViewAdapter);
        SpacesItemDecoration itemDecoration = new SpacesItemDecoration(this, R.dimen.card_horizontal_margin);
        rv.addItemDecoration(itemDecoration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
            if (resultCode != 0) {
                setResult(resultCode, new Intent());
                onBackPressed();
            }
        }
    }
}
