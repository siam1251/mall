package com.ivanhoecambridge.mall.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kay on 2017-01-26.
 */

public class SignInAfterOnBoarding extends BaseActivity {

//    @BindView(R.id.ivLogo)            ImageView ivLogo;
//    @BindView(R.id.rlBottomHalf)      RelativeLayout rlBottomHalf;
//    @BindView(R.id.tvSignUp)            TextView tvSignUp;
//    @BindView(R.id.tvSignUp)            FrameLayout flSignUp;
//    @BindView(R.id.tvDesc)              TextView tvDesc;
//    @Nullable @BindView(R.id.flFb)      FrameLayout flFb;
//    @BindView(R.id.llSignUp)        LinearLayout llSignUp;
//    @Nullable @BindView(R.id.flGoogle)  FrameLayout flGoogle;
//    @BindView(R.id.flEmail)         FrameLayout flEmail;
    @BindView(R.id.ivDismiss)         ImageView ivDismiss;

    /*enum Scene { SCENE_1, SCENE_2};
    Scene mScene = Scene.SCENE_1;*/

    TextView tvSignUp;
    FrameLayout flSignUp;

//    Transition transitionMgr;
    ViewGroup rootContainer;
    private Scene mScene1;
    private Scene mScene2;
    private Scene mScene;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_after_on_boarding);
        ButterKnife.bind(this);

        rootContainer = (ViewGroup) findViewById(R.id.scene_root);
        ivDismiss.bringToFront();

        mScene1 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_one, this);

        mScene2 = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_sign_in_two, this);

        mScene = mScene1;
        mScene.enter();

        validateView();
    }

    private void validateView(){
        if(mScene == mScene1) {
            tvSignUp = (TextView) findViewById(R.id.tvSignUp);
            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSignUp();
                }
            });
        } else if (mScene == mScene2) {
            flSignUp = (FrameLayout) findViewById(R.id.tvSignUp);
            flSignUp.setOnClickListener(new View.OnClickListener() {
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

    @OnClick(R.id.tvSignIn) void onClickSignIn() {
        Toast.makeText(this, "sign in ", Toast.LENGTH_SHORT).show();

    }

    @Optional
    @OnClick(R.id.ivDismiss) void onClickDismiss() {
        finishActivity();
    }


    @Optional
    @OnClick(R.id.flFb) void onClickFb() {
        Toast.makeText(this, "fb", Toast.LENGTH_SHORT).show();
    }


    @Optional
    @OnClick(R.id.flGoogle) void onClickGoogle() {
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
