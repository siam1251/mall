package com.kineticcafe.kcpmall.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.managers.KcpDataListener;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.HomeBottomTapAdapter;
import com.kineticcafe.kcpmall.fragments.DirectoryFragment;
import com.kineticcafe.kcpmall.fragments.HomeFragment;
import com.kineticcafe.kcpmall.fragments.TestFragment;
import com.kineticcafe.kcpmall.views.KcpAnimatedViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, KcpDataListener /*, DirectoryFragment.OnCategoryClickListener*/ {

    protected final Logger logger = new Logger(getClass().getName());
    private DrawerLayout mDrawer;
    private Thread mSplashThread;
    private ActionBarDrawerToggle mToggle;
    private Snackbar mOfflineSnackbar;
    private ImageView ivToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        KcpAnimatedViewPager viewPager = (KcpAnimatedViewPager) findViewById(R.id.vpMain);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlBottom);

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitleList = new ArrayList<>();
        List<Integer> fragmentIconList = new ArrayList<>();

        prepareTabContents(fragmentList, fragmentTitleList, fragmentIconList);
        setupViewPager(viewPager, fragmentList, fragmentTitleList, fragmentIconList);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(viewPager, tabLayout, fragmentTitleList, fragmentIconList);
        viewPager.setTabLayout(tabLayout);

        initializeKcpData();
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
        }
    }

    private void initializeToolbar(){
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        ivToolbar = (ImageView) toolbar.findViewById(R.id.ivToolbar);
        ivToolbar.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
    }


    /**
     * If there's snackbar showing, it simply returns unless onClickListener is set.
     *
     * @param msg
     * @param action
     * @param onClickListener
     */
    public void showSnackBar(int msg, int action, @Nullable View.OnClickListener onClickListener) {
        if (mOfflineSnackbar != null && mOfflineSnackbar.isShownOrQueued() && onClickListener == null)
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
        fragmentList.add(new TestFragment());
        fragmentList.add(new TestFragment());
    }

    /**
     * set custom layout to each tab
     */
    private void setupTabIcons(KcpAnimatedViewPager viewPager, TabLayout tabLayout, List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        for (int i = 0; i < fragmentTitleList.size(); i++) {
            View singleTablayout = LayoutInflater.from(this).inflate(R.layout.tab_img_layout, null);
            viewPager.changeTabStateWhenSelected(singleTablayout, false);
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
    private void setupViewPager(ViewPager viewPager, List<Fragment> fragmentList, List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        HomeBottomTapAdapter adapter = new HomeBottomTapAdapter(this, fragmentList, fragmentTitleList, fragmentIconList);
        viewPager.setAdapter(adapter);
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_test) {
            return true;
        } else if (id == android.R.id.home){
            onBackPressed();
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
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
}

 