package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.NewsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.fragments.DealsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.interfaces.FavouriteInterface;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.DealRecyclerItemDecoration;
import com.kineticcafe.kcpmall.views.NewsRecyclerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-07-05.
 */
public class MyPagesActivity extends AppCompatActivity implements FavouriteInterface {
    protected final Logger logger = new Logger(getClass().getName());

    private final int COLUMN_COUNT = 2;
    private String mPageTitle;
    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;
    private boolean mIsDecorationAdded = false;

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
    }


    private void setUpRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mPageTitle.equals(getResources().getString(R.string.my_page_deals))) {
                    if(FavouriteManager.getInstance(MyPagesActivity.this).getFavDealContentPages().size() == 0) {
                        setUpEmptyPlaceHolder(R.drawable.icn_empty_deals, getResources().getString(R.string.empty_placeholder_desc_deal), true);
                        return;
                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                    staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                    rv.setLayoutManager(staggeredGridLayoutManager);

                    DealsRecyclerViewAdapter dealsRecyclerViewAdapter = new DealsRecyclerViewAdapter (
                            MyPagesActivity.this,
                            false,
                            FavouriteManager.getInstance(MyPagesActivity.this).getFavDealContentPages(),
                            null);
                    dealsRecyclerViewAdapter.setFavouriteListener(MyPagesActivity.this);
                    rv.setAdapter(dealsRecyclerViewAdapter);
                    dealsRecyclerViewAdapter.addFooter(getString(R.string.explore_more_deals), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFinish(Constants.RESULT_DEALS);
                        }
                    });

                    if(!mIsDecorationAdded){
                        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(MyPagesActivity.this, R.dimen.card_vertical_margin, dealsRecyclerViewAdapter);
                        rv.addItemDecoration(itemDecoration);
                    }

                    mAdapter = dealsRecyclerViewAdapter;

                } else if(mPageTitle.equals(getResources().getString(R.string.my_page_events))) {
                    if(FavouriteManager.getInstance(MyPagesActivity.this).getFavEventContentPages().size() == 0) {
                        setUpEmptyPlaceHolder(R.drawable.icn_empty_events, getResources().getString(R.string.empty_placeholder_desc_event), true);
                        return;
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPagesActivity.this);
                    rv.setLayoutManager(linearLayoutManager);
                    NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter (
                            MyPagesActivity.this,
                            FavouriteManager.getInstance(MyPagesActivity.this).getFavEventContentPages());
                    newsRecyclerViewAdapter.setFavouriteListener(MyPagesActivity.this);
                    rv.setAdapter(newsRecyclerViewAdapter);
                    newsRecyclerViewAdapter.addFooter(getString(R.string.explore_more_events), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFinish(Constants.RESULT_EVENTS);
                        }
                    });

                    if(!mIsDecorationAdded){
                        NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(MyPagesActivity.this, R.dimen.card_vertical_margin);
                        rv.addItemDecoration(itemDecoration);
                    }

                    mAdapter = newsRecyclerViewAdapter;
                } else if(mPageTitle.equals(getResources().getString(R.string.my_page_stores))) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                    staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

                    ArrayList<KcpContentPage> kcpContentPages = FavouriteManager.getInstance(MyPagesActivity.this).getFavStoreContentPages();
                    ArrayList<KcpPlaces> kcpPlaces = new ArrayList<>();
                    for(int i = 0; i < kcpContentPages.size(); i++){
                        kcpPlaces.add(kcpContentPages.get(i).getStore());
                    }

                    if(kcpPlaces.size() == 0) {
                        setUpEmptyPlaceHolder(R.drawable.icn_empty_stores, getResources().getString(R.string.empty_placeholder_desc_store), true);
                        return;
                    }

                    rv.setLayoutManager(staggeredGridLayoutManager);
                    CategoryStoreRecyclerViewAdapter categoryStoreRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                            MyPagesActivity.this,
                            kcpPlaces, KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE);
                    categoryStoreRecyclerViewAdapter.setFavouriteListener(MyPagesActivity.this);
                    rv.setAdapter(categoryStoreRecyclerViewAdapter);
                    categoryStoreRecyclerViewAdapter.addFooter(getString(R.string.explore_more_stores), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFinish(Constants.RESULT_STORES);
                        }
                    });

                    if(!mIsDecorationAdded){
                        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(MyPagesActivity.this, R.dimen.card_vertical_margin, categoryStoreRecyclerViewAdapter);
                        rv.addItemDecoration(itemDecoration);
                    }


                    mAdapter = categoryStoreRecyclerViewAdapter;
                } else if(mPageTitle.equals(getResources().getString(R.string.my_page_deals_for_today))) {

                    ArrayList<KcpContentPage> todaysDealList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageListForToday(true);

                    if(todaysDealList == null || todaysDealList.size() == 0) {
                        setUpEmptyPlaceHolder(R.drawable.icn_empty_deals, getResources().getString(R.string.warning_no_deal_for_today), false);
                        return;
                    } else {
                        KcpUtility.sortKcpContentPageByStoreName(todaysDealList);
                    }

                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                    staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                    rv.setLayoutManager(staggeredGridLayoutManager);

                    DealsRecyclerViewAdapter dealsRecyclerViewAdapter = new DealsRecyclerViewAdapter (
                            MyPagesActivity.this,
                            false,
                            todaysDealList,
                            null);

//                    dealsRecyclerViewAdapter.setFavouriteListener(MyPagesActivity.this);
                    rv.setAdapter(dealsRecyclerViewAdapter);
                    dealsRecyclerViewAdapter.addFooter(getString(R.string.explore_more_deals), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFinish(Constants.RESULT_DEALS);
                        }
                    });

                    if(!mIsDecorationAdded){
                        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(MyPagesActivity.this, R.dimen.card_vertical_margin, dealsRecyclerViewAdapter);
                        rv.addItemDecoration(itemDecoration);
                    }

                    mAdapter = dealsRecyclerViewAdapter;
                } else if(mPageTitle.equals(getResources().getString(R.string.my_page_events_for_today))){

                    ArrayList<KcpContentPage> todaysEventList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_FEED).getKcpContentPageListForToday(true);


                    if(todaysEventList == null || todaysEventList.size() == 0) {
                        setUpEmptyPlaceHolder(R.drawable.icn_empty_events, getResources().getString(R.string.warning_no_event_for_today), false);
                        return;
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPagesActivity.this);
                    rv.setLayoutManager(linearLayoutManager);
                    NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter (
                            MyPagesActivity.this,
                            todaysEventList);

//                    newsRecyclerViewAdapter.setFavouriteListener(MyPagesActivity.this);
                    rv.setAdapter(newsRecyclerViewAdapter);
                    newsRecyclerViewAdapter.addFooter(getString(R.string.explore_more_events), R.layout.list_item_my_page_footer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFinish(Constants.RESULT_EVENTS);
                        }
                    });

                    if(!mIsDecorationAdded){
                        NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(MyPagesActivity.this, R.dimen.card_vertical_margin);
                        rv.addItemDecoration(itemDecoration);
                    }

                    mAdapter = newsRecyclerViewAdapter;
                }

                if(!mIsDecorationAdded) mIsDecorationAdded = true;
            }
        });

    }

    private void setUpEmptyPlaceHolder(final int drawable, final String text, final boolean showTitle){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rv.setVisibility(View.GONE);

                final AppBarLayout ablTop = (AppBarLayout)findViewById(R.id.ablTop);
                ablTop.setExpanded(true);

                LinearLayout llPlaceholder = (LinearLayout) findViewById(R.id.llPlaceholder);
                ImageView ivPlaceholder = (ImageView) findViewById(R.id.ivPlaceholder);
                TextView tvPlaceholderDesc = (TextView) findViewById(R.id.tvPlaceholderDesc);
                TextView tvPlaceHolderTitle = (TextView) findViewById(R.id.tvPlaceHolderTitle);
                if(!showTitle) tvPlaceHolderTitle.setVisibility(View.GONE);
                llPlaceholder.setVisibility(View.VISIBLE);
                ivPlaceholder.setImageResource(drawable);
                tvPlaceholderDesc.setText(text);
            }
        });
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

    public void onFinish(int resultCode){
        setResult(resultCode, new Intent());
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityAnimation.exitActivityAnimation(this);
    }

    @Override
    public void didChangeFavourite() {
        setUpRecyclerView();
    }
}
