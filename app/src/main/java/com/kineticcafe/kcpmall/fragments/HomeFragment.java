package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.HomeTopViewPagerAdapter;

public class HomeFragment extends Fragment {

    private View mView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mViewPager = (ViewPager) mView.findViewById(R.id.vpHome);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) mView.findViewById(R.id.tlHome);
        mTabLayout.setupWithViewPager(mViewPager);

        return mView;
    }


    private void setupViewPager(ViewPager viewPager) {
        HomeTopViewPagerAdapter adapter = new HomeTopViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new NewsFragment(), "NEWS");
        adapter.addFrag(new OneFragment(), "DEALS");
        viewPager.setAdapter(adapter);
    }
}
