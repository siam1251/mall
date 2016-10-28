package com.ivanhoecambridge.mall.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;

/**
 * Created by Kay on 2016-10-28.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private int mMovieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Bundle bundle = getIntent().getExtras();
        mMovieId = bundle.getInt(Constants.ARG_MOVIE_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.movie_detail_title));

    }
}
