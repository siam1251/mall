package com.kineticcafe.kcpmall;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashActivity extends AppCompatActivity {

    private Thread mSplashThread;
    private final static int MSG_CONTINUE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        mHandler.sendEmptyMessageDelayed(MSG_CONTINUE, Constants.DURATION_SPLASH_ANIMATION);


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

    /*private final Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch(msg.what){
                case MSG_CONTINUE:
                    finish();
                    overridePendingTransition(R.anim.splashfadein,
                            R.anim.splashfadeout);

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    break;
            }
        }
    };*/

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
