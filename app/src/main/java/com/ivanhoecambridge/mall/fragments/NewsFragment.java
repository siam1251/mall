package com.ivanhoecambridge.mall.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.InfoList;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.KcpMallInfo;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.adapters.NewsRecyclerViewAdapter;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.views.NewsRecyclerItemDecoration;
import com.ivanhoecambridge.mall.widget.EndlessRecyclerViewScrollListener;

import java.io.InputStream;

/**
 * Created by Kay on 2016-05-04.
 */
public class NewsFragment extends BaseFragment {
    public  NewsRecyclerViewAdapter           mNewsRecyclerViewAdapter;
    public  EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private TextView                          tvEmptyState;
    private RecyclerView                      rvNews;

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

        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        rvNews = (RecyclerView) view.findViewById(R.id.rv);
        rvNews.setNestedScrollingEnabled(true);
        setupRecyclerView();
        tvEmptyState = (TextView) view.findViewById(R.id.tvEmptyState);
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
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
                mMainActivity.initializeKcpData(srl);
                mEndlessRecyclerViewScrollListener.onLoadDone();
            }
        });
        return view;
    }

    @Override
    public void onPageActive() {
    }

    public void setEmptyState(@Nullable String warningMsg) {
        if (mMainActivity != null) mMainActivity.setEmptyState(tvEmptyState, warningMsg);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvNews.setLayoutManager(linearLayoutManager);


        mNewsRecyclerViewAdapter = new NewsRecyclerViewAdapter(
                getActivity(),
                KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_FEED).getKcpContentPageList(true),
                true);
        checkForCinemaInfo();
        rvNews.setAdapter(mNewsRecyclerViewAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                HomeFragment.getInstance().downloadMoreFeeds(Constants.EXTERNAL_CODE_FEED);
            }
        };
        rvNews.addOnScrollListener(mEndlessRecyclerViewScrollListener);

        NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(getActivity(), R.dimen.card_vertical_margin);
        rvNews.addItemDecoration(itemDecoration);
    }

    /**
     * Checks for cinema info in the mallinfo.json files. If the info is found then the manual content type is added to the NewsRecyclerViewAdapter
     * so that the movie section will be shown.
     */
    private void checkForCinemaInfo() {
        String jsonAsString = null;
        try {
            InputStream jsonIs = getContext().getAssets().open(Constants.getStringFromResources(getContext(), R.string.mall_info_json));
            if (jsonIs != null) {
                jsonAsString = KcpUtility.convertStreamToString(jsonIs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        KcpMallInfo kcpMallInfo = KcpUtility.convertJsonToObjectClass(jsonAsString, KcpMallInfo.class);
        if (kcpMallInfo != null) {
            for (InfoList infoItem : kcpMallInfo.getInfoList()) {
                if (infoItem.getMenuTitle() != null && infoItem.getMenuTitle().equalsIgnoreCase("cinema")) {
                    mNewsRecyclerViewAdapter.setManualContentTypes(new String[] {"movie"});
                    break;
                }
            }
        }
    }


}
