package com.ivanhoecambridge.mall.onboarding;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;
import com.squareup.picasso.Picasso;

/**
 * Created by petar on 2017-11-10.
 */

public class OnboardingFragment extends Fragment{

    private final static String ARG_TITLE = "title_id";
    private final static String ARG_MESSAGE  = "description_id";
    private final static String ARG_IMAGE_ID = "image_id";
    private final static String ARG_SECOND_IMAGE_ID = "image_id_2";
    private final static String ARG_PAGE     = "page";

    private Handler handler;
    private final int ANIM_DELAY = 2500;
    private ImageView ivOnbdImage;

    private String pageTitle;
    private String pageDescription;
    private int messageId;
    private int titleId;
    private int imageId;
    private int secondImageId;
    private int page;
    private int activeImage;


    public static OnboardingFragment newInstance(int page, int titleId, int messageId, int imageId, int secondImageId) {
        OnboardingFragment onboardFragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_TITLE, titleId);
        args.putInt(ARG_MESSAGE, messageId);
        args.putInt(ARG_IMAGE_ID, imageId);
        args.putInt(ARG_SECOND_IMAGE_ID, secondImageId);
        onboardFragment.setArguments(args);
        return onboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        page = args.getInt(ARG_PAGE);
        titleId = args.getInt(ARG_TITLE);
        messageId = args.getInt(ARG_MESSAGE);
        imageId = args.getInt(ARG_IMAGE_ID);
        secondImageId = args.getInt(ARG_SECOND_IMAGE_ID, 0);
        pageDescription = getString(messageId == 0 ? titleId : messageId);
        activeImage = imageId;
        handler = new Handler(getContext().getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        TextView tvDesc = view.findViewById(R.id.tvOnbdDesc);
        ivOnbdImage = view.findViewById(R.id.ivOnbdImage);
        tvDesc.setText(pageDescription);
        Picasso.with(getContext())
                .load(imageId)
                .into(ivOnbdImage);
        if (secondImageId > 0) {
            startAnimationCallbacks();
        }
        return view;
    }

    private int nextImage() {
       activeImage = (activeImage == secondImageId) ? imageId : secondImageId;
       return activeImage;
    }

    private void animateChanges() {
        final Animation animateFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fade_in);
        animateFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Picasso.with(getContext())
                        .load(nextImage())
                        .into(ivOnbdImage);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation animateFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fade_out);
        animateFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivOnbdImage.startAnimation(animateFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivOnbdImage.startAnimation(animateFadeOut);
    }

    Runnable repeatAnimation = new Runnable() {
        @Override
        public void run() {
            animateChanges();
            handler.postDelayed(this, ANIM_DELAY);
        }
    };

    public void removeAnimationCallbacks() {
        handler.removeCallbacks(repeatAnimation);
    }

    public void startAnimationCallbacks() {
        if (secondImageId > 0) {
            handler.post(repeatAnimation);
        }
    }
}
