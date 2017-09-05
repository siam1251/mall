package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.signup.SignUpManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.janrain.android.Jump;
import com.janrain.android.engage.session.JRSession;
import com.twitter.sdk.android.core.models.Card;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kay on 2017-01-26.
 */

public class SignInAfterOnBoardingActivity extends BaseActivity implements SignUpManager.onSignUpListener{

    private final String PROVIDER_FB = "facebook";
    private final String PROVIDER_GOOGLE = "googleplus";

    RelativeLayout rlSignIn;
    CardView cvSignUpInFirstScene;
    CardView cvSignUpInSecondScene;
    ViewGroup rootContainer;
    private Scene mScene1;
    private Scene mScene2;
    private Scene mScene;
    private SignUpManager signUpManager;

    private CardView cvFacebook;
    private CardView cvGooglePlus;

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

        signUpManager = new SignUpManager(this);


    }

    private void toggleProviderVisibility() {
        cvFacebook.setVisibility(signUpManager.isProviderEnabled(PROVIDER_FB) ? View.VISIBLE : View.GONE);
        cvGooglePlus.setVisibility(signUpManager.isProviderEnabled(PROVIDER_GOOGLE) ? View.VISIBLE : View.GONE);
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
            cvFacebook = (CardView) findViewById(R.id.cvFb);
            cvGooglePlus = (CardView) findViewById(R.id.cvGoogle);
            toggleProviderVisibility();
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
        signUpManager.authenticate(this, "facebook");
    }


    @Optional
    @OnClick(R.id.cvGoogle) void onClickGoogle() {
        signUpManager.authenticate(this, "googleplus");
    }

    void onClickEmail() {

    }

    private JSONObject createFakeUser() {
        JSONObject fakeUser = new JSONObject();
        try {
            fakeUser.put("email", "fakeandroiduser@fakemail.com")
                    .put("displayName", "FakeUser")
                    .put("givenName", "Fake")
                    .put("familyName", "User")
                    .put("birthday", "1970-01-01")
                    .put("password", "password1");
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return fakeUser;
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

    @Override
    public void onPause() {
        Jump.saveToDisk(this);
        super.onPause();
    }


    @Override
    public void onSignUpRequest() {
        setProgressIndicator(true);
    }

    @Override
    public void onSignUpSuccess() {
        setProgressIndicator(false);
        finishActivity();
    }

    @Override
    public void onSignUpFailure(String error, String provider) {
        setProgressIndicator(false);
    }

    private void setProgressIndicator(boolean shouldShowProgress) {
        ProgressBarWhileDownloading.showProgressDialog(this, R.layout.layout_loading_item, shouldShowProgress);
    }


}
