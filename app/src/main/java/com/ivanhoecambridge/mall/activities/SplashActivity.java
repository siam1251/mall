package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.onboarding.TutorialActivity;

public class SplashActivity extends AppCompatActivity {

    private Thread mSplashThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView ivSplash = (ImageView) findViewById(R.id.ivSplash);

        Animation animation = new AlphaAnimation( 0.0f, 1.0f );
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.splash_fake, R.anim.splash_fake);
                finish();
            }
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

        });
        animation.setDuration(Constants.DURATION_MINIMUM_SPLASH_ANIMATION);
        ivSplash.startAnimation(animation);
    }
}