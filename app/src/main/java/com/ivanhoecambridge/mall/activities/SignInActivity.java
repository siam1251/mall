package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kay on 2017-01-27.
 */

public class SignInActivity extends BaseActivity {

    /*@BindView(R.id.tilFirst) TextInputLayout tilFirst;
    @BindView(R.id.tilSecond) TextInputLayout tilSecond;*/

    ViewGroup rootContainer;
    private Scene mScene1;
    private Scene mScene2;
    private Scene mScene;

    TextView tvSignIn;




    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.signin_sign_in));

        rootContainer = (ViewGroup) findViewById(R.id.scene_root);

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
            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene();
                }
            });
            getSupportActionBar().setTitle(getString(R.string.signin_sign_in));
        } else if (mScene == mScene2) {
            tvSignIn = (TextView) findViewById(R.id.tvSignIn);
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeScene();
                }
            });
            getSupportActionBar().setTitle(getString(R.string.signin_sign_up));
        }

        /*tilFirst.setErrorEnabled(true);
        tilSecond.setErrorEnabled(true);*/

    }

    private void changeScene(){
        if(mScene == mScene1) mScene = mScene2;
        else if(mScene == mScene2) mScene = mScene1;
        TransitionManager.go(mScene);
        ButterKnife.bind(this);
        validateView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishActivity() {
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

}
