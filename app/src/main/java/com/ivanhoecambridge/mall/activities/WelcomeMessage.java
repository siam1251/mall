package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

/**
 * Created by Kay on 2016-08-15.
 */
public class WelcomeMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_message);

        TextView tvWelcomeYes = (TextView) findViewById(R.id.tvWelcomeYes);
        tvWelcomeYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(WelcomeMessage.this, ParkingActivity.class), Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
                onFinish();

            }
        });
        TextView tvWelcomeNo = (TextView) findViewById(R.id.tvWelcomeNo);
        tvWelcomeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });
        TextView tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        String welcomeTitle = getString(R.string.welcome_message) + " " + getString(R.string.mall_name) + "!";
        tvWelcomeTitle.setText(welcomeTitle);
    }

    @Override
    public void onBackPressed() {
        onFinish();
    }

    public void onFinish(){
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
