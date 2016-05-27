package com.kineticcafe.kcpmall.fragments;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.adapters.NewsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.fragments.dummy.DummyContent;
import com.kineticcafe.kcpmall.views.GridSpacingItemDecoration;
import com.kineticcafe.kcpmall.widget.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

public class DealsFragment extends BaseFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    public DealsRecyclerViewAdapter mDealsRecyclerViewAdapter;
    public EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    public DealsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DealsFragment newInstance(int columnCount) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);
        RecyclerView rvDeals = (RecyclerView) view.findViewById(R.id.rvDeals);


        /*GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(getActivity(), R.dimen.card_horizontal_margin);
        rvDeals.addItemDecoration(itemDecoration);*/


        /*int spanCount = 2;
        int spacing = getResources().getDimensionPixelSize(R.dimen.card_horizontal_margin); // 50px
        boolean includeEdge = true;
        rvDeals.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));*/

        rvDeals.setNestedScrollingEnabled(true);
        setupRecyclerView(rvDeals);

        final SwipeRefreshLayout srlDeals = (SwipeRefreshLayout) view.findViewById(R.id.srlDeals);
        srlDeals.setColorSchemeResources(R.color.themeColor);
        srlDeals.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*HomeFragment.getInstance().setOnRefreshListener(new HomeFragment.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        srlDeals.setRefreshing(false);
                        mMainActivity.showSnackBar(R.string.warning_download_completed, 0, null);
                    }
                });
                HomeFragment.getInstance().downloadNewsAndDeal();
                mEndlessRecyclerViewScrollListener.onLoadDone();*/
            }
        });

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), mColumnCount);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        ArrayList<KcpContentPage> temp = new ArrayList<>();
        mDealsRecyclerViewAdapter = new DealsRecyclerViewAdapter(getActivity(), temp);
        recyclerView.setAdapter(mDealsRecyclerViewAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore() {

//                HomeFragment.getInstance().downloadMoreFeeds(Constants.EXTERNAL_CODE_FEED);

            }
        };
        recyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }
}
