package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.NewsRecyclerViewAdapter;
import com.kineticcafe.kcpmall.views.DealRecyclerItemDecoration;
import com.kineticcafe.kcpmall.views.NewsRecyclerItemDecoration;
import com.kineticcafe.kcpmall.widget.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-04.
 */
public class NewsFragment extends BaseFragment {
    public NewsRecyclerViewAdapter mNewsRecyclerViewAdapter;
    public EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    /*private static NewsFragment sNewsFragment;
    public static NewsFragment getInstance(){
        if(sNewsFragment == null) sNewsFragment = new NewsFragment();
        return sNewsFragment;
    }*/

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
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

        View view = inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView rvNews = (RecyclerView) view.findViewById(R.id.rvNews);
        rvNews.setNestedScrollingEnabled(true);
        setupRecyclerView(rvNews);

        final SwipeRefreshLayout srlNews = (SwipeRefreshLayout) view.findViewById(R.id.srlNews);
        srlNews.setColorSchemeResources(R.color.themeColor);
        srlNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment.getInstance().setOnRefreshListener(new HomeFragment.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        srlNews.setRefreshing(false);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mNewsRecyclerViewAdapter = new NewsRecyclerViewAdapter(getActivity(), new ArrayList<KcpContentPage>()/*, HomeFragment.getInstance().sTwitterFeedList, HomeFragment.getInstance().sInstaFeedList*/);
        recyclerView.setAdapter(mNewsRecyclerViewAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                HomeFragment.getInstance().downloadMoreFeeds(Constants.EXTERNAL_CODE_FEED);
            }
        };
        recyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);

        NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(getActivity(), R.dimen.card_vertical_margin);
        recyclerView.addItemDecoration(itemDecoration);

    }
}
