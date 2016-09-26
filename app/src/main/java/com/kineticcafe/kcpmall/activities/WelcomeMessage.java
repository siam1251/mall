package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.parking.ChildParking;
import com.kineticcafe.kcpmall.parking.Parking;
import com.kineticcafe.kcpmall.parking.ParkingManager;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.util.List;

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
