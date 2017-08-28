package com.ivanhoecambridge.mall.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategoryRoot;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.adapters.CategoryRecyclerViewAdapter;
import com.ivanhoecambridge.mall.factory.CategoryIconFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;


public class CategoriesFragment extends BaseFragment {

    public CategoryRecyclerViewAdapter mCategoryRecyclerViewAdapter;
    private TextView tvEmptyState;


    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
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
        RecyclerView rvNews = (RecyclerView) view.findViewById(R.id.rv);
        rvNews.setNestedScrollingEnabled(true);
        setupRecyclerView(rvNews);
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
            }
        });
        return view;
    }

    @Override
    public void onPageActive() {}

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        mCategoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(
                getActivity(),
                CategoryIconFactory.getFilteredKcpCategoryList(KcpCategoryRoot.getInstance().getCategoriesList()), KcpContentTypeFactory.PREF_ITEM_TYPE_CAT);
        recyclerView.setAdapter(mCategoryRecyclerViewAdapter);
    }
}
