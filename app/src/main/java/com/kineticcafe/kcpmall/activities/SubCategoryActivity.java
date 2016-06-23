package com.kineticcafe.kcpmall.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.CategoryRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.DealRecyclerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-06-08.
 */
public class SubCategoryActivity extends AppCompatActivity {

    private final int COLUMN_COUNT = 2;
    public CategoryRecyclerViewAdapter mCategoryRecyclerViewAdapter;
    public CategoryStoreRecyclerViewAdapter mCategoryStoreRecyclerViewAdapter;
    private String mActivityType = "";
    private String mExternalCode = "";
    private String mCategoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Bundle bundle = getIntent().getExtras();
        mActivityType = bundle.getString(Constants.ARG_CATEGORY_ACTIVITY_TYPE);
        mExternalCode = bundle.getString(Constants.ARG_EXTERNAL_CODE);
        mCategoryName = bundle.getString(Constants.ARG_CAT_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(mCategoryName);

        RecyclerView rvNews = (RecyclerView) findViewById(R.id.rv);
        rvNews.setNestedScrollingEnabled(true);

        if(mActivityType.equals(Constants.CategoryActivityType.SUBCATEGORY.toString())){
            setupSubCatRecyclerView(rvNews);
        } else if(mActivityType.equals(Constants.CategoryActivityType.STORE.toString())){
            setupStoreRecyclerView(rvNews);
        }
    }

    private void setupSubCatRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ArrayList<KcpCategories> kcpCategories = new ArrayList<KcpCategories>(KcpCategoryRoot.getInstance().getSubcategories(mExternalCode));

        KcpCategories currentCategory = KcpCategoryRoot.getInstance().getCategory(mExternalCode);
        if(currentCategory != null) kcpCategories.add(0, currentCategory);
        if(kcpCategories != null){
            mCategoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(
                    this,
                    kcpCategories, KcpContentTypeFactory.PREF_ITEM_TYPE_SUB_CAT);
            recyclerView.setAdapter(mCategoryRecyclerViewAdapter);
        }
    }

    private void setupStoreRecyclerView(RecyclerView recyclerView) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mCategoryStoreRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                this,
                KcpCategoryRoot.getInstance().getPlaces(mExternalCode), KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE);
        recyclerView.setAdapter(mCategoryStoreRecyclerViewAdapter);

        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(this, R.dimen.card_vertical_margin, mCategoryStoreRecyclerViewAdapter);
        recyclerView.addItemDecoration(itemDecoration);
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
        if(mCategoryStoreRecyclerViewAdapter != null) mCategoryStoreRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
