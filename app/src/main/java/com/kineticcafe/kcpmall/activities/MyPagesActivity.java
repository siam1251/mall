package com.kineticcafe.kcpmall.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
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
import com.kineticcafe.kcpmall.widget.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-07-05.
 */
public class MyPagesActivity extends AppCompatActivity {
    protected final Logger logger = new Logger(getClass().getName());

    private final int COLUMN_COUNT = 2;
    private String mPageTitle;
    private RecyclerView rvNews;

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

        rvNews = (RecyclerView) findViewById(R.id.rv);
        rvNews.setNestedScrollingEnabled(true);
    }


    private void setUpRecyclerView() {
        if(mPageTitle.equals(getResources().getString(R.string.my_page_deals))) {

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            rvNews.setLayoutManager(staggeredGridLayoutManager);

            DealsRecyclerViewAdapter dealsRecyclerViewAdapter = new DealsRecyclerViewAdapter(
                    this,
                    false,
                    FavouriteManager.getInstance(this).getFavDealContentPages(this),
                    null);
            rvNews.setAdapter(dealsRecyclerViewAdapter);

            DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(this, R.dimen.card_vertical_margin, dealsRecyclerViewAdapter);
            rvNews.addItemDecoration(itemDecoration);

        } else if(mPageTitle.equals(getResources().getString(R.string.my_page_events))) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvNews.setLayoutManager(linearLayoutManager);
            NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(
                    this,
                    FavouriteManager.getInstance(this).getFavEventContentPages(this));
            rvNews.setAdapter(newsRecyclerViewAdapter);

            NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(this, R.dimen.card_vertical_margin);
            rvNews.addItemDecoration(itemDecoration);
        } else if(mPageTitle.equals(getResources().getString(R.string.my_page_stores))) {

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            rvNews.setLayoutManager(staggeredGridLayoutManager);

            ArrayList<KcpContentPage> kcpContentPages = FavouriteManager.getInstance(this).getFavStoreContentPages(this);
            ArrayList<KcpPlaces> kcpPlaces = new ArrayList<>();
            for(int i = 0; i < kcpContentPages.size(); i++){
                kcpPlaces.add(kcpContentPages.get(i).getStore());
            }

            CategoryStoreRecyclerViewAdapter categoryStoreRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                    this,
                    kcpPlaces, KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE);
            rvNews.setAdapter(categoryStoreRecyclerViewAdapter);

            DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(this, R.dimen.card_vertical_margin, categoryStoreRecyclerViewAdapter);
            rvNews.addItemDecoration(itemDecoration);

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
        setUpRecyclerView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
