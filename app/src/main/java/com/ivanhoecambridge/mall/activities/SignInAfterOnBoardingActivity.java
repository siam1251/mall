package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kay on 2017-01-26.
 */

public class SignInAfterOnBoardingActivity extends BaseActivity {

    RelativeLayout rlSignIn;
    CardView cvSignUpInFirstScene;
    CardView cvSignUpInSecondScene;
    ViewGroup rootContainer;
    private Scene mScene1;
    private Scene mScene2;
    private Scene mScene;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_after_on_boarding);
        ButterKnife.bind(this);

        rlSignIn = (RelativeLayout) findViewById(R.id.rlSignIn);
        int id = getResources().getIdentifier("img_signup_bg", "drawable", getPackageName());
        if(id != 0 && id != -1) {
            Drawable bgDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
            rlSignIn.setBackground(bgDrawable);
        } else rlSignIn.setBackgroundColor(Color.parseColor("#454545"));

        rootContainer = (ViewGroup) findViewById(R.id.scene_root);

        mScene1 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_boarding_sign_in_one, this);

        mScene2 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_boarding_sign_in_two, this);

        mScene = mScene1;
        mScene.enter();

        validateView();
    }

    private void validateView(){
        if(mScene == mScene1) {
            cvSignUpInFirstScene = (CardView) findViewById(R.id.cvSignUp);
            cvSignUpInFirstScene.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSignUp();
                }
            });
        } else if (mScene == mScene2) {
            cvSignUpInSecondScene = (CardView) findViewById(R.id.cvSignUp);
            cvSignUpInSecondScene.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickEmail();
                }
            });
        }
    }

    public void onClickSignUp() {
        if(mScene == mScene1) changeScene();
        else {
            onClickEmail();
        }
    }

    @OnClick(R.id.ivDismiss) void onClickDismiss() {
        finishActivity();
    }

    @OnClick(R.id.tvSignIn) void onClickSignIn() {
        startActivity(new Intent(SignInAfterOnBoardingActivity.this, SignInActivity.class));
        ActivityAnimation.startActivityAnimation(SignInAfterOnBoardingActivity.this);
    }

    @Optional
    @OnClick(R.id.cvFb) void onClickFb() {
        Toast.makeText(this, "fb", Toast.LENGTH_SHORT).show();
    }


    @Optional
    @OnClick(R.id.cvGoogle) void onClickGoogle() {
        Toast.makeText(this, "google", Toast.LENGTH_SHORT).show();
    }

    void onClickEmail() {
        Toast.makeText(this, "email", Toast.LENGTH_SHORT).show();
    }

    private void changeScene(){
        if(mScene == mScene1) mScene = mScene2;
        else if(mScene == mScene2) mScene = mScene1;
        TransitionManager.go(mScene);
        ButterKnife.bind(this);
        validateView();
    }

    private void finishActivity() {
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }
    @Override
    public void onBackPressed() {
        if(mScene == mScene2) changeScene();
        else finishActivity();
    }
}
