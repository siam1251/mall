package com.kineticcafe.kcpmall.activities;

import android.app.Activity;
import android.content.Context;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.InterestRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Kay on 2016-05-31.
 */
public class InterestedCategoryActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private InterestRecyclerViewAdapter mInterestRecyclerViewAdapter;
    private RecyclerView rvIntrstCat;
    private FrameLayout flIntrstdBot;
    private TextView tvIntrstd;
    private boolean mSpanSizeSet = false;

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
//        flIntrstdBot.setBackgroundColor(getResources().getColor(R.color.themeColor));

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
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                FavouriteManager.getInstance(InterestedCategoryActivity.this).cacheInterestedCategoryList(mInterestRecyclerViewAdapter.getFavCatTempList());
                                InterestedCategoryActivity.this.startActivityForResult(new Intent(InterestedCategoryActivity.this, InterestedStoreActivity.class), Constants.REQUEST_CODE_CHANGE_INTEREST);
                                ActivityAnimation.startActivityAnimation(InterestedCategoryActivity.this);
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                kcpCategoryManager.downloadPlacesForTheseCategoryIds(mInterestRecyclerViewAdapter.getFavCatTempList());
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

//        float compatPadding = - KcpUtility.dpToPx(this, 5);
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
//                    spaceLeft = 0;
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
//                            spaceLeft = 0;
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
                setUpCTA(InterestedCategoryActivity.this, isListEmpty, flIntrstdBot, tvIntrstd);
            }
        });
        recyclerView.setAdapter(mInterestRecyclerViewAdapter);

        if(kcpCategoriesArrayList.size() > 0) {
            flIntrstdBot.setVisibility(View.VISIBLE);
            tvIntrstd.setText(getString(R.string.action_save));
        }
    }

    public interface ItemClickListener{
        void onItemClick(boolean isListEmpty);
    }

    public static void setUpCTA(Context context, boolean isListEmpty, FrameLayout fl, TextView tv){
        if(isListEmpty && fl.getVisibility() == View.VISIBLE) {
            fl.setVisibility(View.GONE);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(context,
                    R.anim.anim_slide_down_out_of_screen);
            slideUpAnimation.reset();
            fl.startAnimation(slideUpAnimation);
        } else if(!isListEmpty && fl.getVisibility() == View.GONE){
            fl.setVisibility(View.VISIBLE);
            tv.setText(context.getString(R.string.action_save));
            Animation slideUpAnimation = AnimationUtils.loadAnimation(context,
                    R.anim.anim_slide_up_from_out_of_screen);
            slideUpAnimation.reset();
            fl.startAnimation(slideUpAnimation);
        }
    }




    public void replaceIfExist(ArrayList<GridLayoutItem> list, int position, GridLayoutItem gridLayoutItem){
        if(position < list.size() ) list.set(position, gridLayoutItem);
        else list.add(position, gridLayoutItem);
    }

    /*public float getTextSize(String string){
        Paint p = new Paint();
        p.setTextSize(getResources().getDimension(R.dimen.intrstd_name));
//        p.setTextSize(KcpUtility.dpToPx(this, 19f));
//        p.setTextSize(KcpUtility.dpToPx(this, 18f));
        return p.measureText(string);
    }*/

//    private static View mInterestedCategoryLayout;
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



        /*TextView textView = new TextView(this);
        textView.setPadding(0,0,0,0);
        textView.setText(text);
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.intrstd_name));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, KcpUtility.dpToPx(this, 15.8f));
//        textView.setTextSize(getResources().getDimension(R.dimen.intrstd_name));

        *//*int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(KcpUtility.getScreenWidth(this), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) getResources().getDimension(R.dimen.intrstd_card_height), View.MeasureSpec.AT_MOST);
        textView.measure(widthMeasureSpec, heightMeasureSpec);*//*

        textView.measure(0, 0);

        return textView.getMeasuredWidth();*/
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
                setResult(Activity.RESULT_CANCELED, new Intent());
                onFinish();
            } else if (resultCode == Constants.RESULT_EXIT){

            } else if (requestCode == Constants.REQUEST_CODE_VIEW_STORE_ON_MAP) {
                if (resultCode != 0) {
                    setResult(resultCode, new Intent());
                    onBackPressed();
                }
            }
        }
    }

    public void checkIfNotSaved(final AlertDialogForInterest.DialogAnsweredListener dialogAnsweredListener){
        ArrayList<Integer> savedFavCatList = FavouriteManager.getInstance(InterestedCategoryActivity.this).getInterestedCategoryList();
        ArrayList<Integer> newFavCatList = mInterestRecyclerViewAdapter.getFavCatTempList();
        if(!KcpUtility.isTwoIntegerListsEqual(savedFavCatList, newFavCatList)){
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
