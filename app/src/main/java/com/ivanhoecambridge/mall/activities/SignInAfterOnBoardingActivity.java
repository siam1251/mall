package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.signup.AuthenticationManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.janrain.android.Jump;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kay on 2017-01-26.
 */

public class SignInAfterOnBoardingActivity extends BaseActivity implements AuthenticationManager.onJanrainAuthenticateListener {

    CoordinatorLayout clSignIn;
    CardView cvSignUpInFirstScene;
    CardView cvSignUpInSecondScene;
    ViewGroup rootContainer;
    private Scene sceneSignUp;
    private Scene sceneSignUpSocial;
    private Scene activeScene;
    private AuthenticationManager authenticationManager;

    //These can't be bound by ButterKnife as they don't exist in the main layout.
    //Would require to be bound to a class, which is then bound to the main view.
    private CardView cvFacebook;
    private CardView cvGooglePlus;
    @BindView(R.id.tvError)
    TextView tvError;
    @BindView(R.id.ivDismiss)
    ImageView ivDismiss;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_after_on_boarding);
        ButterKnife.bind(this);

        clSignIn = (CoordinatorLayout) findViewById(R.id.clSignIn);
        int id = getResources().getIdentifier("img_signup_bg", "drawable", getPackageName());
        if(id != 0 && id != -1) {
            Drawable bgDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
            clSignIn.setBackground(bgDrawable);
        } else clSignIn.setBackgroundColor(Color.parseColor("#454545"));

        rootContainer = (ViewGroup) findViewById(R.id.scene_root);

        sceneSignUp = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_boarding_sign_in_one, this);

        sceneSignUpSocial = Scene.getSceneForLayout(rootContainer,
                R.layout.transition_boarding_sign_in_two, this);

        activeScene = sceneSignUp;
        activeScene.enter();

        validateView();

        authenticationManager = new AuthenticationManager(this, new Handler(Looper.getMainLooper()));
    }

    private void toggleProviderVisibility() {
        cvFacebook.setVisibility(authenticationManager.isProviderEnabled(AuthenticationManager.PROVIDER_FB) ? View.VISIBLE : View.GONE);
        cvGooglePlus.setVisibility(authenticationManager.isProviderEnabled(AuthenticationManager.PROVIDER_GOOGLE) ? View.VISIBLE : View.GONE);
    }

    private void validateView(){
        if(activeScene == sceneSignUp) {
            cvSignUpInFirstScene = (CardView) findViewById(R.id.cvSignUp);
            cvSignUpInFirstScene.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSignUp();
                }
            });
            ivDismiss.setVisibility(View.VISIBLE);
        } else if (activeScene == sceneSignUpSocial) {
            ivDismiss.setVisibility(View.GONE);
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
        if(activeScene == sceneSignUp) changeScene();
        else {
            onClickEmail();
        }
    }

    @OnClick(R.id.tvError)
    public void onClickErrorNotification() {
        setErrorNotificationMessage(null, false);
    }

    @OnClick(R.id.ivDismiss) void onClickDismiss() {
        finishActivity();
    }

    @OnClick(R.id.tvSignIn) void onClickSignIn() {
        if (getCallingActivity() == null) {
            startActivity(createSignInIntent());
        } else {
            startActivityForResult(createSignInIntent(), Constants.REQUEST_CODE_SIGN_UP_SIGN_IN);
        }
        ActivityAnimation.startActivityAnimation(SignInAfterOnBoardingActivity.this);
    }

    @Optional
    @OnClick(R.id.cvFb) void onClickFb() {
        authenticationManager.authenticate(this, AuthenticationManager.PROVIDER_FB);
    }


    @Optional
    @OnClick(R.id.cvGoogle) void onClickGoogle() {
        authenticationManager.authenticate(this, AuthenticationManager.PROVIDER_GOOGLE);
    }


    void onClickEmail() {
        if (getCallingActivity() == null) {
            startActivity(createSignInIntent().putExtra(Constants.KEY_ACTIVE_SCENE_ORDER, SignInActivity.SIGNUP_SCENE));
        } else {
            startActivityForResult(createSignInIntent().putExtra(Constants.KEY_ACTIVE_SCENE_ORDER, SignInActivity.SIGNUP_SCENE), Constants.REQUEST_CODE_SIGN_UP_SIGN_IN);
        }
        ActivityAnimation.startActivityAnimation(this);
    }

    private Intent createSignInIntent() {
        return new Intent(this, SignInActivity.class);
    }

    private void changeScene(){
        if(activeScene == sceneSignUp) activeScene = sceneSignUpSocial;
        else if(activeScene == sceneSignUpSocial) activeScene = sceneSignUp;
        TransitionManager.go(activeScene);
        ButterKnife.bind(this);
        validateView();
    }

    private void finishActivity() {
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }
    @Override
    public void onBackPressed() {
        if(activeScene == sceneSignUpSocial) {
            changeScene();
            if (ProgressBarWhileDownloading.isShowing()) {
                setProgressIndicator(false);
                authenticationManager.removeSocketTimeOutCallback();
            }
        }
        else finishActivity();
    }

    @Override
    public void onPause() {
        Jump.saveToDisk(this);
        authenticationManager.removeSocketTimeOutCallback();
        super.onPause();
    }


    @Override
    public void onAuthenticateRequest(String provider) {
        setProgressIndicator(true);
    }

    @Override
    public void onAuthenticateSuccess() {
        setResult(RESULT_OK);
        setProgressIndicator(false);
        finishActivity();
    }

    @Override
    public void onAuthenticateFailure(AuthenticationManager.ERROR_REASON errorReason, String rawError,  String provider) {
        setResult(Constants.RESULT_FAILED);
        setProgressIndicator(false);
        setErrorNotificationMessage(getErrorMessage(errorReason), true);
    }

    private void setProgressIndicator(boolean shouldShowProgress) {
        ProgressBarWhileDownloading.showProgressDialog(this, R.layout.layout_loading_item, shouldShowProgress);
    }

    private String getErrorMessage(AuthenticationManager.ERROR_REASON errorReason) {
        switch (errorReason) {
            case CANCELLED:
                return KcpUtility.getString(this, R.string.signin_error_cancelled);
            case INVALID_CREDENTIALS:
                return KcpUtility.getString(this, R.string.signin_error_invalid_credentials);
            case UNKNOWN:
            default:
                return KcpUtility.getString(this, R.string.signin_error_unknown);
        }
    }

    /**
     * Sets the error message for the notification that will be displayed at the top.
     * @param error Error message to display.
     * @param shouldDisplay Visibility toggle. true if it should be shown, false if hidden.
     */
    private void setErrorNotificationMessage(@Nullable String error, boolean shouldDisplay) {
        if (error != null) tvError.setText(error);
        animateErrorMessage(shouldDisplay);
        if (shouldDisplay) {
            tvError.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.GONE);
        }
    }

    private void animateErrorMessage(boolean isAppearing) {
        Animation slideDirection = AnimationUtils.loadAnimation(this, isAppearing ? R.anim.anim_slide_down : R.anim.anim_slide_up);
        tvError.startAnimation(slideDirection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_SIGN_UP_SIGN_IN) {
            setResult(resultCode);
            finishActivity();
        }
    }
}
