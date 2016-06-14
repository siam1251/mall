package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.InterestRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.kcpData.KcpCategoryManager;
import com.kineticcafe.kcpmall.utility.Utility;
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
        setupRecyclerView(rvIntrstCat);

        final TextView tvIntrstd = (TextView) findViewById(R.id.tvIntrstd);
        tvIntrstd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
                pb.setVisibility(View.VISIBLE);
                tvIntrstd.setVisibility(View.GONE);
                KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(InterestedStoreActivity.this, new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        tvIntrstd.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                pb.setVisibility(View.GONE);

                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                Utility.saveGson(InterestedStoreActivity.this, Constants.PREFS_KEY_FAV_STORE_LIKE_LINK, mInterestRecyclerViewAdapter.getFavStoreLikeLinkList());
                                setResult(Activity.RESULT_OK, new Intent());
                                finish();
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                kcpCategoryManager.postInterestedStores(mInterestRecyclerViewAdapter.getFavStoreLikeLinkList());

            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ArrayList<KcpPlaces> kcpPlacesArrayList = KcpCategoryRoot.getInstance().getPlacesList();
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

        mInterestRecyclerViewAdapter = new InterestRecyclerViewAdapter(this, kcpPlacesArrayList);
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
                finish();
//                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
    }

    public void checkIfNotSaved(final AlertDialogForInterest.DialogAnsweredListener dialogAnsweredListener){
        ArrayList<String> savedStoreLikeList = Utility.loadGsonArrayListString(this, Constants.PREFS_KEY_FAV_STORE_LIKE_LINK);
        ArrayList<String> newStoreLikeList = mInterestRecyclerViewAdapter.getFavStoreLikeLinkList();
        if(!Utility.isTwoStringListsEqual(savedStoreLikeList, newStoreLikeList)){
            AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
            alertDialogForInterest.getAlertDialog(this, dialogAnsweredListener).show();
        } else {
            dialogAnsweredListener.okClicked();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
