package com.ivanhoecambridge.mall.activities;

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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategoryRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.InterestRecyclerViewAdapter;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.managers.NetworkManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;

import java.util.ArrayList;
import java.util.HashMap;

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
                FavouriteManager.getInstance(InterestedStoreActivity.this).updateFavStore(mInterestRecyclerViewAdapter.getTempStoreMap(), mInterestRecyclerViewAdapter.getRemovedStoreMap(), true, new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                if(NetworkManager.isConnected(InterestedStoreActivity.this)) return;
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                setResult(Constants.RESULT_DONE_PRESSED_WITH_CHANGE, new Intent());
                                onFinish();
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
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
//                InterestedCategoryActivity.setUpCTA(InterestedStoreActivity.this, isListEmpty, flIntrstdBot, tvIntrstd); //in case the user wants to remove the existing store and leave it empty, the CTA should still be visible
            }
        });
        recyclerView.setAdapter(mInterestRecyclerViewAdapter);
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
                mInterestRecyclerViewAdapter.resetLikedList();
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
        HashMap<String, KcpContentPage> savedStoreLikeMap = FavouriteManager.getInstance(this).getFavStoreMap();
        HashMap<String, KcpContentPage> newStoreLikeMap = mInterestRecyclerViewAdapter.getTempStoreMap();

        ArrayList<String> savedStoreLikeList = FavouriteManager.getInstance(this).getLikeListFromContentPage(savedStoreLikeMap);
        ArrayList<String> newStoreLikeList = FavouriteManager.getInstance(this).getLikeListFromContentPage(newStoreLikeMap);

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
