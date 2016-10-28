package com.ivanhoecambridge.mall.onboarding;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.BaseActivity;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class TutorialActivity extends BaseActivity {
    private ArrayList<Fragment> mDescriptionFragments;
    private TutorialDescriptionAdapter mDescriptionAdapter;

    private ArrayList<TutorialImage> mImageFragments;
    private TouchConnectedViewPager mImagePager;
    private TouchConnectedViewPager mDescriptionPager;
    private TutorialImageAdapter mImageAdapter;
    private RelativeLayout rlOnbrd;
    private TextView tvOnbd;
    private TextView tvOnbdSkip;

    private ImageView[] dots;
    private final int NUMB_ONBRD_SCREENS = 5;
    private LinearLayout llViewPagerCountDots;
    private GetStartedButtonClicker mGetStartedButtonClicker;
    private boolean mHasFakeAlphaPage = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if(mHasFakeAlphaPage) setTheme(R.style.Theme_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mDescriptionPager = (TouchConnectedViewPager) findViewById(R.id.vp_tutorial_description);
        mImagePager = (TouchConnectedViewPager) findViewById(R.id.vp_tutorial_image);
        llViewPagerCountDots = (LinearLayout) findViewById(R.id.llViewPagerCircle);

        rlOnbrd = (RelativeLayout) findViewById(R.id.rlOnbrd);
        tvOnbdSkip = (TextView) findViewById(R.id.tvOnbdSkip);
        tvOnbdSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ActivityAnimation.exitActivityAnimation(TutorialActivity.this);
            }
        });
        tvOnbd = (TextView) findViewById(R.id.tvOnbd);
        mGetStartedButtonClicker = new GetStartedButtonClicker();
        tvOnbd.setOnClickListener(mGetStartedButtonClicker);

        mDescriptionFragments = new ArrayList<Fragment>();
        mDescriptionFragments.add(TutorialDescription.newInstance(0, R.string.onbd_one_title, 0));
        mDescriptionFragments.add(TutorialDescription.newInstance(1, R.string.onbd_two_title, R.string.onbd_two_desc));
        mDescriptionFragments.add(TutorialDescription.newInstance(2, R.string.onbd_three_title, R.string.onbd_three_desc));
        mDescriptionFragments.add(TutorialDescription.newInstance(3, R.string.onbd_four_title, R.string.onbd_four_desc));
        mDescriptionFragments.add(TutorialDescription.newInstance(4,R.string.onbd_five_title, R.string.onbd_five_desc));
        if(mHasFakeAlphaPage) mDescriptionFragments.add(TutorialDescription.newInstance(5, 0, 0));

        mDescriptionAdapter = new TutorialDescriptionAdapter(this, getSupportFragmentManager());
        mDescriptionPager.setAdapter(mDescriptionAdapter);
        mDescriptionPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mHasFakeAlphaPage && position == NUMB_ONBRD_SCREENS - 1 && positionOffset > 0){
                    rlOnbrd.setAlpha(1.0f - Math.abs(positionOffset));
                }
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < NUMB_ONBRD_SCREENS; i++) {
                    dots[i].setImageDrawable(TutorialActivity.this.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_unselected));
                }
                dots[position % dots.length].setImageDrawable(TutorialActivity.this.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_selected_black));
                removeGetStartedBtn(position != 0);
                if(position == NUMB_ONBRD_SCREENS - 1) {
                    tvOnbdSkip.setText(getString(R.string.onbd_btn_lets_get_done));
                } else {
                    tvOnbdSkip.setText(getString(R.string.onbd_btn_lets_get_skip));
                }
                controlAnimationPlayback(position);

                if(mHasFakeAlphaPage && position == NUMB_ONBRD_SCREENS){
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    /*if(mHasFakeAlphaPage) overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    else ActivityAnimation.exitActivityAnimation(TutorialActivity.this);*/
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setUiPageViewController();

        mDescriptionPager.setViewPager(mImagePager);
        mImagePager.setViewPager(mDescriptionPager);


        mImagePager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDescriptionPager.onTouchEvent(event);
            }
        });
        mImageFragments = new ArrayList<TutorialImage>();
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_1));
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_2));
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_3));
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_4));
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_5));
        if(mHasFakeAlphaPage) mImageFragments.add(TutorialImage.newInstance(0));

        mImageAdapter = new TutorialImageAdapter(this, getSupportFragmentManager());
        mImagePager.setAdapter(mImageAdapter);
        controlAnimationPlayback(0);
    }

    public void removeGetStartedBtn(boolean hide){
        Animation hideShowAnim = null;
        final int visibility;
        if(hide) {
            if(tvOnbd.getVisibility() == View.GONE) return;
            hideShowAnim = AnimationUtils.loadAnimation(TutorialActivity.this,
                    android.R.anim.fade_out);
            tvOnbd.setOnClickListener(null);
            visibility = View.GONE;
        } else {
            hideShowAnim = AnimationUtils.loadAnimation(TutorialActivity.this,
                    android.R.anim.fade_in);
            tvOnbd.setOnClickListener(mGetStartedButtonClicker);
            visibility = View.VISIBLE;
        }
        hideShowAnim.reset();
        hideShowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                tvOnbd.setVisibility(visibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        tvOnbd.startAnimation(hideShowAnim);
    }

    public class GetStartedButtonClicker implements OnClickListener {

        @Override
        public void onClick(View v) {
            mDescriptionPager.setCurrentItem(1);
            removeGetStartedBtn(true);
        }
    }


    public class TutorialDescriptionAdapter extends FragmentStatePagerAdapter {
        public TutorialDescriptionAdapter(Context context, FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            return mDescriptionFragments.get(i);
        }
        @Override
        public int getCount() {
            return mDescriptionFragments.size();
        }
    }

    public class TutorialImageAdapter extends FragmentStatePagerAdapter {
        public TutorialImageAdapter(Context context, FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            return mImageFragments.get(i);
        }
        @Override
        public int getCount() {
            return mImageFragments.size();
        }
    }

    private void setUiPageViewController() {
        dots = new ImageView[NUMB_ONBRD_SCREENS];

        llViewPagerCountDots.removeAllViews(); //prevent from creating second indicator
        for (int i = 0; i < NUMB_ONBRD_SCREENS; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            int viewPagerCircleMargin = (int) getResources().getDimension(R.dimen.viewpager_circle_dot_margin_onbrd);
            params.setMargins(viewPagerCircleMargin, 0, viewPagerCircleMargin, 0);
            llViewPagerCountDots.addView(dots[i], params);
        }

        if(dots.length > 0) dots[0].setImageDrawable(getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_selected_black));
    }

    public static class TutorialDescription extends Fragment {
        private static final String POSITION = "position";
        private static final String TITLE_ID = "titleId";
        private static final String DESC_ID = "descId";

        public static TutorialDescription newInstance(int position, int title, int desc) {
            TutorialDescription fragment = new TutorialDescription();
            Bundle args = new Bundle();
            args.putInt(POSITION, position);
            args.putInt(TITLE_ID, title);
            args.putInt(DESC_ID, desc);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            super.onCreateView(inflater, container, savedInstanceState);

            Bundle args = getArguments();

            View v = inflater.inflate(R.layout.layout_tutorial_description, container, false);
            TextView tvOnbdTitle = (TextView) v.findViewById(R.id.tvOnbdTitle);
            TextView tvOnbdDesc = (TextView) v.findViewById(R.id.tvOnbdDesc);
            Button btn_tutorial_start = (Button) v.findViewById(R.id.btn_tutorial_start);
            btn_tutorial_start.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            int position = args.getInt(POSITION);
            int title = args.getInt(TITLE_ID);
            int desc = args.getInt(DESC_ID);

            if(title == 0) tvOnbdTitle.setVisibility(View.GONE);
            else tvOnbdTitle.setText(title);
            if(desc == 0) tvOnbdDesc.setVisibility(View.GONE);
            else tvOnbdDesc.setText(desc);

            if(position == 0) {
                tvOnbdTitle.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.onboarding_first_title));
                /*RelativeLayout.LayoutParams pr = (RelativeLayout.LayoutParams) tvOnbdTitle.getLayoutParams();
                int marginLeft = (int) getResources().getDimension(R.dimen.onboarding_first_title_margin);
                pr.setMargins(marginLeft, KcpUtility.dpToPx(getActivity(), 43), marginLeft, 0);;
                tvOnbdTitle.setLayoutParams(pr);*/
            } else {
                tvOnbdTitle.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.onboarding_second_title));
            }

            return v;
        }
    }

    public static class TutorialImage extends Fragment {

        private static final String IMAGE_ID = "imageId";
        private VideoView vvOnbrd;

        public static TutorialImage newInstance(int imageId) {
            TutorialImage fragment = new TutorialImage();
            Bundle args = new Bundle();
            args.putInt(IMAGE_ID, imageId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            Bundle args = getArguments();
            int imageId = args.getInt(IMAGE_ID);

            View v = inflater.inflate(R.layout.layout_tutorial_image, container, false);
            vvOnbrd = (VideoView) v.findViewById(R.id.vvOnbrd);
            if(imageId == 0) {
                vvOnbrd.setVisibility(View.GONE);
//                return v;
            }
            getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
            Uri video = Uri.parse("android.resource://" + getActivity().getPackageName() + "/"
                    + imageId);
            vvOnbrd.setVideoURI(video);
            vvOnbrd.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            vvOnbrd.start();

            return v;
        }

        public void playAnimation(boolean play){
            if(vvOnbrd != null && vvOnbrd.getVisibility() == View.VISIBLE) {
                if(play) {
                    vvOnbrd.seekTo(0);
                    vvOnbrd.start();
                } else {
                    vvOnbrd.pause();
                }
            }
        }
    }

    public void controlAnimationPlayback(int currentTab){
        for(int i = 0; i < mImageFragments.size(); i++) {
            TutorialImage tutorialImage = mImageFragments.get(i);
            tutorialImage.playAnimation(i == currentTab);
        }
    }

}
