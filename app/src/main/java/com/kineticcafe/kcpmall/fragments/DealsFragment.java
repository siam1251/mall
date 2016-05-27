package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.views.DealRecyclerItemDecoration;
import com.kineticcafe.kcpmall.widget.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

public class DealsFragment extends BaseFragment {
    private final int COLUMN_COUNT = 2;
    public DealsRecyclerViewAdapter mDealsRecyclerViewAdapter;
    public EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    /*private static DealsFragment sDealsFragment;
    public static DealsFragment getInstance(){
        if(sDealsFragment == null) sDealsFragment = new DealsFragment();
        return sDealsFragment;
    }*/

    public static DealsFragment newInstance(int columnCount) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);
        RecyclerView rvDeals = (RecyclerView) view.findViewById(R.id.rvDeals);

        rvDeals.setNestedScrollingEnabled(true);
        setupRecyclerView(rvDeals);

        final SwipeRefreshLayout srlDeals = (SwipeRefreshLayout) view.findViewById(R.id.srlDeals);
        srlDeals.setColorSchemeResources(R.color.themeColor);
        srlDeals.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment.getInstance().setOnRefreshListener(new HomeFragment.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        srlDeals.setRefreshing(false);
                        mMainActivity.showSnackBar(R.string.warning_download_completed, 0, null);
                    }
                });
                HomeFragment.getInstance().downloadNewsAndDeal();
                mEndlessRecyclerViewScrollListener.onLoadDone();
            }
        });

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mDealsRecyclerViewAdapter = new DealsRecyclerViewAdapter(getActivity(), new ArrayList<KcpContentPage>(), new ArrayList<KcpContentPage>());
        recyclerView.setAdapter(mDealsRecyclerViewAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore() {
//                HomeFragment.getInstance().downloadMoreFeeds(Constants.EXTERNAL_CODE_FEED);
            }
        };
        recyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(getActivity(), R.dimen.card_vertical_margin, mDealsRecyclerViewAdapter);
        recyclerView.addItemDecoration(itemDecoration);
    }
}
