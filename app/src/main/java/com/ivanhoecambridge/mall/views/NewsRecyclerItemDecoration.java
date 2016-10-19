package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kay on 2016-05-25.
 */
public class NewsRecyclerItemDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    public NewsRecyclerItemDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        mItemOffset = context.getResources().getDimensionPixelSize(itemOffsetId);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        if(position == 0){
            outRect.top = mItemOffset;
        }
    }
}