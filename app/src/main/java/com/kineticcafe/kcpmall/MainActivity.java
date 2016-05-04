package com.kineticcafe.kcpmall;

import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpmall.adapters.HomeBottomTapAdapter;
import com.kineticcafe.kcpmall.fragments.HomeFragment;
import com.kineticcafe.kcpmall.fragments.OneFragment;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.widget.KcpViewPagerWithFaceInOut;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OneFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private TabLayout mTapLayout;
    private ViewPager mViewPager;


    public enum ScrollDirection { RIGHT, LEFT, IDLE }
    private ScrollDirection mScrollDirection = ScrollDirection.IDLE;
    private boolean mTabStateHandledAlready = false;
    private int mCurrentTabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewPager = (ViewPager) findViewById(R.id.vpMain);
        mTapLayout = (TabLayout) findViewById(R.id.tlBottom);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private static final float thresholdOffset = 0.5f;
            private boolean scrollStarted, checkDirection;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset == 0) {
                    mCurrentTabPosition = position;
                }

                View v = mTapLayout.getTabAt(mCurrentTabPosition).getCustomView();
                if (checkDirection) {
                    if (thresholdOffset > positionOffset) {
                        mScrollDirection = ScrollDirection.RIGHT;
                    } else {
                        mScrollDirection = ScrollDirection.LEFT;
                    }
                    checkDirection = false;
                } else {
                    if(positionOffset == 0) {
                        return;
                    }

                    if(mTabStateHandledAlready) return;
                    if( mScrollDirection.equals(ScrollDirection.RIGHT)) {
                        //current tab fade out
                        changeTabStateAsScroll(v, R.color.themeColor, R.color.unselected, positionOffset);
                        if(mTapLayout.getTabCount() > mCurrentTabPosition + 1 ) {
                            //right tab fade in
                            changeTabStateAsScroll(mTapLayout.getTabAt(mCurrentTabPosition + 1).getCustomView(), R.color.themeColor, R.color.unselected, 1 - positionOffset);
                        }
                    } else {
                        //current tab fade out
                        changeTabStateAsScroll(v, R.color.themeColor, R.color.unselected, 1 - positionOffset);
                        if(0 <= mCurrentTabPosition - 1) {
                            //left fade in
                            changeTabStateAsScroll(mTapLayout.getTabAt(mCurrentTabPosition - 1).getCustomView(), R.color.themeColor, R.color.unselected, positionOffset);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
                    scrollStarted = true;
                    checkDirection = true;
                } else {
                    scrollStarted = false;
                }
                if(state == ViewPager.SCROLL_STATE_IDLE) {
                    mTabStateHandledAlready = false;
                }
            }
        });

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitleList = new ArrayList<>();
        List<Integer> fragmentIconList = new ArrayList<>();

        prepareTabContents(fragmentList, fragmentTitleList, fragmentIconList);
        setupViewPager(mViewPager, fragmentList, fragmentTitleList, fragmentIconList);


        mTapLayout.setupWithViewPager(mViewPager);
        setupTabIcons(fragmentTitleList, fragmentIconList);

        for(int i = 0; i < mTapLayout.getTabCount(); i++ ){
            changeTabStateWhenSelected(mTapLayout.getTabAt(i).getCustomView(), i == 0);
        }

        mTapLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabStateHandledAlready = true;
                for(int i = 0; i < mTapLayout.getTabCount(); i++){
                    changeTabStateWhenSelected(mTapLayout.getTabAt(i).getCustomView(), tab.getPosition() == i);
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /** change the tab icon and text state when selected and unselected */
    private void changeTabStateWhenSelected(View v, boolean selected){
        TextView tvTabTitle = (TextView) v.findViewById(R.id.tvTabTitle);
        int color = selected == true ? R.color.themeColor : R.color.unselected;
        tvTabTitle.setTextColor(getResources().getColor(color));

        ImageView ivTabIcon = (ImageView) v.findViewById(R.id.ivTabIcon);
        ivTabIcon.setColorFilter(getResources().getColor(color));


        float alpha = selected == true ? 1.0f : 0.0f;
        tvTabTitle.setAlpha(alpha);

        Animation faceInAnim = new AlphaAnimation(0.0f, 1.0f);
        Animation faceOutAnim = new AlphaAnimation(1.0f, 0.0f);
        faceInAnim.setDuration(Constants.DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION);
        faceInAnim.setStartOffset(Constants.DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION);
        faceInAnim.setFillAfter(true);
        faceInAnim.setFillEnabled(true);

        faceOutAnim.setDuration(Constants.DURATION_MAIN_BOT_TAB_TITLE_ALPHA_ANIMATION);
        faceOutAnim.setStartOffset(Constants.DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION);
        faceOutAnim.setFillAfter(true);
        faceOutAnim.setFillEnabled(true);

        if(selected){
            ivTabIcon.animate().translationY((int) getResources().getDimension(R.dimen.homeTabIconTopMarginWhenSelected)).setDuration(Constants.DURATION_MAIN_BOT_TAB_ICON_SLIDE_UP_ANIMATION).start();
            tvTabTitle.startAnimation(faceInAnim);
        } else {
            ivTabIcon.animate().translationY((int) getResources().getDimension(R.dimen.homeTabIconTopMarginWhenNotSelected)).setDuration(100).start();
            tvTabTitle.startAnimation(faceOutAnim);
        }
    }

    private void changeTabStateAsScroll(View v, int colorFrom, int colorTo, float proportion){
        TextView tvCurrentTab = (TextView) v.findViewById(R.id.tvTabTitle);
        ImageView ivCurrentTab = (ImageView) v.findViewById(R.id.ivTabIcon);

        ivCurrentTab.setColorFilter(Utility.interpolateColor(
                getResources().getColor(colorFrom), getResources().getColor(colorTo), proportion));
        tvCurrentTab.setTextColor(Utility.interpolateColor(
                getResources().getColor(colorFrom), getResources().getColor(colorTo), proportion));
    }



    /** prepares tab contents with fragments, tab icons and texts */
    private void prepareTabContents(List<Fragment> fragmentList, List<String> fragmentTitleList, List<Integer> fragmentIconList){

        String[] menuTitle  = getResources().getStringArray(R.array.menuTitles);
        TypedArray menuIcon = getResources().obtainTypedArray(R.array.menuIcons);
        for(int i = 0; i < menuTitle.length; i++){
            fragmentTitleList.add(menuTitle[i]);
            fragmentIconList.add(menuIcon.getResourceId(i, -1));
        }

        fragmentList.add(new HomeFragment());
        fragmentList.add(new OneFragment());
        fragmentList.add(new OneFragment());
        fragmentList.add(new OneFragment());
    }

    /** set custom layout to each tab */
    private void setupTabIcons(List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        for(int i = 0; i < fragmentTitleList.size(); i++){
            View tabLayout = LayoutInflater.from(this).inflate(R.layout.tab_img_layout, null);
            TextView tabTitle = (TextView) tabLayout.findViewById(R.id.tvTabTitle);
            ImageView tabImage = (ImageView) tabLayout.findViewById(R.id.ivTabIcon);
            tabTitle.setText(fragmentTitleList.get(i));
            tabTitle.setTextColor(getResources().getColor(R.color.unselected));
//            tabTitle.setAlpha(0.0f);

            tabImage.setImageResource(fragmentIconList.get(i));
            tabImage.setColorFilter(getResources().getColor(R.color.unselected));

            mTapLayout.getTabAt(i).setCustomView(tabLayout);
        }
    }

    /** set up view pager */
    private void setupViewPager(ViewPager viewPager, List<Fragment> fragmentList, List<String> fragmentTitleList, List<Integer> fragmentIconList) {
        HomeBottomTapAdapter adapter = new HomeBottomTapAdapter(this, fragmentList, fragmentTitleList, fragmentIconList);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
