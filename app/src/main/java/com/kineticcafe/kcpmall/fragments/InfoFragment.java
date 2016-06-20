package com.kineticcafe.kcpmall.fragments;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.adapters.CategoryRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;

/**
 * Created by Kay on 2016-06-20.
 */
public class InfoFragment extends BaseFragment {

    public CategoryRecyclerViewAdapter mCategoryRecyclerViewAdapter;
    private Rect mRect = new Rect();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        final RecyclerView rvInfo = (RecyclerView) view.findViewById(R.id.rvInfo);

        setupRecyclerView(rvInfo);

        final ImageView ivInfo = (ImageView) view.findViewById(R.id.ivInfo);
        /*rvInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                ivInfo.getLocalVisibleRect(mRect);
                if(mRect.bottom > 0){
                    ivInfo.setY((float) (mRect.top / 2));
                }
            }
        });*/

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        mCategoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(
                getActivity(),
                CategoryIconFactory.getFilteredKcpCategoryList(KcpCategoryRoot.getInstance().getCategoriesList()), KcpContentTypeFactory.PREF_ITEM_TYPE_CAT);
        recyclerView.setAdapter(mCategoryRecyclerViewAdapter);
    }


}
