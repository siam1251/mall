package com.kineticcafe.kcpmall.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.constants.Constants;

public class SplashActivity extends AppCompatActivity {
    private Thread mSplashThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView ivSplash = (ImageView) findViewById(R.id.ivSplash);


        /*Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.splash_fake, R.anim.splash_fake);
        finish();*/


        /*
        Animation animation = new AlphaAnimation( 1.0f, 1.0f );
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.splash_fake, R.anim.splash_fake);
                finish();
            }

            public void onAnimationRepeat(Animation animation) {}

            public void onAnimationStart(Animation animation) {}

        });
        animation.setDuration(1200);
        ivSplash.startAnimation(animation);*/


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

        /*mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(Constants.DURATION_SPLASH_ANIMATION); // 1700 + 500 + 500
                    }
                }
                catch(InterruptedException ex){
                }
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.splash_fake, R.anim.splash_fake);
                finish();
            }
        };

        mSplashThread.start();*/


    }
}