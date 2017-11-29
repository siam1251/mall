package com.ivanhoecambridge.mall.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.util.EventBus;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpDataListener;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.InfoList;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.KcpMallInfoRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.account.KcpAccount;
import com.ivanhoecambridge.mall.adapters.HomeBottomTapAdapter;
import com.ivanhoecambridge.mall.adapters.adapterHelper.ActiveMallRecyclerViewAdapter;
import com.ivanhoecambridge.mall.adapters.adapterHelper.GiftCardRecyclerViewAdapter;
import com.ivanhoecambridge.mall.adapters.adapterHelper.IndexableRecylerView;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.bluedot.BluetoothManager;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.crashReports.CustomizedExceptionHandler;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.DirectoryFragment;
import com.ivanhoecambridge.mall.fragments.HomeFragment;
import com.ivanhoecambridge.mall.fragments.InfoFragment;
import com.ivanhoecambridge.mall.fragments.MapFragment;
import com.ivanhoecambridge.mall.geofence.GeofenceManager;
import com.ivanhoecambridge.mall.interfaces.MapInterface;
import com.ivanhoecambridge.mall.managers.DeepLinkManager;
import com.ivanhoecambridge.mall.managers.ETManager;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.managers.GiftCardManager;
import com.ivanhoecambridge.mall.managers.KcpNotificationManager;
import com.ivanhoecambridge.mall.managers.SidePanelManagers;
import com.ivanhoecambridge.mall.mappedin.Amenities;
import com.ivanhoecambridge.mall.mappedin.AmenitiesManager;
import com.ivanhoecambridge.mall.mappedin.Amenity;
import com.ivanhoecambridge.mall.movies.MovieManager;
import com.ivanhoecambridge.mall.onboarding.TutorialActivity;
import com.ivanhoecambridge.mall.parking.ParkingManager;
import com.ivanhoecambridge.mall.parking.Parkings;
import com.ivanhoecambridge.mall.rating.AppRatingManager;
import com.ivanhoecambridge.mall.rating.RatingListener;
import com.ivanhoecambridge.mall.searchIndex.IndexManager;
import com.ivanhoecambridge.mall.signup.JanrainRecordManager;
import com.ivanhoecambridge.mall.user.AccountManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;
import com.ivanhoecambridge.mall.views.BadgeView;
import com.ivanhoecambridge.mall.views.CircleImageView;
import com.ivanhoecambridge.mall.views.KcpAnimatedViewPager;
import com.ivanhoecambridge.mall.views.ThemeColorImageView;
import com.janrain.android.Jump;
import com.mappedin.sdk.Polygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import factory.HeaderFactory;

import static com.ivanhoecambridge.mall.activities.ParkingActivity.PARKING_RESULT_CODE_SPOT_PENDING;
import static com.ivanhoecambridge.mall.activities.ParkingActivity.PARKING_RESULT_CODE_SPOT_SAVED;
import static com.ivanhoecambridge.mall.bluedot.BluetoothManager.mDidAskToTurnOnBluetooth;
import static com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenterImpl.mAskForBluetooth;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, KcpDataListener, KcpApplication.EtPushListener, RatingListener, ETManager.NotificationRequestListener{

    protected static final String TAG = "MainActivity";
    private final static int WELCOME_MSG_RESET_TIMER_IN_HOUR = 12; //every this hour, it will try to show the welcome msg to help parking

    public final static int VIEWPAGER_PAGE_HOME = 0;
    public final static int VIEWPAGER_PAGE_DIRECTORY = 1;
    public final static int VIEWPAGER_PAGE_MAP = 2;
    public final static int VIEWPAGER_PAGE_INFO = 3;

    protected final Logger logger = new Logger(getClass().getName());
    public DrawerLayout mDrawer;
    private Thread mSplashThread;
    private ActionBarDrawerToggle mToggle;
    private Snackbar mOfflineSnackbar;
    private ImageView ivToolbar;
    private KcpAnimatedViewPager mViewPager;
    private View scRightDrawerLayout;
    private AppBarLayout ablTopNav;
    private Toolbar mToolbar;
    public RelativeLayout rlDestinationEditor;
    private EditText etStartStore;
    private EditText etDestStore;
    private FrameLayout flActiveMallDot;
    private int mCurrentViewPagerTapPosition = VIEWPAGER_PAGE_HOME;
    private GiftCardRecyclerViewAdapter mGiftCardRecyclerViewAdapter;
    private AlertDialog                 ratingDialog;
    private RecyclerView                rvGiftCard;
    private ProgressBar                 pbGCUpdate;
    private TextView tvGCUpdateMessage;


    private ImageView       ivDrawerLayoutBg;
    private LinearLayout    llDisplayNameSettings;
    private TextView        tvSignInOrOut;
    private TextView        tvDrawerLayoutAccount;
    private CircleImageView ivDrawerLayoutUser;

    private BadgeView badgeDeals;
    private BadgeView badgeEvents;
    private BadgeView badgeStores;
    private BadgeView badgeInterests;
    private ThemeColorImageView ivMoreMenu;
    public static boolean mActiveMall = false;
    public boolean mSplashScreenGone = false; //when map initializes it causes lag to splashscreen. Use this variable to see if splash screen's gone

    //internal
    private boolean isFirebaseAnalyticsOn;

    //GEOFENCE
    public GeofenceManager mGeofenceManager;
    private Animation mMenuActiveMallDotAnim;
    private boolean mDidPressLocationAccessRetry = false;

    public RecyclerView rvMallDirectory; //SEARCH RECYCLERVIEW FROM DIRECTORY FRAGMENT
    public IndexableRecylerView rvMap; //Store rv from Map Fragment
    public boolean mIsDataLoaded = false;

    //ET Push
    private ETPush etPush;
    public static MovieManager sMovieManager;

    private JanrainRecordManager jrRecordManager;
    private Handler uiHandler = new Handler();
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KcpNotificationManager.onWelcomeNotiClick(this, intent);
        new DeepLinkManager(this).handleDeepLink(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SplashScreen);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean didOnboardingAppear = KcpUtility.loadFromSharedPreferences(this, Constants.PREF_KEY_ONBOARDING_DID_APPEAR, false);
        if(!didOnboardingAppear) {
            startActivityForResult(new Intent(MainActivity.this, TutorialActivity.class), Constants.REQUEST_CODE_TUTORIAL);
            ActivityAnimation.startActivityAnimation(MainActivity.this);
        }

        KcpNotificationManager.onWelcomeNotiClick(this, getIntent());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBarWhileDownloading.showProgressDialog(MainActivity.this, R.layout.layout_loading_item, true);
            }
        });

        final ImageView splashImage = (ImageView) MainActivity.this.findViewById(R.id.ivSplash);
        splashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (mSplashThread) {
                    mSplashThread.notifyAll();
                }
            }
        });
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(Constants.DURATION_SPLASH_ANIMATION);
                    }
                } catch (InterruptedException ex) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSplashScreenGone = true;
                        splashImage.setVisibility(View.GONE);
                        Animation animFadeOut = AnimationUtils.loadAnimation(MainActivity.this,
                                R.anim.splash_fade_out);
                        splashImage.startAnimation(animFadeOut);
                        if(mReadyToLoadMapListener != null) mReadyToLoadMapListener.onReady();
                        ProgressBarWhileDownloading.showProgressDialog(MainActivity.this, R.layout.layout_loading_item, false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showRatingDialog();
                            }
                        }, 1000);
                    }
                });

            }
        };
        mSplashThread.start();
        initializeToolbar();
        rvMallDirectory = (RecyclerView) findViewById(R.id.rvMallDirectory);
        rvMap = (IndexableRecylerView) findViewById(R.id.rvMap);

        ablTopNav = (AppBarLayout)findViewById(R.id.ablTopNav);
        rlDestinationEditor = (RelativeLayout) findViewById(R.id.rlDestinationEditor);
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        etStartStore = (EditText) findViewById(R.id.etStartStore);
        etDestStore = (EditText) findViewById(R.id.etDestStore);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment.getInstance().didTapBack();
            }
        });
        mViewPager = (KcpAnimatedViewPager) findViewById(R.id.vpMain);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerTapPosition = position;
                updateActivePage(position);

                showMapToolbar(position);
                if(position == VIEWPAGER_PAGE_MAP || position == VIEWPAGER_PAGE_INFO ) expandTopNav(); //TODO: change this hardcode

                if(position == VIEWPAGER_PAGE_INFO) {
                    InfoFragment.getInstance().setParkingSpotCTA();
                } else if(position == VIEWPAGER_PAGE_DIRECTORY) {
                    //when searchView's expanded, keyboard slides up the view, the user chooses to change fragment, come back to Directory Fragment, the recyclerView's not cleared as mSearchItem.collapseActionView() hasn't been called
                    if(DirectoryFragment.getInstance().mSearchItem != null) DirectoryFragment.getInstance().mSearchItem.collapseActionView();
                }

                if(position == VIEWPAGER_PAGE_MAP) {
                    if(mAskForBluetooth && !mDidAskToTurnOnBluetooth) {
                        BluetoothManager bluetoothManager = new BluetoothManager(MainActivity.this);
                        bluetoothManager.turnOnBluetooth();
                    }
                    if(mOnDealsClickListener != null && Amenities.isToggled(MainActivity.this, Amenities.GSON_KEY_DEAL, false)) mOnDealsClickListener.onDealsClick(true);
                    mViewPager.setPagingEnabled(false); //disable swiping between pagers
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.scRightDrawerLayout)); //enable the right drawerlayout
                    setToolbarElevation(true);
                    if(MapFragment.getInstance().mSearchItem != null) MapFragment.getInstance().mSearchItem.collapseActionView();
                } else {
                    if(rlDestinationEditor.getVisibility() == View.VISIBLE) MapFragment.getInstance().didTapBack();
                    mViewPager.setPagingEnabled(true);
                    setToolbarElevation(false);
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.scRightDrawerLayout));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlBottom);

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitleList = new ArrayList<>();
        List<Integer> fragmentIconList = new ArrayList<>();

        prepareTabContents(fragmentList, fragmentTitleList, fragmentIconList);
        setupViewPager(fragmentList, fragmentTitleList, fragmentIconList);

        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons(tabLayout, fragmentTitleList, fragmentIconList);
        mViewPager.setTabLayout(tabLayout);

        initializeKcpData(null);
        setUpLeftSidePanel();
        setUpRightSidePanel();

        scRightDrawerLayout = (View) findViewById(R.id.scRightDrawerLayout);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.scRightDrawerLayout));

        if(didOnboardingAppear) { //to prevent the permission from showing up before onboarding's finished
            mGeofenceManager = new GeofenceManager(this);
        }
        setActiveMall(false, false);


        //ET Push
        EventBus.getInstance().register(this);
        if (etPush == null) {
            // If etPush is null then get it from our application class or register for updates
            etPush = KcpApplication.getEtPush(this);
            if (etPush != null) {
                // We can assume that we need to execute the tasks in our interface if our previous call resulted in a non-null etPush otherwise this would have already executed and our etPush would not have been null to begin with :)
                this.onReadyForPush(etPush);
            }
        }

        if(BuildConfig.DEBUG) {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(this, "/mnt/sdcard/"));
            else Toast.makeText(this, "CustomizedExceptionHandler is turned off", Toast.LENGTH_SHORT).show();
        }

        new DeepLinkManager(this).handleDeepLink(getIntent());
    }

    public int getViewerPosition(){
        return mCurrentViewPagerTapPosition;
    }

    public void expandTopNav() {
        ablTopNav.setExpanded(true);
    }

    public void openLeftDrawerLayout(){
        mDrawer.openDrawer(GravityCompat.START);
    }

    public void openRightDrawerLayout(){
        mDrawer.openDrawer(scRightDrawerLayout);
    }

    public void initializeKcpData(final SwipeRefreshLayout srl){
        Constants.setLocale(this, R.string.locale);
        HeaderFactory.constructHeader();
        if(!KcpUtility.isNetworkAvailable(this)){
            mOfflineSnackbar = null;
            if(srl != null) srl.setRefreshing(false);
            ProgressBarWhileDownloading.showProgressDialog(this, R.layout.layout_loading_item, false);
            this.onDataDownloaded(); //TODO: error here when offline
            this.showSnackBar(R.string.warning_no_internet_connection, R.string.warning_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressBarWhileDownloading.showProgressDialog(MainActivity.this, R.layout.layout_loading_item, true);
                    initializeKcpData(srl);
                }
            });
            return;
        } else {
            if(KcpAccount.getInstance().isTokenAvailable()) HomeFragment.getInstance().initializeHomeData();
            else initializeAccount();
            DirectoryFragment.getInstance().initializeDirectoryData();
//            MapFragment.getInstance().initializeMap(); //TODO: cause int com.mappedin.jpct.Texture.getOpenGLID(int) from MappedIn - investigate
            InfoFragment.getInstance();
            initializeMapData();
            initializeParkingData();
            initializeSeachIndex();
            if(BuildConfig.MOVIE)initializeMovieData();
        }
    }


    private void updateActivePage(int position) {
        switch (position) {
            case VIEWPAGER_PAGE_HOME:
            default:
                HomeFragment.getInstance().onPageActive();
                break;
            case VIEWPAGER_PAGE_DIRECTORY:
                DirectoryFragment.getInstance().onPageActive();
                break;
            case VIEWPAGER_PAGE_MAP:
                MapFragment.getInstance().onPageActive();
                break;
            case VIEWPAGER_PAGE_INFO:
                InfoFragment.getInstance().onPageActive();
                break;
        }
    }

    private void initializeSeachIndex() {
        IndexManager indexManager = new IndexManager(this, R.layout.layout_loading_item, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        indexManager.downloadSearchIndexes();
    }

    private void initializeToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ivToolbar = (ImageView) mToolbar.findViewById(R.id.ivToolbar);
        ivToolbar.setVisibility(View.VISIBLE);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDrawer.addDrawerListener(mToggle);
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                setActiveMallDot(false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideGiftCardList();
                GiftCardManager.getInstance(MainActivity.this).updateBalance();
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    Analytics.getInstance(MainActivity.this).logEvent("Myprofiletab_Open", "PROFILE", "Open Profile Tab");
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                setActiveMallDot(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        mToggle.syncState();
    }


    // ------------------------------------- START OF MAP FRAGMENT -------------------------------------
    public void showMapToolbar(int position){
        if(position == VIEWPAGER_PAGE_HOME) {
            ivToolbar.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else if(position == VIEWPAGER_PAGE_DIRECTORY) {
            ivToolbar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_directory));
            mToolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        } else if(position == VIEWPAGER_PAGE_MAP) {
            ivToolbar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_map));
            mToolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        } else if(position == VIEWPAGER_PAGE_INFO) {
            ivToolbar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_info));
            mToolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        }
    }


    private TextWatcher mTextWatcherForStartStore;
    private TextWatcher mTextWatcherForDestStore;

    public void toggleDestinationEditor(final boolean hide, final String start, final String dest, final MapFragment.FocusListener focusListener){

        final TextView tvStartStore = (TextView) findViewById(R.id.tvStartStore);
        final TextView tvDestStore = (TextView) findViewById(R.id.tvDestStore);
        final int leftPaddingWithoutPrefix = (int) getResources().getDimension(R.dimen.activity_margin_small);


        if(mTextWatcherForStartStore == null) {
            mTextWatcherForStartStore = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    MapFragment.getInstance().onTextChange(s.toString());
                    if(s.toString().equals("")) {
                        tvStartStore.setText(getResources().getString(R.string.hint_search_start));
                        etStartStore.setPadding(leftPaddingWithoutPrefix, 0, leftPaddingWithoutPrefix, 0);
                    } else {
                        tvStartStore.setText(getResources().getString(R.string.hint_search_start_when_not_empty));
                        etStartStore.setPadding(KcpUtility.dpToPx(MainActivity.this, 42), 0, leftPaddingWithoutPrefix, 0);
                    }
                }
            };
            etStartStore.addTextChangedListener(mTextWatcherForStartStore);
        }


        if(mTextWatcherForDestStore == null) {
            mTextWatcherForDestStore = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    MapFragment.getInstance().onTextChange(s.toString());
                    if(s.toString().equals("")) {
                        tvDestStore.setText(getResources().getString(R.string.hint_search_destination));
                        etDestStore.setPadding(leftPaddingWithoutPrefix, 0, leftPaddingWithoutPrefix, 0);
                    } else {
                        tvDestStore.setText(getResources().getString(R.string.hint_search_destination_when_not_empty));
                        etDestStore.setPadding(KcpUtility.dpToPx(MainActivity.this, 25), 0, leftPaddingWithoutPrefix, 0);
                    }
                }
            };
            etDestStore.addTextChangedListener(mTextWatcherForDestStore);
        }


        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        if(hide) {
            Utility.closeKeybaord(this);
            if(isDestinationEditorVisible()){
                Animation slideDownAnimation = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.anim_slide_up_out_of_screen);
                slideDownAnimation.reset();
                rlDestinationEditor.startAnimation(slideDownAnimation);
                slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rlDestinationEditor.setVisibility(View.GONE);
                        mToolbar.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        } else {
            etStartStore.setOnFocusChangeListener(focusListener);
            etDestStore.setOnFocusChangeListener(focusListener);

            mToolbar.setVisibility(View.INVISIBLE); //SHOUDLN'T SET TO GONE - it changes the layout height resulting in map re-rendering and slows down process
            rlDestinationEditor.setVisibility(View.VISIBLE);
            Animation slideDownAnimation = AnimationUtils.loadAnimation(MainActivity.this,
                    R.anim.anim_slide_down);
            slideDownAnimation.reset();
            rlDestinationEditor.startAnimation(slideDownAnimation);
            slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            setDestionationNames(start, dest); //set destination names - ex) start : A&W , destionation : Club Monaco
            moveFocusToNextEditText();
        }
    }

    public void setDestionationNames(String start, String dest){
        if(dest != null) etDestStore.setText(dest);
        if(start != null) etStartStore.setText(start);
    }

    public void moveFocusToNextEditText(){
        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        if(etStartStore.getText() == null || etStartStore.getText().toString().equals("")) {
            etDestStore.clearFocus();
            etStartStore.requestFocus();
            //open keyboard if closed, leave it open if opened
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else if(etDestStore.getText() == null || etDestStore.getText().toString().equals("")) {
            etStartStore.clearFocus();
            etDestStore.requestFocus();
            //open keyboard if closed, leave it open if opened
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    public String getStartStoreName(){
        if(etStartStore == null || etStartStore.getText() == null) return "";
        return etStartStore.getText().toString();
    }

    public String getDestStoreName(){
        if(etDestStore == null || etDestStore.getText() == null) return "";
        return etDestStore.getText().toString();
    }

    public boolean isEditTextsEmpty(){
        if(etStartStore.getText().toString() != null && !etStartStore.getText().toString().equals("") &&
                etDestStore.getText().toString() != null && !etDestStore.getText().toString().equals("") ) {

            etStartStore.clearFocus();
            etDestStore.clearFocus();
            rlDestinationEditor.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isDestinationEditorVisible(){
        return rlDestinationEditor.getVisibility() == View.VISIBLE;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setToolbarElevation(boolean setElevation){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(setElevation) ablTopNav.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
            else ablTopNav.setElevation(0);

        }
    }

    // ------------------------------------- END OF MAP FRAGMENT -------------------------------------

    private void initializeAccount(){
        AccountManager accountManager = new AccountManager(this, HeaderFactory.getHeaders(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        HomeFragment.getInstance().initializeHomeData();
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        HomeFragment.getInstance().initializeHomeData();
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        accountManager.downloadUserToken();

    }

    /**
     * Checks for the <em>amenities.json</em> file based on the locale. If the locale file does not exist for that mall, it will default to the english file.
     * @return resource ID value that contains the name of the file.
     */
    private int getValidAmenitiesFile() {
        int validInfoFileId = R.string.amenities_json_default;

        if (!KcpUtility.isCurrentLocale(this, "en")) {
            AssetManager assetManager = this.getAssets();
            try {
                if (Arrays.asList(assetManager.list("")).contains(getString(R.string.amenities_json))) {
                    validInfoFileId = R.string.amenities_json;
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return validInfoFileId;
    }

    private void initializeMapData(){
        String data = "";
        try {
            data = KcpUtility.convertStreamToString(getAssets().open(Constants.getStringFromResources(this, getValidAmenitiesFile())));
            Gson gson = new Gson();
            AmenitiesManager.sAmenities = gson.fromJson(data, Amenities.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeParkingData(){

        String data = "";
        try {

            data = KcpUtility.convertStreamToString(getAssets().open(Constants.PARKING_OFFLINE_TEXT));
            Gson gson = new Gson();
            ParkingManager.sParkings = gson.fromJson(data, Parkings.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeMovieData(){
        sMovieManager = new MovieManager(this, R.layout.layout_loading_item, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        sMovieManager.downloadMovies();
    }


    private void setUpLeftSidePanel(){
        pbGCUpdate = findViewById(R.id.pbGCUpdate);
        tvGCUpdateMessage = findViewById(R.id.tvGCUpdateMessage);
        ivDrawerLayoutBg = (ImageView) findViewById(R.id.ivDrawerLayoutBg);

        llDisplayNameSettings = (LinearLayout) findViewById(R.id.llDisplayNameSettings);
        //ACCOUNT
        tvSignInOrOut = (TextView) findViewById(R.id.tvSignIn);
        tvSignInOrOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSignInOrOut.getText().toString().equals(getString(R.string.drawer_sign_out))) {
                    jrRecordManager.signOut();
                    toggleUserSignIn(false);
                } else {
                    startActivity(new Intent(MainActivity.this, SignInAfterOnBoardingActivity.class));
                    ActivityAnimation.startActivityAnimation(MainActivity.this);
                }
            }
        });

        ivDrawerLayoutUser = (CircleImageView) findViewById(R.id.ivDrawerLayoutUser);
        ivDrawerLayoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        tvDrawerLayoutAccount = (TextView) findViewById(R.id.tvDrawerLayoutAccount);
        tvDrawerLayoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ImageView ivDrawerlayoutSetting = (ImageView) findViewById(R.id.ivDrawerlayoutSetting);
        ivDrawerlayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Setting clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //BADGES
        FrameLayout flDeals = (FrameLayout) findViewById(R.id.flDeals);
        badgeDeals = (BadgeView) findViewById(R.id.badgeDeals);
        flDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getDealFavSize(), getResources().getString(R.string.my_page_deals));
            }
        });

        FrameLayout flEvents = (FrameLayout) findViewById(R.id.flEvents);
        badgeEvents = (BadgeView) findViewById(R.id.badgeEvents);
        flEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getEventAnnouncementFavSize(), getResources().getString(R.string.my_page_events));
            }
        });

        FrameLayout flStores = (FrameLayout) findViewById(R.id.flStores);
        badgeStores = (BadgeView) findViewById(R.id.badgeStores);
        flStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getStoreFavSize(), getResources().getString(R.string.my_page_stores));
            }
        });

        FrameLayout flInterests = (FrameLayout) findViewById(R.id.flInterests);
        badgeInterests = (BadgeView) findViewById(R.id.badgeInterests);
        flInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getInterestFavSize(), getResources().getString(R.string.my_page_interests));
            }
        });

        ivMoreMenu = (ThemeColorImageView) findViewById(R.id.ivMoreMenu);
        ivMoreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, ivMoreMenu);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_gc, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_remove) {
                            AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                            final GiftCardRecyclerViewAdapter giftCardRecyclerViewAdapter = new GiftCardRecyclerViewAdapter(MainActivity.this, GiftCardManager.getInstance(MainActivity.this).getGiftCards());
                            alertDialogForInterest.showGCAlertDialog(MainActivity.this, giftCardRecyclerViewAdapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int j) {
                                    boolean[] checkedStatus = giftCardRecyclerViewAdapter.getCheckedStatus();
                                    for(int i = 0; i < checkedStatus.length; i++) {
                                        if(checkedStatus[i]) {
                                            GiftCardManager.getInstance(MainActivity.this).removeCard(giftCardRecyclerViewAdapter.getGiftCard(i).getCardNumber());
                                            Toast.makeText(MainActivity.this, getString(R.string.warning_gift_card_has_been_removed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    mGiftCardRecyclerViewAdapter.updateData();
                                    mGiftCardRecyclerViewAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            return;
                                        }
                                    });
                        } else if (id == R.id.action_faq) {
                            KcpMallInfoRoot kcpMallInfoRoot = KcpMallInfoRoot.getInstance();
                            kcpMallInfoRoot.createOfflineKcpMallInfo(MainActivity.this, Constants.getStringFromResources(getApplicationContext(), R.string.mall_info_json));
                            List<InfoList> infoList = kcpMallInfoRoot.getKcpMallInfo().getInfoList();
                            if(infoList != null) {
                                for(int i = 0; i < infoList.size(); i++) {
                                    if( (infoList.get(i).getMenuTitle() != null && infoList.get(i).getMenuTitle().toLowerCase().contains("gift"))
                                            || (infoList.get(i).getTitle() != null && infoList.get(i).getTitle().toLowerCase().contains("gift")) ) {
                                        Intent intent = new Intent(MainActivity.this, MallInfoDetailActivity.class);
                                        intent.putExtra(Constants.ARG_CONTENT_PAGE, infoList.get(i));
                                        startActivityForResult(intent, Constants.REQUEST_CODE_LOCATE_GUEST_SERVICE);
                                    }
                                }
                            }
                        } else if (id == R.id.action_refresh) {
                            GiftCardManager.getInstance(MainActivity.this).updateBalance();
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });


        //GC
        View.OnClickListener footerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, GiftCardActivity.class), Constants.REQUEST_CODE_GIFT_CARD);
                ActivityAnimation.startActivityAnimation(MainActivity.this);
            }
        };

        rvGiftCard =  findViewById(R.id.rvGiftCard);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvGiftCard.setLayoutManager(linearLayoutManager);
        mGiftCardRecyclerViewAdapter = new GiftCardRecyclerViewAdapter(this, GiftCardManager.getInstance(this).getGiftCards(), footerClickListener);
        rvGiftCard.setAdapter(mGiftCardRecyclerViewAdapter);

        GiftCardManager.getInstance(this).setGiftCardUpdateListener(new GiftCardManager.GiftCardUpdateListener() {
            @Override
            public void onGiftCardUpdated() {
                mGiftCardRecyclerViewAdapter.updateData();
                showGiftCardsList();
            }

            @Override
            public void onGiftCardUpdateFailed(final String errorMessage) {
                showGiftCardsListWithError(errorMessage);
            }
        });

        //SIDE PANEL MANAGER
        SidePanelManagers sidePanelManagers = new SidePanelManagers(this, badgeDeals, badgeEvents, badgeStores, badgeInterests);

        //version
        TextView tvVersionNumber = (TextView) findViewById(R.id.tvVersionNumber);
        String versionCodeSuffix = BuildConfig.DEBUG ? "-" + getString(R.string.version_code_suffix) : "";
        String versionNameSuffix = " (" + BuildConfig.VERSION_CODE + versionCodeSuffix + ")";
        tvVersionNumber.setText(getString(R.string.version_name_prefix) + " " + BuildConfig.VERSION_NAME + versionNameSuffix);

        TextView tvTargetDeviceId = findViewById(R.id.tvTargetDeviceId);
        String deviceId = ETManager.getDeviceId();
        if (BuildConfig.DEBUG && !deviceId.isEmpty()) {
            tvTargetDeviceId.setText(getString(R.string.title_debug_target_device, deviceId));
            tvTargetDeviceId.setVisibility(View.VISIBLE);
        }
    }

    private void showGiftCardsList() {
        rvGiftCard.setVisibility(View.VISIBLE);
        setGiftCardUpdateIndicator(false);
        toggleGiftCardUpdateText(null, false);
    }

    private void showGiftCardsListWithError(@NonNull String errorMessage) {
        setGiftCardUpdateIndicator(false);
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(2000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                showGiftCardsList();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        tvGCUpdateMessage.setText(errorMessage);
        tvGCUpdateMessage.setAnimation(fadeOut);
        tvGCUpdateMessage.startAnimation(fadeOut);
    }

    private void hideGiftCardList() {
        rvGiftCard.setVisibility(View.GONE);
        setGiftCardUpdateIndicator(true);
        toggleGiftCardUpdateText(getString(R.string.drawer_gc_retrieving_balance), true);
    }

    /**
     * Toggles the visibility of Gift Card update text.
     * @param message The message to display.
     * @param shouldShowMessage true to show, false to hide.
     */
    private void toggleGiftCardUpdateText(String message, boolean shouldShowMessage) {
        if (message == null) {
            message = getString(R.string.drawer_gc_retrieving_balance);
        }
        tvGCUpdateMessage.setText(message);
        tvGCUpdateMessage.setVisibility(shouldShowMessage ? View.VISIBLE : View.GONE);
    }

    private void setGiftCardUpdateIndicator(boolean shouldShow) {
        pbGCUpdate.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }


    public void setActiveMallDot(boolean enable) {
        if(mActiveMall && !enable) {
            flActiveMallDot.setVisibility(View.GONE);
        } else if(mActiveMall && enable) {
            flActiveMallDot.setVisibility(View.VISIBLE);
            if(mMenuActiveMallDotAnim == null) { //to run it only once
                mMenuActiveMallDotAnim = new AlphaAnimation(0.1f, 1.0f);
                mMenuActiveMallDotAnim.setDuration(Constants.DURATION_MAIN_DRAWER_ACTIVE_MALL_DOT);
                mMenuActiveMallDotAnim.setRepeatMode(Animation.REVERSE);
                mMenuActiveMallDotAnim.setRepeatCount(5);
                flActiveMallDot.startAnimation(mMenuActiveMallDotAnim);
            }
        }
    }

    /**
     *
     * @param forceRefresh used when recreating the view's necessary ex. when data's downloaded
     * @param activeMallEnabled if activeMall status's not changed when calling setActiveMall, return because there is no point calling this function again.
     */
    public void setActiveMall(boolean forceRefresh, boolean activeMallEnabled){
        try {
            if(!forceRefresh && mActiveMall == activeMallEnabled) return;
            mActiveMall = activeMallEnabled;
            ScrollView scLeftPanel = (ScrollView) findViewById(R.id.scLeftPanel);
            LinearLayout llActiveMall = (LinearLayout) findViewById(R.id.llActiveMall);
            RecyclerView rvTodaysDeals = (RecyclerView) findViewById(R.id.rvTodaysDeals);
            RecyclerView rvTodaysEvents = (RecyclerView) findViewById(R.id.rvTodaysEvents);
            flActiveMallDot = (FrameLayout) findViewById(R.id.flActiveMallDot);

            TextView tvMyFav = (TextView) findViewById(R.id.tvMyFav);
            TextView tvMyGC = (TextView) findViewById(R.id.tvMyGC);

            TextView tvDeals = (TextView) findViewById(R.id.tvDeals);
            TextView tvEvents = (TextView) findViewById(R.id.tvEvents);
            TextView tvStores = (TextView) findViewById(R.id.tvStores);
            TextView tvInterests = (TextView) findViewById(R.id.tvInterests);

            TextView tvEmptyTodaysDeal = (TextView) findViewById(R.id.tvEmptyTodaysDeal);
            TextView tvEmptyTodaysEvent = (TextView) findViewById(R.id.tvEmptyTodaysEvent);

            BadgeView badgeTodaysDeals = (BadgeView) findViewById(R.id.badgeTodaysDeals);
            BadgeView badgeTodaysEvents = (BadgeView) findViewById(R.id.badgeTodaysEvents);

            FrameLayout flTodaysDeals = (FrameLayout) findViewById(R.id.flTodaysDeals);
            FrameLayout flTodaysEvents = (FrameLayout) findViewById(R.id.flTodaysEvents);


            TextView tvPrivacyPolicy = (TextView) findViewById(R.id.tvPrivacyPolicy);
            TextView tvTermsOfService = (TextView) findViewById(R.id.tvTermsOfService);
            TextView tvDot = (TextView) findViewById(R.id.tvDot);
            TextView tvVersionNumber = (TextView) findViewById(R.id.tvVersionNumber);
            TextView tvTargetDeviceId = findViewById(R.id.tvTargetDeviceId);

            tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.openWebPage(MainActivity.this, getString(R.string.url_privacy_policy));
                }
            });

            tvTermsOfService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.openWebPage(MainActivity.this, getString(R.string.url_terms));
                }
            });

            int hamburgerMenuColor;
            int panelBackgroundColor;
            int generalTextColor;
            int badgeTextColor;
            int privacyTextColor;
            int versionNumberTextColor;
            Drawable drawerLayoutBgDrawable;
            updateProfileAvatarForActiveMall(activeMallEnabled);

            if(activeMallEnabled) {

                Log.v("Geofence", "Active Mall Set TRUE");

                long minStartTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(WELCOME_MSG_RESET_TIMER_IN_HOUR);
                long lastTimeRan = KcpUtility.loadLongFromCache(this, Constants.PREF_KEY_WELCOME_MSG_TIME_SAVER, -1);

                boolean didWelcomeMessageAlreadyAppear = KcpUtility.loadFromSharedPreferences(this, Constants.PREF_KEY_WELCOME_MSG_DID_APPEAR, false);
                if(   (Constants.IS_APP_IN_PRODUCTION && (!ParkingManager.isParkingLotSaved(this) && lastTimeRan <= minStartTime)) ||
                        (!Constants.IS_APP_IN_PRODUCTION && !ParkingManager.isParkingLotSaved(this)) ) {
                    if(!didWelcomeMessageAlreadyAppear) {
                        KcpUtility.cacheToPreferences(this, Constants.PREF_KEY_WELCOME_MSG_TIME_SAVER, System.currentTimeMillis());
                        KcpUtility.cacheToPreferences(this, Constants.PREF_KEY_WELCOME_MSG_DID_APPEAR, true);
                    }
                }

                llActiveMall.setVisibility(View.VISIBLE);
                setActiveMallDot(true);
                panelBackgroundColor = getResources().getColor(R.color.active_mall_bg);
                hamburgerMenuColor = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.themeColor)));
                badgeTextColor = getResources().getColor(R.color.active_mall_badge_text_color);
                generalTextColor = getResources().getColor(R.color.active_mall_text_color);
                if (jrRecordManager == null) {
                    drawerLayoutBgDrawable = ContextCompat.getDrawable(this, R.drawable.img_profile_activemall_signin);
                } else {
                    drawerLayoutBgDrawable = ContextCompat.getDrawable(this, jrRecordManager.isUserSignedIn() ? R.drawable.img_profile_activemall_signout : R.drawable.img_profile_activemall_signin);
                }
                privacyTextColor = getResources().getColor(R.color.white);
                versionNumberTextColor = getResources().getColor(R.color.white);

                LinearLayoutManager llManagerEvents = new LinearLayoutManager(this);
                LinearLayoutManager llManagerDeals = new LinearLayoutManager(this);


                ArrayList<KcpContentPage> todaysEventList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_FEED).getKcpContentPageListForToday(true);
                if(todaysEventList == null || todaysEventList.size() == 0) tvEmptyTodaysEvent.setVisibility(View.VISIBLE);
                else {
                    tvEmptyTodaysEvent.setVisibility(View.GONE);
                    KcpUtility.sortKcpContentpageByExpiryDate(todaysEventList); //sort the event list by store name
                }
                ActiveMallRecyclerViewAdapter todaysEventAdapter = new ActiveMallRecyclerViewAdapter (
                        this,
                        todaysEventList,
                        KcpContentTypeFactory.ITEM_TYPE_EVENT);
                rvTodaysEvents.setAdapter(todaysEventAdapter);
                rvTodaysEvents.setLayoutManager(llManagerEvents);
                rvTodaysEvents.setNestedScrollingEnabled(false);
                badgeTodaysEvents.setBadgeText(todaysEventList == null ? 0 : todaysEventList.size());

                flTodaysEvents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getEventAnnouncementFavSize(), getResources().getString(R.string.my_page_events_for_today));
                    }
                });


                ArrayList<KcpContentPage> todaysDealList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageListForToday(true);
                if(todaysDealList == null || todaysDealList.size() == 0) tvEmptyTodaysDeal.setVisibility(View.VISIBLE);
                else {
                    tvEmptyTodaysDeal.setVisibility(View.GONE);
                    KcpUtility.sortKcpContentpageByExpiryDate(todaysDealList);
                }
                ActiveMallRecyclerViewAdapter todaysDealAdapter = new ActiveMallRecyclerViewAdapter (
                        this,
                        todaysDealList,
                        KcpContentTypeFactory.ITEM_TYPE_DEAL);
                rvTodaysDeals.setAdapter(todaysDealAdapter);
                rvTodaysDeals.setLayoutManager(llManagerDeals);
                rvTodaysDeals.setNestedScrollingEnabled(false);
                badgeTodaysDeals.setBadgeText(todaysDealList == null ? 0 : todaysDealList.size());

                flTodaysDeals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getDealFavSize(), getResources().getString(R.string.my_page_deals_for_today));
                    }
                });

                showSnackBar(R.string.warning_active_mall_activated, R.string.action_ok, getResources().getColor(R.color.white), null);

                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

            } else {
                Log.v("Geofence", "Active Mall Set FALSE");

                llActiveMall.setVisibility(View.GONE);
                setActiveMallDot(false);
                panelBackgroundColor = Color.WHITE;
                hamburgerMenuColor = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.active_mall_off_state)));
                badgeTextColor = Color.WHITE;
                generalTextColor = Color.BLACK;
                drawerLayoutBgDrawable = ContextCompat.getDrawable(this, R.drawable.img_profile_bg_inactive);
                privacyTextColor = getResources().getColor(R.color.privacy_policy_text_color);
                versionNumberTextColor = getResources().getColor(R.color.insta_desc_color);
            }

            scLeftPanel.setBackgroundColor(panelBackgroundColor);
            setHamburgerMenuColor(hamburgerMenuColor);

            badgeDeals.setBadgeTextColor(badgeTextColor);
            badgeEvents.setBadgeTextColor(badgeTextColor);
            badgeStores.setBadgeTextColor(badgeTextColor);
            badgeInterests.setBadgeTextColor(badgeTextColor);
            mGiftCardRecyclerViewAdapter.setBadgeColor(generalTextColor, badgeTextColor);


            ivDrawerLayoutBg.setImageDrawable(drawerLayoutBgDrawable);
            tvMyFav.setTextColor(generalTextColor);
            tvMyGC.setTextColor(generalTextColor);
            tvDeals.setTextColor(generalTextColor);
            tvEvents.setTextColor(generalTextColor);
            tvStores.setTextColor(generalTextColor);
            tvInterests.setTextColor(generalTextColor);
            tvPrivacyPolicy.setTextColor(privacyTextColor);
            tvTermsOfService.setTextColor(privacyTextColor);
            tvDot.setTextColor(privacyTextColor);
            tvVersionNumber.setTextColor(versionNumberTextColor);
            tvTargetDeviceId.setTextColor(versionNumberTextColor);
            ivMoreMenu.setColor(generalTextColor, generalTextColor);
        } catch (Resources.NotFoundException e) {
            logger.error(e);
        } catch (Exception e){
            logger.error(e);
        }

    }

    private void setHamburgerMenuColor(int color){
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            final View v = mToolbar.getChildAt(i);

            if (v instanceof ImageButton) {
                ((ImageButton) v).setColorFilter(colorFilter);
            }
        }
    }

    private void setDealParkingStatus(boolean isOn, RelativeLayout rl, TextView tv, ImageView iv, String onText, String offText){
        if(isOn){
            rl.setBackgroundColor(getResources().getColor(R.color.white));
            tv.setText(onText);
        } else {
            rl.setBackgroundColor(getResources().getColor(R.color.intrstd_card_bg_with_opacity));
            tv.setText(offText);
        }

        iv.setSelected(!isOn);
    }

    private void setParkingStatus(boolean isOn, RelativeLayout rl, TextView tv, ImageView iv, String onText, String offText){
        if(ParkingManager.isParkingLotSaved(this)) {
            setDealParkingStatus(isOn, rl, tv, iv, onText, offText);
        } else {
            rl.setBackgroundColor(getResources().getColor(R.color.intrstd_card_bg_with_opacity));
            tv.setText(getResources().getString(R.string.map_filter_save_parking));
            iv.setSelected(true);
        }
    }

    public void setUpRightSidePanel() {
        final RelativeLayout rlSeeDeal = (RelativeLayout) findViewById(R.id.rlSeeDeal);
        final ImageView ivFilterDeal = (ImageView) findViewById(R.id.ivFilterDeal);
        final TextView tvFilterDeal = (TextView) findViewById(R.id.tvFilterDeal);

        setDealParkingStatus(Amenities.isToggled(this, Amenities.GSON_KEY_DEAL, false), rlSeeDeal, tvFilterDeal, ivFilterDeal, getResources().getString(R.string.map_filter_hide_deal), getResources().getString(R.string.map_filter_see_deal));
        rlSeeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivFilterDeal.isSelected()) {
                    Analytics.getInstance(MainActivity.this).logEvent("MAP_Recommendeddeals_Click", "MAP", "Click See Recommended Deals");
                }
                Utility.startSqueezeAnimationForInterestedCat(new Utility.SqueezeListener() {
                    @Override
                    public void OnSqueezeAnimationDone() {
                    }
                }, MainActivity.this, rlSeeDeal);
                setDealParkingStatus(ivFilterDeal.isSelected(), rlSeeDeal, tvFilterDeal, ivFilterDeal, getResources().getString(R.string.map_filter_hide_deal), getResources().getString(R.string.map_filter_see_deal));
                if(mOnDealsClickListener != null) {
                    ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true); //RECOMMENDED DEALS - use EXTERNAL_CODE_DEAL for All Deal
                    if(dealContentPages == null || dealContentPages.size() == 0) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.warning_no_recommended_deals), Toast.LENGTH_LONG).show();
                        setDealParkingStatus(false, rlSeeDeal, tvFilterDeal, ivFilterDeal, getResources().getString(R.string.map_filter_hide_deal), getResources().getString(R.string.map_filter_see_deal));
                        return;
                    } else {
                        mOnDealsClickListener.onDealsClick(!ivFilterDeal.isSelected());
                    }
                    Amenities.saveToggle(MainActivity.this, Amenities.GSON_KEY_DEAL, !ivFilterDeal.isSelected());
                }
            }
        });

        final RelativeLayout rlSeeParking = (RelativeLayout) findViewById(R.id.rlSeeParking);
        final ImageView ivFilterParking= (ImageView) findViewById(R.id.ivFilterParking);
        final TextView tvFilterParking = (TextView) findViewById(R.id.tvFilterParking);

        setParkingStatus(Amenities.isToggled(this, Amenities.GSON_KEY_PARKING, false), rlSeeParking, tvFilterParking, ivFilterParking, getResources().getString(R.string.map_filter_hide_parking), getResources().getString(R.string.map_filter_see_parking));
        rlSeeParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ParkingManager.isParkingLotSaved(MainActivity.this))
                    Analytics.getInstance(MainActivity.this).logEvent("MAP_Savemyparkinglot_Click", "MAP", "Click Save My Parking Lot");
                Utility.startSqueezeAnimationForInterestedCat(new Utility.SqueezeListener() {
                    @Override
                    public void OnSqueezeAnimationDone() {
                    }
                }, MainActivity.this, rlSeeParking);
                if(ParkingManager.isParkingLotSaved(MainActivity.this)) Amenities.saveToggle(MainActivity.this, Amenities.GSON_KEY_PARKING, ivFilterParking.isSelected());
                setParkingStatus(Amenities.isToggled(MainActivity.this, Amenities.GSON_KEY_PARKING, false), rlSeeParking, tvFilterParking, ivFilterParking, getResources().getString(R.string.map_filter_hide_parking), getResources().getString(R.string.map_filter_see_parking));
                if(mOnParkingClickListener != null) {
                    mOnParkingClickListener.onParkingClick(!ivFilterParking.isSelected(), true);
                }
            }
        });

        View llAmenitySwitch = findViewById(R.id.llAmenitySwitch);
        List<Amenities.AmenityLayout> amenityList = new ArrayList<>();

        if(AmenitiesManager.sAmenities == null) return;
        for(int i = 0; i < AmenitiesManager.sAmenities.getAmenityList().size(); i++){
            final Amenities.Amenity amenity = AmenitiesManager.sAmenities.getAmenityList().get(i);
//            if(amenity.isEnabled()) {
                if(amenity.getExternalIds() != null && amenity.getExternalIds().length > 0){
                    final String externalID = amenity.getExternalIds()[0];
                    amenityList.add(new Amenities.AmenityLayout(
                                    MainActivity.this,
                                    (ViewGroup) llAmenitySwitch,
                                    R.layout.layout_amenities,
                                    amenity.getTitle(),
                                    Amenities.isToggled(MainActivity.this, Amenities.GSON_KEY_AMENITY + externalID, amenity.isEnabled()),
                                    new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            Amenities.saveToggle(MainActivity.this, Amenities.GSON_KEY_AMENITY + externalID, isChecked);
                                            if(amenity.getExternalIds() == null || amenity.getExternalIds().length == 0) return;
                                            for(String externalId : amenity.getExternalIds()) {
                                                if(mOnAmenityClickListener != null) mOnAmenityClickListener.onAmenityClick(isChecked, externalId, false, null);
                                            }
                                        }
                                    }
                            )
                    );

                }
//            }
        }

        ((ViewGroup)llAmenitySwitch).removeAllViews();
        for(int i = 0; i < amenityList.size(); i++){
            ((ViewGroup) llAmenitySwitch).addView(amenityList.get(i).getView());
        }
    }

    public static Amenities.OnAmenityClickListener mOnAmenityClickListener;
    public void setOnAmenityClickListener(Amenities.OnAmenityClickListener amenityClickListener) {
        mOnAmenityClickListener = amenityClickListener;
    }

    public static Amenities.OnDealsClickListener mOnDealsClickListener;
    public void setOnDealsClickListener(Amenities.OnDealsClickListener onDealsClickListener) {
        mOnDealsClickListener = onDealsClickListener;
    }

    public static Amenities.OnParkingClickListener mOnParkingClickListener;
    public void setOnParkingClickListener(Amenities.OnParkingClickListener onParkingClickListener) {
        mOnParkingClickListener = onParkingClickListener;
    }


    public void startMyPageActivity(int listSize, final String myPageType){
        mDrawer.closeDrawers();
        if(myPageType.equals(getResources().getString(R.string.my_page_interests))){
            startActivityForResult(new Intent(MainActivity.this, InterestedCategoryActivity.class), Constants.REQUEST_CODE_CHANGE_INTEREST);
            ActivityAnimation.startActivityAnimation(MainActivity.this);
        } else {
            Intent intent = new Intent(MainActivity.this, MyPagesActivity.class);
            intent.putExtra(Constants.ARG_CAT_NAME, myPageType);
            MainActivity.this.startActivityForResult(intent, Constants.REQUEST_CODE_MY_PAGE_TYPE);
            ActivityAnimation.startActivityAnimation(MainActivity.this);
        }
    }


    public void showSnackBar(int msg, int action, int textColor, @Nullable View.OnClickListener onClickListener) {
        if(mOfflineSnackbar == null) logger.debug("mOfflineSnackbar = null");
        else logger.debug("mOfflineSnackbar is NOT null");
        if(mOfflineSnackbar != null && mOfflineSnackbar.isShownOrQueued()) logger.debug("mOfflineSnackbar.isShownOrQueued()");


        if (mOfflineSnackbar != null && mOfflineSnackbar.isShownOrQueued()) {
            logger.debug("returned from showSnacBar");
            return;
        }
        final CoordinatorLayout clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        if (onClickListener == null) {
            mOfflineSnackbar = Snackbar
                    .make(clMain, getResources().getString(msg), Snackbar.LENGTH_LONG);
        } else {
            mOfflineSnackbar = Snackbar
                    .make(clMain, getResources().getString(msg), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(action), onClickListener);
        }

        mOfflineSnackbar.setActionTextColor(getResources().getColor(R.color.themeColor));
        View snackbarView = mOfflineSnackbar.getView();

        CoordinatorLayout.LayoutParams param = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
        snackbarView.setLayoutParams(param);
        snackbarView.setBackgroundColor(Color.DKGRAY);

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        if(textColor == 0) textView.setTextColor(getResources().getColor(R.color.white));
        else textView.setTextColor(textColor);

        TextView actionText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(getResources().getColor(R.color.warningColor));

        logger.debug("showing showSnacBar, msg: " + getResources().getString(msg));
        mOfflineSnackbar.show();

    }
    /**
     * If there's snackbar showing, it simply returns unless onClickListener is set.
     *
     * @param msg
     * @param action
     * @param onClickListener
     */
    public void showSnackBar(int msg, int action, @Nullable View.OnClickListener onClickListener) {
        showSnackBar(msg, action, 0, onClickListener);
    }

    private void animateHamburgerToArrow() {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mToggle.onDrawerSlide(mDrawer, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(500);
        anim.start();
    }

    /**
     * prepares tab contents with fragments, tab icons and texts
     */
    private void prepareTabContents(List<Fragment> fragmentList, List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        String[] menuTitle = getResources().getStringArray(R.array.menuTitles);
        TypedArray menuIcon = getResources().obtainTypedArray(R.array.menuIcons);
        for (int i = 0; i < menuTitle.length; i++) {
            fragmentTitleList.add(menuTitle[i]);
            fragmentIconList.add(menuIcon.getResourceId(i, -1));
        }

        fragmentList.add(HomeFragment.getInstance());
        fragmentList.add(DirectoryFragment.getInstance());
        fragmentList.add(MapFragment.getInstance());
        fragmentList.add(InfoFragment.getInstance());

    }

    /**
     * set custom layout to each tab
     */
    private void setupTabIcons(TabLayout tabLayout, List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        for (int i = 0; i < fragmentTitleList.size(); i++) {
            View singleTablayout = LayoutInflater.from(this).inflate(R.layout.tab_img_layout, null);
            mViewPager.changeTabStateWhenSelected(singleTablayout, false);
            TextView tabTitle = (TextView) singleTablayout.findViewById(R.id.tvTabTitle);
            ImageView tabImage = (ImageView) singleTablayout.findViewById(R.id.ivTabIcon);
            tabTitle.setText(fragmentTitleList.get(i));
            tabImage.setImageResource(fragmentIconList.get(i));

            tabLayout.getTabAt(i).setCustomView(singleTablayout);
        }
    }

    /**
     * set up view pager
     */
    private void setupViewPager(List<Fragment> fragmentList, List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        HomeBottomTapAdapter adapter = new HomeBottomTapAdapter(this, fragmentList, fragmentTitleList, fragmentIconList);
        mViewPager.setAdapter(adapter);
    }

    public void setDrawerIndicatorEnabled(boolean enabled, String toolbarTitle){
        ivToolbar.setVisibility(View.GONE);
        mToggle.setDrawerIndicatorEnabled(enabled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!enabled);
        getSupportActionBar().setDisplayShowTitleEnabled(!enabled);
        getSupportActionBar().setTitle(toolbarTitle);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if(mDrawer.isDrawerOpen(GravityCompat.END)){
            mDrawer.closeDrawer(GravityCompat.END);
        } else {
            //hide direction card
            if(mCurrentViewPagerTapPosition == VIEWPAGER_PAGE_MAP) {
                if(MapFragment.getInstance().isDirectionCardVisible() || isDestinationEditorVisible()){
                    MapFragment.getInstance().didTapBack();
                    return;
                }
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.executePendingTransactions();
            if (fragmentManager.getBackStackEntryCount() < 1) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStackImmediate();
                fragmentManager.executePendingTransactions();
                if (fragmentManager.getBackStackEntryCount() < 1) {
                    initializeToolbar(); //BUG: toolbar initialization has to be done - otherwise, the arrow button won't show up the secondtime fragment's created
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(BuildConfig.DEBUG) {
            getMenuInflater().inflate(R.menu.menu_main_debug, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_backend_vm:
                HeaderFactory.changeCatalog(Constants.HEADER_VALUE_DATAHUB_CATALOG_VM);
                initializeKcpData(null);
                break;
            case R.id.action_backend_mp:
                HeaderFactory.changeCatalog(Constants.HEADER_VALUE_DATAHUB_CATALOG_MP);
                initializeKcpData(null);
                break;
            case R.id.home:
                break;
            case R.id.action_test:
                GiftCardManager.getInstance(this).fakeReduce();
                // setActiveMall(true, !mActiveMall); //enable to toggle geofence for testing
                break;
            case R.id.action_geofence_test:
                mGeofenceManager.setGeofence(true);
                break;
            case R.id.action_geofence_disconnect:
                mGeofenceManager.setGeofence(false);
                setActiveMall(false, false);
                flActiveMallDot.setVisibility(View.GONE);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                ActivityAnimation.startActivityAnimation(MainActivity.this);
                break;
            case R.id.action_focusall:
                MapFragment.getInstance().focusAll();
                break;
            case R.id.action_firebase:
                isFirebaseAnalyticsOn = !isFirebaseAnalyticsOn;
                Toast.makeText(this, "Firebase Analytics is " + (isFirebaseAnalyticsOn ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
                FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(isFirebaseAnalyticsOn);
                item.setTitle("Toggle Analytics (" + (isFirebaseAnalyticsOn ? "OFF" : "ON") + ")");
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setEmptyState(TextView tvEmptyState, @Nullable String warningMsg) {
        if (warningMsg != null) {
            tvEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText(warningMsg);
        } else tvEmptyState.setVisibility(View.GONE);
    }

    public void selectPage(int pageIndex){
        mDrawer.closeDrawers();
        mViewPager.setCurrentItem(pageIndex);
    }

    @Override
    public void onDataDownloaded() {
        synchronized (mSplashThread) {
            mSplashThread.notifyAll();
        }
        if (KcpUtility.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ETManager.requestNotificationPermission(MainActivity.this, MainActivity.this);
                }
            }, 1500);

        }
    }

    public static RefreshListener mOnRefreshListener;
    public void setOnRefreshListener(RefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    @Override
    public void onReadyForPush(ETPush etPush) {
        this.etPush = etPush;
    }

    @Override
    public void onNotificationRequestRequired() {
        AlertDialog notificationRequest = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.notif_dialog_title, getString(R.string.app_name)))
                .setMessage(getString(R.string.notif_dialog_message))
                .setPositiveButton(getString(R.string.notif_dialog_action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ETManager.toggleAllNotifications(true);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.notif_dialog_action_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ETManager.toggleAllNotifications(false);
                        dialog.dismiss();
                    }
                })
                .create();
        notificationRequest.show();
    }


    public interface RefreshListener {
        void onRefresh(int msg);
    }


    public ReadyToLoadMapListener mReadyToLoadMapListener;
    public void setOnReadyToLoadMapListener(ReadyToLoadMapListener readyToLoadMapListener){
        mReadyToLoadMapListener = readyToLoadMapListener;
    }
    public interface ReadyToLoadMapListener {
        void onReady();
    }

    private void handleActivityResult(final int requestCode, final int resultCode, final Intent data){
        selectPage(2);
        if(!MapFragment.getInstance().mMapLoaded) {
            MapFragment.getInstance().setMapInterface(new MapInterface() {
                @Override
                public void mapLoaded() {
                    handleActivityResult(requestCode, resultCode, data);
                }
            });
            return;
        }

        rvMallDirectory.setVisibility(View.INVISIBLE);
        int code = data.getIntExtra(Constants.REQUEST_CODE_KEY, 0);
        String externalCode = String.valueOf(resultCode);
        ArrayList<Polygon> polygons = MapFragment.getPolygonsFromLocationWithExternalCode(externalCode);
        if(polygons == null || polygons.size() == 0) return;
        Polygon storePolygon = polygons.get(0);
        if(code == Constants.REQUEST_CODE_SHOW_PARKING_SPOT){
            String parkingName = data.getStringExtra(Constants.REQUEST_CODE_KEY_PARKING_NAME);
            if(parkingName != null) {
                int parkingPosition = ParkingManager.sParkings.getParkingPositionByName(parkingName);
                if(BuildConfig.PARKING_POLYGON) {
                    Polygon nearestParkingPolygon = MapFragment.getNearestParkingPolygonFromStorePolygon(storePolygon);
                    MapFragment.getInstance().showParkingSpotFromDetailActivity(parkingPosition, nearestParkingPolygon);
                } else {
                    if(parkingPosition != -1) MapFragment.getInstance().showParkingSpotFromDetailActivity(parkingPosition, storePolygon);
                }
            }
        } else if(code == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
            if (polygons != null && polygons.size() > 0) {
                MapFragment.getInstance().showStoreOnTheMapFromDetailActivity(storePolygon);
            } else {
                MapFragment.getInstance().mPendingExternalCode = externalCode;
            }
        }
    }

    private void logLocationAccessEvent(String type) {
        Analytics.getInstance(this).logEvent(type + "_Location_Access", "Onboarding", type + " Location Access");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == Constants.REQUEST_CODE_CHANGE_INTEREST) {
                if (resultCode == Activity.RESULT_OK) {
                    HomeFragment.getInstance().downloadNewsAndDeal();
                } else {

                }
            } else if (requestCode == Constants.REQUEST_CODE_MY_PAGE_TYPE) {
                if(data == null) {
                } else {
                    int code = data.getIntExtra(Constants.REQUEST_CODE_KEY, VIEWPAGER_PAGE_HOME);
                    if(code == Constants.REQUEST_CODE_SHOW_PARKING_SPOT || code == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP){
                        handleActivityResult(requestCode, resultCode, data);
                    } else {
                        if (resultCode == Constants.RESULT_DEALS) {
                            selectPage(VIEWPAGER_PAGE_HOME);
                            HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_DEALS);
                        } else if (resultCode == Constants.RESULT_EVENTS) {
                            selectPage(VIEWPAGER_PAGE_HOME);
                            HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_NEWS);
                        } else if (resultCode == Constants.RESULT_STORES) {
                            selectPage(VIEWPAGER_PAGE_DIRECTORY);
                            DirectoryFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_DEALS);
                        }
                    }
                }
            } else if (requestCode == Constants.REQUEST_CODE_LOCATE_GUEST_SERVICE) {
                if (resultCode == Activity.RESULT_OK) {
                    //turn on the guest service switch and drop a pin
                    selectPage(MainActivity.VIEWPAGER_PAGE_MAP);
                    String locationID = data.getStringExtra(Constants.ARG_LOCATION_ID);
                    String mapName = data.getStringExtra(Constants.ARG_LOCATION_MAP_NAME);
                    if(mapName != null) MapFragment.getInstance().setMapLevel(-50, null, mapName);
                    Amenity amenityLocation = MapFragment.getAmenityWithLocationId(locationID);
                    String externalID = amenityLocation.amenityType;

                    final Amenities.Amenity amenity = AmenitiesManager.sAmenities.getAmenityWithExternalId(externalID);
                    boolean isToggled = Amenities.isToggled(MainActivity.this, Amenities.GSON_KEY_AMENITY + externalID, amenity == null ? false : amenity.isEnabled());
                    if(!isToggled) {
                        Amenities.saveToggle(MainActivity.this, Amenities.GSON_KEY_AMENITY + externalID, true);
                        setUpRightSidePanel();
                        if(mOnAmenityClickListener != null) mOnAmenityClickListener.onAmenityClick(true, externalID, true, mapName);
                    } else {
                        MapFragment.getInstance().clickOverlayWithNameAndPosition(externalID, mapName);
                    }
                }
            } else if(requestCode == Constants.REQUEST_CODE_SAVE_PARKING_SPOT) {
                mDrawer.closeDrawers();
                if (resultCode == PARKING_RESULT_CODE_SPOT_SAVED) {
                    setUpRightSidePanel();
                    if(mOnParkingClickListener != null && Amenities.isToggled(this, Amenities.GSON_KEY_PARKING, false)) mOnParkingClickListener.onParkingClick(true, true);
                    InfoFragment.getInstance().setParkingSpotCTA();
                } else if (resultCode == PARKING_RESULT_CODE_SPOT_PENDING){
                    MapFragment.getInstance().showParkingSpotAtBlueDot();
                }
            } else if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
                //1. show store on the map 2. show nearest parking spot from a store
                //data == null : backpressed
                //data != null  && !externalCode.equals("0") : locate store
                //data != null && data.getIntExtra(Constants.REQUEST_CODE_KEY, 0) == Constants.REQUEST_CODE_SHOW_PARKING_SPOT : parking spot
                if(data == null) {
                    //backpressed
                } else {
                    handleActivityResult(requestCode, resultCode, data);
                }
            } else if (requestCode == Constants.REQUEST_CODE_TUTORIAL){
                KcpUtility.saveToSharedPreferences(this, Constants.PREF_KEY_ONBOARDING_DID_APPEAR, true);
                mGeofenceManager = new GeofenceManager(this);
            } else if(requestCode == Constants.REQUEST_CODE_GIFT_CARD) {
                if (resultCode == Activity.RESULT_OK) {
                    mGiftCardRecyclerViewAdapter.updateData();
                }

            }
        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void showRatingDialog() {
        if (ratingDialog != null) {
            ratingDialog.show();
        }
    }

    private void launchPlayStore() {
        Intent playIntent = new Intent(Intent.ACTION_VIEW);
        playIntent.setData(Uri.parse(getString(R.string.ar_play_store_url, BuildConfig.APPLICATION_ID)));
        startActivity(playIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGeofenceManager != null && mGeofenceManager.getGoogleApiClient() != null) {
            mGeofenceManager.getGoogleApiClient().connect();
        }
        AppRatingManager.getInstance().getUsage(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGeofenceManager != null && mGeofenceManager.getGoogleApiClient() != null) {
            mGeofenceManager.getGoogleApiClient().disconnect();
        }
    }

    @Override
    public void onDestroy() {
        if(mGeofenceManager != null) {
            mGeofenceManager.unRegisterBroadcastReceiver();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserDetails();
    }

    private void loadUserDetails() {
        if (Jump.getSignedInUser() != null) {
            jrRecordManager = new JanrainRecordManager(this);
            if (jrRecordManager.isUserSignedIn()) {
                toggleUserSignIn(true);
            }
        }
    }

    /**
     * Toggles the sign in button text and user display name.
     * @param isSignedIn boolean to toggle visibility and text.
     */
    private void toggleUserSignIn(boolean isSignedIn) {
        if (mActiveMall) {
            ivDrawerLayoutBg.setImageDrawable(ContextCompat.getDrawable(this, isSignedIn ?
                    R.drawable.img_profile_activemall_signout : R.drawable.img_profile_activemall_signin));
        } else {
            ivDrawerLayoutBg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_profile_bg_inactive));
        }
        tvSignInOrOut.setBackground(ContextCompat.getDrawable(this, isSignedIn ?
                R.drawable.btn_selector_signout : R.drawable.btn_style_corner_radius_with_selected_state));
        tvSignInOrOut.setTextColor(colour(isSignedIn ?
                R.color.black : R.color.profile_signup_text));
        tvSignInOrOut.setText(getString(isSignedIn ? R.string.drawer_sign_out : R.string.drawer_sign_in));
        llDisplayNameSettings.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);

        resizeBackgroundImage(isSignedIn);
        updateUserViews(isSignedIn);

        KcpUtility.saveToSharedPreferences(this, JanrainRecordManager.KEY_USER_SIGNED_IN, isSignedIn);
        if (isSignedIn) {
            KcpUtility.cacheToPreferences(this, JanrainRecordManager.KEY_USER_ID, jrRecordManager.getUserId());
        } else {
            AccountManager.signOutAndReset(this);
            mGiftCardRecyclerViewAdapter.updateData();
        }

    }

    private int colour(int colourId) {
        return ContextCompat.getColor(this, colourId);
    }

    private void resizeBackgroundImage(boolean isSignedIn) {
        RelativeLayout.LayoutParams lpDrawerLayoutBg = (RelativeLayout.LayoutParams) ivDrawerLayoutBg.getLayoutParams();
        lpDrawerLayoutBg.height = (int) getResources().getDimension((isSignedIn) ? R.dimen.profile_sign_out_bg_height : R.dimen.profile_sign_in_bg_height);
        ivDrawerLayoutBg.setLayoutParams(lpDrawerLayoutBg);
    }

    private void updateUserViews(boolean isSignedIn) {
        if (isSignedIn) {
            tvDrawerLayoutAccount.setText(jrRecordManager.getFullName());
            jrRecordManager.loadProfileImage(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    ivDrawerLayoutUser.setImageBitmap(resource);
                }
            });
        } else {
            //not that it matters as the TextView gets hidden anyways.
            tvDrawerLayoutAccount.setText("");
            ivDrawerLayoutUser.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_profile_default));
        }
        updateProfileAvatarForActiveMall(mActiveMall);
    }

    /**
     * Updates the colors and filter modes for the user avatar image depending on active mall state.
     * @param isActiveMall Active Mall status.
     */
    private void updateProfileAvatarForActiveMall(boolean isActiveMall) {
        if (isActiveMall) {
            tvDrawerLayoutAccount.setTextColor(colour(R.color.profile_name_activemall));
            ivDrawerLayoutUser.setBorderFilterColor(R.color.profile_activemall_border_color);
            ivDrawerLayoutUser.setSrcFilterMode(PorterDuff.Mode.SRC_IN);
        } else {
            tvDrawerLayoutAccount.setTextColor(colour(R.color.profile_name_inactive));
            //only reset the porter duff mode if the user doesn't exist or has no image or not signed in.
            ivDrawerLayoutUser.resetToDefaultColors((jrRecordManager == null || !jrRecordManager.userHasProfileImage() || !jrRecordManager.isUserSignedIn()));
        }
    }

    @Override
    public void onCreateRatingDialog(final DialogActionListener dialogActionListener) {
        if (ratingDialog == null) {
            ratingDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.ar_dialog_title, getString(R.string.app_name)))
                    .setMessage(getString(R.string.ar_dialog_message))
                    .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogActionListener.onDialogOK();
                            dialog.dismiss();
                            launchPlayStore();
                        }
                    })
                    .setNegativeButton(getString(R.string.action_later), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialogActionListener.onDialogDeferred();
                        }
                    })
                    .create();
        }
    }

    @Override
    public void onDestroyRatingDialog() {
        ratingDialog = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case GeofenceManager.LOCATION_REQUEST:
                if (mGeofenceManager.canAccessLocation()) {
                    mGeofenceManager.setGeofence(true);
                    logLocationAccessEvent("Allow");
                    ETManager.requestNotificationPermission(this, this);
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        for (int i = 0, len = permissions.length; i < len; i++) {
                            String permission = permissions[i];
                            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                                boolean showRationale = shouldShowRequestPermissionRationale( permission );
                                if (! showRationale) { // user denied flagging NEVER ASK AGAIN

                                    //Without this check, analytics would be sent even if the user was not prompted for location access
                                    if (mDidPressLocationAccessRetry) {
                                        logLocationAccessEvent("Disallow");
                                    }
                                } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                                    View v = getLayoutInflater().inflate(R.layout.alertdialog_interest, null);
                                    TextView tvAlertDialogInterest = (TextView) v.findViewById(R.id.tvAlertDialogInterest);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                    builder.setTitle(getString(R.string.title_permission_denied));
                                    if(BuildConfig.BLUEDOT) tvAlertDialogInterest.setText(getString(R.string.warning_require_location_permission_for_blue_dot));
                                    else tvAlertDialogInterest.setText(getString(R.string.warning_require_location_permission));
                                    builder.setPositiveButton(getString(R.string.action_sure), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            logLocationAccessEvent("Disallow");
                                            return;
                                        }
                                    });
                                    builder.setNegativeButton(getString(R.string.action_retry), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(MainActivity.this, GeofenceManager.INITIAL_PERMS, GeofenceManager.LOCATION_REQUEST);
                                            mDidPressLocationAccessRetry = true;
                                            return;
                                        }
                                    });
                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            ETManager.requestNotificationPermission(MainActivity.this, MainActivity.this);
                                        }
                                    });
                                    builder.setView(v);
                                    builder.show();
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

}

