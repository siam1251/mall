package com.kineticcafe.kcpmall.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.managers.KcpDataListener;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.HomeBottomTapAdapter;
import com.kineticcafe.kcpmall.analytics.FirebaseTracking;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.fragments.DirectoryFragment;
import com.kineticcafe.kcpmall.fragments.HomeFragment;
import com.kineticcafe.kcpmall.fragments.InfoFragment;
import com.kineticcafe.kcpmall.fragments.MapFragment;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.managers.SidePanelManagers;
import com.kineticcafe.kcpmall.mappedin.Amenities;
import com.kineticcafe.kcpmall.mappedin.AmenitiesManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.BadgeView;
import com.kineticcafe.kcpmall.views.CTA;
import com.kineticcafe.kcpmall.views.KcpAnimatedViewPager;

import java.util.ArrayList;
import java.util.List;

//public class MainActivity extends AppCompatActivity
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, KcpDataListener /*, DirectoryFragment.OnCategoryClickListener*/ {


    private final int VIEWPAGER_PAGE_HOME = 0;
    private final int VIEWPAGER_PAGE_DIRECTORY = 1;
    private final int VIEWPAGER_PAGE_MAP = 2;
    private final int VIEWPAGER_PAGE_INFO = 3;

    protected final Logger logger = new Logger(getClass().getName());
    private DrawerLayout mDrawer;
    private DrawerLayout mDrawerRight;
    private Thread mSplashThread;
    private ActionBarDrawerToggle mToggle;
    private Snackbar mOfflineSnackbar;
    private ImageView ivToolbar;
    private KcpAnimatedViewPager mViewPager;
    private View scRightDrawerLayout;
    private AppBarLayout ablTopNav;
    private Toolbar mToolbar;
    private RelativeLayout rlDestinationEditor;
    private EditText etStartStore;
    private EditText etDestStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseTracking.getInstance(this).logAppLaunch();

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
                        splashImage.setVisibility(View.GONE);
                        Animation animFadeOut = AnimationUtils.loadAnimation(MainActivity.this,
                                R.anim.splash_fade_out);
                        splashImage.startAnimation(animFadeOut);
                    }
                });

            }
        };
        mSplashThread.start();
        initializeToolbar();

        //disabling the default navigation drawer
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


        ablTopNav = (AppBarLayout)findViewById(R.id.ablTopNav);
        rlDestinationEditor = (RelativeLayout) findViewById(R.id.rlDestinationEditor);
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        etStartStore = (EditText) findViewById(R.id.etStartStore);
        etDestStore = (EditText) findViewById(R.id.etDestStore);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDestinationEditor(true, null, null, null, null);
            }
        });
        mViewPager = (KcpAnimatedViewPager) findViewById(R.id.vpMain);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position == VIEWPAGER_PAGE_MAP || position == VIEWPAGER_PAGE_INFO ) expandTopNav(); //TODO: change this hardcode

                if(position == VIEWPAGER_PAGE_MAP) {
                    mViewPager.setPagingEnabled(false); //disable swiping between pagers
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.scRightDrawerLayout)); //enable the right drawerlayout
                    showMapToolbar(true); //enable map's toolbar
                } else {
                    mViewPager.setPagingEnabled(true);
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.scRightDrawerLayout));
                    showMapToolbar(false);
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

        initializeKcpData();
        setUpLeftSidePanel();
        setUpRightSidePanel();

        scRightDrawerLayout = (View) findViewById(R.id.scRightDrawerLayout);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.scRightDrawerLayout));
    }

    public void expandTopNav() {
        ablTopNav.setExpanded(true);
    }

    public void openRightDrawerLayout(){
        mDrawer.openDrawer(scRightDrawerLayout);
    }

    private void initializeKcpData(){
        if(!KcpUtility.isNetworkAvailable(this)){
            ProgressBarWhileDownloading.showProgressDialog(this, R.layout.layout_loading_item, false);
            this.onDataDownloaded(); //TODO: error here when offline
            this.showSnackBar(R.string.warning_no_internet_connection, R.string.warning_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressBarWhileDownloading.showProgressDialog(MainActivity.this, R.layout.layout_loading_item, true);
                    initializeKcpData();
                }
            });
            return;
        } else {
            ProgressBarWhileDownloading.showProgressDialog(MainActivity.this, R.layout.layout_loading_item, true);
            HomeFragment.getInstance().initializeHomeData();
            DirectoryFragment.getInstance().initializeDirectoryData();
            InfoFragment.getInstance().initializeMallInfoData();
            initializeMapData();
        }
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
        mToggle.syncState();
    }


    // ------------------------------------- START OF MAP FRAGMENT -------------------------------------
    public void showMapToolbar(boolean enableMapToolbar){
        if(enableMapToolbar){
            ivToolbar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.menu_map));
        } else {
            ivToolbar.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    public void toggleDestinationEditor(boolean forceHide, String start, String dest, MapFragment.EditTextTextChangeListener editTextTextChangeListener, MapFragment.FocusListener focusListener){
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        if(rlDestinationEditor.getVisibility() == View.VISIBLE || forceHide) {
            Animation slideDownAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_up_out_of_screen);
            slideDownAnimation.reset();
            rlDestinationEditor.startAnimation(slideDownAnimation);
            slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rlDestinationEditor.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            imm.hideSoftInputFromWindow(etStartStore.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etDestStore.getWindowToken(), 0);
        }
        else {
            etStartStore.addTextChangedListener(editTextTextChangeListener);
            etDestStore.addTextChangedListener(editTextTextChangeListener);

            etStartStore.setOnFocusChangeListener(focusListener);
            etDestStore.setOnFocusChangeListener(focusListener);

            mToolbar.setVisibility(View.GONE);
            rlDestinationEditor.setVisibility(View.VISIBLE);
            Animation slideDownAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.anim_slide_down);
            slideDownAnimation.reset();
            rlDestinationEditor.startAnimation(slideDownAnimation);

            setDestionationNames(start, dest); //set destination names - ex) start : A&W , destionation : Club Monaco
            if(start != null) etDestStore.requestFocus(); //if start name is empty, request focus to destination
            else etStartStore.requestFocus();

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void setDestionationNames(String start, String dest){
        if(start != null) etStartStore.setText(start);
        if(dest != null) etDestStore.setText(dest);
        if(start != null && dest == null) etDestStore.requestFocus();
        if(start == null && dest != null) etStartStore.requestFocus();
    }

    public String getStartStoreText(){
        return etStartStore.getText().toString();
    }

    public String getDestStoreText(){
        return etDestStore.getText().toString();
    }


    // ------------------------------------- END OF MAP FRAGMENT -------------------------------------

    private void initializeMapData(){

        String data = "";
        try {
            data = KcpUtility.convertStreamToString(getAssets().open(HeaderFactory.AMENITIES_OFFLINE_TEXT));
            Gson gson = new Gson();
            AmenitiesManager.sAmenities = gson.fromJson(data, Amenities.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AmenitiesManager amenitiesManager = new AmenitiesManager(this, R.layout.layout_loading_item, new Handler(Looper.getMainLooper()) {
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
        amenitiesManager.downloadAmenities();
    }


    private void setUpLeftSidePanel(){
        //ACCOUNT
        TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
        tvDetailDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView ivDrawerLayoutUser = (ImageView) findViewById(R.id.ivDrawerLayoutUser);
//        ivDrawerLayoutUser.setImageResource(R.drawable.test_profile);
        ivDrawerLayoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "User Image clicked", Toast.LENGTH_SHORT).show();
            }
        });

        TextView tvDrawerLayoutAccount = (TextView) findViewById(R.id.tvDrawerLayoutAccount);
        tvDrawerLayoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Account clicked", Toast.LENGTH_SHORT).show();
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
        BadgeView badgeDeals = (BadgeView) findViewById(R.id.badgeDeals);
        flDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getDealFavSize(), getResources().getString(R.string.my_page_deals));
            }
        });

        FrameLayout flEvents = (FrameLayout) findViewById(R.id.flEvents);
        BadgeView badgeEvents = (BadgeView) findViewById(R.id.badgeEvents);
        flEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getEventAnnouncementFavSize(), getResources().getString(R.string.my_page_events));
            }
        });

        FrameLayout flStores = (FrameLayout) findViewById(R.id.flStores);
        BadgeView badgeStores = (BadgeView) findViewById(R.id.badgeStores);
        flStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getStoreFavSize(), getResources().getString(R.string.my_page_stores));
            }
        });

        FrameLayout flInterests = (FrameLayout) findViewById(R.id.flInterests);
        flInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyPageActivity(FavouriteManager.getInstance(MainActivity.this).getInterestFavSize(), getResources().getString(R.string.my_page_interests));
            }
        });

        BadgeView badgeInterests = (BadgeView) findViewById(R.id.badgeInterests);


        //GC
        FrameLayout flAddGC = (FrameLayout) findViewById(R.id.flAddGC);
        flAddGC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Add a gift card", Toast.LENGTH_SHORT).show();
            }
        });

        SidePanelManagers sidePanelManagers = new SidePanelManagers(this, badgeDeals, badgeEvents, badgeStores, badgeInterests);
    }

    private void setUpRightSidePanel() {
        RelativeLayout rlSeeDeal = (RelativeLayout) findViewById(R.id.rlSeeDeal);
        final ImageView ivFilterDeal = (ImageView) findViewById(R.id.ivFilterDeal);
        final TextView tvFilterDeal = (TextView) findViewById(R.id.tvFilterDeal);
        rlSeeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivFilterDeal.isSelected()){
                    ivFilterDeal.setSelected(false);
                    tvFilterDeal.setText(getResources().getString(R.string.map_filter_hide_deal));
                } else {
                    ivFilterDeal.setSelected(true);
                    tvFilterDeal.setText(getResources().getString(R.string.map_filter_see_deal));
                }
            }
        });
        RelativeLayout rlSeeParking = (RelativeLayout) findViewById(R.id.rlSeeParking);
        final ImageView ivFilterParking= (ImageView) findViewById(R.id.ivFilterParking);
        final TextView tvFilterParking = (TextView) findViewById(R.id.tvFilterParking);
        rlSeeParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivFilterParking.isSelected()){
                    tvFilterParking.setText(getResources().getString(R.string.map_filter_hide_parking));
                   ivFilterParking.setSelected(false);
                } else {
                   ivFilterParking.setSelected(true);
                    tvFilterParking.setText(getResources().getString(R.string.map_filter_see_parking));
                }
            }
        });

        View llAmenitySwitch = findViewById(R.id.llAmenitySwitch);
        List<Amenities.AmenityLayout> amenityList = new ArrayList<>();

        for(int i = 0; i < AmenitiesManager.sAmenities.getAmenityList().size(); i++){

            Amenities.Amenity amenity = AmenitiesManager.sAmenities.getAmenityList().get(i);
            if(amenity.isEnabled()) {
                amenityList.add(new Amenities.AmenityLayout(
                                MainActivity.this,
                                (ViewGroup) llAmenitySwitch,
                                R.layout.layout_amenities,
                                amenity.getTitle(),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        )
                );
            }
        }

        ((ViewGroup)llAmenitySwitch).removeAllViews();
        for(int i = 0; i < amenityList.size(); i++){
            ((ViewGroup) llAmenitySwitch).addView(amenityList.get(i).getView());
        }
    }

    public void startMyPageActivity(int listSize, final String myPageType){
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

    /**
     * If there's snackbar showing, it simply returns unless onClickListener is set.
     *
     * @param msg
     * @param action
     * @param onClickListener
     */
    public void showSnackBar(int msg, int action, @Nullable View.OnClickListener onClickListener) {
        if (mOfflineSnackbar != null && (mOfflineSnackbar.isShownOrQueued() || onClickListener == null))
            return;
        final CoordinatorLayout clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        if (onClickListener == null) {
            mOfflineSnackbar = Snackbar
                    .make(clMain, getResources().getString(msg), Snackbar.LENGTH_SHORT);
        } else {
            mOfflineSnackbar = Snackbar
                    .make(clMain, getResources().getString(msg), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(action), onClickListener);
        }

        mOfflineSnackbar.setActionTextColor(getResources().getColor(R.color.themeColor));
        View snackbarView = mOfflineSnackbar.getView();

        CoordinatorLayout.LayoutParams param = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
        param.bottomMargin = (int) getResources().getDimension(R.dimen.main_app_bar_layout_height);
        snackbarView.setLayoutParams(param);

        snackbarView.setBackgroundColor(Color.DKGRAY);

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.white));

        TextView actionText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(getResources().getColor(R.color.warningColor));

        mOfflineSnackbar.show();
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
//        fragmentList.add(MapFragment.getInstance());
        fragmentList.add(new MapFragment());
//        fragmentList.add(new TestFragment());
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
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.executePendingTransactions();
            if (fragmentManager.getBackStackEntryCount() < 1){
                super.onBackPressed();
            } else {
                fragmentManager.executePendingTransactions();
                fragmentManager.popBackStack();
                fragmentManager.executePendingTransactions();
                if (fragmentManager.getBackStackEntryCount() < 1){
                    initializeToolbar(); //BUG: toolbar initialization has to be done - otherwise, the arrow button won't show up the secondtime fragment's created
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_backend_vm) {
            HeaderFactory.changeCatalog(Constants.HEADER_VALUE_DATAHUB_CATALOG_VM);
            initializeKcpData();
            return true;
        } else if (id == R.id.action_backend_mp) {
            HeaderFactory.changeCatalog(Constants.HEADER_VALUE_DATAHUB_CATALOG_MP);
            initializeKcpData();
            return true;
        } else if (id == android.R.id.home){
            onBackPressed();
        }

        else if (id == R.id.action_test) {
            throw new RuntimeException("This is a crash");
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

    private void selectPage(int pageIndex){
//        tabLayout.setScrollPosition(pageIndex, 0f, true);
        mDrawer.closeDrawers();
        mViewPager.setCurrentItem(pageIndex);
    }

    @Override
    public void onDataDownloaded() {
        synchronized (mSplashThread) {
            mSplashThread.notifyAll();
        }
    }

    public static RefreshListener mOnRefreshListener;

    public void setOnRefreshListener(RefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    public interface RefreshListener {
        void onRefresh(int msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_CHANGE_INTEREST) {
            if (resultCode == Activity.RESULT_OK) {
                HomeFragment.getInstance().downloadNewsAndDeal();
            } else {

            }
        } else if (requestCode == Constants.REQUEST_CODE_MY_PAGE_TYPE) {
            if (resultCode == Constants.RESULT_DEALS) {
                selectPage(0);
                HomeFragment.getInstance().selectPage(1);
            } else if (resultCode == Constants.RESULT_EVENTS) {
                selectPage(0);
                HomeFragment.getInstance().selectPage(0);
            } else if (resultCode == Constants.RESULT_STORES) {
                selectPage(1);
                DirectoryFragment.getInstance().selectPage(1);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
}

