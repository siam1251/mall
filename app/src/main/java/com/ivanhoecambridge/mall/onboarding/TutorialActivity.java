package com.ivanhoecambridge.mall.onboarding;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.BaseActivity;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by petar on 2017-11-10.
 */

public class TutorialActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private ArrayList<Fragment> fragmentList;
    @BindView(R.id.viewPagerOnboarding)
    ViewPager viewPager;
    @BindView(R.id.tlDots)
    TabLayout dotIndicators;
    @BindView(R.id.tvOnbdGetStarted)
    TextView tvGetStarted;
    @BindView(R.id.tvOnbdNext)
    TextView tvNext;
    @BindView(R.id.tvOnbdSkip)
    TextView tvSkip;
    private OnboardingFragment currentFragment;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.getInstance(this).logScreenView(this, "Onboarding Page " + viewPager.getCurrentItem());
    }

    private void init() {
        fragmentList = new ArrayList<>();
        fragmentList.add(OnboardingFragment.newInstance(0, R.string.onbd_one_title, 0, R.drawable.onboarding_1, 0));
        fragmentList.add(OnboardingFragment.newInstance(1, R.string.onbd_two_title, R.string.onbd_two_desc, R.drawable.onboarding_2, R.drawable.onboarding_2_alt));
        fragmentList.add(OnboardingFragment.newInstance(2, R.string.onbd_three_title, R.string.onbd_three_desc, R.drawable.onboarding_3, R.drawable.onboarding_3_alt));
        fragmentList.add(OnboardingFragment.newInstance(3,
                getStringResByBoolean(R.string.onbd_four_title, R.string.onbd_four_no_bluedot_desc, BuildConfig.BLUEDOT),
                getStringResByBoolean(R.string.onbd_four_desc, R.string.onbd_four_no_bluedot_title, BuildConfig.BLUEDOT),
                R.drawable.onboarding_4, 0));
        fragmentList.add(OnboardingFragment.newInstance(4, R.string.onbd_five_title, R.string.onbd_five_desc, R.drawable.onboarding_5, 0));

        OnboardingPagerAdapter onbdAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        onbdAdapter.addFragments(fragmentList);
        viewPager.setAdapter(onbdAdapter);
        viewPager.addOnPageChangeListener(this);
        dotIndicators.setupWithViewPager(viewPager);

    }

    private int getStringResByBoolean(int originalId, int altId, boolean checkValue) {
        return checkValue ? originalId : altId;
    }

    @OnClick(R.id.tvOnbdGetStarted)
    public void onGetStarted() {
        animateToStartTutorial();
        viewPager.setCurrentItem(1);
    }

    @OnClick(R.id.tvOnbdNext)
    public void onNextPage() {
        if (!isLastPage(viewPager.getCurrentItem())) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, false);
        } else {
           startMainActivity();
        }
    }

    @OnClick(R.id.tvOnbdSkip)
    public void onSkipTutorial() {
        startMainActivity();
    }

    private void startMainActivity() {
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }

    private boolean isLastPage(int position) {
        return position == fragmentList.size()-1;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0 && positionOffset < 0.3) {
            toggleViewVisibility(true);
        } else if (position == 1){
            toggleViewVisibility(false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Analytics.getInstance(getApplicationContext()).logScreenView(this, "Onboarding Page " + position);
        if (currentFragment != null) {
            currentFragment.removeAnimationCallbacks();
        }
        currentFragment = (OnboardingFragment) fragmentList.get(position);
        currentFragment.startAnimationCallbacks();

        if (position == 0) {
            animateToInitialPosition();
        } else if (position == 1) {
            animateToStartTutorial();
        }
        if (!isLastPage(position)) {
            tvNext.setText(getString(R.string.onbd_btn_next));
            tvSkip.setVisibility(View.VISIBLE);
        } else {
            tvNext.setText(getString(R.string.onbd_btn_lets_get_done));
            tvSkip.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void animateToInitialPosition() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(400);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tvNext.setVisibility(View.GONE);
                tvSkip.setVisibility(View.GONE);
                dotIndicators.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvGetStarted.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvGetStarted.startAnimation(animation);
    }

    private void animateToStartTutorial() {
        if (tvGetStarted.getVisibility() == View.GONE) return;
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvNext.setVisibility(View.VISIBLE);
                tvSkip.setVisibility(View.VISIBLE);
                dotIndicators.setVisibility(View.VISIBLE);
                tvGetStarted.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvGetStarted.startAnimation(animation);
    }



    private void toggleViewVisibility(final boolean isFirstPage) {
        tvGetStarted.setVisibility(isFirstPage ? View.VISIBLE : View.GONE);
        tvNext.setVisibility(isFirstPage ? View.GONE : View.VISIBLE);
        tvSkip.setVisibility(isFirstPage ? View.GONE : View.VISIBLE);
        dotIndicators.setVisibility(isFirstPage ? View.INVISIBLE : View.VISIBLE);
    }

    public static class OnboardingPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        private OnboardingPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        private void addFragments(List<Fragment> fragmentList) {
            this.fragmentList = fragmentList;
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
