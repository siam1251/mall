package com.ivanhoecambridge.mall.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategoryRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.InterestRecyclerViewAdapter;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.CategoryIconFactory;
import factory.HeaderFactory;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.managers.NetworkManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Kay on 2016-05-31.
 */
public class InterestedCategoryActivity extends AppCompatActivity {

    protected static final String TAG = "InterestedCategory";
    protected final Logger logger = new Logger(getClass().getName());
    private InterestRecyclerViewAdapter mInterestRecyclerViewAdapter;
    private RecyclerView rvIntrstCat;
    private FrameLayout flIntrstdBot;
    private TextView tvIntrstd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrstd_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if(FavouriteManager.getInstance(this).getInterestFavSize() > 0) getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_interested_category_update));
        else getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_interested_category));

        flIntrstdBot = (FrameLayout) findViewById(R.id.flIntrstdBot);
        rvIntrstCat = (RecyclerView) findViewById(R.id.rvIntrstCat);
        rvIntrstCat.setNestedScrollingEnabled(false); //set false for smooth scroll when nesting recyclerview inside nestedscrollview

        tvIntrstd = (TextView) findViewById(R.id.tvIntrstd);
        tvIntrstd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
                pb.setVisibility(View.VISIBLE);
                tvIntrstd.setVisibility(View.GONE);
                KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(InterestedCategoryActivity.this, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        tvIntrstd.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                if(NetworkManager.isConnected(InterestedCategoryActivity.this)) return;
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                InterestedCategoryActivity.this.startActivityForResult(new Intent(InterestedCategoryActivity.this, InterestedStoreActivity.class), Constants.REQUEST_CODE_CHANGE_INTEREST);
                                ActivityAnimation.startActivityAnimation(InterestedCategoryActivity.this);
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                kcpCategoryManager.downloadPlacesForTheseCategoryIds(mInterestRecyclerViewAdapter.getCatIdsFromMap());
                FavouriteManager.getInstance(InterestedCategoryActivity.this).updateFavCat(mInterestRecyclerViewAdapter.getTempCatMap(), mInterestRecyclerViewAdapter.getRemovedCatMap(), true, null);
            }
        });
        setupRecyclerView(rvIntrstCat);
    }

    public static class GridLayoutItem {
        public int spanCount;
        public int relativeLayoutRule;

        public GridLayoutItem(int spanCount, int relativelayoutRule){
            this.spanCount = spanCount;
            this.relativeLayoutRule = relativelayoutRule;
        }
    }

    public static class nameComparator implements Comparator<KcpCategories> {
        @Override
        public int compare(KcpCategories o1, KcpCategories o2) {
            try {
                return (o1.getCategoryName().toString().toLowerCase()).compareTo(o2.getCategoryName().toString().toLowerCase());
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        final int maxSpanCount = KcpUtility.getScreenWidth(this);
        ArrayList<KcpCategories> kcpCategoriesArrayList = CategoryIconFactory.getFilteredKcpCategoryList(KcpCategoryRoot.getInstance().getFingerPrintCategoriesList());
        Collections.sort(kcpCategoriesArrayList, new nameComparator());

        final ArrayList<GridLayoutItem> gridLayoutItemArrayList = new ArrayList<GridLayoutItem>();

        int firstSpanSize;
        int secondSpanSize;
        int thirdSpanSize;

        float compatPadding = 0;

        for(int position = 0 ; position < kcpCategoriesArrayList.size(); position++) {
            if(position < gridLayoutItemArrayList.size()) continue;

            String firstItemName = kcpCategoriesArrayList.get(position).getCategoryName();
            float firstItemWidth = getTextSize(firstItemName);
            float spaceLeft = getSpaceLeftFromOneSide(firstItemWidth);
            float totalSize = firstItemWidth  + spaceLeft + spaceLeft;

            replaceIfExist(gridLayoutItemArrayList, position, new GridLayoutItem(maxSpanCount, RelativeLayout.CENTER_HORIZONTAL));

            if( position + 1 < kcpCategoriesArrayList.size() ){
                String secondItemName = kcpCategoriesArrayList.get(position + 1).getCategoryName();
                float secondItemWidth = getTextSize(secondItemName);

                spaceLeft = getSpaceLeftFromOneSide(firstItemWidth + compatPadding + secondItemWidth);

                if(spaceLeft > 0) {
                    totalSize = spaceLeft + firstItemWidth + compatPadding + secondItemWidth + spaceLeft;
                    firstSpanSize = (int) ((float) (spaceLeft + firstItemWidth + compatPadding / 2) / totalSize * maxSpanCount);
                    secondSpanSize = maxSpanCount - firstSpanSize;

                    replaceIfExist(gridLayoutItemArrayList, position, new GridLayoutItem(firstSpanSize, RelativeLayout.ALIGN_PARENT_RIGHT));
                    replaceIfExist(gridLayoutItemArrayList, position + 1, new GridLayoutItem(secondSpanSize, RelativeLayout.ALIGN_PARENT_LEFT));

                    if( position + 2 < kcpCategoriesArrayList.size() ){
                        String thirdItemName = kcpCategoriesArrayList.get(position + 2).getCategoryName();
                        float thirdItemWidth = getTextSize(thirdItemName);
                        spaceLeft = getSpaceLeftFromOneSide(firstItemWidth + compatPadding + secondItemWidth + compatPadding + thirdItemWidth);

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

        mInterestRecyclerViewAdapter = new InterestRecyclerViewAdapter(this, kcpCategoriesArrayList, gridLayoutItemArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(boolean isListEmpty) {
//                setUpCTA(InterestedCategoryActivity.this, isListEmpty, flIntrstdBot, tvIntrstd); //in case the user wants to remove the existing store and leave it empty, the CTA should still be visible
            }
        });
        recyclerView.setAdapter(mInterestRecyclerViewAdapter);

        if(kcpCategoriesArrayList.size() > 0) {
            flIntrstdBot.setVisibility(View.VISIBLE);
            tvIntrstd.setText(getString(R.string.action_next));
        }
    }

    public interface ItemClickListener{
        void onItemClick(boolean isListEmpty);
    }

    public static void setUpCTA(Context context, boolean isListEmpty, final FrameLayout fl, TextView tv){
        if(isListEmpty && fl.getVisibility() == View.VISIBLE) {
            fl.setVisibility(View.GONE);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(context,
                    R.anim.anim_slide_down_out_of_screen);
            slideUpAnimation.reset();
            fl.startAnimation(slideUpAnimation);
        } else if(!isListEmpty && fl.getVisibility() == View.GONE){

            tv.setText(context.getString(R.string.action_next));
            Animation slideUpAnimation = AnimationUtils.loadAnimation(context,
                    R.anim.anim_slide_up_from_out_of_screen);
            slideUpAnimation.reset();
            slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    fl.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            fl.startAnimation(slideUpAnimation);
        }
    }

    public void replaceIfExist(ArrayList<GridLayoutItem> list, int position, GridLayoutItem gridLayoutItem){
        if(position < list.size() ) list.set(position, gridLayoutItem);
        else list.add(position, gridLayoutItem);
    }

    private View mInterestedCategoryLayout;
    public int getTextSize(String text) {

        if(mInterestedCategoryLayout == null){
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInterestedCategoryLayout = inflater.inflate(R.layout.list_item_interested_category, null);
        }

        TextView tvIntrstd = (TextView) mInterestedCategoryLayout.findViewById(R.id.tvIntrstd);
        tvIntrstd.setText(text);
        mInterestedCategoryLayout.setLayoutParams(new ViewGroup.LayoutParams(0,0));
        mInterestedCategoryLayout.measure(KcpUtility.getScreenWidth(this), KcpUtility.getScreenHeight(this));

        return mInterestedCategoryLayout.getMeasuredWidth();
    }

    public float getSpaceLeftFromOneSide(float size){
        int screenWidth = KcpUtility.getScreenWidth(this);
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
                mInterestRecyclerViewAdapter.resetFavCatList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkIfNotSaved(new AlertDialogForInterest.DialogAnsweredListener() {
            @Override
            public void okClicked() {
                onFinish();
            }
        });
    }

    public void onFinish(){
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_CHANGE_INTEREST) {
            if(resultCode == Constants.RESULT_DONE_PRESSED_WITH_CHANGE){
                setResult(Activity.RESULT_OK, new Intent());
                onFinish();
            } else if (resultCode == Constants.RESULT_DONE_PRESSED_WITHOUT_CHANGE) {
                Log.e(TAG, "SHOULD NEVER ENTER HERE");
            } else if (resultCode == Constants.RESULT_EXIT){
                setResult(Activity.RESULT_OK, new Intent());
            } else if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
                if (resultCode != 0) {
                    setResult(resultCode, new Intent());
                    onBackPressed();
                }
            }
        }
    }

    public void checkIfNotSaved(final AlertDialogForInterest.DialogAnsweredListener dialogAnsweredListener){
        HashMap<String, KcpCategories> savedStoreLikeMap = FavouriteManager.getInstance(InterestedCategoryActivity.this).getFavCatMap();
        HashMap<String, KcpCategories> newStoreLikeMap = mInterestRecyclerViewAdapter.getTempCatMap();

        ArrayList<String> savedStoreLikeList = FavouriteManager.getInstance(InterestedCategoryActivity.this).getLikeListFromMap(savedStoreLikeMap);
        ArrayList<String> newStoreLikeList = FavouriteManager.getInstance(InterestedCategoryActivity.this).getLikeListFromMap(newStoreLikeMap);

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
}
