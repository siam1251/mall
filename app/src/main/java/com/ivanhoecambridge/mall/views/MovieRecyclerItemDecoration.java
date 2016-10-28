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
public class MovieRecyclerItemDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;
    private RecyclerView.Adapter mAdapter;

    public MovieRecyclerItemDecoration(@NonNull Context context, @DimenRes int itemOffsetId, RecyclerView.Adapter adapter) {
        mItemOffset = context.getResources().getDimensionPixelSize(itemOffsetId);
        mAdapter = adapter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        final int itemCount = state.getItemCount();
        if(position == 0){
            outRect.left = mItemOffset;
        } else if(itemCount > 0 && position == itemCount - 1 ){
            outRect.right = mItemOffset;
        }
    }
}