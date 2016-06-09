package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.views.DealRecyclerItemDecoration;

public class CategoryStoreFragment extends BaseFragment {
    private final int COLUMN_COUNT = 2;
    private static final String ARG_EXTERNAL_CODE = "external_code";
    private static final String ARG_CAT_NAME = "cat_name";
    public CategoryStoreRecyclerViewAdapter mCategoryStoreRecyclerViewAdapter;
    private String mExternalCode = "";
    private String mCategoryName = "";

    public static CategoryStoreFragment newInstance(String externalCode, String categoryName) {
        CategoryStoreFragment fragment = new CategoryStoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXTERNAL_CODE, externalCode);
        args.putString(ARG_CAT_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExternalCode = getArguments().getString(ARG_EXTERNAL_CODE);
            mCategoryName = getArguments().getString(ARG_CAT_NAME);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_categories, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        setupRecyclerView(rv);

        //setting the toolbar inside the layout
        /*Toolbar toolbar= (Toolbar) view.findViewById(R.id.toolbar);
        mMainActivity.setSupportActionBar(toolbar);
        mMainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mMainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mMainActivity.getSupportActionBar().setTitle(mCategoryName);*/

        mMainActivity.setDrawerIndicatorEnabled(false, mCategoryName);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mCategoryStoreRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                getActivity(),
                KcpCategoryRoot.getInstance().getPlaces(mExternalCode), KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE);
        recyclerView.setAdapter(mCategoryStoreRecyclerViewAdapter);

        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(getActivity(), R.dimen.card_vertical_margin, mCategoryStoreRecyclerViewAdapter);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_test).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
