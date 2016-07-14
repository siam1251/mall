package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.NewsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.fragments.DealsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.DealRecyclerItemDecoration;
import com.kineticcafe.kcpmall.views.NewsRecyclerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-07-05.
 */
public class MyPagesActivity extends AppCompatActivity {
    protected final Logger logger = new Logger(getClass().getName());

    private final int COLUMN_COUNT = 2;
    private String mPageTitle;
    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Bundle bundle = getIntent().getExtras();
        mPageTitle = bundle.getString(Constants.ARG_CAT_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(mPageTitle);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setNestedScrollingEnabled(true);

        setUpRecyclerView();
    }


    private void setUpRecyclerView() {
        if(mPageTitle.equals(getResources().getString(R.string.my_page_deals))) {

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            rv.setLayoutManager(staggeredGridLayoutManager);

            DealsRecyclerViewAdapter dealsRecyclerViewAdapter = new DealsRecyclerViewAdapter (
                    this,
                    false,
                    FavouriteManager.getInstance(this).getFavDealContentPages(),
                    null);
            rv.setAdapter(dealsRecyclerViewAdapter);
            dealsRecyclerViewAdapter.addFooter(getString(R.string.explore_more_deals), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFinish(Constants.RESULT_DEALS);
                }
            });

            DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(this, R.dimen.card_vertical_margin, dealsRecyclerViewAdapter);
            rv.addItemDecoration(itemDecoration);

            mAdapter = dealsRecyclerViewAdapter;

        } else if(mPageTitle.equals(getResources().getString(R.string.my_page_events))) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rv.setLayoutManager(linearLayoutManager);
            NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter (
                    this,
                    FavouriteManager.getInstance(this).getFavEventContentPages());
            rv.setAdapter(newsRecyclerViewAdapter);
            newsRecyclerViewAdapter.addFooter(getString(R.string.explore_more_events), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFinish(Constants.RESULT_EVENTS);
                }
            });

            NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(this, R.dimen.card_vertical_margin);
            rv.addItemDecoration(itemDecoration);

            mAdapter = newsRecyclerViewAdapter;
        } else if(mPageTitle.equals(getResources().getString(R.string.my_page_stores))) {

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            rv.setLayoutManager(staggeredGridLayoutManager);

            ArrayList<KcpContentPage> kcpContentPages = FavouriteManager.getInstance(this).getFavStoreContentPages();
            ArrayList<KcpPlaces> kcpPlaces = new ArrayList<>();
            for(int i = 0; i < kcpContentPages.size(); i++){
                kcpPlaces.add(kcpContentPages.get(i).getStore());
            }

            CategoryStoreRecyclerViewAdapter categoryStoreRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                    this,
                    kcpPlaces, KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE);
            rv.setAdapter(categoryStoreRecyclerViewAdapter);
            categoryStoreRecyclerViewAdapter.addFooter(getString(R.string.explore_more_stores), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFinish(Constants.RESULT_STORES);
                }
            });

            DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(this, R.dimen.card_vertical_margin, categoryStoreRecyclerViewAdapter);
            rv.addItemDecoration(itemDecoration);

            mAdapter = categoryStoreRecyclerViewAdapter;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    public void onFinish(int resultCode){
        setResult(resultCode, new Intent());
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
