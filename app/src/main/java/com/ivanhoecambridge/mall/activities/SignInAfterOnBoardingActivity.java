package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.signup.SignUpManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.janrain.android.Jump;

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

    private final String TAG = getClass().getSimpleName();

    CoordinatorLayout clSignIn;
    CardView cvSignUpInFirstScene;
    CardView cvSignUpInSecondScene;
    ViewGroup rootContainer;
    private Scene sceneSignUp;
    private Scene sceneSignUpSocial;
    private Scene activeScene;
    private SignUpManager signUpManager;

    //These can't be bound by ButterKnife as they don't exist in the main layout.
    //Would require to be bound to a class, which is then bound to the main view.
    private CardView cvFacebook;
    private CardView cvGooglePlus;
    @BindView(R.id.tvError)
    TextView tvError;
    @BindView(R.id.ivDismiss)
    ImageView ivDismiss;



    //Janrain can sometimes experience a socket timeout through AppAuth with Google+
    //since janrain swallows the exception and we aren't notified in any way I've added a handler to disable the progress and display an error
    //after a minute.
    private final long socketTimeoutTimer = 60000;
    private Handler timeHandler;
    private Runnable socketError = new Runnable() {
        @Override
        public void run() {
            setProgressIndicator(false);
            setErrorNotificationMessage(getString(R.string.signin_error_unknown), true);
        }
    };

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

        signUpManager = new SignUpManager(this);
        timeHandler = new Handler();
    }

    private void toggleProviderVisibility() {
        cvFacebook.setVisibility(signUpManager.isProviderEnabled(PROVIDER_FB) ? View.VISIBLE : View.GONE);
        cvGooglePlus.setVisibility(signUpManager.isProviderEnabled(PROVIDER_GOOGLE) ? View.VISIBLE : View.GONE);
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
        startActivity(new Intent(SignInAfterOnBoardingActivity.this, SignInActivity.class));
        ActivityAnimation.startActivityAnimation(SignInAfterOnBoardingActivity.this);
    }

    @Optional
    @OnClick(R.id.cvFb) void onClickFb() {
        signUpManager.authenticate(this, PROVIDER_FB);
    }


    @Optional
    @OnClick(R.id.cvGoogle) void onClickGoogle() {
        signUpManager.authenticate(this, PROVIDER_GOOGLE);
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
                timeHandler.removeCallbacks(socketError);
            }
        }
        else finishActivity();
    }

    @Override
    public void onPause() {
        Jump.saveToDisk(this);
        timeHandler.removeCallbacks(socketError);
        super.onPause();
    }


    @Override
    public void onSignUpRequest(String provider) {
        setProgressIndicator(true);
        if (provider.equals(PROVIDER_GOOGLE)) {
            timeHandler.postDelayed(socketError, socketTimeoutTimer);
        }
    }

    @Override
    public void onSignUpSuccess() {
        setProgressIndicator(false);
        finishActivity();
    }

    @Override
    public void onSignUpFailure(SignUpManager.ERROR_REASON errorReason, String provider) {
        setProgressIndicator(false);
        setErrorNotificationMessage(getErrorMessage(errorReason), true);
    }

    private void setProgressIndicator(boolean shouldShowProgress) {
        ProgressBarWhileDownloading.showProgressDialog(this, R.layout.layout_loading_item, shouldShowProgress);
    }

    private String getErrorMessage(SignUpManager.ERROR_REASON errorReason) {
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


}
