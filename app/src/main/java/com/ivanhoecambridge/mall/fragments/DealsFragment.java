package com.ivanhoecambridge.mall.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.DealsRecyclerViewAdapter;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.views.DealRecyclerItemDecoration;
import com.ivanhoecambridge.mall.widget.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

public class DealsFragment extends BaseFragment {
    private final int COLUMN_COUNT = 2;
    public DealsRecyclerViewAdapter mDealsRecyclerViewAdapter;
    public EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private TextView tvEmptyState;

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
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        RecyclerView rvDeals = (RecyclerView) view.findViewById(R.id.rv);

        rvDeals.setNestedScrollingEnabled(true);
        setupRecyclerView(rvDeals);

        final SwipeRefreshLayout srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        tvEmptyState = (TextView) view.findViewById(R.id.tvEmptyState);
        srl.setColorSchemeResources(R.color.themeColor);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainActivity.setOnRefreshListener(new MainActivity.RefreshListener() {
                    @Override
                    public void onRefresh(int msg) {
                        srl.setRefreshing(false);
                        mMainActivity.showSnackBar(msg, 0, null);
                    }
                });
                if(mMainActivity.mIsDataLoaded) HomeFragment.getInstance().initializeHomeData();
                else mMainActivity.initializeKcpData();
                mEndlessRecyclerViewScrollListener.onLoadDone();
            }
        });

        return view;
    }

    public void setEmptyState(@Nullable String warningMsg){
        if(mMainActivity != null) mMainActivity.setEmptyState(tvEmptyState, warningMsg);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        ArrayList<KcpContentPage> dealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true);
        ArrayList<KcpContentPage> recommendedList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true);
        KcpUtility.sortKcpContentpageByExpiryDate(recommendedList);
        KcpUtility.sortKcpContentpageByExpiryDate(dealsList);

        mDealsRecyclerViewAdapter = new DealsRecyclerViewAdapter(
                getActivity(),
                true,
                recommendedList,
                dealsList);
        recyclerView.setAdapter(mDealsRecyclerViewAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore() {
            }
        };
        recyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        DealRecyclerItemDecoration itemDecoration = new DealRecyclerItemDecoration(getActivity(), R.dimen.card_vertical_margin, mDealsRecyclerViewAdapter);
        recyclerView.addItemDecoration(itemDecoration);
    }
}
