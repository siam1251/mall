package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.InterestRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.kcpData.KcpCategoryManager;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-31.
 */
public class InterestedCategoryActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private InterestRecyclerViewAdapter mInterestRecyclerViewAdapter;
    private RecyclerView rvIntrstCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrstd_category);
//        setContentView(R.layout.activity_intrstd_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_interested_category));

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
                KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(InterestedCategoryActivity.this, new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {

                        tvIntrstd.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                Utility.saveGson(InterestedCategoryActivity.this, Constants.PREFS_KEY_CATEGORY, mInterestRecyclerViewAdapter.getFavCatTempList());
                                InterestedCategoryActivity.this.startActivityForResult(new Intent(InterestedCategoryActivity.this, InterestedStoreActivity.class), Constants.REQUEST_CODE_CHANGE_INTEREST);
//                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                kcpCategoryManager.downloadPlacesForTheseCategoryIds(mInterestRecyclerViewAdapter.getFavCatTempList());
            }
        });
    }

    public static class GridLayoutItem {
        public int spanCount;
        public int relativeLayoutRule;

        public GridLayoutItem(int spanCount, int relativelayoutRule){
            this.spanCount = spanCount;
            this.relativeLayoutRule = relativelayoutRule;
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        final int maxSpanCount = 100;
//        ArrayList<KcpCategories> kcpCategoriesArrayList = KcpCategoryRoot.getInstance().getFingerPrintCategoriesList();
        ArrayList<KcpCategories> kcpCategoriesArrayList = CategoryIconFactory.getFilteredKcpCategoryList(KcpCategoryRoot.getInstance().getFingerPrintCategoriesList());

        final ArrayList<GridLayoutItem> gridLayoutItemArrayList = new ArrayList<GridLayoutItem>();

        float txtMar = getResources().getDimension(R.dimen.intrst_card_txt_horizontal_margin);
        int firstSpanSize;
        int secondSpanSize;
        int thirdSpanSize;

        float compatPadding = Utility.dpToPx(this, 5);
        for(int position = 0 ; position < kcpCategoriesArrayList.size(); position++){

            if(position < gridLayoutItemArrayList.size()) continue;

            String firstItemName = kcpCategoriesArrayList.get(position).getCategoryName();
            float firstItemWidth = getTextSize(firstItemName) + txtMar * 2;
            float spaceLeft = getSpaceLeftFromOneSide(firstItemWidth);
            float totalSize = firstItemWidth  + spaceLeft + spaceLeft;

            replaceIfExist(gridLayoutItemArrayList, position, new GridLayoutItem(maxSpanCount, RelativeLayout.CENTER_HORIZONTAL));

            if( position + 1 < kcpCategoriesArrayList.size() ){
                String secondItemName = kcpCategoriesArrayList.get(position + 1).getCategoryName();
                float secondItemWidth = getTextSize(secondItemName) + txtMar * 2;

                spaceLeft = getSpaceLeftFromOneSide(firstItemWidth + secondItemWidth);

                if(spaceLeft > 0) {
                    totalSize = spaceLeft + firstItemWidth + compatPadding + secondItemWidth + spaceLeft;
                    firstSpanSize = (int) ((float) (spaceLeft + firstItemWidth + compatPadding/2) / totalSize * maxSpanCount);
                    secondSpanSize = maxSpanCount - firstSpanSize;
                    replaceIfExist(gridLayoutItemArrayList, position, new GridLayoutItem(firstSpanSize, RelativeLayout.ALIGN_PARENT_RIGHT));
                    replaceIfExist(gridLayoutItemArrayList, position + 1, new GridLayoutItem(secondSpanSize, RelativeLayout.ALIGN_PARENT_LEFT));

                    if( position + 2 < kcpCategoriesArrayList.size() ){
                        String thirdItemName = kcpCategoriesArrayList.get(position + 2).getCategoryName();
                        float thirdItemWidth = getTextSize(thirdItemName) + txtMar * 2;
                        spaceLeft = getSpaceLeftFromOneSide(firstItemWidth + secondItemWidth + thirdItemWidth);

                        if(spaceLeft > 0) { //meaning we can fit in 3 items
                            totalSize = spaceLeft + firstItemWidth + compatPadding + secondItemWidth + compatPadding + thirdItemWidth + spaceLeft;
                            firstSpanSize = (int) ((float) (spaceLeft + firstItemWidth + compatPadding / 2) / totalSize * maxSpanCount);
                            secondSpanSize = (int) ((float) ( secondItemWidth + compatPadding )/ totalSize * maxSpanCount);
                            thirdSpanSize = maxSpanCount - firstSpanSize - secondSpanSize;
                            replaceIfExist(gridLayoutItemArrayList, position, new GridLayoutItem(firstSpanSize, RelativeLayout.ALIGN_PARENT_RIGHT));
                            replaceIfExist(gridLayoutItemArrayList, position + 1, new GridLayoutItem(secondSpanSize, RelativeLayout.CENTER_HORIZONTAL));
                            replaceIfExist(gridLayoutItemArrayList, position + 2, new GridLayoutItem(thirdSpanSize, RelativeLayout.ALIGN_PARENT_LEFT));
                        }
                    }
                }
            }
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, maxSpanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return gridLayoutItemArrayList.get(position).spanCount;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        mInterestRecyclerViewAdapter = new InterestRecyclerViewAdapter(this, kcpCategoriesArrayList, gridLayoutItemArrayList);
        recyclerView.setAdapter(mInterestRecyclerViewAdapter);
    }

    public void replaceIfExist(ArrayList<GridLayoutItem> list, int position, GridLayoutItem gridLayoutItem){
        if(position < list.size() ) list.set(position, gridLayoutItem);
        else list.add(position, gridLayoutItem);
    }

    public float getTextSize(String string){
        Paint p = new Paint();
//        p.setTextSize(getResources().getDimension(R.dimen.intrstd_name));
        p.setTextSize(Utility.dpToPx(this, 15));
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
                mInterestRecyclerViewAdapter.resetFavCatTempList();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_CHANGE_INTEREST) {
            if(resultCode == Activity.RESULT_OK){
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public void checkIfNotSaved(final AlertDialogForInterest.DialogAnsweredListener dialogAnsweredListener){
        ArrayList<Integer> savedFavCatList = Utility.loadGsonArrayList(this, Constants.PREFS_KEY_CATEGORY);
        ArrayList<Integer> newFavCatList = mInterestRecyclerViewAdapter.getFavCatTempList();
        if(!Utility.isTwoIntegerListsEqual(savedFavCatList, newFavCatList)){
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
}
