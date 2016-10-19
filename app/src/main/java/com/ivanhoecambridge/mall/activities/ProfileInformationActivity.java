package com.ivanhoecambridge.mall.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.InterestRecyclerViewAdapter;

/**
 * Created by Kay on 2016-05-31.
 */
public class ProfileInformationActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private InterestRecyclerViewAdapter mInterestRecyclerViewAdapter;
    private RecyclerView rvIntrstCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrstd_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_interested_category));



    }
}
