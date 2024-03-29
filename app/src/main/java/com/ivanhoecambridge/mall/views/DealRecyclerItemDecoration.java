package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;

/**
 * Created by Kay on 2016-05-25.
 */
public class DealRecyclerItemDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;
    private RecyclerView.Adapter mAdapter;

    public DealRecyclerItemDecoration(@NonNull Context context, @DimenRes int itemOffsetId, RecyclerView.Adapter adapter) {
        mItemOffset = context.getResources().getDimensionPixelSize(itemOffsetId);
        mAdapter = adapter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
        int type = mAdapter.getItemViewType(position);

        if(parent.getChildCount() > position && parent.getChildAt(position).getTop() == 0){ //adding the top margin to all the items in the first row
            outRect.top = mItemOffset;
        }

        if(type == KcpContentTypeFactory.ITEM_TYPE_DEAL || type == KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE){
            if(spanIndex == 0){
                outRect.left = mItemOffset;
            } else {
                outRect.right = mItemOffset;
            }
        }
    }
}