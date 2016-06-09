package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.MainActivity;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;


public class PlacesFragment extends BaseFragment {
    public CategoryStoreRecyclerViewAdapter mPlaceRecyclerViewAdapter;
    private TextView tvEmptyState;

    public static PlacesFragment newInstance() {
        PlacesFragment fragment = new PlacesFragment();
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
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        RecyclerView rvNews = (RecyclerView) view.findViewById(R.id.rv);
        rvNews.setNestedScrollingEnabled(true);
        setupRecyclerView(rvNews);

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

                DirectoryFragment.getInstance().downloadPlaces();
            }
        });
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setLayoutManager(linearLayoutManager);
        mPlaceRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                getActivity(),
                KcpPlacesRoot.getInstance().getPlacesList(), KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE);
        recyclerView.setAdapter(mPlaceRecyclerViewAdapter);
    }

}
