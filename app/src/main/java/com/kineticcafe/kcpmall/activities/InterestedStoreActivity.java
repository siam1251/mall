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
import android.support.v7.widget.RecyclerView;
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
import com.kineticcafe.kcpmall.kcpData.KcpCategoryManager;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-31.
 */
public class InterestedStoreActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private InterestRecyclerViewAdapter mInterestRecyclerViewAdapter;
    private RecyclerView rvIntrstCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrstd_category);

        TextView tvIntrstdCatDesc = (TextView) findViewById(R.id.tvIntrstdCatDesc);
        tvIntrstdCatDesc.setText(getResources().getString(R.string.intrstd_store_desc));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_interested_store));

        rvIntrstCat = (RecyclerView) findViewById(R.id.rvIntrstCat);
        rvIntrstCat.setNestedScrollingEnabled(false); //set false for smooth scroll when nesting recyclerview inside nestedscrollview
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

    public static class GridLayoutItem {
        public int spanCount;
        public int relativeLayotRule;

        public GridLayoutItem(int spanCount, int relativelayoutRule){
            this.spanCount = spanCount;
            this.relativeLayotRule = relativelayoutRule;
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ArrayList<KcpPlaces> kcpPlacesArrayList = KcpCategoryRoot.getInstance().getPlacesList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        mInterestRecyclerViewAdapter = new InterestRecyclerViewAdapter(this, kcpPlacesArrayList);
        recyclerView.setAdapter(mInterestRecyclerViewAdapter);
    }

    public void replaceIfExist(ArrayList<GridLayoutItem> list, int position, GridLayoutItem gridLayoutItem){
        if(position < list.size() ) list.set(position, gridLayoutItem);
        else list.add(position, gridLayoutItem);
    }

    public float getTextSize(String string){
        Paint p = new Paint();
        p.setTextSize(getResources().getDimension(R.dimen.intrstd_name));
        return p.measureText(string);
    }

    public float getSpaceLeftFromOneSide(float size){
        int screenWidth = Utility.getScreenWidth(this);
        return (screenWidth - size) / 2f;
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
