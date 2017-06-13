package com.ivanhoecambridge.mall.activities;

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

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.AncmtRecyclerViewAdapter;
import com.ivanhoecambridge.mall.adapters.CategoryStoreRecyclerViewAdapter;
import com.ivanhoecambridge.mall.adapters.EventRecyclerViewAdapter;
import com.ivanhoecambridge.mall.adapters.NewsRecyclerViewAdapter;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.adapters.DealsRecyclerViewAdapter;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.DealRecyclerItemDecoration;
import com.ivanhoecambridge.mall.views.NewsRecyclerItemDecoration;

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

        setUpRecyclerView();
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


                    ArrayList<KcpContentPage> dealContentPages =  FavouriteManager.getInstance(MyPagesActivity.this).getFavDealContentPages();
                    KcpUtility.sortKcpContentpageByExpiryDate(dealContentPages);

                    DealsRecyclerViewAdapter dealsRecyclerViewAdapter = new DealsRecyclerViewAdapter (
                            MyPagesActivity.this,
                            false,
                            dealContentPages,
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

                    ArrayList<KcpContentPage> favEventPages =  FavouriteManager.getInstance(MyPagesActivity.this).getFavEventContentPages();
                    KcpUtility.sortKcpContentpageByExpiryDate(favEventPages);

                    NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter (
                            MyPagesActivity.this,
                            favEventPages
                            );
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
                    KcpUtility.sortPlaceList(kcpPlaces);
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
                } else if(mPageTitle.equals(getResources().getString(R.string.my_page_all_ancmt))){
                    ArrayList<KcpContentPage> todaysEventList = (ArrayList<KcpContentPage>) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);

                    if(todaysEventList != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPagesActivity.this);
                        rv.setLayoutManager(linearLayoutManager);
                        AncmtRecyclerViewAdapter newsRecyclerViewAdapter = new AncmtRecyclerViewAdapter (
                                MyPagesActivity.this,
                                todaysEventList,
                                false);
                        rv.setAdapter(newsRecyclerViewAdapter);
                        mAdapter = newsRecyclerViewAdapter;
                    }
                } else if(mPageTitle.equals(getResources().getString(R.string.my_page_all_event))){
                    ArrayList<KcpContentPage> todaysEventList = (ArrayList<KcpContentPage>) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);
                    if(todaysEventList != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPagesActivity.this);
                        rv.setLayoutManager(linearLayoutManager);
                        EventRecyclerViewAdapter newsRecyclerViewAdapter = new EventRecyclerViewAdapter (
                                MyPagesActivity.this,
                                todaysEventList,
                                true,
                                null);
                        rv.setAdapter(newsRecyclerViewAdapter);
                        newsRecyclerViewAdapter.setFavouriteListener(MyPagesActivity.this);
                        mAdapter = newsRecyclerViewAdapter;
                    }
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
        Analytics.getInstance(this).logScreenView(this, mPageTitle + " Screen");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
                if(data == null) {
                } else {
                    int code = data.getIntExtra(Constants.REQUEST_CODE_KEY, 0);
                    if(code == Constants.REQUEST_CODE_SHOW_PARKING_SPOT){
                        String parkingName = data.getStringExtra(Constants.REQUEST_CODE_KEY_PARKING_NAME);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.REQUEST_CODE_KEY, Constants.REQUEST_CODE_SHOW_PARKING_SPOT);
                        intent.putExtra(Constants.REQUEST_CODE_KEY_PARKING_NAME, parkingName);
                        setResult(Integer.valueOf(resultCode), intent);
                        onBackPressed();
                    } else if(code == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP){
                        Intent intent = new Intent();
                        intent.putExtra(Constants.REQUEST_CODE_KEY, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                        setResult(Integer.valueOf(resultCode), intent);
                        onBackPressed();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
