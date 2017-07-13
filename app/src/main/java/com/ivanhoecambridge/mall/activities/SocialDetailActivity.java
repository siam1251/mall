package com.ivanhoecambridge.mall.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.SocialFeedDetailRecyclerViewAdapter;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.HomeFragment;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import constants.MallConstants;

/**
 * Created by Kay on 2016-07-05.
 */
public class SocialDetailActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private int mItemType;
    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_sub_category);

        Bundle bundle = getIntent().getExtras();
        mItemType = bundle.getInt(Constants.ARG_ACTIVITY_TYPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        String pageTitle = "";

        if (mItemType == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM){
            ImageView icn_share = (ImageView) toolbar.findViewById(R.id.icn_share);
            icn_share.setVisibility(View.VISIBLE);
            icn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Utility.isAppInstalled(SocialDetailActivity.this, MallConstants.INSTAGRAM_PACKAGE_NAME)){
                        Utility.openInstagramWithDialog(SocialDetailActivity.this,
                                getResources().getString(R.string.title_open_instagram_app),
                                getResources().getString(R.string.warning_open_instagram_app),
                                getResources().getString(R.string.action_ok),
                                getResources().getString(R.string.action_no_thanks),
                                MallConstants.INSTAGRAM_USER_NAME);
                    } else {
                        Utility.openInstagramWithDialog(SocialDetailActivity.this,
                                getResources().getString(R.string.title_open_instagram_website),
                                getResources().getString(R.string.warning_open_instagram_website),
                                getResources().getString(R.string.action_ok),
                                getResources().getString(R.string.action_no_thanks),
                                MallConstants.INSTAGRAM_USER_NAME);
                    }
                }
            });
            pageTitle = "@" + MallConstants.INSTAGRAM_USER_NAME;
        } else if (mItemType == KcpContentTypeFactory.ITEM_TYPE_TWITTER) {
            pageTitle = "@" + MallConstants.TWITTER_SCREEN_NAME;
            toolbar.setBackgroundColor(getResources().getColor(R.color.twitter_theme_color));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        getSupportActionBar().setTitle(pageTitle);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setNestedScrollingEnabled(true);
        rv.setBackgroundColor(Color.WHITE);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        if (mItemType == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rv.setLayoutManager(linearLayoutManager);
            SocialFeedDetailRecyclerViewAdapter socialFeedDetailRecyclerViewAdapter = new SocialFeedDetailRecyclerViewAdapter (
                    this,
                    HomeFragment.sInstaFeedList, null);
            rv.setAdapter(socialFeedDetailRecyclerViewAdapter);
        } else if(mItemType == KcpContentTypeFactory.ITEM_TYPE_TWITTER) {
            rv.setVisibility(View.GONE);
            ListView lvTwitter = (ListView) findViewById(R.id.lvTwitter);
            lvTwitter.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lvTwitter.setNestedScrollingEnabled(true);
            }

            final UserTimeline userTimeline = new UserTimeline.Builder()
                    .screenName(MallConstants.TWITTER_SCREEN_NAME)
                    .build();

            final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                    .setTimeline(userTimeline)
                    .build();

            lvTwitter.setAdapter(adapter);

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

        if (mItemType == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM) {
            Analytics.getInstance(this).logScreenView(this, "Instagram Feed Screen");
        } else if (mItemType == KcpContentTypeFactory.ITEM_TYPE_TWITTER) {
            Analytics.getInstance(this).logScreenView(this, "Twitter Feed Screen");
        }
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
