package com.kineticcafe.kcpmall.adapters;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-04-29.
 */
//public class HomeBottomTapAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider{
public class HomeBottomTapAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();
    private List<Integer> mFragmentIconList = new ArrayList<>();

    public HomeBottomTapAdapter(AppCompatActivity activity,
                                List<Fragment> fragmentList,
                                List<String> fragmentTitleList,
                                List<Integer> fragmentIconList) {
        super(activity.getSupportFragmentManager());
        mFragmentList = fragmentList;
        mFragmentTitleList = fragmentTitleList;
        mFragmentIconList = fragmentIconList;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


//    @Override
//    public int getPageIconResId(int position) {
//        return mFragmentIconList.get(position);
//    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title, int icon) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
        mFragmentIconList.add(icon);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}