package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.InterestRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-31.
 */
public class InterestedStoreActivity extends AppCompatActivity {
    private final int MAX_SPAN = 3;
    protected final Logger logger = new Logger(getClass().getName());
    private InterestRecyclerViewAdapter mInterestRecyclerViewAdapter;
    private RecyclerView rvIntrstCat;
    private FrameLayout flIntrstdBot;
    private TextView tvIntrstd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrstd_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_interested_store));

        rvIntrstCat = (RecyclerView) findViewById(R.id.rvIntrstCat);
        flIntrstdBot = (FrameLayout) findViewById(R.id.flIntrstdBot);

        tvIntrstd = (TextView) findViewById(R.id.tvIntrstd);
        tvIntrstd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
                pb.setVisibility(View.VISIBLE);
                tvIntrstd.setVisibility(View.GONE);
                KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(InterestedStoreActivity.this, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        tvIntrstd.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                pb.setVisibility(View.GONE);

                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:

                                ArrayList<String> savedStoreLikeList = FavouriteManager.getInstance(InterestedStoreActivity.this).getInterestedStoreList();
                                ArrayList<String> newStoreLikeList = mInterestRecyclerViewAdapter.getFavStoreLikeLinkList();
                                if(!KcpUtility.isTwoStringListsEqual(savedStoreLikeList, newStoreLikeList)){
                                    FavouriteManager.getInstance(InterestedStoreActivity.this).cacheInterestedStoreList(mInterestRecyclerViewAdapter.getFavStoreLikeLinkList());
                                    setResult(Constants.RESULT_DONE_PRESSED_WITH_CHANGE, new Intent());
                                } else {
                                    setResult(Constants.RESULT_DONE_PRESSED_WITHOUT_CHANGE, new Intent());
                                }
                                onFinish();
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });

                kcpCategoryManager.postInterestedStores(mInterestRecyclerViewAdapter.getFavStoreLikeLinkList());
            }
        });
        setupRecyclerView(rvIntrstCat);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ArrayList<KcpPlaces> kcpPlacesArrayList = KcpCategoryRoot.getInstance().getPlacesList(KcpPlaces.PLACE_TYPE_STORE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MAX_SPAN);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mInterestRecyclerViewAdapter.getItemViewType(position)){
                    case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_STORES:
                        return MAX_SPAN;
                    case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_STORES:
                        return MAX_SPAN;
                    default:
                        return 1;
                }
            }
        });

        mInterestRecyclerViewAdapter = new InterestRecyclerViewAdapter(this, kcpPlacesArrayList, new InterestedCategoryActivity.ItemClickListener() {
            @Override
            public void onItemClick(boolean isListEmpty) {
                InterestedCategoryActivity.setUpCTA(InterestedStoreActivity.this, isListEmpty, flIntrstdBot, tvIntrstd);
            }
        });
        recyclerView.setAdapter(mInterestRecyclerViewAdapter);
        InterestedCategoryActivity.setUpCTA(InterestedStoreActivity.this, kcpPlacesArrayList.size() == 0, flIntrstdBot, tvIntrstd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_interested_cat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reset:
                mInterestRecyclerViewAdapter.resetFavStoreLikeLinkList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkIfNotSaved(new AlertDialogForInterest.DialogAnsweredListener() {
            @Override
            public void okClicked() {
                setResult(Constants.RESULT_EXIT, new Intent());
                onFinish();
            }
        });
    }

    public void onFinish(){
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }


    public void checkIfNotSaved(final AlertDialogForInterest.DialogAnsweredListener dialogAnsweredListener){
        ArrayList<String> savedStoreLikeList = FavouriteManager.getInstance(this).getInterestedStoreList();
        ArrayList<String> newStoreLikeList = mInterestRecyclerViewAdapter.getFavStoreLikeLinkList();
        if(!KcpUtility.isTwoStringListsEqual(savedStoreLikeList, newStoreLikeList)){
            AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
            alertDialogForInterest.getAlertDialog(
                    this,
                    R.string.title_unsaved_changes,
                    R.string.warning_exit_interest,
                    R.string.action_exit,
                    R.string.action_cancel,
                    dialogAnsweredListener).show();
        } else {
            dialogAnsweredListener.okClicked();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
            if (resultCode != 0) {
                setResult(resultCode, new Intent());
                onBackPressed();
            }
        }
    }
}
