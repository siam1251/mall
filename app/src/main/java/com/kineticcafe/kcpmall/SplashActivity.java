package com.kineticcafe.kcpmall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashActivity extends AppCompatActivity {

    private Thread mSplashThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashThread = new Thread(){
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
                overridePendingTransition(R.anim.splashfadein, R.anim.splashfadeout);
                finish();
            }
        };

        mSplashThread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }
}
