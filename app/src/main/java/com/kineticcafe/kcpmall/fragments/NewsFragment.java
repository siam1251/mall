package com.kineticcafe.kcpmall.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpmall.Cheeses;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.NewsAdapter;
import com.kineticcafe.kcpmall.model.InstagramFeed;
import com.kineticcafe.kcpmall.model.TwitterFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-05-04.
 */
public class NewsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View mView;

    private String mParam1;
    private String mParam2;

    public NewsFragment() {}

    public static OneFragment newInstance(String param1, String param2) {
        OneFragment fragment = new OneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        mView = inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView rv = (RecyclerView) mView.findViewById(R.id.rv_news);
        rv.setNestedScrollingEnabled(true);
        setupRecyclerView(rv);

        return mView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ArrayList<String> temp = new ArrayList<>();
        temp.add("a");
        temp.add("a");
        temp.add("a");
        temp.add("a");
        temp.add("a");
        recyclerView.setAdapter(new NewsAdapter(getActivity(), temp, new ArrayList<TwitterFeed>(), new ArrayList<InstagramFeed>()));
    }

}
