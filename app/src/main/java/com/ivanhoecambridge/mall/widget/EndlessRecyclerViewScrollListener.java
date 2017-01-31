package com.ivanhoecambridge.mall.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Kay on 2016-05-16.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
//    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
//    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }


//    private int previousTotal = 0; // The total number of items in the dataset after the last load
//    private boolean loading = true; // True if we are still waiting for the last set of data to load.
//    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
//    int firstVisibleItem, visibleItemCount, totalItemCount;
//
    private boolean mIsLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public void onLoadDone(){
        mIsLoading = false;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        super.onScrolled(view, dx, dy);

        totalItemCount = mLayoutManager.getItemCount();

        if(mLayoutManager instanceof  LinearLayoutManager){
            lastVisibleItem = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();
        }  else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItem = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }   else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);
        }

        if (!mIsLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            mIsLoading = true;
            onLoadMore();
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore();
}