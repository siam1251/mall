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
import android.widget.TextView;
import android.widget.VideoView;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.BaseActivity;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

public class TutorialActivity extends BaseActivity {
    private ArrayList<Fragment> mDescriptionFragments;
    private TutorialDescriptionAdapter mDescriptionAdapter;

    private ArrayList<TutorialImage> mImageFragments;
    private TouchConnectedViewPager mImagePager;
    private TouchConnectedViewPager mDescriptionPager;
    private TutorialImageAdapter mImageAdapter;
    private TextView tvOnbd;
    private TextView tvOnbdSkip;

    //CIRCLE VIEWPAGER INDICATOR
    public ImageView[] dots;
    public int dotsCount;
    public LinearLayout llViewPagerCountDots;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mDescriptionPager = (TouchConnectedViewPager) findViewById(R.id.vp_tutorial_description);
        mImagePager = (TouchConnectedViewPager) findViewById(R.id.vp_tutorial_image);
        llViewPagerCountDots = (LinearLayout) findViewById(R.id.llViewPagerCircle);


        tvOnbdSkip = (TextView) findViewById(R.id.tvOnbdSkip);
        tvOnbdSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ActivityAnimation.exitActivityAnimation(TutorialActivity.this);
            }
        });
        tvOnbd = (TextView) findViewById(R.id.tvOnbd);
        tvOnbd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDescriptionPager.setCurrentItem(1, true);
                removeGetStartedBtn(true);
            }
        });

        mDescriptionFragments = new ArrayList<Fragment>();
        mDescriptionFragments.add(TutorialDescription.newInstance(0));
        mDescriptionFragments.add(TutorialDescription.newInstance(1));
        mDescriptionFragments.add(TutorialDescription.newInstance(2));
        mDescriptionFragments.add(TutorialDescription.newInstance(4));
        mDescriptionFragments.add(TutorialDescription.newInstance(5));


        mDescriptionAdapter = new TutorialDescriptionAdapter(this, getSupportFragmentManager());
        mDescriptionPager.setAdapter(mDescriptionAdapter);
        mDescriptionPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(TutorialActivity.this.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_unselected));
                }
                dots[position % dots.length].setImageDrawable(TutorialActivity.this.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_selected_black));
                removeGetStartedBtn(position != 0);
                if(position == mDescriptionFragments.size() - 1) {
                    tvOnbdSkip.setText(getString(R.string.onbd_btn_lets_get_done));
                } else {
                    tvOnbdSkip.setText(getString(R.string.onbd_btn_lets_get_skip));
                }
                controlAnimationPlayback(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {



            }
        });
        Log.d("NewsRecyclerViewAdapter", "setUiPageViewController");
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
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_1));
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_1));
        mImageFragments.add(TutorialImage.newInstance(R.raw.animate_1));

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
            tvOnbd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDescriptionPager.setCurrentItem(1);
                    removeGetStartedBtn(true);
                }
            });
            visibility = View.VISIBLE;
        }
        hideShowAnim.reset();
        hideShowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvOnbd.setVisibility(visibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvOnbd.startAnimation(hideShowAnim);

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
        dotsCount = 5; //viewpagerAdapter.getCount(); used for actual counting
        dots = new ImageView[dotsCount];

        llViewPagerCountDots.removeAllViews(); //prevent from creating second indicator
        for (int i = 0; i < dotsCount; i++) {
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

        public static TutorialDescription newInstance(int position) {
            TutorialDescription fragment = new TutorialDescription();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            super.onCreateView(inflater, container, savedInstanceState);

            Bundle args = getArguments();
            int position = args.getInt("position");

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
            if(position == 0){
                tvOnbdTitle.setText(R.string.onbd_one_title);
                tvOnbdDesc.setVisibility(View.GONE);
            } else if(position == 1){
                tvOnbdTitle.setText(R.string.onbd_two_title);
                tvOnbdDesc.setText(R.string.onbd_two_desc);
            } else if(position == 2){
                tvOnbdTitle.setText(R.string.onbd_three_title);
                tvOnbdDesc.setText(R.string.onbd_three_desc);
            } else if(position == 3){
                tvOnbdTitle.setText(R.string.onbd_four_title);
                tvOnbdDesc.setText(R.string.onbd_four_desc);
            } else if(position == 4){
                tvOnbdTitle.setText(R.string.onbd_five_title);
                tvOnbdDesc.setText(R.string.onbd_five_desc);
            }

            return v;
        }
    }

    public static class TutorialImage extends Fragment {

        private static final String IMAGE_ID = "imageId";
        private VideoView vvOnbrd;
        private boolean mShouldPlay = false;

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
            /*ImageView ivOnbrd = (ImageView) v.findViewById(R.id.ivOnbrd);
            ivOnbrd.setImageResource(imageId);*/

            vvOnbrd = (VideoView) v.findViewById(R.id.vvOnbrd);
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
//            mShouldPlay = play;
            if(vvOnbrd != null) {
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
