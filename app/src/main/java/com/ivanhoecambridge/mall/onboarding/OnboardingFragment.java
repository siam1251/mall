package com.ivanhoecambridge.mall.onboarding;

import android.os.Bundle;
import android.os.Handler;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.CustomFontView.CustomFontTextView;
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
    private ImageView ivOnbdImageAlt;
    private ImageView ivOnbdFrame;

    private String       pageTitle;
    private String       pageDescription;
    private int          messageId;
    private int          titleId;
    private int          imageId;
    private int          secondImageId;
    private int          page;
    private LinearLayout llOnboarding;





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
        handler = new Handler(getContext().getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        llOnboarding = (LinearLayout) view;
        CustomFontTextView tvDesc = view.findViewById(R.id.tvOnbdDesc);
        if (page == 0) {
            tvDesc.setTextSize(21);
            tvDesc.setFont(getContext(), getString(R.string.fontFamily_roboto_medium));
        }
        ivOnbdImage = view.findViewById(R.id.ivOnbdImage);
        ivOnbdImageAlt = view.findViewById(R.id.ivOnbdImageAlt);
        ivOnbdFrame = view.findViewById(R.id.ivOnbdImageFrame);
        tvDesc.setText(pageDescription);
        Picasso.with(getContext())
                .load(imageId)
                .into(ivOnbdImage);
        if (secondImageId > 0) {
            Picasso.with(getContext())
                    .load(secondImageId)
                    .into(ivOnbdImageAlt);
        }
        return view;
    }

    private void fadeTransition() {
        Fade transition = new Fade();
        transition.setDuration(450);

        TransitionManager.beginDelayedTransition(llOnboarding, transition);
        if (ivOnbdImage.getVisibility() == View.VISIBLE) {
            ivOnbdImageAlt.setVisibility(View.VISIBLE);
            ivOnbdImage.setVisibility(View.INVISIBLE);
        } else {
            ivOnbdImage.setVisibility(View.VISIBLE);
            ivOnbdImageAlt.setVisibility(View.INVISIBLE);
        }
    }

    Runnable repeatAnimation = new Runnable() {
        @Override
        public void run() {
            fadeTransition();
            handler.postDelayed(this, ANIM_DELAY);
        }
    };


    public void removeAnimationCallbacks() {
        handler.removeCallbacks(repeatAnimation);

    }

    private void showOnboardingFrameImage() {
        ivOnbdFrame.setVisibility(View.VISIBLE);
    }

    public void startAnimationCallbacks() {
        if (secondImageId > 0) {
            showOnboardingFrameImage();
            handler.post(repeatAnimation);
        }
    }
}
