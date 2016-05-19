package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.NewsAdapter;
import com.kineticcafe.kcpmall.widget.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-04.
 */
public class NewsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public NewsAdapter mNewsAdapter;
    public EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

//    public NewsFragment() {}

    private static NewsFragment sNewsFragment;
    public static NewsFragment getInstance(){
        if(sNewsFragment == null) sNewsFragment = new NewsFragment();
        return sNewsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        ArrayList<KcpContentPage> temp = new ArrayList<>();
        mNewsAdapter = new NewsAdapter(getActivity(), temp, HomeFragment.sTwitterTweets, HomeFragment.sInstagramFeeds);
        recyclerView.setAdapter(mNewsAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                HomeFragment.getInstance().downloadMoreFeeds(Constants.EXTERNAL_CODE_FEED);
            }
        };
        recyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }
}
