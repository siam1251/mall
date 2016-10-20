package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kay on 2016-06-15.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(Context context, @DimenRes int itemOffsetId) {
        this.space = context.getResources().getDimensionPixelSize(itemOffsetId);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//        outRect.left = space;
//        outRect.right = space;
//        outRect.bottom = space;

        if(parent.getChildAdapterPosition(view) == 0) outRect.left = space;


    }
}
